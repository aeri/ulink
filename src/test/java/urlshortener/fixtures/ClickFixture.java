package urlshortener.fixtures;

import urlshortener.domain.Click;
import urlshortener.domain.ShortURL;

public class ClickFixture {

    
    /** 
     * @param su
     * @return Click
     */
    public static Click click(ShortURL su) {
        return new Click(null, su.getHash(), null, "safari", "mac", "1.1.1.1", "spain", "ES", 0L);
    }
}
