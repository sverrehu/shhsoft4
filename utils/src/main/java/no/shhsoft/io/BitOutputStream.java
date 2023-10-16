package no.shhsoft.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BitOutputStream
extends FilterOutputStream {

    private int nextBitMask = 1;
    private int currByte;

    public BitOutputStream(final OutputStream out) {
        super(out);
    }

    @Override
    public void close()
    throws IOException {
        if (nextBitMask != 1) {
            super.write(currByte);
        }
        super.close();
    }

    @Override
    public void write(final int b)
    throws IOException {
        int mask = 1;
        for (int q = 8 - 1; q >= 0; q--) {
            writeBit((b & mask) == 0 ? 0 : 1);
            mask <<= 1;
        }
    }

    public void writeBit(final int bit)
    throws IOException {
        if (bit == 1) {
            currByte |= nextBitMask;
        } else if (bit != 0) {
            throw new IOException("A bit is either 0 or 1.");
        }
        nextBitMask <<= 1;
        if (nextBitMask == 0x100) {
            super.write(currByte);
            currByte = 0;
            nextBitMask = 1;
        }
    }

    public void writeBit(final boolean bit)
    throws IOException {
        if (bit) {
            writeBit(1);
        } else {
            writeBit(0);
        }
    }

}
