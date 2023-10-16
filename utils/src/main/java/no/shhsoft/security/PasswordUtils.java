package no.shhsoft.security;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class PasswordUtils {

    private PasswordUtils() {
        /* not to be instantiated */
    }

    public static void clear(final char[] password) {
        if (password == null) {
            return;
        }
        for (int q = password.length - 1; q >= 0; q--) {
            password[q] = 0xffff;
        }
    }

    public static void clear(final byte[] password) {
        if (password == null) {
            return;
        }
        for (int q = password.length - 1; q >= 0; q--) {
            password[q] = (byte) 0xff;
        }
    }

    public static char[] copy(final char[] password) {
        if (password == null) {
            return null;
        }
        final char[] ret = new char[password.length];
        System.arraycopy(password, 0, ret, 0, ret.length);
        return ret;
    }

    public static boolean equal(final char[] p1, final char[] p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        if (p1.length != p2.length) {
            return false;
        }
        for (int q = 0; q < p1.length; q++) {
            if (p1[q] != p2[q]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] toBytes(final char[] password) {
        final byte[] bytes = new byte[2 * password.length];
        for (int q = 0, w = 0; q < password.length; q++) {
            bytes[w++] = (byte) ((password[q] >>> 8) & 0xff);
            bytes[w++] = (byte) (password[q] & 0xff);
        }
        return bytes;
    }

}
