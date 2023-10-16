package no.shhsoft.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WordSplitUtilsTest {

    private static void assertEqualsOnSplit(final String[] expected, final String s) {
        final String[] split = WordSplitUtils.splitByWords(s);
        if (expected == null) {
            assertNull(split);
            return;
        }
        assertNotNull(split);
        assertEquals("Array length mismatch", expected.length, split.length);
        for (int q = 0; q < expected.length; q++) {
            assertEquals("Mismatch at index " + q, expected[q], split[q]);
        }
    }

    @Test
    public void shouldSplitCorrectly() {
        assertEqualsOnSplit(new String[0], "");
        assertEqualsOnSplit(new String[] {"\r\n"}, "\r\n");
        assertEqualsOnSplit(new String[] {"foo"} , "foo");
        assertEqualsOnSplit(new String[] {"foo", " "} , "foo ");
        assertEqualsOnSplit(new String[] {" ", "foo"} , " foo");
        assertEqualsOnSplit(new String[] {"foo", " \t"} , "foo \t");
        assertEqualsOnSplit(new String[] {" \t", "foo"} , " \tfoo");
        assertEqualsOnSplit(new String[] {"foo", " ", "bar"} , "foo bar");
        assertEqualsOnSplit(new String[] {"foo", "-", "bar"} , "foo-bar");
        assertEqualsOnSplit(new String[] {"!\"#$", "foo", "%&/{}[]", "bar", "()=+\'\\"},
                            "!\"#$foo%&/{}[]bar()=+\'\\");
        assertEqualsOnSplit(new String[] {"foo", "123", "bar"} , "foo123bar");
        assertEqualsOnSplit(new String[] {"foo", "\r\n", "123", "\r\n", "bar"},
                            "foo\r\n123\r\nbar");
    }

}
