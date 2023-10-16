package no.shhsoft.security;

import no.shhsoft.utils.HexUtils;
import no.shhsoft.utils.StringUtils;

import java.security.SecureRandom;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HashedPasswordUtils {

    private static final SecureRandom RANDOM;
    private static final int NUM_SALT_BYTES = 16;
    private static final int SALTED_SHA1_NUM_HASH_BYTES = 20; /* SHA-1; 160 bits */
    private static final String SALTED_SHA1_PREFIX = "s+sha1";
    private static final char SEPARATOR = ':';
    private static final int NUM_HASH_BYTES = SALTED_SHA1_NUM_HASH_BYTES;
    public static final char[] SEPARATORS = new char[] { SEPARATOR };
    public static final String PREFIX = SALTED_SHA1_PREFIX;
    public static final int HASHED_PASSWORD_STRING_LENGTH = PREFIX.length() + 2 * (NUM_SALT_BYTES + NUM_HASH_BYTES) + 2;

    static {
        try {
            RANDOM = SecureRandom.getInstance("SHA1PRNG");
        } catch (final Exception e) {
            throw new RuntimeException("Unable to get instance of SecureRandom");
        }
    }

    private HashedPasswordUtils() {
    }

    private static byte[] generateSalt() {
        return generateSalt(NUM_SALT_BYTES);
    }

    static byte[] generateSalt(final int numSaltBytes) {
        final byte[] salt = new byte[numSaltBytes];
        for (int q = salt.length - 1; q >= 0; q--) {
            salt[q] = (byte) (RANDOM.nextInt() & 0xff);
        }
        return salt;
    }

    private static byte[] getPasswordBytes(final char[] password) {
        final byte[] passwordBytes = new byte[2 * password.length];
        int idx = 0;
        for (int q = password.length - 1; q >= 0; q--) {
            passwordBytes[idx++] = (byte) ((password[q] >>> 8) & 0xff);
            passwordBytes[idx++] = (byte) (password[q] & 0xff);
        }
        return passwordBytes;
    }

    private static String createDigestForSaltedSha1(final char[] password, final byte[] saltBytes) {
        final byte[] passwordBytes = getPasswordBytes(password);
        final byte[] toDigest = new byte[2 * (passwordBytes.length + saltBytes.length)];
        int offs = 0;
        System.arraycopy(saltBytes, 0, toDigest, offs, saltBytes.length);
        offs += saltBytes.length;
        System.arraycopy(passwordBytes, 0, toDigest, offs, passwordBytes.length);
        offs += passwordBytes.length;
        System.arraycopy(saltBytes, 0, toDigest, offs, saltBytes.length);
        offs += saltBytes.length;
        System.arraycopy(passwordBytes, 0, toDigest, offs, passwordBytes.length);
        offs += passwordBytes.length;
        final String digested = Digest.sha1(toDigest);
        PasswordUtils.clear(toDigest);
        return digested;
    }

    private static boolean isMatchForSaltedSha1(final String[] parts, final char[] password,
                                                final String hashedPasswordString) {
        if (parts.length != 3) {
            throw new RuntimeException("Unexpected number of parts for hashed password `" + hashedPasswordString + "'");
        }
        final byte[] saltBytes = HexUtils.hexStringToBytes(parts[1]);
        final byte[] passwordBytes = HexUtils.hexStringToBytes(parts[2]);
        if (passwordBytes.length != SALTED_SHA1_NUM_HASH_BYTES) {
            throw new RuntimeException("Unexpected number of bytes in hashed password `" + hashedPasswordString + "'");
        }
        final String digest = createDigestForSaltedSha1(password, saltBytes);
        return digest.equalsIgnoreCase(parts[2]);
    }

    public static String generateHashedPassword(final char[] password) {
        final byte[] saltBytes = generateSalt();
        final String digested = createDigestForSaltedSha1(password, saltBytes);
        return PREFIX + SEPARATOR + HexUtils.bytesToHexString(saltBytes) + SEPARATOR + digested;
    }

    public static boolean isMatch(final char[] password, final String hashedPasswordString) {
        final String[] parts = StringUtils.split(hashedPasswordString, SEPARATORS, true, false);
        if (parts.length == 0) {
            throw new RuntimeException("No parts found in hashed password `" + hashedPasswordString + "'");
        }
        if (SALTED_SHA1_PREFIX.equals(parts[0])) {
            return isMatchForSaltedSha1(parts, password, hashedPasswordString);
        }
        throw new RuntimeException("Unknown method `" + parts[0] + "' for hashed password `" + hashedPasswordString + "'");
    }

}
