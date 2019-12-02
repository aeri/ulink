package urlshortener.repository;

import urlshortener.domain.Browser;
import urlshortener.domain.Click;
import urlshortener.domain.Country;
import urlshortener.domain.Platform;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ClickRepository {

    List<Click> findByHash(String hash);

    Long clicksByHash(String hash);

    Click save(Click cl);

    void update(Click cl);

    void delete(Long id);

    void deleteAll();

    CompletableFuture<Long> count();

    List<Click> list(Long limit, Long offset);

	CompletableFuture<List<Country>> retrieveCountries(String hash);

    CompletableFuture<List<Browser>> retrieveBrowsers(String hash);

    CompletableFuture<List<Platform>> retrievePlatforms(String hash);

    CompletableFuture<List<Country>> retrieveCountriesGlobal();

    CompletableFuture<List<Browser>> retrieveBrowsersGlobal();

    CompletableFuture<List<Platform>> retrievePlatformsGlobal();
}
