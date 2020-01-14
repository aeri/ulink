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
    private Long latency;

    public Click(Long id, String hash, Date created,
                 String browser, String platform, String ip, String country, String countryCode, Long latency) {
        this.id = id;
        this.hash = hash;
        this.created = created;
        this.browser = browser;
        this.platform = platform;
        this.ip = ip;
        this.country = country;
        this.countryCode = countryCode;
        this.latency = latency;
    }

    
    /** 
     * @return Long
     */
    public Long getId() {
        return id;
    }

    
    /** 
     * @return String
     */
    public String getHash() {
        return hash;
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
    public String getBrowser() {
        return browser;
    }

    
    /** 
     * @return String
     */
    public String getPlatform() {
        return platform;
    }

    
    /** 
     * @return String
     */
    public String getIp() {
        return ip;
    }

    
    /** 
     * @return String
     */
    public String getCountry() {
        return country;
    }

    
    /** 
     * @return String
     */
    public String getCountryCode() {
        return countryCode;
    }

    
    /** 
     * @return Long
     */
    public Long getLantency() {
        return latency;
    }
}
