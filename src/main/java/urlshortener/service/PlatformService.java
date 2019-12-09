package urlshortener.service;

public class PlatformService {
    public PlatformService() {
    }

    public String getPlatform(String browserDetails) {
        // Code based on:
        // https://stackoverflow.com/questions/1326928/how-can-i-get-client-information-such-as-os-and-browser
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