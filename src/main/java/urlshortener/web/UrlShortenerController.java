package urlshortener.web;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import urlshortener.domain.ShortURL;
import urlshortener.service.*;

import io.ipinfo.api.IPInfo;

import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;

import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Base64;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;

@Controller
public class UrlShortenerController {
	private final ShortURLService shortUrlService;

	private final ClickService clickService;
	private final PlatformService platformService = new PlatformService();
	private final BrowserService browserService = new BrowserService();

	@Autowired
	private Environment IPINFO_TOKEN;

	@Autowired
	private Environment EN_US_PATH;

	private static Pattern pDomainNameOnly;
	private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

	static {
		pDomainNameOnly = Pattern.compile(DOMAIN_NAME_PATTERN);
	}

	private static boolean isValidDomainName(String domainName) {
		return pDomainNameOnly.matcher(domainName).find();
	}

	public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService) {
		this.shortUrlService = shortUrlService;
		this.clickService = clickService;
	}

	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ModelAndView redirectTo(@PathVariable String id, HttpServletRequest request) {
		ShortURL l = shortUrlService.findByKey(id);

		IPInfo ipInfo;
		String countryName = null;
		String countryCode = null;
		String userAgent = request.getHeader("User-Agent");
		String platform = platformService.getPlatform(userAgent);
		String browser = browserService.getBrowser(userAgent);

		if (l != null) {

			try {
				ipInfo = IPInfo.builder().setToken(IPINFO_TOKEN.getProperty("ipinfo.token"))
						.setCountryFile(new File(EN_US_PATH.getProperty("path.en_us"))).build();

				System.out.println("Redirection requested from " + request.getRemoteAddr());
				IPResponse response = ipInfo.lookupIP("1.1.1.1"); // Only works for external IPs
				// IPResponse response = ipInfo.lookupIP(request.getRemoteAddr()); // Only works
				// for external IPs

				// Print out the country code
				countryName = response.getCountryName();
				countryCode = response.getCountryCode();
			} catch (RateLimitedException ex) {
				System.out.println("RateLimitedException");
				// Handle rate limits here.
			}

			String notSafe = "";

			clickService.saveClick(id, extractIP(request), countryName, countryCode, platform, browser);

			if (!l.getSafe()) {

				ModelAndView modelAndView = new ModelAndView("warning");

				modelAndView.addObject("malware", notSafe);
				modelAndView.addObject("link", l.getTarget());

				return modelAndView;
			} else {
				return new ModelAndView("redirect:" + l.getTarget());
			}

		} else {
			ModelAndView model = new ModelAndView("error");
			model.setStatus(HttpStatus.NOT_FOUND);
			return model;
		}
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https", "ftp" });

		if (isValidDomainName(url)) {
			url = "http://" + url;
		}

		if (urlValidator.isValid(url)) {
			HttpHeaders h = new HttpHeaders();
			CheckGSB checkGSB = new CheckGSB();
			String notSafe;
			try {
				System.out.println(url);
				notSafe = checkGSB.check(url);
			} catch (GeneralSecurityException | IOException e1) {
				e1.printStackTrace();
				ShortURL su = new ShortURL(url, false);
				return new ResponseEntity<>(su, h, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

				RestTemplate rest = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(700))
						.setReadTimeout(Duration.ofMillis(700)).build();
				HttpEntity<String> requestEntity = new HttpEntity<String>("", h);

				ResponseEntity<String> response = getResponse(rest, url, requestEntity);

				System.out.println("Status code: " + response.getStatusCode());
				ShortURL su = shortUrlService.save(url, request.getRemoteAddr(), notSafe);
				h.setLocation(su.getUri());
				System.out.println("Peticion correcta");
				return new ResponseEntity<>(su, h, HttpStatus.CREATED);
			} catch (SocketTimeoutException | ResourceAccessException e) { // Timeout OR Unknown host
				System.out.println(e.getMessage());
				System.out.println("Peticion incorrecta Timeout");
				ShortURL su = new ShortURL(url, false);
				return new ResponseEntity<>(su, h, HttpStatus.GATEWAY_TIMEOUT);
			} catch (HttpClientErrorException e) { // Client error
				System.out.println(e.getMessage());
				if (e.getRawStatusCode() == 404) {
					System.out.println("Peticion incorrecta HttpClientErrorException");
					ShortURL su = new ShortURL(url, false);
					return new ResponseEntity<>(su, h, HttpStatus.NOT_FOUND);
				} else {
					ShortURL su = shortUrlService.save(url, request.getRemoteAddr(), notSafe);
					h.setLocation(su.getUri());
					System.out.println("Peticion correcta");
					return new ResponseEntity<>(su, h, HttpStatus.CREATED);
				}
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	ResponseEntity<String> getResponse(RestTemplate rest, String url, HttpEntity<String> requestEntity)
			throws SocketTimeoutException {
		// Execute http get request as client
		return rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
	}

	@RequestMapping(value = "/linkConfirm", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ShortURL> shortenerConfirm(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {
		System.out.println("linkConfirm Request");
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https", "ftp" });

		if (isValidDomainName(url)) {
			url = "http://" + url;
		}
		if (urlValidator.isValid(url)) {
			HttpHeaders h = new HttpHeaders();
			CheckGSB checkGSB = new CheckGSB();
			String notSafe;
			try {
				notSafe = checkGSB.check(url);
			} catch (GeneralSecurityException | IOException e1) {
				e1.printStackTrace();
				ShortURL su = new ShortURL(url, false);
				return new ResponseEntity<>(su, h, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			ShortURL su = shortUrlService.save(url, request.getRemoteAddr(), notSafe);

			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	@GetMapping("/stadistics")
	public ModelAndView stadistics(HttpServletRequest request) throws Throwable {
		GetStats getStats = new GetStats();
		return getStats.getGlobal(clickService, shortUrlService);
	}

	@GetMapping("/link-stats-access")
	public ModelAndView linkStatsAccess(HttpServletRequest request) {
		ModelAndView modelAndView;
		modelAndView = new ModelAndView("link-stats-access");
		modelAndView.addObject("failedAccess", "");

		return modelAndView;
	}

	@PostMapping("/linkStats")
	public ModelAndView linkStats(@RequestParam("shortenedUrl") String shortenedUrl, @RequestParam("code") String code,
			HttpServletRequest request) throws Throwable {

		GetStats getStats = new GetStats();
		return getStats.getLocal(clickService, shortUrlService, shortenedUrl, code);

	}

	@ResponseBody
	@RequestMapping(value = "/qr",  method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<String> qr(@RequestParam("link") String link){
		HttpHeaders h = new HttpHeaders();
		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

		RestTemplate rest = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(700))
				.setReadTimeout(Duration.ofMillis(2000)).build();
		HttpEntity<String> requestEntity = new HttpEntity<String>("", h);
		String url = "https://chart.googleapis.com/chart?cht=qr&chl=" + link + "&chs=160x160&chld=L|0";
		ResponseEntity<byte[]> response =  rest.exchange(url, HttpMethod.GET, requestEntity, byte[].class);
		String base64 = Base64.getEncoder().encodeToString(response.getBody());
		return new ResponseEntity<String>(base64, HttpStatus.OK);
		//return response;	
		
	}

}
