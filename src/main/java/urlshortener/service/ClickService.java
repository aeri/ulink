package urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import urlshortener.domain.*;
import urlshortener.repository.ClickRepository;

import java.sql.Date;
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

    @Async
    public void saveClick(String hash, String ip, String countryName, String countryCode, String platform, String browser, long latency) {
        Click cl = ClickBuilder.newInstance().hash(hash).createdNow().ip(ip)
        .country(countryName).countryCode(countryCode).platform(platform).browser(browser).latency(latency).build();
        cl = clickRepository.save(cl);
        log.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }
    public List<Country> retrieveCountries(String hash) {
        return clickRepository.retrieveCountries(hash);
    }
    public List<Browser> retrieveBrowsers(String hash) {
        return clickRepository.retrieveBrowsers(hash);
    }
    public List<Platform> retrievePlatforms(String hash) {
        return clickRepository.retrievePlatforms(hash);
    }
    public List<Country> retrieveCountriesGlobal() {
        return clickRepository.retrieveCountriesGlobal();
    }
    public List<Browser> retrieveBrowsersGlobal() {
        return clickRepository.retrieveBrowsersGlobal();
    }
    public List<Platform> retrievePlatformsGlobal() {
        return clickRepository.retrievePlatformsGlobal();
    }

    public Long count() {
        return clickRepository.count();
    }

    public Long retrieveAverageLatency(Timestamp since){
        return clickRepository.retrieveAverageLatency(since);
    }

}