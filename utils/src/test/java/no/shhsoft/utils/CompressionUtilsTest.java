package no.shhsoft.utils;

import no.shhsoft.utils.CompressionUtils.FileNameAndContents;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CompressionUtilsTest {

    private void assertCorrectGz(final byte[] data) {
        final byte[] compressed = CompressionUtils.gzCompress(data);
        final byte[] decompressed = CompressionUtils.gzDecompress(compressed);
        assertArrayEquals(data, decompressed);
    }

    private FileNameAndContents createFileNameAndContents(final int n) {
        return new FileNameAndContents("filename" + n + ".txt", ("This is the contents of file " + n).getBytes());
    }

    @Test
    public void shouldReturnNullForNullGz() {
        assertNull(CompressionUtils.gzCompress(null));
        assertNull(CompressionUtils.gzDecompress(null));
    }

    @Test
    public void shouldCompressAndDecompressCorrectlyGz() {
        assertCorrectGz("This should be compressed and decompressed correctly\r\n\0".getBytes());
    }

    @Test
    public void shouldCompressAndDecompressCorrectlyZip() {
        final FileNameAndContents[] fileNamesAndContents = new FileNameAndContents[3];
        for (int q = 0; q < fileNamesAndContents.length; q++) {
            fileNamesAndContents[q] = createFileNameAndContents(q);
        }
        final byte[] compressed = CompressionUtils.zipCompress(fileNamesAndContents);
        final List<FileNameAndContents> decompressed = CompressionUtils.zipDecompress(compressed);
        assertEquals(fileNamesAndContents.length, decompressed.size());
        for (int q = 0; q < fileNamesAndContents.length; q++) {
            assertEquals(fileNamesAndContents[q].getFileName(), decompressed.get(q).getFileName());
            assertArrayEquals(fileNamesAndContents[q].getContents(), decompressed.get(q).getContents());
        }
    }

}
