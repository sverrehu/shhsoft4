package no.shhsoft.utils;

import java.io.ByteArrayOutputStream;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Base64Utils {

    /* Optimization note 2011-03-15: Compared to Apache Commons Codec, my encoder is faster,
     * but my decoder is slower.  I suspect the addBytes method and its use of
     * ByteArrayOutputStream is the culprit. */
    private static final String STRING_62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final byte[] MIME_6263 = new byte[] { '+', '/' };
    private static final byte[] URL_6263 = new byte[] { '-', '_' };
    private static final byte[] MIME_CHAR_64 = (STRING_62 + new String(MIME_6263)).getBytes();
    private static final byte[] URL_CHAR_64 = (STRING_62 + new String(URL_6263)).getBytes();
    private static final char PAD = '=';

    private Base64Utils() {
    }

    public enum Encoding {

        BASE64,
        BASE64URL

    }

    private static void addBytes(final ByteArrayOutputStream out, final int[] bytes, final int len) {
        if (len <= 1) {
            return;
        }
        int value = (bytes[0] & 63) << 2;
        value |= bytes[1] >>> 4 & 3;
        out.write(value);
        if (len == 2) {
            return;
        }
        value = (bytes[1] & 15) << 4;
        value |= bytes[2] >>> 2 & 15;
        out.write(value);
        if (len == 3) {
            return;
        }
        value = (bytes[2] & 3) << 6;
        value |= bytes[3] & 63;
        out.write(value);
    }

    private static String insertLineBreaks(final String s, final int lineLength) {
        final StringBuilder sb = new StringBuilder();
        int from = 0;
        while (from < s.length()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            if (from + lineLength <= s.length()) {
                sb.append(s, from, from + lineLength);
            } else {
                sb.append(s.substring(from));
            }
            from += lineLength;
        }
        return sb.toString();
    }

    private static String encode(final byte[] base64chars, final boolean usePadding,
                                 final byte[] data, final int len, final boolean breakLines) {
        final StringBuilder sb = new StringBuilder(5 + len * 4 / 3);
        /* Three input bytes will give four output characters by grouping six by six bits. */
        for (int idx = 0;;) {
            /* Part of the first of three bytes */
            if (idx >= len) {
                break;
            }
            int b = data[idx++] & 255;
            int x = b >> 2;
            sb.append((char) base64chars[x]);

            /* Part of the first and part of the second of three bytes */
            x = (b & 3) << 4;
            if (idx < len) {
                b = data[idx++] & 255;
                x |= b >> 4;
                sb.append((char) base64chars[x]);
            } else {
                sb.append((char) base64chars[x]);
                if (usePadding) {
                    sb.append(PAD);
                    sb.append(PAD);
                }
                break;
            }

            /* Part of the second and part of the third of three bytes */
            x = (b & 15) << 2;
            if (idx < len) {
                b = data[idx++] & 255;
                x |= b >> 6;
                sb.append((char) base64chars[x]);
            } else {
                sb.append((char) base64chars[x]);
                if (usePadding) {
                    sb.append(PAD);
                }
                break;
            }

            /* Part of the third of three bytes */
            x = b & 63;
            sb.append((char) base64chars[x]);
        }
        if (breakLines) {
            return insertLineBreaks(sb.toString(), 76);
        }
        return sb.toString();
    }

    private static byte[] decode(final byte[] char6263, final boolean requirePadding, final String b64) {
        final int inputLength = b64.length();
        final ByteArrayOutputStream out = new ByteArrayOutputStream(inputLength * 3 / 4);
        final int[] b4 = new int[4];
        int b4index = 0;
        final char[] b64chars = b64.toCharArray();
        for (int q = 0; q < inputLength; q++) {
            int c = b64chars[q];
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                continue;
            }
            final int value;
            if (c == PAD) {
                int numPads = 0;
                for (; q < inputLength; q++) {
                    c = b64chars[q];
                    if (c == PAD) {
                        ++numPads;
                    } else if (!Character.isWhitespace(c)) {
                        throw new RuntimeException("Character `" + (char) c + "' found after padding");
                    }
                }
                if (numPads > 2) {
                    throw new RuntimeException("Too many padding characters");
                }
                if (b4index + numPads != 4) {
                    throw new RuntimeException("Incorrect number of padding characters");
                }
                addBytes(out, b4, b4index);
                b4index = 0;
                break;
            } else if (c >= 'A' && c <= 'Z') {
                value = c - 'A';
            } else if (c >= 'a' && c <= 'z') {
                value = 'Z' - 'A' + 1 + c - 'a';
            } else if (c >= '0' && c <= '9') {
                value = 2 * ('Z' - 'A' + 1) + c - '0';
            } else if (c == char6263[0]) {
                value = 2 * ('Z' - 'A' + 1) + '9' - '0' + 1;
            } else if (c == char6263[1]) {
                value = 2 * ('Z' - 'A' + 1) + '9' - '0' + 2;
            } else {
                throw new RuntimeException("Invalid character `" + (char) c + "' in BASE64 string");
            }
            if (value < 0) {
                throw new RuntimeException("Don't think this is supposed to happen");
            }
            if (b4index == b4.length) {
                addBytes(out, b4, b4index);
                b4index = 0;
            }
            b4[b4index++] = value;
        }
        if (requirePadding && b4index != 0 && b4index != 4) {
            throw new RuntimeException("Incorrect number of BASE64 characters at end");
        }
        addBytes(out, b4, b4index);
        return out.toByteArray();
    }

    private static byte[] toChars(final Encoding encoding) {
        switch (encoding) {
            case BASE64:
                return MIME_CHAR_64;
            case BASE64URL:
                return URL_CHAR_64;
            default:
                throw new RuntimeException("Unsupported encoding `" + encoding.toString() + "'");
        }
    }

    private static byte[] toChars6263(final Encoding encoding) {
        switch (encoding) {
            case BASE64:
                return MIME_6263;
            case BASE64URL:
                return URL_6263;
            default:
                throw new RuntimeException("Unsupported encoding `" + encoding.toString() + "'");
        }
    }

    private static boolean toUsePadding(final Encoding encoding) {
        switch (encoding) {
            case BASE64:
                return true;
            case BASE64URL:
                return false;
            default:
                throw new RuntimeException("Unsupported encoding `" + encoding.toString() + "'");
        }
    }

    public static String encode(final Encoding encoding, final byte[] data, final int len, final boolean breakLines) {
        return encode(toChars(encoding), toUsePadding(encoding), data, len, breakLines);
    }

    public static String encode(final Encoding encoding, final byte[] data, final int len) {
        return encode(encoding, data, len, false);
    }

    public static String encode(final Encoding encoding, final byte[] data, final boolean breakLines) {
        return encode(encoding, data, data.length, breakLines);
    }

    public static String encode(final Encoding encoding, final byte[] data) {
        return encode(encoding, data, data.length, false);
    }

    public static String encode(final byte[] data, final int len, final boolean breakLines) {
        return encode(MIME_CHAR_64, true, data, len, breakLines);
    }

    public static String encode(final byte[] data, final int len) {
        return encode(data, len, false);
    }

    public static String encode(final byte[] data, final boolean breakLines) {
        return encode(data, data.length, breakLines);
    }

    public static String encode(final byte[] data) {
        return encode(data, data.length);
    }

    public static String encodeForUrl(final byte[] data, final int len) {
        return encode(URL_CHAR_64, false, data, len, false);
    }

    public static String encodeForUrl(final byte[] data) {
        return encodeForUrl(data, data.length);
    }

    public static byte[] decode(final Encoding encoding, final boolean requirePadding, final String b64) {
        return decode(toChars6263(encoding), requirePadding, b64);
    }

    public static byte[] decode(final Encoding encoding, final String b64) {
        return decode(toChars6263(encoding), toUsePadding(encoding), b64);
    }

    public static byte[] decode(final String b64) {
        return decode(MIME_6263, true, b64);
    }

    public static byte[] decodeForUrl(final String b64) {
        return decode(URL_6263, false, b64);
    }

}
