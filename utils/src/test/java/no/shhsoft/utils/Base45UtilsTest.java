package no.shhsoft.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Base45UtilsTest {

    private void encodeDecodeAndCheck(final String inputString, final String expectedEncoded) {
        final String encoded = Base45Utils.encode(inputString.getBytes());
        assertEquals("Failed", expectedEncoded, encoded);
        assertEquals("Failed", inputString, new String(Base45Utils.decode(encoded)));
    }

    @Test
    public void testExamples() {
        encodeDecodeAndCheck("", "");
        encodeDecodeAndCheck("AB", "BB8");
        encodeDecodeAndCheck("Hello!!", "%69 VD92EX0");
        encodeDecodeAndCheck("base-45", "UJCLQE7W581");
        encodeDecodeAndCheck("ietf!", "QED8WEX0");
    }

    @Test
    public void testVariousLengths() {
        for (int q = 1; q < 256; q++) {
            final byte[] data = new byte[q];
            for (int w = 0; w < q; w++) {
                data[w] = (byte) w;
            }
            final String encoded = Base45Utils.encode(data);
            assertFalse("Failed", Character.isWhitespace(encoded.charAt(encoded.length() - 1)));
            final byte[] decoded = Base45Utils.decode(encoded);
            Base64UtilsTest.assertEqualsWithLength(null, data, decoded, q);
        }
    }

}
