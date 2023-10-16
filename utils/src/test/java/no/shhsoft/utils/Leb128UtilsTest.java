package no.shhsoft.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Leb128UtilsTest {

    @Test
    public void shouldEncodeUnsignedZeroAsOneByteZero() {
        final byte[] bytes = Leb128Utils.encodeUnsigned(0);
        assertArrayEquals(new byte[] { 0x00 }, bytes);
    }

    @Test
    public void shouldEncodeSignedZeroAsOneByteZero() {
        final byte[] bytes = Leb128Utils.encodeSigned(0);
        assertArrayEquals(new byte[] { 0x00 }, bytes);
    }

    @Test
    public void shouldEncodeUnsignedAccordingToWikipedia() {
        final byte[] bytes = Leb128Utils.encodeUnsigned(624485);
        assertArrayEquals(new byte[] { (byte) 0xe5, (byte) 0x8e, 0x26 }, bytes);
    }

    @Test
    public void shouldEncodeSignedAccordingToWikipedia() {
        final byte[] bytes = Leb128Utils.encodeSigned(-123456);
        assertArrayEquals(new byte[] { (byte) 0xc0, (byte) 0xbb, 0x78 }, bytes);
    }

    @Test
    public void shouldHandleSelectedUnsignedNumbers() {
        assertUnsignedEncodeAndDecodeEquals(1);
        assertUnsignedEncodeAndDecodeEquals(2);
        assertUnsignedEncodeAndDecodeEquals(Long.MAX_VALUE);
        assertUnsignedEncodeAndDecodeEquals(Integer.MAX_VALUE);
    }

    @Test
    public void shouldHandleSelectedSignedNumbers() {
        assertSignedEncodeAndDecodeEquals(1);
        assertSignedEncodeAndDecodeEquals(2);
        assertSignedEncodeAndDecodeEquals(-1);
        assertSignedEncodeAndDecodeEquals(-2);
        assertSignedEncodeAndDecodeEquals(Long.MIN_VALUE);
        assertSignedEncodeAndDecodeEquals(Integer.MIN_VALUE);
    }

    @Test
    public void shouldHandleSomeRandomNumbers() {
        final Random rnd = new Random();
        for (int q = 0; q < 100000; q++) {
            final long n = rnd.nextLong();
            if (n >= 0) {
                assertUnsignedEncodeAndDecodeEquals(n);
            }
            assertSignedEncodeAndDecodeEquals(n);
        }
    }

    @Test
    public void shouldHandleSomeNoneRandomNumbers() {
        for (long n = 0; n < 100000; n++) {
            assertUnsignedEncodeAndDecodeEquals(n);
            assertSignedEncodeAndDecodeEquals(n);
        }
    }

    private void assertUnsignedEncodeAndDecodeEquals(final long expected) {
        final byte[] bytes = Leb128Utils.encodeUnsigned(expected);
        final long actual = Leb128Utils.decodeUnsigned(new ByteArrayInputStream(bytes));
        assertEquals(expected, actual);
    }

    private void assertSignedEncodeAndDecodeEquals(final long expected) {
        final byte[] bytes = Leb128Utils.encodeSigned(expected);
        final long actual = Leb128Utils.decodeSigned(new ByteArrayInputStream(bytes));
        assertEquals(expected, actual);
    }

}
