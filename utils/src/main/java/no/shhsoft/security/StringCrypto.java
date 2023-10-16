package no.shhsoft.security;

import no.shhsoft.utils.Base64Utils;
import no.shhsoft.utils.Base64Utils.Encoding;
import no.shhsoft.utils.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringCrypto {

    private static final String CRYPTO_ALGORITHM = "AES";
    private static final byte[] PBE_SALT = {0x23, (byte) 0xae, 0x75, (byte) 0xf4, 0x23,
                                            (byte) 0xda, (byte) 0x89, (byte) 0xbd};
    private static final int PBE_ITERATIONS = 2048;
    private static final Encoding DEFAULT_ENCODING = Encoding.BASE64;

    private StringCrypto() {
        /* not to be instantiated */
    }

    private static byte[] encrypt(final byte[] data, final Key key) {
        try {
            final Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (final GeneralSecurityException e) {
            throw new CryptoException("Encryption error.", e);
        }
    }

    private static byte[] decrypt(final byte[] data, final int offset, final int len, final Key key) {
        try {
            final Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data, offset, len);
        } catch (final BadPaddingException e) {
            throw new CryptoException("Decryption error.  Wrong password?", e);
        } catch (final GeneralSecurityException e) {
            throw new CryptoException("Decryption error.", e);
        }
    }

    private static byte[] decrypt(final byte[] data, final Key key) {
        return decrypt(data, 0, data.length, key);
    }

    private static Key convertPasswordToKey(final char[] password) {
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(password, PBE_SALT, PBE_ITERATIONS);
        try {
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(keyFactory.generateSecret(pbeKeySpec).getEncoded());
            md.update(PBE_SALT);
            return new SecretKeySpec(md.digest(), CRYPTO_ALGORITHM);
        } catch (final NoSuchAlgorithmException e) {
            throw new CryptoException("Missing crypto algorithm", e);
        } catch (final InvalidKeySpecException e) {
            throw new CryptoException("Invalid password", e);
        }
    }

    public static byte[] encrypt(final byte[] data, final char[] password) {
        return encrypt(data, convertPasswordToKey(password));
    }

    public static String encrypt(final String string, final char[] password, final Encoding encoding) {
        final byte[] data = encrypt(StringUtils.getBytesUtf8(string), convertPasswordToKey(password));
        return Base64Utils.encode(encoding, data);
    }

    public static String encrypt(final String string, final char[] password) {
        return encrypt(string, password, DEFAULT_ENCODING);
    }

    public static String encryptForUrl(final String string, final char[] password) {
        return encrypt(string, password, Encoding.BASE64URL);
    }

    public static byte[] decrypt(final byte[] data, final char[] password) {
        return decrypt(data, convertPasswordToKey(password));
    }

    public static String decrypt(final String string, final char[] password, final Encoding encoding) {
        final byte[] data = Base64Utils.decode(encoding, string);
        return StringUtils.newStringUtf8(decrypt(data, convertPasswordToKey(password)));
    }

    public static String decrypt(final String string, final char[] password) {
        return decrypt(string, password, DEFAULT_ENCODING);
    }

    public static String decryptForUrl(final String string, final char[] password) {
        return decrypt(string, password, Encoding.BASE64URL);
    }

}
