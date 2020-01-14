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

    
    /** 
     * @return ShortURLBuilder
     */
    static ShortURLBuilder newInstance() {
        return new ShortURLBuilder();
    }

    
    /** 
     * @return ShortURL
     */
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

    
    /** 
     * @param url
     * @return ShortURLBuilder
     */
    ShortURLBuilder target(String url) {
        target = url;
        SecureHash rand= new SecureHash();
        hash = rand.generateRandomString(6);
        return this;
    }


    
    /** 
     * @return ShortURLBuilder
     */
    ShortURLBuilder createdNow() {
        this.created = new Date(System.currentTimeMillis());
        return this;
    }


    
    /** 
     * @param safe
     * @return ShortURLBuilder
     */
    ShortURLBuilder treatAsSafe(String safe) {
        this.safe = safe;
        return this;
    }

    
    /** 
     * @param ip
     * @return ShortURLBuilder
     */
    ShortURLBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }


    
    /** 
     * @param extractor
     * @return ShortURLBuilder
     */
    ShortURLBuilder uri(Function<String, URI> extractor) {
        this.uri = extractor.apply(hash);
        return this;
    }

    
    /** 
     * @return ShortURLBuilder
     */
    ShortURLBuilder code() {
        SecureHash rand= new SecureHash();
        code = rand.generateRandomString(4);
        return this;
    }

}
