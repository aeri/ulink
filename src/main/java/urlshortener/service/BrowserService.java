package urlshortener.service;

public class BrowserService {
    public BrowserService() {
    }

    public String getBrowser(String browserDetails) {
        // Code based on:
        // https://stackoverflow.com/questions/1326928/how-can-i-get-client-information-such-as-os-and-browser
        String userAgent = browserDetails;
        String user = userAgent.toLowerCase();
        if (user.contains("msie")) {
            return "Internet Explorer";
        } else if (user.contains("edge")) {
            return "Netscape";
        } else if (user.contains("safari") && user.contains("version")) {
            return "Safari";
        } else if (user.contains("opr") || user.contains("opera")) {
            return "Opera";
        } else if (user.contains("chrome")) {
            return "Chrome";
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)
                || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1)
                || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            return "Netscape";

        } else if (user.contains("firefox")) {
            return "Firefox";
        } else if (user.contains("rv")) {
            return "Internet Explorer";
        } else {
            return "Unknown Browser";
        }
    }
}