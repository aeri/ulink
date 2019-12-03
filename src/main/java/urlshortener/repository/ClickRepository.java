package urlshortener.repository;

import urlshortener.domain.Browser;
import urlshortener.domain.Click;
import urlshortener.domain.Country;
import urlshortener.domain.Platform;

import java.util.List;

public interface ClickRepository {

    List<Click> findByHash(String hash);

    Long clicksByHash(String hash);

    Click save(Click cl);

    void update(Click cl);

    void delete(Long id);

    void deleteAll();

    Long count();

    List<Click> list(Long limit, Long offset);

	List<Country> retrieveCountries(String hash);

    List<Browser> retrieveBrowsers(String hash);

    List<Platform> retrievePlatforms(String hash);

    List<Country> retrieveCountriesGlobal();

    List<Browser> retrieveBrowsersGlobal();

    List<Platform> retrievePlatformsGlobal();
}