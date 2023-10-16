package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HexUtils {

    private static final char[] HEX_CHARS = ("0123456789abcdef").toCharArray();

    private HexUtils() {
        /* not to be instantiated */
    }

    /**
     * @throws NumberFormatException
     */
    public static int parseHexDigit(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return 10 + c - 'a';
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + c - 'A';
        }
        throw new NumberFormatException("Unparsable hex digit `" + c + "'");
    }

    /**
     * @throws NumberFormatException
     */
    public static byte[] hexStringToBytes(final String s) {
        final char[] c = s.toCharArray();
        if ((c.length & 1) == 1) {
            throw new NumberFormatException("Hex string `" + s
                                            + "' contains an odd number of characters");
        }
        final byte[] b = new byte[c.length / 2];
        int w = 0;
        for (int q = 0; q < b.length; q++) {
            final int b1 = parseHexDigit(c[w++]);
            final int b2 = parseHexDigit(c[w++]);
            b[q] = (byte) ((b1 << 4) | b2);
        }
        return b;
    }

    public static String byteToHexString(final byte b) {
        final char[] chars = new char[2];
        chars[0] = HEX_CHARS[(b >> 4) & 15];
        chars[1] = HEX_CHARS[b & 15];
        return new String(chars);
    }

    public static String bytesToHexString(final byte[] b) {
        final int len = b.length;
        final StringBuilder sb = new StringBuilder(2 * len);
        for (final byte aB : b) {
            sb.append(HEX_CHARS[(aB >> 4) & 15]);
            sb.append(HEX_CHARS[aB & 15]);
        }
        return sb.toString();
    }

    public static String stringToHex(final String s, final String sep) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        final byte[] b = s.getBytes();
        for (final byte aB : b) {
            sb.append(HEX_CHARS[(aB >> 4) & 15]);
            sb.append(HEX_CHARS[aB & 15]);
            if (sep != null) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }

    public static String stringToHex(final String s) {
        return stringToHex(s, null);
    }

    public static String to4DigitHex(final char c) {
        final char[] chars = new char[4];
        int value = c;
        int idx = chars.length - 1;
        for (;;) {
            chars[idx] = HEX_CHARS[value & 15];
            if (--idx < 0) {
                break;
            }
            value >>>= 4;
        }
        return new String(chars);
    }

}
