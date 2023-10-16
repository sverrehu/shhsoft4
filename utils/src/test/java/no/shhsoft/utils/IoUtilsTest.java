package no.shhsoft.utils;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class IoUtilsTest
extends TestCase {

    private static byte[] generateContents(final int len) {
        final byte[] ret = new byte[len];
        for (int q = 0; q < ret.length; q++) {
            ret[q] = (byte) (q & 0xff);
        }
        return ret;
    }

    private static String getTempFilename() {
        try {
            return File.createTempFile("IoUtilsTest", ".test").toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void doTestWriteAndReadFileWithContentLength(final int len) {
        final String filename = getTempFilename();
        try {
            final byte[] data = generateContents(len);
            IoUtils.writeFile(filename, data);
            final byte[] data2 = IoUtils.readFile(filename);
            assertEquals(data.length, data2.length);
            for (int q = 0; q < data.length; q++) {
                assertEquals(data[q], data2[q]);
            }
        } finally {
            new File(filename).delete();
        }
    }

    public void testWriteAndReadFile() {
        doTestWriteAndReadFileWithContentLength(0);
        for (int q = IoUtils.getBufferSize() - 2; q <= IoUtils.getBufferSize() + 2; q++) {
            doTestWriteAndReadFileWithContentLength(q);
        }
    }

}
