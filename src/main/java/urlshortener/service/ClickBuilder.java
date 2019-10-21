package urlshortener.service;

import urlshortener.domain.Click;

import java.sql.Date;

public class ClickBuilder {

    private String hash;
    private Date created;
    private String referrer;
    private String browser;
    private String platform;
    private String ip;
    private String country;

    static ClickBuilder newInstance() {
        return new ClickBuilder();
    }

    Click build() {
        return new Click(null, hash, created, referrer,
                browser, platform, ip, country);
    }

    ClickBuilder hash(String hash) {
        this.hash = hash;
        return this;
    }

    ClickBuilder createdNow() {
        this.created = new Date(System.currentTimeMillis());
        return this;
    }

    ClickBuilder noReferrer() {
        this.referrer = null;
        return this;
    }

    ClickBuilder unknownBrowser() {
        this.browser = null;
        return this;
    }

    ClickBuilder unknownPlatform() {
        this.platform = null;
        return this;
    }


    ClickBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }

    ClickBuilder withoutCountry() {
        this.country = null;
        return this;
    }

}
