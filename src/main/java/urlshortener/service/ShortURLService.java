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

	@Cacheable
	public ShortURL findByKey(String id) {
		return shortURLRepository.findByKey(id);
	}

	public ShortURL findByKeyCode(String id, String code) {
		return shortURLRepository.findByKeyCode(id, code);
	}

	@CachePut(key = "#su.hash")
	public ShortURL mark(ShortURL su, String safeness) {
		return shortURLRepository.mark(su, safeness);
	}

	public List<ShortURL> retrieveUrls(int limit, int offset) {
		return shortURLRepository.retrieveUrls(limit, offset);
	}

	public ShortURL save(String url, String ip , String safe) {
		ShortURL su = ShortURLBuilder.newInstance().target(url)
				.uri((String hash) -> linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null)).toUri())
				.createdNow().treatAsSafe(safe).ip(ip).code().build();
		return shortURLRepository.save(su);
	}

	public Long count() {
		return shortURLRepository.count();
	}
}