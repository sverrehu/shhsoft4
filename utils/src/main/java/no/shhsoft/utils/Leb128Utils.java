package no.shhsoft.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Leb128Utils {

    private Leb128Utils() {
    }

    public static byte[] encodeUnsigned(final long n) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        long rest = n;
        do {
            int b = (int) (rest & 0x7f);
            rest >>>= 7; /* Logical shift, not arithmetic shift */
            if (rest != 0) {
                b |= 0x80;
            }
            bytes.write(b);
        } while (rest != 0);
        return bytes.toByteArray();
    }

    public static long decodeUnsigned(final ByteArrayInputStream bytes) {
        long value = 0;
        int shift = 0;
        for (;;) {
            final int b = bytes.read() & 0xff;
            if (shift > 64 - 7) {
                throw new RuntimeException("Attempt to decode an unsigned value too big to fit in a long");
            }
            value |= ((long) (b & 0x7f)) << shift;
            if ((b & 0x80) == 0) {
                break;
            }
            shift += 7;
        }
        return value;
    }

    public static byte[] encodeSigned(final long n) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        long rest = n;
        boolean done = false;
        while (!done) {
            int b = (int) (rest & 0x7f);
            rest >>= 7; /* Arithmetic shift */
            if ((rest == 0 && (b & 0x40) == 0) || (rest == -1 && (b & 0x40) != 0)) {
                done = true;
            } else {
                b |= 0x80;
            }
            bytes.write(b);
        }
        return bytes.toByteArray();
    }

    public static long decodeSigned(final ByteArrayInputStream bytes) {
        long value = 0;
        int shift = 0;
        int b;
        do {
            b = bytes.read() & 0xff;
            if (shift >= 64) {
                throw new RuntimeException("Attempt to decode a signed value too big to fit in a long");
            }
            value |= ((long) (b & 0x7f)) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        if ((shift < 64) && (b & 0x40) != 0){
            value |= -(1L << shift);
        }
        return value;
    }

}
