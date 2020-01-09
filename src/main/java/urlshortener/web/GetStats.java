package urlshortener.web;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

public class GetStats {

	private static final Logger log = LoggerFactory.getLogger(GetStats.class);

	public Map<String, String> getGlobal(ClickService clickService, ShortURLService shortUrlService, Timestamp since)
			throws Throwable {
		// Countries
		CompletableFuture<HashMap<String, String>> countryList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrieveCountriesGlobal());
			log.debug(rjsC);

			hmap.put("mapdata", rjsC);

			return hmap;
		});
		// Browsers
		CompletableFuture<HashMap<String, String>> browsersList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrieveBrowsersGlobal());
			log.debug(rjsC);

			hmap.put("browsersdata", rjsC);

			return hmap;
		});
		CompletableFuture<HashMap<String, String>> platformsList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrievePlatformsGlobal());
			log.debug(rjsC);

			hmap.put("platformdata", rjsC);

			return hmap;
		});

		CompletableFuture<HashMap<String, String>> totalURL = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();

			hmap.put("totalURL", shortUrlService.count().toString());

			return hmap;
		});

		CompletableFuture<HashMap<String, String>> totalClicks = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();

			hmap.put("totalClicks", clickService.count().toString());

			return hmap;
		});

		CompletableFuture<HashMap<String, String>> averageLatency = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			try {
				hmap.put("averageTime", clickService.retrieveAverageLatency(since).toString());
			} catch (NullPointerException e) {
				// No results retreived
				hmap.put("averageTime", "-1");
			}

			return hmap;
		});

		CompletableFuture.allOf(countryList, browsersList, platformsList, totalURL, totalClicks, averageLatency);

		return Stream
				.of(countryList, browsersList, platformsList, totalURL, totalClicks, averageLatency)
				.map(CompletableFuture::join).flatMap(m -> m.entrySet().stream())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	public Map<String, String> getLocal(ClickService clickService, ShortURLService shortUrlService,
		String hashId) throws Throwable {
		CompletableFuture<HashMap<String, String>> countryList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrieveCountries(hashId));
			log.debug(rjsC);

			hmap.put("mapdata", rjsC);

			return hmap;
		});
		// Browsers
		CompletableFuture<HashMap<String, String>> browsersList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrieveBrowsers(hashId));
			log.debug(rjsC);

			hmap.put("browsersdata", rjsC);

			return hmap;
		});
		CompletableFuture<HashMap<String, String>> platformsList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrievePlatforms(hashId));
			log.debug(rjsC);

			hmap.put("platformdata", rjsC);

			return hmap;
		});

		CompletableFuture.allOf(countryList, browsersList, platformsList);

		return Stream.of(countryList, browsersList, platformsList).map(CompletableFuture::join)
				.flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Entry::getKey, Entry::getValue));


	}

}
