package urlshortener.domain;

import java.sql.Date;

public class Click {

    private Long id;
    private String hash;
    private Date created;
    private String browser;
    private String platform;
    private String ip;
    private String country;
    private String countryCode;

    public Click(Long id, String hash, Date created,
                 String browser, String platform, String ip, String country, String countryCode) {
        this.id = id;
        this.hash = hash;
        this.created = created;
        this.browser = browser;
        this.platform = platform;
        this.ip = ip;
        this.country = country;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public Date getCreated() {
        return created;
    }

    public String getBrowser() {
        return browser;
    }

    public String getPlatform() {
        return platform;
    }

    public String getIp() {
        return ip;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
