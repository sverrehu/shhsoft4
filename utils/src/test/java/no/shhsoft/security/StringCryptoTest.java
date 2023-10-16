package no.shhsoft.security;

import no.shhsoft.utils.Base64Utils.Encoding;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringCryptoTest {

    private static void assertEncryptDecrypt(final String string, final char[] password, final Encoding encoding) {
        final String encrypted = StringCrypto.encrypt(string, password, encoding);
        final String decrypted = StringCrypto.decrypt(encrypted, password, encoding);
        assertEquals("Failed for Encoding " + encoding.toString(), string, decrypted);
    }

    @Test
    public void shouldEncryptAndDecryptVarious() {
        for (final Encoding encoding : Encoding.values()) {
            for (int q = 0; q < 256; q++) {
                final StringBuilder sb = new StringBuilder(q);
                for (int w = 0; w < q; w++) {
                    sb.append((char) (50 + w));
                }
                assertEncryptDecrypt(sb.toString(), "foobar".toCharArray(), encoding);
            }
        }
    }

}
