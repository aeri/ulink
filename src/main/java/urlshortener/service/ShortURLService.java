package urlshortener.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@Service
@CacheConfig(cacheNames = { "links" })
public class ShortURLService {

	private final ShortURLRepository shortURLRepository;

	public ShortURLService(ShortURLRepository shortURLRepository) {
		this.shortURLRepository = shortURLRepository;
	}

	
	/** 
	 * @param id
	 * @return ShortURL
	 */
	@Cacheable
	public ShortURL findByKey(String id) {
		return shortURLRepository.findByKey(id);
	}

	
	/** 
	 * @param id
	 * @param code
	 * @return ShortURL
	 */
	public ShortURL findByKeyCode(String id, String code) {
		return shortURLRepository.findByKeyCode(id, code);
	}

	
	/** 
	 * @param su
	 * @param safeness
	 * @return ShortURL
	 */
	@CachePut(key = "#su.hash")
	public ShortURL mark(ShortURL su, String safeness) {
		return shortURLRepository.mark(su, safeness);
	}

	
	/** 
	 * @param limit
	 * @param offset
	 * @return List<ShortURL>
	 */
	public List<ShortURL> retrieveUrls(int limit, int offset) {
		return shortURLRepository.retrieveUrls(limit, offset);
	}

	
	/** 
	 * @param url
	 * @param ip
	 * @param safe
	 * @return ShortURL
	 */
	public ShortURL save(String url, String ip , String safe) {
		ShortURL su = ShortURLBuilder.newInstance().target(url)
				.uri((String hash) -> linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null)).toUri())
				.createdNow().treatAsSafe(safe).ip(ip).code().build();
		return shortURLRepository.save(su);
	}

	
	/** 
	 * @return Long
	 */
	public Long count() {
		return shortURLRepository.count();
	}
}