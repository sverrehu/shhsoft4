package no.shhsoft.io;

import no.shhsoft.utils.UncheckedInterruptedException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BlockingByteBuffer {

    private final byte[] buffer;
    private int writeIdx;
    private int readIdx;
    private int numUnread;
    private boolean eof;
    private final byte[] singleWrite = new byte[1];
    private final byte[] singleRead = new byte[1];

    public BlockingByteBuffer(final int size) {
        this.buffer = new byte[size];
    }

    private int getNumFreeBytes() {
        return buffer.length - numUnread;
    }

    private int getNumAvailableBytes() {
        return numUnread;
    }

    public synchronized void close() {
        eof = true;
    }

    public synchronized void write(final byte[] b, final int off, final int len) {
        if (eof) {
            throw new IllegalStateException("Buffer is closed");
        }
        if (len <= 0) {
            return;
        }
        int numBytesLeftToWrite = len;
        int offset = off;
        while (numBytesLeftToWrite > 0) {
            int numFreeBytes;
            for (;;) {
                numFreeBytes = getNumFreeBytes();
                if (numFreeBytes > 0) {
                    break;
                }
                try {
                    wait();
                } catch (final InterruptedException e) {
                    throw new UncheckedInterruptedException(e);
                }
            }
            if (writeIdx == buffer.length) {
                writeIdx = 0;
            }
            final int numBytesToWrite = Math.min(numBytesLeftToWrite, numFreeBytes);
            final int numBytesAtEndOfBuffer = buffer.length - writeIdx;
            if (numBytesAtEndOfBuffer >= numBytesToWrite) {
                System.arraycopy(b, offset, buffer, writeIdx, numBytesToWrite);
                writeIdx += numBytesToWrite;
                offset += numBytesToWrite;
            } else {
                System.arraycopy(b, offset, buffer, writeIdx, numBytesAtEndOfBuffer);
                offset += numBytesAtEndOfBuffer;
                final int numRemainingBytes = numBytesToWrite - numBytesAtEndOfBuffer;
                System.arraycopy(b, offset, buffer, 0, numRemainingBytes);
                writeIdx = numRemainingBytes;
                offset += numRemainingBytes;
            }
            numBytesLeftToWrite -= numBytesToWrite;
            numUnread += numBytesToWrite;
            notifyAll();
        }
    }

    public synchronized void write(final byte[] b) {
        write(b, 0, b.length);
    }

    public synchronized void write(final int b) {
        singleWrite[0] = (byte) b;
        write(singleWrite, 0, 1);
    }

    public synchronized int read(final byte[] b, final int off, final int len) {
        if (len <= 0) {
            return 0;
        }
        int numAvailableBytes;
        for (;;) {
            numAvailableBytes = getNumAvailableBytes();
            if (numAvailableBytes > 0) {
                break;
            }
            if (eof) {
                return -1;
            }
            try {
                wait();
            } catch (final InterruptedException e) {
                throw new UncheckedInterruptedException(e);
            }
        }
        if (readIdx == buffer.length) {
            readIdx = 0;
        }
        final int numBytesToRead = Math.min(len, numAvailableBytes);
        final int numBytesAtEndOfBuffer = buffer.length - readIdx;
        if (numBytesAtEndOfBuffer >= numBytesToRead) {
            System.arraycopy(buffer, readIdx, b, off, numBytesToRead);
            readIdx += numBytesToRead;
        } else {
            int offset = off;
            System.arraycopy(buffer, readIdx, b, offset, numBytesAtEndOfBuffer);
            offset += numBytesAtEndOfBuffer;
            final int numRemainingBytes = numBytesToRead - numBytesAtEndOfBuffer;
            System.arraycopy(buffer, 0, b, offset, numRemainingBytes);
            readIdx = numRemainingBytes;
        }
        numUnread -= numBytesToRead;
        notifyAll();
        return numBytesToRead;
    }

    public synchronized int read(final byte[] b) {
        return read(b, 0, b.length);
    }

    public synchronized int read() {
        read(singleRead, 0, 1);
        return singleRead[0];
    }

    public int getNumUnread() {
        return numUnread;
    }

    public int getBufferSize() {
        return buffer.length;
    }

}
