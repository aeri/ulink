package urlshortener.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

    private String hash;
    private String target;
    private URI uri;
    private Date created;
    private String safe;
    private String ip;
    private Boolean confirmed;
    private String code;

    public ShortURL(String hash, String target, URI uri,
                    Date created, String safe, String ip,
                    String code) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.created = created;
        this.safe = safe;
        this.ip = ip;
        this.confirmed = true;
        this.code = code;
    }

    public ShortURL(String target, Boolean confirmed) {
        this.hash = null;
        this.target = target;
        this.uri = null;
        this.created = null;
        this.safe = null;
        this.ip = null;
        this.confirmed = confirmed;
        this.code = null;
    }

    public ShortURL() {
    }

    
    /** 
     * @return String
     */
    public String getHash() {
        return hash;
    }

    
    /** 
     * @return String
     */
    public String getTarget() {
        return target;
    }

    
    /** 
     * @return URI
     */
    public URI getUri() {
        return uri;
    }

    
    /** 
     * @return Date
     */
    public Date getCreated() {
        return created;
    }

    
    /** 
     * @return String
     */
    public String getSafe() {
        return safe;
    }

    
    /** 
     * @return String
     */
    public String getIP() {
        return ip;
    }

    
    /** 
     * @return Boolean
     */
    public Boolean getConfirmed() {
        return confirmed;
    }

    
    /** 
     * @return String
     */
    public String getCode() {
        return code;
    }


}
