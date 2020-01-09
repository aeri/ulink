package urlshortener.service;

import io.ipinfo.api.IPInfo;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import urlshortener.web.UrlShortenerController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class GetIPInfo {

    private IPInfo ipInfo;

    @Autowired
    private Environment IPINFO_TOKEN;

    @Autowired
    private Environment EN_US_PATH;

    @Autowired
    ResourceLoader resourceLoader;

    private File file;

    public static String token;
    public static String en_us;

    private static final Logger log = LoggerFactory.getLogger(UrlShortenerController.class);


    /**
     * 
     */
    @PostConstruct
    public void init() {
        token = IPINFO_TOKEN.getProperty("ipinfo.token");
        en_us = EN_US_PATH.getProperty("path.en_us");
        file= new File(en_us);
    }


    /**
     * 
     * @param addr
     * @return
     * @throws RateLimitedException
     * @throws IOException
     */
    public IPResponse getIpResponse(String addr) throws RateLimitedException, IOException {
        log.info("BCD");
        ipInfo = IPInfo.builder().setToken(token)
                .setCountryFile(file).build();

        log.debug("Redirection requested from " + addr);
        return ipInfo.lookupIP(addr);
    }
}
