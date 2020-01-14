package urlshortener.service;

import urlshortener.domain.Click;

import java.sql.Date;

public class ClickBuilder {

    private String hash;
    private Date created;
    private String browser;
    private String platform;
    private String ip;
    private String country;
    private String countryCode;
    private long latency;

    
    /** 
     * @return ClickBuilder
     */
    static ClickBuilder newInstance() {
        return new ClickBuilder();
    }

    
    /** 
     * @return Click
     */
    Click build() {
        return new Click(null, hash, created,
                browser, platform, ip, country, countryCode, latency);
    }

    
    /** 
     * @param hash
     * @return ClickBuilder
     */
    ClickBuilder hash(String hash) {
        this.hash = hash;
        return this;
    }

    
    /** 
     * @return ClickBuilder
     */
    ClickBuilder createdNow() {
        this.created = new Date(System.currentTimeMillis());
        return this;
    }

    
    /** 
     * @return ClickBuilder
     */
    ClickBuilder unknownBrowser() {
        this.browser = null;
        return this;
    }

    
    /** 
     * @param browser
     * @return ClickBuilder
     */
    ClickBuilder browser(String browser) {
        this.browser = browser;
        return this;
    }

    
    /** 
     * @return ClickBuilder
     */
    ClickBuilder unknownPlatform() {
        this.platform = null;
        return this;
    }

    
    /** 
     * @param platform
     * @return ClickBuilder
     */
    ClickBuilder platform(String platform) {
        this.platform = platform;
        return this;
    }


    
    /** 
     * @param ip
     * @return ClickBuilder
     */
    ClickBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }

    
    /** 
     * @param countryName
     * @return ClickBuilder
     */
    ClickBuilder country(String countryName) {
        this.country = countryName;
        return this;
    }

    
    /** 
     * @return ClickBuilder
     */
    ClickBuilder withoutCountry() {
        this.country = null;
        return this;
    }

    
    /** 
     * @return ClickBuilder
     */
    ClickBuilder withoutCountryCode() {
        this.countryCode = null;
        return this;
    }

    
    /** 
     * @param countryCode
     * @return ClickBuilder
     */
    ClickBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    
    /** 
     * @param latency
     * @return ClickBuilder
     */
    ClickBuilder latency(long latency) {
        this.latency = latency;
        return this;
    }

}
