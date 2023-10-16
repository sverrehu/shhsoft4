package no.shhsoft.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BitInputStream
extends FilterInputStream {

    private int nextBitMask = 0x100;
    private int currByte;

    public BitInputStream(final InputStream in) {
        super(in);
    }

    @Override
    public int read()
    throws IOException {
        int mask = 1;
        int bits = 0;
        for (int q = 8 - 1; q >= 0; q--) {
            final int bit = readBit();
            if (bit < 0) {
                if (mask == 1) {
                    return -1;
                }
                return bits;
            }
            if (bit != 0) {
                bits |= mask;
            }
            mask <<= 1;
        }
        return bits;
    }

    public int readBit()
    throws IOException {
        if (nextBitMask == 0x100) {
            currByte = super.read();
            if (currByte < 0) {
                return -1;
            }
            nextBitMask = 1;
        }
        final int bit = (currByte & nextBitMask) == 0 ? 0 : 1;
        nextBitMask <<= 1;
        return bit;
    }

}
