package urlshortener.scheduler;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.services.safebrowsing.model.ThreatMatch;
import urlshortener.domain.ShortURL;
import urlshortener.service.CheckGSB;
import urlshortener.service.ShortURLService;

@Component
public class GoogleSafe {

	private static final int LIMIT = 500;

	private ShortURLService shortUrlService;
	private CheckGSB gsb;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	// private static final SimpleDateFormat dateFormat = new
	// SimpleDateFormat("HH:mm:ss");

	public GoogleSafe(ShortURLService shortUrlService) {
		this.gsb = new CheckGSB();
		this.shortUrlService = shortUrlService;
	}

	private void checkList(List<ShortURL> listURL) {
		List<String> listStringURL = new ArrayList<>();
		// Create list of urls as strings
		for(ShortURL url : listURL){
			listStringURL.add(url.getTarget());
		}
		try {
			// Get threat results from Google Safe Browsing
			List<ThreatMatch> threatResults = gsb.check(listStringURL);
			Hashtable<String, ThreatMatch> threatTable = new Hashtable<>();
			// Store threat results in hash table
			if(threatResults != null){
				for(ThreatMatch tm : threatResults){
					threatTable.put(tm.getThreat().getUrl(), tm);
				}
			}
			// Iterate through url list and change safeness value if needed
			for(ShortURL url : listURL){
				log.debug(url.getTarget());
				if(url.getSafe() == null){
					// If it's null, it is considered as safe (but must be fixed)
					if(threatTable.containsKey(url.getTarget())){
						// It was safe, now it's not
						log.info("Changed: "+ url.getTarget());
						shortUrlService.mark(url, threatTable.get(url.getTarget()).getThreatType());
					}
					else{
						// It's safe, but we fix that null in DB
						shortUrlService.mark(url, "");
					}
				}
				else{
					if(url.getSafe().isEmpty() && threatTable.containsKey(url.getTarget())){
						// It was safe, now it's not
						log.info("Changed: "+ url.getTarget());
						shortUrlService.mark(url, threatTable.get(url.getTarget()).getThreatType());
					}
					else if(!url.getSafe().isEmpty() && !threatTable.containsKey(url.getTarget())){
						// It was not safe, now it is
						log.info("Changed: "+ url.getTarget());
						shortUrlService.mark(url, "");
					}
				}
			}
		}
		catch(GoogleJsonResponseException e){
			log.debug("Google Safe Browsing quota exceeded");
		}
		catch (GeneralSecurityException | IOException e) {
			log.debug("Google Safe Browsing general failed");
			e.printStackTrace();
		}
	}

	@Scheduled(cron = "0 0/30 * * * *")
	public void check() {
		int offset = 0;
		List<ShortURL> listURL = shortUrlService.retrieveUrls(LIMIT, offset);
		log.info("COMIENZA EL CHECK");
		while (listURL.size() != 0) {
			log.info("PETICION MULTIPLE A GSB: " + listURL.size() + " URL");
			checkList(listURL);
			offset=offset+LIMIT;
			listURL = shortUrlService.retrieveUrls(LIMIT, offset);
		}
		log.info("TERMINA EL CHECK");

	}

}
