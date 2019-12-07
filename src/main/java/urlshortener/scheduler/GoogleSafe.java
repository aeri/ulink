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

	private static final Logger log = LoggerFactory.getLogger(GoogleSafe.class);

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
				
				System.out.println(s.getTarget());
				
				if (safe && result != "" || !safe && result=="") {
					System.out.println("Cahnged: "+ s.getTarget());
					shortUrlService.mark(s, !safe);
				}
				
			} catch (GeneralSecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Scheduled(cron = "*/10 * * * * *")
	public void check() {
		int offset = 0;
		List<ShortURL> listURL = shortUrlService.retrieveUrls(LIMIT, offset);
		log.info("COMIENZA EL CHECK");
		System.out.println("START!");
		while (listURL.size() != 0) {
			System.out.println("A");
			checkList(listURL);
			System.out.println("B");
			offset=offset+LIMIT;
			listURL = shortUrlService.retrieveUrls(LIMIT, offset);
			System.out.println(listURL);
			
		}
		log.info("TERMINA EL CHECK");

	}

}
