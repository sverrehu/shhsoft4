package no.shhsoft.utils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RegexCacheTest {

    @Test
    public void shouldMatchGroups() {
        assertArrayEquals(new String[] { "foo", "bar" },
                          RegexCache.getMatchGroups("(...)(...)", "foobar"));
    }

    @Test
    public void shouldNotMatchGroups() {
        assertArrayEquals(new String[] {},
                          RegexCache.getMatchGroups("(...)(...)", "xx"));
    }

}
