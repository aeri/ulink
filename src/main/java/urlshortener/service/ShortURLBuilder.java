package urlshortener.service;

import urlshortener.domain.ShortURL;

import java.net.URI;
import java.sql.Date;
import java.util.function.Function;

public class ShortURLBuilder {

    private String hash;
    private String target;
    private URI uri;
    private Date created;
    private String safe;
    private String ip;
    private String code;

    static ShortURLBuilder newInstance() {
        return new ShortURLBuilder();
    }

    ShortURL build() {
        return new ShortURL(
                hash,
                target,
                uri,
                created,
                safe,
                ip,
                code
        );
    }

    ShortURLBuilder target(String url) {
        target = url;
        
        //RandomHash rand= new RandomHash();
        //hash = rand.hash();
        SecureHash rand= new SecureHash();
        hash = rand.generateRandomString(6, url);
        return this;
    }


    ShortURLBuilder createdNow() {
        this.created = new Date(System.currentTimeMillis());
        return this;
    }


    ShortURLBuilder treatAsSafe(String safe) {
        this.safe = safe;
        return this;
    }

    ShortURLBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }


    ShortURLBuilder uri(Function<String, URI> extractor) {
        this.uri = extractor.apply(hash);
        return this;
    }

    ShortURLBuilder code() {
        SecureHash rand= new SecureHash();
        code = rand.generateRandomString(4, null);
        return this;
    }

}
