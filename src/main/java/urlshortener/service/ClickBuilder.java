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

    static ClickBuilder newInstance() {
        return new ClickBuilder();
    }

    Click build() {
        return new Click(null, hash, created,
                browser, platform, ip, country, countryCode);
    }

    ClickBuilder hash(String hash) {
        this.hash = hash;
        return this;
    }

    ClickBuilder createdNow() {
        this.created = new Date(System.currentTimeMillis());
        return this;
    }

    ClickBuilder unknownBrowser() {
        this.browser = null;
        return this;
    }

    ClickBuilder browser(String browser) {
        this.browser = browser;
        return this;
    }

    ClickBuilder unknownPlatform() {
        this.platform = null;
        return this;
    }

    ClickBuilder platform(String platform) {
        this.platform = platform;
        return this;
    }


    ClickBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }

    ClickBuilder country(String countryName) {
        this.country = countryName;
        return this;
    }

    ClickBuilder withoutCountry() {
        this.country = null;
        return this;
    }

    ClickBuilder withoutCountryCode() {
        this.countryCode = null;
        return this;
    }

    ClickBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

}
