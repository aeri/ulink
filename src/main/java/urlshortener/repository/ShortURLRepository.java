package urlshortener.repository;

import urlshortener.domain.ShortURL;

import java.util.List;

public interface ShortURLRepository {

    ShortURL findByKey(String id);

    List<ShortURL> findByTarget(String target);

    ShortURL save(ShortURL su);

    ShortURL mark(ShortURL urlSafe, String safeness);

    void update(ShortURL su);

    void delete(String id);

    Long count();

	ShortURL findByKeyCode(String id, String code);

	List<ShortURL> retrieveUrls(int limit, int offset);

}