package no.shhsoft.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CompressionUtils {

    private CompressionUtils() {
    }

    public static final class FileNameAndContents {

        private final String fileName;
        private final byte[] contents;

        public FileNameAndContents(final String fileName, final byte[] contents) {
            this.fileName = fileName;
            this.contents = contents;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContents() {
            return contents;
        }

    }

    public static byte[] gzCompress(final byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            final ByteArrayOutputStream outBytes = new ByteArrayOutputStream(data.length);
            final OutputStream compressor = new BufferedOutputStream(new GZIPOutputStream(new BufferedOutputStream(outBytes)));
            compressor.write(data);
            compressor.close();
            return outBytes.toByteArray();
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static byte[] gzDecompress(final byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            final ByteArrayInputStream inBytes = new ByteArrayInputStream(data);
            final InputStream decompressor = new BufferedInputStream(new GZIPInputStream(new BufferedInputStream(inBytes)));
            final byte[] decompressed = IoUtils.read(decompressor);
            decompressor.close();
            return decompressed;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static byte[] zipCompress(final FileNameAndContents... fileNamesAndContents) {
        if (fileNamesAndContents == null) {
            return null;
        }
        try {
            final ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            final ZipOutputStream compressor = new ZipOutputStream(new BufferedOutputStream(outBytes));
            compressor.setLevel(Deflater.BEST_COMPRESSION);
            for (final FileNameAndContents fileNameAndContents : fileNamesAndContents) {
                final ZipEntry zipEntry = new ZipEntry(fileNameAndContents.getFileName());
                final byte[] contents = fileNameAndContents.getContents();
                compressor.putNextEntry(zipEntry);
                compressor.write(contents, 0, contents.length);
                compressor.closeEntry();
            }
            compressor.close();
            return outBytes.toByteArray();
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public static List<FileNameAndContents> zipDecompress(final byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            final ByteArrayInputStream inBytes = new ByteArrayInputStream(data);
            final ZipInputStream decompressor = new ZipInputStream(new BufferedInputStream(inBytes));
            final List<FileNameAndContents> list = new ArrayList<>();
            for (;;) {
                final ZipEntry zipEntry = decompressor.getNextEntry();
                if (zipEntry == null) {
                    break;
                }
                byte[] contents = new byte[0];
                final byte[] buffer = new byte[65536];
                for (;;) {
                    final int n = decompressor.read(buffer, 0, buffer.length);
                    if (n > 0) {
                        final byte[] newContents = new byte[contents.length + n];
                        System.arraycopy(contents, 0, newContents, 0, contents.length);
                        System.arraycopy(buffer, 0, newContents, contents.length, n);
                        contents = newContents;
                    }
                    if (n < 0) {
                        break;
                    }
                }
                decompressor.closeEntry();
                list.add(new FileNameAndContents(zipEntry.getName(), contents));
            }
            decompressor.close();
            return list;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

}
