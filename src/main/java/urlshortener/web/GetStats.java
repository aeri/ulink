package urlshortener.web;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

public class GetStats {

	public ModelAndView getGlobal(ClickService clickService, ShortURLService shortUrlService) throws Throwable {

		ModelAndView modelAndView;

		// Countries
		CompletableFuture<HashMap<String, String>> countryList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrieveCountriesGlobal());
			System.out.println(rjsC);

			hmap.put("mapdata", rjsC);

			return hmap;
		});
		// Browsers
		CompletableFuture<HashMap<String, String>> browsersList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrieveBrowsersGlobal());
			System.out.println(rjsC);

			hmap.put("browsersdata", rjsC);

			return hmap;
		});
		CompletableFuture<HashMap<String, String>> platformsList = CompletableFuture.supplyAsync(() -> {
			HashMap<String, String> hmap = new HashMap<String, String>();
			Gson gson = new Gson();
			String rjsC = "";

			rjsC = gson.toJson(clickService.retrievePlatformsGlobal());
			System.out.println(rjsC);

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

		Map<String, String> hmap = Stream.of(countryList, browsersList, platformsList, totalURL, totalClicks)
				.map(CompletableFuture::join).flatMap(m -> m.entrySet().stream())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		modelAndView = new ModelAndView("stadistics");

		modelAndView.addAllObjects(hmap);

		return modelAndView;

	}

	public ModelAndView getLocal(ClickService clickService, ShortURLService shortUrlService, String shortenedUrl,
			String code) throws Throwable {

		ModelAndView modelAndView;

		String hashId = shortenedUrl.substring(shortenedUrl.lastIndexOf('/') + 1);

		ShortURL l = shortUrlService.findByKeyCode(hashId, code);

		if (l == null) {
			modelAndView = new ModelAndView("link-stats-access");
			modelAndView.addObject("failedAccess", "Incorrect shortened URL or code");
			modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		else {
			CompletableFuture<HashMap<String, String>> countryList = CompletableFuture.supplyAsync(() -> {
				HashMap<String, String> hmap = new HashMap<String, String>();
				Gson gson = new Gson();
				String rjsC = "";

				rjsC = gson.toJson(clickService.retrieveCountries(hashId));
				System.out.println(rjsC);

				hmap.put("mapdata", rjsC);

				return hmap;
			});
			// Browsers
			CompletableFuture<HashMap<String, String>> browsersList = CompletableFuture.supplyAsync(() -> {
				HashMap<String, String> hmap = new HashMap<String, String>();
				Gson gson = new Gson();
				String rjsC = "";

				rjsC = gson.toJson(clickService.retrieveBrowsers(hashId));
				System.out.println(rjsC);

				hmap.put("browsersdata", rjsC);

				return hmap;
			});
			CompletableFuture<HashMap<String, String>> platformsList = CompletableFuture.supplyAsync(() -> {
				HashMap<String, String> hmap = new HashMap<String, String>();
				Gson gson = new Gson();
				String rjsC = "";

				rjsC = gson.toJson(clickService.retrievePlatforms(hashId));
				System.out.println(rjsC);

				hmap.put("platformdata", rjsC);

				return hmap;
			});

			modelAndView = new ModelAndView("map");
			modelAndView.addObject("url", hashId);

			Map<String, String> hmap = Stream.of(countryList, browsersList, platformsList).map(CompletableFuture::join)
					.flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			modelAndView.addAllObjects(hmap);

		}

		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

		for (Long threadID : threadMXBean.getAllThreadIds()) {
			ThreadInfo info = threadMXBean.getThreadInfo(threadID);
			System.out.println("Thread name: " + info.getThreadName());
			System.out.println("Thread State: " + info.getThreadState());
			System.out.println(String.format("CPU time: %s ns", threadMXBean.getThreadCpuTime(threadID)));
		}

		return modelAndView;

	}

}
