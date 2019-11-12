package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class ShortURLService {

    private final ShortURLRepository shortURLRepository;

    public ShortURLService(ShortURLRepository shortURLRepository) {
        this.shortURLRepository = shortURLRepository;
    }

    public ShortURL findByKey(String id) {
        return shortURLRepository.findByKey(id);
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
