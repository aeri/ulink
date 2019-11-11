package urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import urlshortener.domain.Click;
import urlshortener.domain.Country;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ClickRepository;

import java.sql.Date;
import java.util.List;

@Service
public class ClickService {

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);

    private final ClickRepository clickRepository;

    public ClickService(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }

    public void saveClick(String hash, String ip, String countryName, String countryCode, String platform, String browser) {
        Click cl = ClickBuilder.newInstance().hash(hash).createdNow().ip(ip)
        .country(countryName).countryCode(countryCode).platform(platform).browser(browser).build();
        cl = clickRepository.save(cl);
        log.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }
    public List<Country> retrieveCountries(String hash) {
        return clickRepository.retrieveCountries(hash);
    }

}
