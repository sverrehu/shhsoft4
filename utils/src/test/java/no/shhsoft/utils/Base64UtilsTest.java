package no.shhsoft.utils;

import no.shhsoft.utils.Base64Utils.Encoding;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Base64UtilsTest {

    private void testVariousLengths(final Encoding encoding, final boolean insertLineBreaks) {
        for (int q = 1; q < 256; q++) {
            final byte[] data = new byte[q];
            for (int w = 0; w < q; w++) {
                data[w] = (byte) w;
            }
            final String encoded = Base64Utils.encode(encoding, data, insertLineBreaks);
            assertFalse("Failed for Encoding " + encoding.toString(), Character.isWhitespace(encoded.charAt(encoded.length() - 1)));
            final byte[] decoded = Base64Utils.decode(encoding, encoded);
            assertEqualsWithLength("Failed for Encoding " + encoding.toString(), data, decoded, q);
        }
    }

    private void encodeDecodeAndCheck(final Encoding encoding, final String inputString, final String expectedEncoded) {
        final String encoded = Base64Utils.encode(inputString.getBytes());
        assertEquals("Failed for Encoding " + encoding.toString(), expectedEncoded, encoded);
        assertEquals("Failed for Encoding " + encoding.toString(), inputString, new String(Base64Utils.decode(encoded)));
    }

    private static String toString(final byte[] data, final int len) {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int q = 0; q < len; q++) {
            if (q > 0) {
                sb.append(", ");
            }
            sb.append((data[q]) & 0xff);
        }
        sb.append(']');
        return sb.toString();
    }

    private static String toString(final byte[] data) {
        return toString(data, data.length);
    }

    static void assertEqualsWithLength(final String message, final byte[] expected, final byte[] actual, final int len) {
        boolean match = true;
        if (actual.length != len) {
            match = false;
        } else {
            for (int q = 0; q < len; q++) {
                if (actual[q] != expected[q]) {
                    match = false;
                    break;
                }
            }
        }
        if (!match) {
            final StringBuilder sb = new StringBuilder();
            if (message != null) {
                sb.append(message);
                sb.append("  ");
            }
            sb.append("Expected ");
            sb.append(toString(expected, len));
            sb.append(" but was ");
            sb.append(toString(actual));
            throw new AssertionError(sb.toString());
        }
    }

    @Test
    public void testRfc4648Vctors() {
        for (final Encoding encoding : Encoding.values()) {
            encodeDecodeAndCheck(encoding, "", "");
            encodeDecodeAndCheck(encoding, "f", "Zg==");
            encodeDecodeAndCheck(encoding, "fo", "Zm8=");
            encodeDecodeAndCheck(encoding, "foo", "Zm9v");
            encodeDecodeAndCheck(encoding, "foob", "Zm9vYg==");
            encodeDecodeAndCheck(encoding, "fooba", "Zm9vYmE=");
            encodeDecodeAndCheck(encoding, "foobar", "Zm9vYmFy");
        }
    }

    @Test
    public void testVariousLengthsWithoutLineBreaks() {
        for (final Encoding encoding : Encoding.values()) {
            testVariousLengths(encoding, false);
        }
    }

    @Test
    public void testVariousLengthsWithLineBreaks() {
        for (final Encoding encoding : Encoding.values()) {
            testVariousLengths(encoding, true);
        }
    }

    @Test
    public void shouldThrowExceptionOnInvalidCharacter() {
        try {
            Base64Utils.decode("*");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage().contains("Invalid character"));
        }
    }

    @Test
    public void shouldThrowExceptionOnInvalidCharacterAfterPadding() {
        try {
            Base64Utils.decode("AA==x");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage().contains("found after padding"));
        }
    }

    @Test
    public void shouldThrowExceptionOnTooShortPadding() {
        try {
            Base64Utils.decode("AA=");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage().contains("Incorrect number of padding characters"));
        }
    }

    @Test
    public void shouldThrowExceptionOnTooLongPadding() {
        try {
            Base64Utils.decode("AA===");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage().contains("Too many padding characters"));
        }
    }

    @Test
    public void shouldThrowExceptionOnIncorrectEnding() {
        try {
            Base64Utils.decode("A");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage().contains("characters at end"));
        }
    }

}
