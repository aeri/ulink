package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@Service
@CacheConfig(cacheNames= {"links"})
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
        return shortURLRepository.findByKeyCode(id,code);
    }

    public ShortURL save(String url, String ip) {
        ShortURL su = ShortURLBuilder.newInstance()
                .target(url)
                .uri((String hash) -> linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null)).toUri())
                .createdNow()
                .treatAsSafe()
                .ip(ip)
                .code()
                .build();
        return shortURLRepository.save(su);
    }
    public Long count() {
        return shortURLRepository.count();
    }
}