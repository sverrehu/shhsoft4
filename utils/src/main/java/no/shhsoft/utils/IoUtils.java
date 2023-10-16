package no.shhsoft.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class IoUtils {

    private static final Logger LOG = Logger.getLogger(IoUtils.class.getName());
    private static final int DEFAULT_BUFFER_SIZE = 65536;
    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String FILE_PREFIX = "file:";
    private static int bufferSize = DEFAULT_BUFFER_SIZE;
    private static ResourceStreamOpener resourceStreamOpener = new DefaultResourceStreamOpener();

    static final class DefaultResourceStreamOpener
    implements ResourceStreamOpener {

        @Override
        public InputStream openResource(final String name) {
            return IoUtils.class.getResourceAsStream(name);
        }

    }

    private IoUtils() {
        /* not to be instantiated. */
    }

    private static byte[] readFileOrResourceOnly(final String name) {
        if (name.startsWith(CLASSPATH_PREFIX)) {
            return readResource(name.substring(CLASSPATH_PREFIX.length()));
        }
        if (name.startsWith(FILE_PREFIX)) {
            return readFile(name.substring(FILE_PREFIX.length()));
        }
        return null;
    }

    public static void setBufferSize(final int bufferSize) {
        IoUtils.bufferSize = bufferSize;
    }

    public static int getBufferSize() {
        return bufferSize;
    }

    public static void close(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static void closeSilently(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (final IOException e) {
            LOG.finest("Error closing closeable: " + e.getMessage());
        }
    }

    public static void closeSilently(final Socket socket) {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
        } catch (final IOException e) {
            LOG.finest("Error closing socket: " + e.getMessage());
        }
    }

    public static void closeSilently(final ServerSocket serverSocket) {
        if (serverSocket == null) {
            return;
        }
        try {
            serverSocket.close();
        } catch (final IOException e) {
            LOG.finest("Error closing server socket: " + e.getMessage());
        }
    }

    public static void write(final OutputStream out, final byte[] data, final int off, final int len) {
        try {
            out.write(data, off, len);
            out.flush();
        } catch (final IOException e) {
            throw new UncheckedIoException("Error writing to stream", e);
        }
    }

    public static void write(final OutputStream out, final byte[] data) {
        write(out, data, 0, data.length);
    }

    public static byte[] read(final InputStream in) {
        byte[] ret = new byte[0];
        final byte[] buff = new byte[bufferSize];
        for (;;) {
            final int numRead;
            try {
                numRead = in.read(buff);
            } catch (final IOException e) {
                throw new UncheckedIoException(e);
            }
            if (numRead < 0) {
                break;
            }
            final byte[] newRet = new byte[ret.length + numRead];
            System.arraycopy(ret, 0, newRet, 0, ret.length);
            System.arraycopy(buff, 0, newRet, ret.length, numRead);
            ret = newRet;
        }
        return ret;
    }

    public static void writeFile(final String filename, final byte[] data, final int off,
                                 final int len) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filename));
            write(out, data, off, len);
            out.close();
            out = null;
        } catch (final IOException e) {
            throw new UncheckedIoException("Error writing file `" + filename + "'", e);
        } finally {
            closeSilently(out);
        }
    }

    public static void writeFile(final String filename, final byte[] data) {
        writeFile(filename, data, 0, data.length);
    }

    public static byte[] readFileGz(final String name) {
        return decompressIfGzip(readFile(name), name);
    }

    /**
     * @return <code>null</code> if not found.
     */
    public static byte[] readFile(final String filename) {
        final File file = new File(filename);
        final int size = (int) file.length();
        final byte[] buff = new byte[size];
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file), bufferSize);
            in.read(buff, 0, size);
            return buff;
        } catch (final FileNotFoundException e) {
            return null;
        } catch (final IOException e) {
            throw new UncheckedIoException("Error reading file `" + filename + "'", e);
        } finally {
            closeSilently(in);
        }
    }

    public static InputStream openResource(final String name) {
        InputStream in = resourceStreamOpener.openResource(name);
        if (in == null && name.length() > 0 && name.charAt(0) != '/') {
            in = resourceStreamOpener.openResource("/" + name);
        }
        return in;
    }

    public static boolean resourceExists(final String name) {
        InputStream in = null;
        try {
            in = openResource(name);
            return in != null;
        } finally {
            closeSilently(in);
        }
    }

    public static byte[] readResourceGz(final String name) {
        return decompressIfGzip(readResource(name), name);
    }

    private static byte[] decompressIfGzip(final byte[] bytes, final String name) {
        if (bytes == null || !isGzipName(name)) {
            return bytes;
        }
        return CompressionUtils.gzDecompress(bytes);
    }

    private static boolean isGzipName(final String name) {
        final String lowerName = name.toLowerCase();
        return lowerName.endsWith(".gz") || lowerName.endsWith(".z");
    }

    /**
     * @return <code>null</code> if not found.
     */
    public static byte[] readResource(final String name) {
        InputStream in = null;
        try {
            in = openResource(name);
            if (in == null) {
                return null;
            }
            in = new BufferedInputStream(in, bufferSize);
            return read(in);
        } finally {
            closeSilently(in);
        }
    }

    public static byte[] readUrl(final URL url) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(url.openStream(), 8192);
            return read(in);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeSilently(in);
        }
    }

    public static boolean containsRecognizedPrefix(final String name) {
        return name.startsWith(CLASSPATH_PREFIX) || name.startsWith(FILE_PREFIX);
    }

    public static byte[] readFileOrResource(final String name) {
        final byte[] data = readFileOrResourceOnly(name);
        if (data != null) {
            return data;
        }
        return readFile(name);
    }

    public static byte[] readResourceOrFile(final String name) {
        final byte[] data = readFileOrResourceOnly(name);
        if (data != null) {
            return data;
        }
        return readResource(name);
    }

    public static void setResourceStreamOpener(final ResourceStreamOpener resourceStreamOpener) {
        IoUtils.resourceStreamOpener = resourceStreamOpener;
    }

    public static void readFully(final InputStream in, final byte[] buff, final int offs, final int len) {
        try {
            int totalRead = 0;
            int n;
            while (totalRead < len && (n = in.read(buff, offs + totalRead, len - totalRead)) >= 0) {
                totalRead += n;
            }
            if (totalRead != len) {
                throw new UncheckedIoException(new EOFException("Unexpected end of input"));
            }
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static byte[] readFully(final InputStream in, final int len) {
        final byte[] buff = new byte[len];
        readFully(in, buff, 0, len);
        return buff;
    }

    public static void readFully(final Reader in, final char[] buff, final int offs, final int len) {
        try {
            int totalRead = 0;
            int n;
            while (totalRead < len && (n = in.read(buff, offs + totalRead, len - totalRead)) >= 0) {
                totalRead += n;
            }
            if (totalRead != len) {
                throw new UncheckedIoException(new EOFException("Unexpected end of input"));
            }
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static char[] readFully(final Reader in, final int len) {
        final char[] buff = new char[len];
        readFully(in, buff, 0, len);
        return buff;
    }

    public static URL fileToUrl(final File file) {
        if (file == null) {
            return null;
        }
        try {
            return file.toURI().toURL();
        } catch (final MalformedURLException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static URL fileToUrl(final String filename) {
        return fileToUrl(new File(filename));
    }

    public static void mkdirs(final String dirname) {
        mkdirs(new File(dirname));
    }

    private static void mkdirs(final File dir) {
        if (dir.isDirectory()) {
            return;
        }
        if (dir.exists()) {
            throw new UncheckedIoException("File exists but is not a directory: " + dir.getPath());
        }
        if (!dir.mkdirs()) {
            throw new UncheckedIoException("Unable to create directory " + dir.getPath());
        }
    }

    public static long getFileAgeInMs(final String filename) {
        return getFileAgeInMs(new File(filename));
    }

    private static long getFileAgeInMs(final File file) {
        final long lastModified = file.lastModified();
        if (lastModified == 0L) {
            throw new UncheckedIoException("Unable to get modification time of " + file.getPath());
        }
        return System.currentTimeMillis() - lastModified;
    }

}
