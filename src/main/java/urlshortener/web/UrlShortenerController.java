package urlshortener.web;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.services.safebrowsing.Safebrowsing;
import com.google.api.services.safebrowsing.model.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
public class UrlShortenerController {
    private final ShortURLService shortUrlService;

    private final ClickService clickService;
    @Value("${google.api_key}")
    private String GOOGLE_API_KEY;

    public static final JacksonFactory GOOGLE_JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String GOOGLE_CLIENT_ID = "1"; // client id
    public static final String GOOGLE_CLIENT_VERSION = "0.0.1"; // client version
    public static final String GOOGLE_APPLICATION_NAME = "ulink"; // appication name
    public static final List<String> GOOGLE_THREAT_TYPES = Arrays.asList("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION");
    public static final List<String> GOOGLE_PLATFORM_TYPES = Arrays.asList("ANY_PLATFORM");
    public static final List<String> GOOGLE_THREAT_ENTRYTYPES = Arrays.asList("URL");
    public static NetHttpTransport httpTransport;

    public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService) {
        this.shortUrlService = shortUrlService;
        this.clickService = clickService;
    }

    private String CheckGSB(String url) throws GeneralSecurityException, IOException {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        List<String> urls = Arrays.asList(url);

        FindThreatMatchesRequest findThreatMatchesRequest = createFindThreatMatchesRequest(urls);

        Safebrowsing.Builder safebrowsingBuilder = new Safebrowsing.Builder(httpTransport, GOOGLE_JSON_FACTORY, null).setApplicationName(GOOGLE_APPLICATION_NAME);
        Safebrowsing safebrowsing = safebrowsingBuilder.build();
        FindThreatMatchesResponse findThreatMatchesResponse = safebrowsing.threatMatches().find(findThreatMatchesRequest).setKey(GOOGLE_API_KEY).execute();

        List<ThreatMatch> threatMatches = findThreatMatchesResponse.getMatches();

        if (threatMatches != null && threatMatches.size() > 0) {
            return threatMatches.get(0).getThreatType();
        }
        else{
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


    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request){
        ShortURL l = shortUrlService.findByKey(id);

        if (l != null) {
            String notSafe = null;

            try {
                notSafe = CheckGSB(l.getTarget());
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if(notSafe != ""){
                clickService.saveClick(id, extractIP(request));
                HttpHeaders h = new HttpHeaders();
                h.setLocation(URI.create("http://google.com"));
                return new ResponseEntity<>(h, HttpStatus.PERMANENT_REDIRECT);
            }
            else{
                clickService.saveClick(id, extractIP(request));
                return createSuccessfulRedirectToResponse(l);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              HttpServletRequest request) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
        if (urlValidator.isValid(url)) {
            HttpHeaders h = new HttpHeaders();
            RestTemplate rest = new RestTemplate();
            HttpEntity<String> requestEntity = new HttpEntity<String>("", h);
            try{
                ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, requestEntity, String.class); // Execute http get request as client
                System.out.println("Status code: " + response.getStatusCode());
                ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
                h.setLocation(su.getUri());
                System.out.println("Peticion correcta");
                return new ResponseEntity<>(su, h, HttpStatus.CREATED);
            }
            catch(ResourceAccessException e){ // Unknown host
                System.out.println(e.getMessage());
                System.out.println("Peticion incorrecta ResourceAccessException");
                ShortURL su = new ShortURL(url, false);
                return new ResponseEntity<>(su, h, HttpStatus.OK);
            }
            catch(HttpClientErrorException e){ // Client error
                System.out.println(e.getMessage());
                if(e.getRawStatusCode() == 404){
                    System.out.println("Peticion incorrecta HttpClientErrorException");
                    ShortURL su = new ShortURL(url, false);
                    return new ResponseEntity<>(su, h, HttpStatus.OK);
                }
                else{
                    ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
                h.setLocation(su.getUri());
                System.out.println("Peticion correcta");
                return new ResponseEntity<>(su, h, HttpStatus.CREATED);
                }  
            }  
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/linkConfirm", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortenerConfirm(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              HttpServletRequest request) {
        System.out.println("linkConfirm Request");
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
        if (urlValidator.isValid(url)) {
            ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
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

    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }


}
