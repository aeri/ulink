package urlshortener.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import urlshortener.domain.ShortURL;
import urlshortener.service.CheckGSB;
import urlshortener.service.ShortURLService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

@Component
public class GoogleSafe {

	private static final int LIMIT = 100;

	private ShortURLService shortUrlService;
	private CheckGSB gsb;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	//private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public GoogleSafe(ShortURLService shortUrlService) {
		this.gsb = new CheckGSB();
		this.shortUrlService = shortUrlService;
	}

	private void checkList(List<ShortURL> listURL) {

		for (ShortURL s : listURL) {
			try {
				String safe = s.getSafe();
				String result = gsb.check(s.getTarget());
				log.debug(s.getTarget());
				
				if (!safe.isEmpty() && !result.equals("") || safe.isEmpty()   && result.equals("")) {
					log.debug("Changed: "+ s.getTarget());
					shortUrlService.mark(s, result);
				}
				
			}
			catch(GoogleJsonResponseException e){
				log.debug("Google Safe Browsing quota exceeded");
			}
			catch (GeneralSecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

	@Scheduled(cron = "* */30 * * * *")
	public void check() {
		int offset = 0;
		List<ShortURL> listURL = shortUrlService.retrieveUrls(LIMIT, offset);
		log.debug("COMIENZA EL CHECK");
		while (listURL.size() != 0) {
			checkList(listURL);
			offset=offset+LIMIT;
			listURL = shortUrlService.retrieveUrls(LIMIT, offset);
		}
		log.debug("TERMINA EL CHECK");

	}

}
