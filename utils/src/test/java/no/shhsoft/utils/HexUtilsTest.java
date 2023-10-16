package no.shhsoft.utils;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HexUtilsTest
extends TestCase {

    public void testShouldConvertForwardAndBack()
    throws Exception {
        final byte[] b0 = new byte[256];
        for (int q = 0; q < b0.length; q++) {
            b0[q] = (byte) (q & 0xff);
        }
        final byte[] b1 = HexUtils.hexStringToBytes(HexUtils.bytesToHexString(b0));
        assertEquals(b0.length, b1.length);
        for (int q = 0; q < b0.length; q++) {
            assertEquals(b0[q], b1[q]);
        }
    }

}
