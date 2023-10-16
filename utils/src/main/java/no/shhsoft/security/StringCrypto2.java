package no.shhsoft.security;

import no.shhsoft.utils.Base64Utils;
import no.shhsoft.utils.Base64Utils.Encoding;
import no.shhsoft.utils.StringUtils;
import no.shhsoft.utils.UncheckedIoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

/**
 * Better than, but incompatible with the original StringCrypto:
 *
 *   * No fixed salt
 *   * Compatible encryption between Java and Android
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringCrypto2 {

    private static final Logger LOG = Logger.getLogger(StringCrypto2.class.getName());
    private static final SecureRandom random;
    private static final String CRYPTO_ALGORITHM = "AES";
    private static final String CRYPTO_ALGORITHM_WITH_DETAILS = "AES/CBC/PKCS5Padding";
    private static final int PBE_SALT_BYTES = 8;
    private static final int PBE_ITERATIONS = 2048;
    private static final Encoding DEFAULT_ENCODING = Encoding.BASE64;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class EncryptionData {

        private final byte[] salt;
        private final byte[] iv;
        private final byte[] data;

        public EncryptionData(final byte[] salt, final byte[] iv, final byte[] data) {
            this.salt = salt;
            this.iv = iv;
            this.data = data;
        }

        public byte[] getSalt() {
            return salt;
        }

        public byte[] getIv() {
            return iv;
        }

        public byte[] getData() {
            return data;
        }

    }

    private static final class KeyAndSalt {

        private final Key key;
        private final byte[] salt;

        public KeyAndSalt(final Key key, final byte[] salt) {
            this.key = key;
            this.salt = salt;
        }

        public Key getKey() {
            return key;
        }

        public byte[] getSalt() {
            return salt;
        }

    }

    private StringCrypto2() {
    }

    private static byte[] encrypt(final byte[] data, final KeyAndSalt keyAndSalt) {
        try {
            final Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM_WITH_DETAILS);
            cipher.init(Cipher.ENCRYPT_MODE, keyAndSalt.getKey());
            final AlgorithmParameters params = cipher.getParameters();
            final byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            final byte[] encryptedData = cipher.doFinal(data);
            final EncryptionData encryptionData = new EncryptionData(keyAndSalt.getSalt(), iv, encryptedData);
            return encode(encryptionData);
        } catch (final GeneralSecurityException e) {
            throw new CryptoException("Encryption error.", e);
        }
    }

    private static byte[] decrypt(final byte[] data, final Key key, final byte[] iv) {
        try {
            final Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM_WITH_DETAILS);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher.doFinal(data);
        } catch (final BadPaddingException e) {
            throw new CryptoException("Decryption error.  Wrong password?", e);
        } catch (final GeneralSecurityException e) {
            throw new CryptoException("Decryption error.", e);
        }
    }

    private static KeyAndSalt convertPasswordToKey(final char[] password) {
        final byte[] salt = generateSalt();
        final Key key = convertPasswordToKey(password, salt);
        return new KeyAndSalt(key, salt);
    }

    private static Key convertPasswordToKey(final char[] password, final byte[] salt) {
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, PBE_ITERATIONS, 128); /* Key >128 requires JCE Unlimited Strength jars */
        try {
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(/*"PBEWithMD5AndDES"*/ "PBKDF2WithHmacSHA1");
            final Key tempKey = keyFactory.generateSecret(pbeKeySpec);
            return new SecretKeySpec(tempKey.getEncoded(), CRYPTO_ALGORITHM);
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
        final EncryptionData encryptionData = decode(data);
        return decrypt(encryptionData.getData(), convertPasswordToKey(password, encryptionData.getSalt()), encryptionData.getIv());
    }

    public static String decrypt(final String string, final char[] password, final Encoding encoding) {
        final byte[] data = Base64Utils.decode(encoding, string);
        return StringUtils.newStringUtf8(decrypt(data, password));
    }

    public static String decrypt(final String string, final char[] password) {
        return decrypt(string, password, DEFAULT_ENCODING);
    }

    public static String decryptForUrl(final String string, final char[] password) {
        return decrypt(string, password, Encoding.BASE64URL);
    }

    private static byte[] generateSalt() {
        final byte[] bytes = new byte[PBE_SALT_BYTES];
        random.nextBytes(bytes);
        return bytes;
    }

    private static byte[] encode(final EncryptionData encryptionData) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(byteStream);
        encodeByteArray(out, encryptionData.getSalt());
        encodeByteArray(out, encryptionData.getIv());
        encodeByteArray(out, encryptionData.getData());
        return byteStream.toByteArray();
    }

    private static EncryptionData decode(final byte[] bytes) {
        final DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        final byte[] salt = decodeByteArray(in);
        final byte[] iv = decodeByteArray(in);
        final byte[] data = decodeByteArray(in);
        return new EncryptionData(salt, iv, data);
    }

    private static void encodeByteArray(final DataOutputStream out, final byte[] data) {
        try {
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    private static byte[] decodeByteArray(final DataInputStream in) {
        try {
            final int length = in.readInt();
            final byte[] bytes = new byte[length];
            in.read(bytes);
            return bytes;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

}
