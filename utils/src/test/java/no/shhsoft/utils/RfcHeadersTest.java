package no.shhsoft.utils;

import no.shhsoft.utils.RfcHeaders;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RfcHeadersTest {

    @Test
    public void shouldAddAndRetrieveSingle() {
        final RfcHeaders headers = new RfcHeaders();
        headers.put("a", "b");
        Assert.assertEquals("b", headers.getSingle("a"));
        Assert.assertEquals("b", headers.getSingle("A"));
        Assert.assertNull(headers.getSingle("b"));
        final List<String> oneMatch = headers.get("a");
        Assert.assertNotNull(oneMatch);
        Assert.assertEquals(1, oneMatch.size());
        final List<String> noMatch = headers.get("b");
        Assert.assertNotNull(noMatch);
        Assert.assertEquals(0, noMatch.size());
    }

    @Test
    public void shouldAddAndRetrieveDouble() {
        final RfcHeaders headers = new RfcHeaders();
        headers.put("a", "b");
        headers.put("A", "c");
        Assert.assertEquals("b", headers.getSingle("a"));
        Assert.assertEquals("b", headers.getSingle("A"));
        final List<String> twoMatches = headers.get("a");
        Assert.assertNotNull(twoMatches);
        Assert.assertEquals(2, twoMatches.size());
        Assert.assertEquals("b", twoMatches.get(0));
        Assert.assertEquals("c", twoMatches.get(1));
    }

    @Test
    public void shouldParseSingleLine() {
        Assert.assertEquals("b", RfcHeaders.parse("a:b").getSingle("a"));
        Assert.assertEquals("b", RfcHeaders.parse("a:b\r\n").getSingle("a"));
        Assert.assertEquals("b", RfcHeaders.parse("a:b\r\n\r\n").getSingle("a"));
    }

    @Test
    public void shouldParseSingleFolded() {
        Assert.assertEquals("b c", RfcHeaders.parse("a:b\r\n c").getSingle("a"));
        Assert.assertEquals("b c", RfcHeaders.parse("a:b\r\n c\r\n").getSingle("a"));
        Assert.assertEquals("b c", RfcHeaders.parse("a:b\r\n c\r\n\r\n").getSingle("a"));
    }

    @Test
    public void shouldParseTwoFolded() {
        final RfcHeaders headers = RfcHeaders.parse("a:b\r\n c\r\nd:e\r\n f");
        Assert.assertEquals("b c", headers.getSingle("a"));
        Assert.assertEquals("e f", headers.getSingle("d"));
    }

}
