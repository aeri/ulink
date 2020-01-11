package urlshortener.service;

import org.springframework.stereotype.Service;

@Service
public class PlatformService {
    public PlatformService() {
    }

    /**
     * Decodes platform (OS) from browser details string
     * 
     * Code based on:
     * <https://stackoverflow.com/questions/1326928/how-can-i-get-client-information-such-as-os-and-browser>
     * 
     * @param browserDetails string containing browser details
     * @return platform (OS) used by client
     */
    public String getPlatform(String browserDetails) {
        String userAgent = browserDetails.toLowerCase();
        if (userAgent.indexOf("windows") >= 0) {
            return "Windows";
        } else if (userAgent.indexOf("mac") >= 0) {
            return "Mac";
        } else if (userAgent.indexOf("x11") >= 0) {
            if (userAgent.contains("bsd")) {
                return "BSD";
            }
            return "Linux";
        } else if (userAgent.indexOf("android") >= 0) {
            return "Android";
        } else if (userAgent.indexOf("iphone") >= 0) {
            return "IPhone";
        } else {
            return "UnKnown, More-Info: " + userAgent;
        }
    }
}