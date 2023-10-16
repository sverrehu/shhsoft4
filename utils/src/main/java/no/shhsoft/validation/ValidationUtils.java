package no.shhsoft.validation;

import no.shhsoft.utils.StringUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ValidationUtils {

    public static final String ALNUM
        = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private ValidationUtils() {
        /* not to be instantiated */
    }

    public static boolean isAccordingToLimits(final String s, final boolean allowNull,
                                              final int minLen, final int maxLen,
                                              final char[] allowedChars) {
        if (allowNull && s == null) {
            return true;
        }
        final int len = s.length();
        return len >= minLen && len <= maxLen && StringUtils.containsOnly(s, allowedChars);
    }

    public static boolean isAccordingToLimits(final String s, final boolean allowNull,
                                              final int minLen, final int maxLen,
                                              final String allowedChars) {
        return isAccordingToLimits(s, allowNull, minLen, maxLen, allowedChars.toCharArray());
    }

    public static boolean isAccordingToLimits(final String s, final int minLen, final int maxLen,
                                              final char[] allowedChars) {
        return isAccordingToLimits(s, false, minLen, maxLen, allowedChars);
    }

    public static boolean isAccordingToLimits(final String s, final int minLen, final int maxLen,
                                              final String allowedChars) {
        return isAccordingToLimits(s, false, minLen, maxLen, allowedChars.toCharArray());
    }

    public static boolean isAccordingToLimits(final String s, final int maxLen,
                                              final char[] allowedChars) {
        return isAccordingToLimits(s, false, 0, maxLen, allowedChars);
    }

    public static boolean isAccordingToLimits(final String s, final int maxLen,
                                              final String allowedChars) {
        return isAccordingToLimits(s, false, 0, maxLen, allowedChars.toCharArray());
    }

    public static boolean containsControlChars(final String s) {
        if (s == null) {
            return false;
        }
        for (int q = s.length() - 1; q >= 0; q--) {
            final int c = s.charAt(q);
            if (Character.isISOControl(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsControlCharsExceptCrLf(final String s) {
        if (s == null) {
            return false;
        }
        for (int q = s.length() - 1; q >= 0; q--) {
            final int c = s.charAt(q);
            if (Character.isISOControl(c) && c != '\r' && c != '\n') {
                return true;
            }
        }
        return false;
    }

    public static boolean containsVerticalSpace(final String s) {
        if (s == null) {
            return false;
        }
        for (int q = s.length() - 1; q >= 0; q--) {
            final int c = s.charAt(q);
            if (c == '\r' || c == '\n' || c == 0x0b) {
                return true;
            }
        }
        return false;
    }

}
