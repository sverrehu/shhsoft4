package no.shhsoft.web.utils;

import no.shhsoft.utils.UncheckedIoException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HttpFetcherTest {

    @Test
    public void shouldAddParametersCorrectly() {
        assertAddedCorrectly("http://example.com/foo.bar", null, "http://example.com/foo.bar");
        assertAddedCorrectly("http://example.com/foo.bar?a=b", null, "http://example.com/foo.bar?a=b");
        assertAddedCorrectly("http://example.com/foo.bar", "a=b", "http://example.com/foo.bar?a=b");
        assertAddedCorrectly("http://example.com/foo.bar?a=b&c=d", "e=f&g=h", "http://example.com/foo.bar?a=b&c=d&e=f&g=h");
        assertAddedCorrectly("http://example.com/", "a=b", "http://example.com/?a=b");
        assertAddedCorrectly("http://example.com/foo.bar?a=b&c=d#ref", "e=f&g=h", "http://example.com/foo.bar?a=b&c=d&e=f&g=h#ref");
        assertAddedCorrectly("http://example.com/foo.bar?a=%41", "b=%42", "http://example.com/foo.bar?a=%41&b=%42");
    }

    private void assertAddedCorrectly(final String initialUrlString, final String encodedParams, final String newUrlString) {
        try {
            final URL initialUrl = new URL(initialUrlString);
            final URL newUrl = HttpFetcher.addParams(initialUrl, encodedParams);
            assertEquals(newUrlString, newUrl.toString());
        } catch (final MalformedURLException e) {
            throw new UncheckedIoException(e);
        }
    }

}
