package no.shhsoft.io;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
@Ignore
public final class BlockingByteBufferTest {

    private byte nextByte = 0;
    private final Random random = new Random();

    private synchronized byte[] getBytes(final int n) {
        final byte[] bytes = new byte[n];
        for (int q = 0; q < n; q++) {
            bytes[q] = nextByte++;
        }
        return bytes;
    }

    private void assertCorrectWriteAndRead(final BlockingByteBuffer buffer, final int n) {
        final byte[] writtenBytes = getBytes(n);
        final byte[] readBytes = new byte[n];
        buffer.write(writtenBytes, 0, n);
        assertEquals(n, buffer.read(readBytes, 0, n));
        assertArrayEquals(writtenBytes, readBytes);
    }

    @Test
    public void shouldWorkForInterchangingWriteAndReadOfVariousSizesAndRotations() {
        final BlockingByteBuffer buffer = new BlockingByteBuffer(3);
        assertCorrectWriteAndRead(buffer, 1);
        assertCorrectWriteAndRead(buffer, 2);
        assertCorrectWriteAndRead(buffer, 3);
        assertCorrectWriteAndRead(buffer, 1);
        assertCorrectWriteAndRead(buffer, 3);
        assertCorrectWriteAndRead(buffer, 3);
        assertCorrectWriteAndRead(buffer, 2);
        assertCorrectWriteAndRead(buffer, 1);
    }

    @Test
    public void shouldWorkWithTwoThreadsAndUnpredictableWritesAndReads() {
        final int bufferSize = 7;
        final BlockingByteBuffer buffer = new BlockingByteBuffer(bufferSize);
        final int totalBytes = 100000;
        final Thread writer = new Thread(new Runnable() {

            private byte nextByteToWrite = 0;

            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                int numBytesWritten = 0;
                while (numBytesWritten < totalBytes) {
                    final int numBytesToWrite = Math.min(Math.abs(random.nextInt()) % (3 * bufferSize),
                                                         totalBytes - numBytesWritten);
                    final byte[] bytes = new byte[numBytesToWrite];
                    for (int q = 0; q < bytes.length; q++) {
                        bytes[q] = nextByteToWrite++;
                    }
                    buffer.write(bytes);
                    numBytesWritten += numBytesToWrite;
                }
                buffer.close();
            }

        });
        writer.start();
        byte nextByteToRead = 0;
        int totalBytesRead = 0;
        for (;;) {
            final int numBytesToRead = Math.abs(random.nextInt()) % (3 * bufferSize);
            final byte[] bytes = new byte[numBytesToRead];
            final int numBytesRead = buffer.read(bytes);
            if (numBytesRead < 0) {
                break;
            }
            for (int q = 0; q < numBytesRead; q++) {
                assertEquals(nextByteToRead++, bytes[q]);
            }
            totalBytesRead += numBytesRead;
        }
        assertEquals(totalBytes, totalBytesRead);
    }

}
