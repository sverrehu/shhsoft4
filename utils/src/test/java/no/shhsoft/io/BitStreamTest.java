package no.shhsoft.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BitStreamTest {

    private void assertReadBitsCorrectly(final byte[] bitsInBytes, final int numBits, final int expectedValue) {
        try {
            final ByteArrayInputStream in = new ByteArrayInputStream(bitsInBytes);
            final BitInputStream bitsIn = new BitInputStream(in);
            int mask = 1;
            int newBits = 0;
            for (int q = 0; q < numBits; q++) {
                final int expectedBit = ((expectedValue & mask) != 0) ? 1 : 0;
                assertEquals(expectedBit, bitsIn.readBit());
                if (expectedBit == 1) {
                    newBits |= mask;
                }
                mask <<= 1;
            }
            assertEquals(expectedValue, newBits);
            bitsIn.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertWriteAndReadBitsCorrectly(final int bits, final int numBits) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final BitOutputStream bitsOut = new BitOutputStream(out);
            int mask = 1;
            for (int q = 0; q < numBits; q++) {
                bitsOut.writeBit((bits & mask) != 0);
                mask <<= 1;
            }
            bitsOut.close();
            assertReadBitsCorrectly(out.toByteArray(), numBits, bits);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldHandleSingleBit() {
        assertWriteAndReadBitsCorrectly(0, 1);
        assertWriteAndReadBitsCorrectly(1, 1);
    }

    @Test
    public void shouldHandleSingleByte() {
        assertWriteAndReadBitsCorrectly(0x87, 8);
    }

    @Test
    public void shouldHandleBytePlusOne() {
        assertWriteAndReadBitsCorrectly(0x087, 9);
        assertWriteAndReadBitsCorrectly(0x187, 9);
    }

    @Test
    public void shouldHandleBitsAndBytes()
    throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BitOutputStream bitsOut = new BitOutputStream(out);
        bitsOut.writeBit(1);
        bitsOut.write(0xfe);
        bitsOut.writeBit(0);
        bitsOut.write(0xef);
        bitsOut.close();
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final BitInputStream bitsIn = new BitInputStream(in);
        assertEquals(1, bitsIn.readBit());
        assertEquals(0xfe, bitsIn.read());
        assertEquals(0, bitsIn.readBit());
        assertEquals(0xef, bitsIn.read());
        bitsIn.close();
    }

    @Test
    public void shouldPreserveOrder()
    throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(3);
        out.write(61);
        out.close();
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final BitInputStream bitsIn = new BitInputStream(in);
        assertEquals(3, bitsIn.read());
        assertEquals(61, bitsIn.read());
        bitsIn.close();
    }

}
