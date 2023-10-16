package no.shhsoft.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringUtilsTest {

    @Test
    public void testLtrim1() {
        assertNull(StringUtils.ltrim(null));
    }

    @Test
    public void testLtrim2() {
        assertEquals("", StringUtils.ltrim(""));
    }

    @Test
    public void testLtrim3() {
        assertEquals("", StringUtils.ltrim(" "));
    }

    @Test
    public void testLtrim4() {
        assertEquals("foo ", StringUtils.ltrim(" \r\n\tfoo "));
    }

    @Test
    public void testRtrim1() {
        assertNull(StringUtils.rtrim(null));
    }

    @Test
    public void testRtrim2() {
        assertEquals("", StringUtils.rtrim(""));
    }

    @Test
    public void testRtrim3() {
        assertEquals("", StringUtils.rtrim(" "));
    }

    @Test
    public void testRtrim4() {
        assertEquals(" foo", StringUtils.rtrim(" foo\r\n\t "));
    }

    @Test
    public void testFindMatchingEndParen1() {
        assertEquals(1, StringUtils.findMatchingEndParen("<>", 0));
    }

    @Test
    public void testFindMatchingEndParen2() {
        assertEquals(5, StringUtils.findMatchingEndParen("()(())", 2));
    }

    @Test
    public void testFindMatchingEndParen3() {
        assertEquals(-1, StringUtils.findMatchingEndParen("[qwe", 0));
    }

    @Test
    public void testFindMatchingEndParen4() {
        assertEquals(-1, StringUtils.findMatchingEndParen("<<>", 0));
    }

    @Test
    public void testFindMatchingEndParen6() {
        try {
            StringUtils.findMatchingEndParen("q()", 0);
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage().contains("Current char"));
        }
    }

    @Test
    public void shouldSplit() {
        assertEquals(0, StringUtils.split(null, null, false, false).length);
        assertEquals(0, StringUtils.split("", "".toCharArray(), false, false).length);
        assertArrayEquals(new String[] { "foo", "bar", "gazonk" },
                          StringUtils.split("foo|bar&gazonk", new char[] { '|', '&' }, false, false));
        assertArrayEquals(new String[] { " foo ", " bar " },
                          StringUtils.split(" foo | bar ", new char[] { '|' }, false, false));
        assertArrayEquals(new String[] { " foo ", " bar " },
                          StringUtils.split("| foo || bar |", new char[] { '|' }, false, false));
        assertArrayEquals(new String[] { "", " foo ", " ", " bar ", "" },
                          StringUtils.split("| foo | | bar |", new char[] { '|' }, false, true));
        assertArrayEquals(new String[] { "", " foo ", " ", " bar ", "a" },
                          StringUtils.split("| foo | | bar |a", new char[] { '|' }, false, true));
        assertArrayEquals(new String[] { "foo", "bar" },
                          StringUtils.split(" foo | bar ", new char[] { '|' }, true, false));
        assertArrayEquals(new String[] { "foo", "bar" },
                          StringUtils.split("| foo || bar| |", new char[] { '|' }, true, false));
        assertArrayEquals(new String[] { "", "foo", "", "bar", "", "" },
                          StringUtils.split("| foo || bar| |", new char[] { '|' }, true, true));
        assertArrayEquals(new String[] { "", "foo", "", "bar", "", "" },
                          StringUtils.split(" | foo || bar| | ", new char[] { '|' }, true, true));
    }

    @Test
    public void shouldMergeLinearSpace() {
        assertEquals(" ", StringUtils.mergeLinearWhiteSpace(" \t \t\t  "));
        assertEquals("foo bar", StringUtils.mergeLinearWhiteSpace("foo \t \t\t  bar"));
        assertEquals("foo \n bar", StringUtils.mergeLinearWhiteSpace("foo \t\n \t\t  bar"));
    }

    @Test
    public void shouldMergeAnySpace() {
        assertEquals(" ", StringUtils.mergeAnyWhiteSpace(" \t \n\t\t  "));
        assertEquals("foo bar", StringUtils.mergeAnyWhiteSpace("foo \t \n\t\t  bar"));
    }

    @Test
    public void shouldEscapeJavaLikeString() {
        assertEquals("\\\"'/\\u1234\\r\\n\\b\\t\\f",
                     StringUtils.escapeJavaLikeString("\"'/\u1234\r\n\b\t\f", false, false));
        assertEquals("\\\"\\'/\\u1234\\r\\n\\b\\t\\f",
                     StringUtils.escapeJavaLikeString("\"'/\u1234\r\n\b\t\f", true, false));
        assertEquals("\\\"'\\/\\u1234\\r\\n\\b\\t\\f",
                     StringUtils.escapeJavaLikeString("\"'/\u1234\r\n\b\t\f", false, true));
    }

    @Test
    public void shouldReturnSameStringIfNotTruncated() {
        assertNull(StringUtils.trimToLength(null, 3));
        assertEquals("", StringUtils.trimToLength("", 3));
        assertEquals("foo", StringUtils.trimToLength("foo", 3));
    }

    @Test
    public void shouldTrimToCorrectLength() {
        assertEquals("...", StringUtils.trimToLength("foobar", 3));
        assertEquals("fo...", StringUtils.trimToLength("foobar", 5));
    }

}
