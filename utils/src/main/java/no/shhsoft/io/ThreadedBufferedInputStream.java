package no.shhsoft.io;

import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedInterruptedException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ThreadedBufferedInputStream
extends InputStream {

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final BlockingByteBuffer buffer;
    private final BufferFiller bufferFiller;
    private final Thread thread;

    private static final class BufferFiller
    implements Runnable {

        private final InputStream in;
        private final BlockingByteBuffer buffer;
        private final byte[] bytes;
        private boolean done;
        private IOException ioException;

        public BufferFiller(final InputStream in, final BlockingByteBuffer buffer) {
            this.in = in;
            this.buffer = buffer;
            this.bytes = new byte[buffer.getBufferSize()];
        }

        public void terminate() {
            done = true;
        }

        public IOException getIoException() {
            return ioException;
        }

        @Override
        public void run() {
            while (!done) {
                try {
                    final int numBytesRead = in.read(bytes);
                    if (numBytesRead < 0) {
                        break;
                    }
                    buffer.write(bytes, 0, numBytesRead);
                } catch (final IOException e) {
                    this.ioException = e;
                    break;
                } catch (final UncheckedInterruptedException e) {
                    break;
                }
            }
            buffer.close();
            IoUtils.closeSilently(in);
        }
    }

    private void assertNoException()
    throws IOException {
        if (bufferFiller.getIoException() != null) {
            System.out.println("throwing");
            throw new IOException(bufferFiller.getIoException());
        }
    }

    public ThreadedBufferedInputStream(final InputStream wrappedInputStream, final int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be something");
        }
        buffer = new BlockingByteBuffer(bufferSize);
        bufferFiller = new BufferFiller(wrappedInputStream, buffer);
        thread = new Thread(bufferFiller);
        thread.setDaemon(true);
        thread.start();
    }

    public ThreadedBufferedInputStream(final InputStream wrappedInputStream) {
        this(wrappedInputStream, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public int read()
    throws IOException {
        assertNoException();
        return buffer.read();
    }

    @Override
    public int read(final byte[] b)
    throws IOException {
        assertNoException();
        return buffer.read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len)
    throws IOException {
        assertNoException();
        return buffer.read(b, off, len);
    }

    @Override
    public int available()
    throws IOException {
        assertNoException();
        return buffer.getNumUnread();
    }

    @Override
    public void close()
    throws IOException {
        assertNoException();
        bufferFiller.terminate();
        thread.interrupt();
    }

}
