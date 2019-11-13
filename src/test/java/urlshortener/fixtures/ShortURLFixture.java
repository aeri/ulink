package urlshortener.fixtures;

import urlshortener.domain.ShortURL;

public class ShortURLFixture {

    public static ShortURL url1() {
        return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, "A");
    }

    public static ShortURL url1modified() {
        return new ShortURL("1", "http://www.unizar.org/", null, null, null, null, "B");
    }

    public static ShortURL url2() {
        return new ShortURL("2", "http://www.unizar.es/", null, null, null, null, "C");
    }

    public static ShortURL url3() {
        return new ShortURL("3", "http://www.google.es/", null, null, null, null, "D");
    }

    public static ShortURL badUrl() {
        return new ShortURL(null, null, null, null, null, null, "D");
    }

    public static ShortURL urlSponsor() {
        return new ShortURL("3", null, null, null, null, null, "F");
    }

    public static ShortURL urlSafe() {
        return new ShortURL("4", null, null, null, null, null, "G");
    }

    public static ShortURL someUrl() {
        return new ShortURL("someKey", "http://example.com/", null, null, null,
                null, "H");
    }
}
