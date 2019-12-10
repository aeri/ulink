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

@Component
public class GoogleSafe {

	private static final int LIMIT = 100;

	private static ShortURLService shortUrlService;
	private static CheckGSB gsb;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	//private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public GoogleSafe(ShortURLService shortUrlService) {
		this.gsb = new CheckGSB();
		this.shortUrlService = shortUrlService;
	}

	private void checkList(List<ShortURL> listURL) {

		for (ShortURL s : listURL) {
			try {
				Boolean safe = s.getSafe(); 
				String result = gsb.check(s.getTarget());
				log.debug(s.getTarget());
				
				if (safe && result != "" || !safe && result=="") {
					log.debug("Changed: "+ s.getTarget());
					shortUrlService.mark(s, !safe);
				}
				
			} catch (GeneralSecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Scheduled(cron = "*/20 * * * * *")
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
