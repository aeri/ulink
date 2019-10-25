package urlshortener.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

    private String hash;
    private String target;
    private URI uri;
    private String sponsor;
    private Date created;
    private String owner;
    private Integer mode;
    private Boolean safe;
    private String ip;
    private String country;
    private Boolean confirmed;

    public ShortURL(String hash, String target, URI uri, String sponsor,
                    Date created, String owner, Integer mode, Boolean safe, String ip,
                    String country) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.sponsor = sponsor;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.safe = safe;
        this.ip = ip;
        this.country = country;
        this.confirmed = true;
    }

    public ShortURL(String target, Boolean confirmed) {
        this.hash = null;
        this.target = target;
        this.uri = null;
        this.sponsor = null;
        this.created = null;
        this.owner = null;
        this.mode = null;
        this.safe = null;
        this.ip = null;
        this.country = null;
        this.confirmed = confirmed;
    }

    public ShortURL() {
    }

    public String getHash() {
        return hash;
    }

    public String getTarget() {
        return target;
    }

    public URI getUri() {
        return uri;
    }

    public Date getCreated() {
        return created;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getMode() {
        return mode;
    }

    public String getSponsor() {
        return sponsor;
    }

    public Boolean getSafe() {
        return safe;
    }

    public String getIP() {
        return ip;
    }

    public String getCountry() {
        return country;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

}
