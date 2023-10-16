package no.shhsoft.web.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UrlBuilderTest {

    @Test
    public void testStuff() {
        assertEquals("/foo", UrlBuilder.url("/foo").toString());
        assertEquals("/foo?bar=gazonk",
                     UrlBuilder.url("/foo").param("bar", "gazonk").toString());
        assertEquals("/foo?bar=gazonk&xyzzy=foobar",
                     UrlBuilder.url("/foo").param("bar", "gazonk").param("xyzzy", "foobar").toString());
        assertEquals("/foo?foo" + UrlUtils.encode("=") + "=bar" + UrlUtils.encode("&"),
                     UrlBuilder.url("/foo").param("foo=", "bar&").toString());
    }

    @Test
    public void testImmutability() {
        final UrlBuilder builder = UrlBuilder.url("/foo");
        assertEquals("/foo?bar=gazonk", builder.param("bar", "gazonk").toString());
        assertEquals("/foo?xyzzy=foobar", builder.param("xyzzy", "foobar").toString());
    }

}
