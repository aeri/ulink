package urlshortener.web;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import urlshortener.domain.ShortURL;
import urlshortener.service.*;

import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.time.Duration;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
    private final GetIPInfo getIPInfo = new GetIPInfo();


    @Autowired
    private Environment LATENCY_PERIOD;

    private static Pattern pDomainNameOnly;
    private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

    private static final Logger log = LoggerFactory.getLogger(UrlShortenerController.class);

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

    public static boolean validateHTTP_URI(String uri) {
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e1) {
            return false;
        }
        return "http".equals(url.getProtocol()) || "https".equals(url.getProtocol());
    }

    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ModelAndView redirectTo(@PathVariable String id, HttpServletRequest request) {
        long start = System.currentTimeMillis();
        log.info(id);
        ShortURL l = shortUrlService.findByKey(id);

        String countryName = null;
        String countryCode = null;

        String platform;
        String browser;

        try {
            String userAgent = request.getHeader("User-Agent");
            platform = platformService.getPlatform(userAgent);
            browser = browserService.getBrowser(userAgent);
        } catch (NullPointerException e) {
            platform = "Unknown";
            browser = "Unknown";
        }

        if (l != null) {

            try {
                log.info("ABC");
                IPResponse response = getIPInfo.getIpResponse(request.getRemoteAddr());


                // Print out the country code
                countryName = response.getCountryName();
                countryCode = response.getCountryCode();
            } catch (NullPointerException e) {
                log.debug("Failed to retrieve IP file");
                ModelAndView model = new ModelAndView("error500");
                model.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return model;
            }
            catch (RateLimitedException | IOException ex) {
                log.debug("RateLimitedException");
                // Handle rate limits here.
            }

            long end = System.currentTimeMillis();
            clickService.saveClick(id, extractIP(request), countryName, countryCode, platform, browser, end - start);
            if (!l.getSafe().isEmpty()) {
                ModelAndView modelAndView = new ModelAndView("warning");
                modelAndView.addObject("malware", l.getSafe());
                modelAndView.addObject("link", l.getTarget());
                return modelAndView;

            } else {
                request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);

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
    public ResponseEntity<ShortURL> shortener(@RequestParam(value = "url", required = true) String url,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https", "ftp" });
        HttpHeaders h = new HttpHeaders();

        if (!validateHTTP_URI(url)) {
            url = "http://" + url;
        }

        if (urlValidator.isValid(url)) {

            final String toU = url;

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
            RestTemplate rest = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(700))
                    .setReadTimeout(Duration.ofMillis(700)).build();
            HttpEntity<String> requestEntity = new HttpEntity<String>("", h);

            CompletableFuture<ResponseEntity<String>> fResponse = CompletableFuture.supplyAsync(() -> {

                try {
                    return getResponse(rest, toU, requestEntity);
                } catch (SocketTimeoutException | HttpClientErrorException e) {
                    // e.printStackTrace();
                    log.info(e.getMessage());
                    log.info("Peticion incorrecta Timeout");
                    return new ResponseEntity<String>(h, HttpStatus.GATEWAY_TIMEOUT);
                }
            });

            CheckGSB checkGSB = new CheckGSB();
            String notSafe;
            try {
                log.debug(url);
                // Check url in Google Safe Browsing
                notSafe = checkGSB.checkSingleUrl(url);
            } catch (GoogleJsonResponseException e) {
                log.debug("Google Safe Browsing quota exceeded");
                notSafe = "";
            } catch (GeneralSecurityException | IOException e1) {
                e1.printStackTrace();
                ShortURL su = new ShortURL(url, false);
                return new ResponseEntity<>(su, h, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                ResponseEntity<String> response;
                response = fResponse.get();
                log.debug("Status code: " + response.getStatusCode());

                if (response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
                    log.debug("Peticion incorrecta HttpClientErrorException");
                    ShortURL su = new ShortURL(url, false);
                    return new ResponseEntity<>(su, h, HttpStatus.GATEWAY_TIMEOUT);
                } else {
                    ShortURL su = shortUrlService.save(url, request.getRemoteAddr(), notSafe);
                    h.setLocation(su.getUri());
                    log.debug("Peticion correcta");
                    return new ResponseEntity<>(su, h, HttpStatus.CREATED);
                }

            } catch (ResourceAccessException e) { // Timeout OR Unknown host
                log.debug(e.getMessage());
                log.debug("Peticion incorrecta Timeout");
                ShortURL su = new ShortURL(url, false);
                return new ResponseEntity<>(su, h, HttpStatus.GATEWAY_TIMEOUT);

            } catch (NullPointerException e) {
                log.info(e.getMessage());
                log.info("Fallo al guardar url en la base");
                ShortURL su = new ShortURL(url, false);
                return new ResponseEntity<>(su, h, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (InterruptedException | ExecutionException e) {
                log.info(e.getMessage());
                log.info("Error HTTP asincrono");
                ShortURL su = new ShortURL(url, false);
                return new ResponseEntity<>(su, h, HttpStatus.GATEWAY_TIMEOUT);
            }

        } else {
            ShortURL su = new ShortURL(url, false);
            return new ResponseEntity<>(su, h, HttpStatus.BAD_REQUEST);
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
        log.debug("linkConfirm Request");
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https", "ftp" });

        if (isValidDomainName(url)) {
            url = "http://" + url;
        }
        if (urlValidator.isValid(url)) {
            HttpHeaders h = new HttpHeaders();
            CheckGSB checkGSB = new CheckGSB();
            String notSafe;
            try {
                // Check url in Google Safe Browsing
                notSafe = checkGSB.checkSingleUrl(url);
            } 
            catch (GoogleJsonResponseException e) {
                log.debug("Google Safe Browsing quota exceeded");
                notSafe = "";
            }
            catch (GeneralSecurityException | IOException e1) {
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

    @GetMapping("/statistics")
    public ModelAndView statistics(HttpServletRequest request) throws Throwable {
        GetStats getStats = new GetStats();
        String minutes = LATENCY_PERIOD.getProperty("metric.latency.period");
        Timestamp since = new Timestamp(
                System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Long.parseLong(minutes)));
        return getStats.getGlobal(clickService, shortUrlService, since);
    }

    @GetMapping("/link-stats-access")
    public ModelAndView linkStatsAccess(HttpServletRequest request) {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("link-stats-access");
        modelAndView.addObject("failedAccess", "");

        return modelAndView;
    }

    @PostMapping("/linkStats")
    public ModelAndView linkStats(@RequestParam("shortenedUrl") String shortenedUrl, @RequestParam("code") String code)
            throws Throwable {
        GetStats getStats = new GetStats();
        return getStats.getLocal(clickService, shortUrlService, shortenedUrl, code);

    }

    @ResponseBody
    @RequestMapping(value = "/qr", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qr(@RequestParam("link") String link) {
        HttpHeaders h = new HttpHeaders();
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

        RestTemplate rest = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(700))
                .setReadTimeout(Duration.ofMillis(2000)).build();
        HttpEntity<String> requestEntity = new HttpEntity<String>("", h);
        String url = "https://chart.googleapis.com/chart?cht=qr&chl=" + link + "&chs=160x160&chld=L|0";
        return rest.exchange(url, HttpMethod.GET, requestEntity, byte[].class);
    }

}
