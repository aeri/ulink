package urlshortener.fixtures;

import urlshortener.domain.Click;
import urlshortener.domain.ShortURL;

public class ClickFixture {

    public static Click click(ShortURL su) {
        return new Click(null, su.getHash(), null, null, null, null, null, null);
    }
}
