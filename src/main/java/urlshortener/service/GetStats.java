package urlshortener.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;


@Service
public class GetStats {

	private static final Logger log = LoggerFactory.getLogger(GetStats.class);


	/**
	 * Returns global statistics (browsers, platforms and countries redirections,
	 * total number of shortened URLs and redirections and avg redirection latency)
	 * 
	 * @param clickService ClickService object
	 * @param shortUrlService ShortURLService object
	 * @param since Date since avg redirection time stats are going to be provided
	 * @return map containing statistics 
	 * @throws Throwable
	 */
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
		// Platforms
		CompletableFuture<HashMap<String, String>> platformsList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrievePlatformsGlobal());
			log.debug(rjsC);

			hmap.put("platformdata", rjsC);

			return hmap;
		});
		// Total number of shortened URLs
		CompletableFuture<HashMap<String, String>> totalURL = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();

			hmap.put("totalURL", shortUrlService.count().toString());

			return hmap;
		});
		// Total number of redirections
		CompletableFuture<HashMap<String, String>> totalClicks = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();

			hmap.put("totalClicks", clickService.count().toString());

			return hmap;
		});
		// Average redirection time (latency)
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


	/**
	 * Returns statistics (browsers, platforms and countries redirections)
	 * about a specific shortened url
	 * 
	 * @param clickService ClickService object
	 * @param shortUrlService ShortURLService object
	 * @param hashId hash used as shortened url
	 * @return map containing statistics 
	 * @throws Throwable
	 */
	public Map<String, String> getLocal(ClickService clickService, ShortURLService shortUrlService,
		String hashId) throws Throwable {
		// Countries
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
		// Platforms
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
