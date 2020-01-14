package urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import urlshortener.domain.*;
import urlshortener.repository.ClickRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ClickService {

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);

    private final ClickRepository clickRepository;

    public ClickService(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }

    /**
     * Asynchronously, stores a redirection records in database
     * 
     * @param hash
     * @param ip
     * @param countryName
     * @param countryCode
     * @param platform
     * @param browser
     * @param latency
     */
    @Async
    public void saveClick(String hash, String ip, String countryName, String countryCode, String platform, String browser, long latency) {
        Click cl = ClickBuilder.newInstance().hash(hash).createdNow().ip(ip)
        .country(countryName).countryCode(countryCode).platform(platform).browser(browser).latency(latency).build();
        cl = clickRepository.save(cl);
        log.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }
    
    /** 
     * @param hash
     * @return List<Country>
     */
    public List<Country> retrieveCountries(String hash) {
        return clickRepository.retrieveCountries(hash);
    }
    
    /** 
     * @param hash
     * @return List<Browser>
     */
    public List<Browser> retrieveBrowsers(String hash) {
        return clickRepository.retrieveBrowsers(hash);
    }
    
    /** 
     * @param hash
     * @return List<Platform>
     */
    public List<Platform> retrievePlatforms(String hash) {
        return clickRepository.retrievePlatforms(hash);
    }
    
    /** 
     * @return List<Country>
     */
    public List<Country> retrieveCountriesGlobal() {
        return clickRepository.retrieveCountriesGlobal();
    }
    
    /** 
     * @return List<Browser>
     */
    public List<Browser> retrieveBrowsersGlobal() {
        return clickRepository.retrieveBrowsersGlobal();
    }
    
    /** 
     * @return List<Platform>
     */
    public List<Platform> retrievePlatformsGlobal() {
        return clickRepository.retrievePlatformsGlobal();
    }

    
    /** 
     * @return Long
     */
    public Long count() {
        return clickRepository.count();
    }

    
    /** 
     * @param since
     * @return Long
     */
    public Long retrieveAverageLatency(Timestamp since){
        return clickRepository.retrieveAverageLatency(since);
    }

}