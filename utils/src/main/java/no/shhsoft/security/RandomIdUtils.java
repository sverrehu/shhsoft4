package no.shhsoft.security;

import java.security.SecureRandom;

/**
 * A generator for cryptographically strong random identifiers. Uses Java's
 * <code>java.security.SecureRandom</code> to provide randomness.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RandomIdUtils {

    private static SecureRandom random;
    private static final char[] ALL_CHARACTERS
        = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] NON_CONFUSING_CHARACTERS
        = "23456789ABCDEFGHJKLMNPQRSTUVWXYabcdefhjkmnpqrstuvwxy".toCharArray();
    private static final char[] NON_CONFUSING_LC_CHARACTERS
        = "23456789abcdefhjkmnpqrstuvwxy".toCharArray();
    private static final char[] NON_CONFUSING_UC_CHARACTERS
        = "23456789ABCDEFGHJKLMNPQRSTUVWXY".toCharArray();
    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] DIGITS = "0123456789".toCharArray();

    private RandomIdUtils() {
    }

    /**
     * Specifies a pseudo-random number generator to use when generating the IDs. If this method is
     * not called, the class will use SHA1PRNG.
     *
     * @param prng
     *            the random number generator to use.
     */
    public static void setRandomGenerator(final SecureRandom prng) {
        random = prng;
    }

    /**
     * Generates a new ID with characters and digits that should be easily identifiable by humans.
     *
     * @param len
     *            the number of characters in the returned string.
     * @return a string with random characters.
     */
    public static String generateHumanReadableId(final int len) {
        return generateString(len, NON_CONFUSING_CHARACTERS);
    }

    public static String generateHumanReadableIdLowerCase(final int len) {
        return generateString(len, NON_CONFUSING_LC_CHARACTERS);
    }

    public static String generateHumanReadableIdUpperCase(final int len) {
        return generateString(len, NON_CONFUSING_UC_CHARACTERS);
    }

    /**
     * Generates a new ID. The ID is a <code>String</code> of characters 0-9, A-Z and a-z. With 62
     * different values, each character makes up approximately 5.95 bits of randomness. If you want
     * e.g. 256 bit ranomness, you need a string of length 44.
     *
     * @param len
     *            the number of characters in the returned string.
     * @return a string with random characters.
     */
    public static String generateId(final int len) {
        return generateString(len, ALL_CHARACTERS);
    }

    public static String generateIdWithLetterFirst(final int len) {
        if (len == 0) {
            return "";
        }
        return generateString(1, LETTERS) + generateString(len - 1, ALL_CHARACTERS);
    }

    /**
     * Generates a new numeric ID. The ID is a <code>String</code> of characters 0-9.
     *
     * @param len
     *            the number of digits in the returned string.
     * @return a string with random characters.
     */
    public static String generateNumericId(final int len) {
        return generateString(len, DIGITS);
    }

    public static String generateId(final int len, final char[] characters) {
        return generateString(len, characters);
    }

    public static char[] generatePassword(final int minLen, final int maxLen) {
        final int len = rnd(minLen, maxLen);
        return generatePassword(len);
    }

    public static char[] generatePassword(final int len) {
        return generateChars(len, NON_CONFUSING_CHARACTERS);
    }

    /**
     * Generates a new string of random characters, based on the given character array.
     *
     * @param len
     *            the number of characters in the returned string.
     * @param source
     *            the character array to pick from.
     * @return a string with random characters.
     */
    private static String generateString(final int len, final char[] source) {
        return new String(generateChars(len, source));
    }

    private static int rnd(final int from, final int to) {
        if (to <= from) {
            return from;
        }
        init();
        final int n = random.nextInt();
        return from + Math.abs(n % (to - from + 1));
    }

    private static char[] generateChars(final int len, final char[] source) {
        init();
        final byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        final char[] chars = new char[len];
        for (int q = len - 1; q >= 0; q--) {
            chars[q] = source[(bytes[q] & 0xff) % source.length];
        }
        return chars;
    }

    /**
     * Initializes this class by fetching an instance of the SHA1PRNG random number generator. May
     * throw a <code>RuntimeException</code> in the unlikely event that this type of generator is
     * not available.
     */
    private static synchronized void init() {
        if (random != null) {
            return;
        }
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (final Exception e) {
            throw new RuntimeException("Unable to get instance of SecureRandom");
        }
    }

}
