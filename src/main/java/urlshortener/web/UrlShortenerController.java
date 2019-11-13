package urlshortener.web;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.safebrowsing.Safebrowsing;
import com.google.api.services.safebrowsing.model.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import urlshortener.domain.Browser;
import urlshortener.domain.Country;
import urlshortener.domain.Platform;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.impl.ShortURLRepositoryImpl;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import io.ipinfo.api.IPInfo;

import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;

@Controller
public class UrlShortenerController {
    private final ShortURLService shortUrlService;

    private final ClickService clickService;

    @Autowired
    private Environment GOOGLE_API_KEY;

    @Autowired
    private Environment IPINFO_TOKEN;

    public static final JacksonFactory GOOGLE_JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String GOOGLE_CLIENT_ID = "1"; // client id
    public static final String GOOGLE_CLIENT_VERSION = "0.0.1"; // client version
    public static final String GOOGLE_APPLICATION_NAME = "ulink"; // appication name
    public static final List<String> GOOGLE_THREAT_TYPES = Arrays.asList("MALWARE", "SOCIAL_ENGINEERING",
            "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION");
    public static final List<String> GOOGLE_PLATFORM_TYPES = Arrays.asList("ANY_PLATFORM");
    public static final List<String> GOOGLE_THREAT_ENTRYTYPES = Arrays.asList("URL");
    public static NetHttpTransport httpTransport;

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

    private String CheckGSB(String url) throws GeneralSecurityException, IOException {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        List<String> urls = Arrays.asList(url);

        FindThreatMatchesRequest findThreatMatchesRequest = createFindThreatMatchesRequest(urls);

        Safebrowsing.Builder safebrowsingBuilder = new Safebrowsing.Builder(httpTransport, GOOGLE_JSON_FACTORY, null)
                .setApplicationName(GOOGLE_APPLICATION_NAME);
        Safebrowsing safebrowsing = safebrowsingBuilder.build();

        FindThreatMatchesResponse findThreatMatchesResponse = safebrowsing.threatMatches()
                .find(findThreatMatchesRequest).setKey(GOOGLE_API_KEY.getProperty("google.api_key")).execute();

        List<ThreatMatch> threatMatches = findThreatMatchesResponse.getMatches();

        if (threatMatches != null && threatMatches.size() > 0) {
            return threatMatches.get(0).getThreatType();
        } else {
            return "";
        }

    }

    private FindThreatMatchesRequest createFindThreatMatchesRequest(List<String> urls) {
        FindThreatMatchesRequest findThreatMatchesRequest = new FindThreatMatchesRequest();

        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientId(GOOGLE_CLIENT_ID);
        clientInfo.setClientVersion(GOOGLE_CLIENT_VERSION);
        findThreatMatchesRequest.setClient(clientInfo);

        ThreatInfo threatInfo = new ThreatInfo();
        threatInfo.setThreatTypes(GOOGLE_THREAT_TYPES);
        threatInfo.setPlatformTypes(GOOGLE_PLATFORM_TYPES);
        threatInfo.setThreatEntryTypes(GOOGLE_THREAT_ENTRYTYPES);

        List<ThreatEntry> threatEntries = new ArrayList<ThreatEntry>();

        for (String url : urls) {
            ThreatEntry threatEntry = new ThreatEntry();
            threatEntry.set("url", url);
            threatEntries.add(threatEntry);
        }
        threatInfo.setThreatEntries(threatEntries);
        findThreatMatchesRequest.setThreatInfo(threatInfo);

        return findThreatMatchesRequest;
    }

    String getPlatform(String browserDetails) {
        // Code based on:
        // https://stackoverflow.com/questions/1326928/how-can-i-get-client-information-such-as-os-and-browser
        String userAgent = browserDetails.toLowerCase();
        if (userAgent.indexOf("windows") >= 0) {
            return "Windows";
        } else if (userAgent.indexOf("mac") >= 0) {
            return "Mac";
        } else if (userAgent.indexOf("x11") >= 0) {
            if (userAgent.contains("bsd")) {
                return "BSD";
            }
            return "Linux";
        } else if (userAgent.indexOf("android") >= 0) {
            return "Android";
        } else if (userAgent.indexOf("iphone") >= 0) {
            return "IPhone";
        } else {
            return "UnKnown, More-Info: " + userAgent;
        }
    }

    String getBrowser(String browserDetails) {
        // Code based on:
        // https://stackoverflow.com/questions/1326928/how-can-i-get-client-information-such-as-os-and-browser
        String userAgent = browserDetails;
        String user = userAgent.toLowerCase();
        if (user.contains("msie")) {
            return "Internet Explorer";
        } else if (user.contains("edge")) {
            return "Netscape";
        } else if (user.contains("safari") && user.contains("version")) {
            return "Safari";
        } else if (user.contains("opr") || user.contains("opera")) {
            return "Opera";
        } else if (user.contains("chrome")) {
            return "Chrome";
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)
                || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1)
                || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            return "Netscape";

        } else if (user.contains("firefox")) {
            return "Firefox";
        } else if (user.contains("rv")) {
            return "Internet Explorer";
        } else {
            return "Unknown Browser";
        }
    }

    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ModelAndView redirectTo(@PathVariable String id, HttpServletRequest request) {
        ShortURL l = shortUrlService.findByKey(id);

        IPInfo ipInfo;
        String countryName = null;
        String countryCode = null;
        String userAgent = request.getHeader("User-Agent");
        String platform = getPlatform(userAgent);
        String browser = getBrowser(userAgent);
        try {
            // Working in local deployment
            // ipInfo =
            // IPInfo.builder().setToken(IPINFO_TOKEN.getProperty("ipinfo.token")).setCountryFile(new
            // File("src/main/resources/en_US.json")).build();

            // Working in Docker deployment
            ipInfo = IPInfo.builder().setToken(IPINFO_TOKEN.getProperty("ipinfo.token"))
                    .setCountryFile(new File("/en_US.json")).build();

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

        if (l != null) {
            String notSafe = null;

            try {
                notSafe = CheckGSB(l.getTarget());
                if (notSafe != "") {
                    clickService.saveClick(id, extractIP(request), countryName, countryCode, platform, browser);
                    HttpHeaders h = new HttpHeaders();

                    ModelAndView modelAndView = new ModelAndView("warning");

                    modelAndView.addObject("malware", notSafe);
                    modelAndView.addObject("link", l.getTarget());

                    return modelAndView;

                } else {
                    clickService.saveClick(id, extractIP(request), countryName, countryCode, platform, browser);
                    return new ModelAndView("redirect:" + l.getTarget());
                }
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                ModelAndView model = new ModelAndView("error");
                model.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return model;
            } catch (IOException e) {
                e.printStackTrace();
                ModelAndView model = new ModelAndView("error");
                model.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return model;
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
            try {
                RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

                RestTemplate rest = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(700))
                        .setReadTimeout(Duration.ofMillis(700)).build();
                HttpEntity<String> requestEntity = new HttpEntity<String>("", h);

                ResponseEntity<String> response = getResponse(rest, url, requestEntity);

                System.out.println("Status code: " + response.getStatusCode());
                ShortURL su = shortUrlService.save(url, request.getRemoteAddr());
                h.setLocation(su.getUri());
                System.out.println("Peticion correcta");
                return new ResponseEntity<>(su, h, HttpStatus.CREATED);
            } catch (SocketTimeoutException e) { // Timeout
                System.out.println(e.getMessage());
                System.out.println("Peticion incorrecta Timeout");
                ShortURL su = new ShortURL(url, false);
                return new ResponseEntity<>(su, h, HttpStatus.OK);
            } catch (ResourceAccessException e) { // Unknown host
                System.out.println(e.getMessage());
                System.out.println("Peticion incorrecta ResourceAccessException");
                ShortURL su = new ShortURL(url, false);
                return new ResponseEntity<>(su, h, HttpStatus.OK);
            } catch (HttpClientErrorException e) { // Client error
                System.out.println(e.getMessage());
                if (e.getRawStatusCode() == 404) {
                    System.out.println("Peticion incorrecta HttpClientErrorException");
                    ShortURL su = new ShortURL(url, false);
                    return new ResponseEntity<>(su, h, HttpStatus.OK);
                } else {
                    ShortURL su = shortUrlService.save(url, request.getRemoteAddr());
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
            ShortURL su = shortUrlService.save(url, request.getRemoteAddr());
            HttpHeaders h = new HttpHeaders();
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
    public ModelAndView stadistics(HttpServletRequest request) {
        System.out.println("global stadistics");
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("stadistics");

        Long totalURL = shortUrlService.count();
        modelAndView.addObject("totalURL", totalURL);

        Long totalClicks = clickService.count();
        modelAndView.addObject("totalClicks", totalClicks);


        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        for(Long threadID : threadMXBean.getAllThreadIds()) {
            ThreadInfo info = threadMXBean.getThreadInfo(threadID);
            System.out.println("Thread name: " + info.getThreadName());
            System.out.println("Thread State: " + info.getThreadState());
            System.out.println(String.format("CPU time: %s ns",
                    threadMXBean.getThreadCpuTime(threadID)));
        }


        Gson gson = new Gson();
        String rjs= "";

        //Countries
        List<Country> countryList = clickService.retrieveCountriesGlobal();
        rjs = gson.toJson(countryList);
        System.out.println(rjs);
        modelAndView.addObject("mapdata", rjs);

        //Browsers
        List<Browser> browsersList = clickService.retrieveBrowsersGlobal();
        gson = new Gson();
        rjs = gson.toJson(browsersList);
        System.out.println(rjs);
        modelAndView.addObject("browsersdata", rjs);

        //Plaforms
        List<Platform> platformsList = clickService.retrievePlatformsGlobal();
        gson = new Gson();
        rjs = gson.toJson(platformsList);
        System.out.println(rjs);
        modelAndView.addObject("platformdata", rjs);


        return modelAndView;
    }

    @GetMapping("/link-stats-access")
    public ModelAndView linkStatsAccess(HttpServletRequest request) {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("link-stats-access");
        modelAndView.addObject("failedAccess", "");
        return modelAndView;
    }

    @PostMapping("/linkStats")
    public ModelAndView linkStats(@RequestParam("shortenedUrl") String shortenedUrl,
                                    @RequestParam("code") String code,  HttpServletRequest request) {

    	
    	String hashId = shortenedUrl.substring(shortenedUrl.lastIndexOf('/')+1) ;

    	ShortURL l = shortUrlService.findByKeyCode(hashId, code);
    	
    	ModelAndView modelAndView;
    	    
        
    	
    	if (l == null) {
            modelAndView = new ModelAndView("link-stats-access");
            modelAndView.addObject("failedAccess", "Incorrect shortened URL or code");
    		modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
         
    	}
    	else {
    	    Gson gson = new Gson();
    	    String rjs= "";
            modelAndView = new ModelAndView("map");
            modelAndView.addObject("url", hashId);

    	    //Countries
        	List<Country> countryList = clickService.retrieveCountries(hashId);
            rjs = gson.toJson(countryList);
        	System.out.println(rjs);
            modelAndView.addObject("mapdata", rjs);

        	//Browsers
            List<Browser> browsersList = clickService.retrieveBrowsers(hashId);
            gson = new Gson();
            rjs = gson.toJson(browsersList);
            System.out.println(rjs);
            modelAndView.addObject("browsersdata", rjs);

            //Plaforms
            List<Platform> platformsList = clickService.retrievePlatforms(hashId);
            gson = new Gson();
            rjs = gson.toJson(platformsList);
            System.out.println(rjs);
            modelAndView.addObject("platformdata", rjs);

    	}
        return modelAndView;
    }

}
