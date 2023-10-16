package no.shhsoft.validation;

/**
 * Big note!  This class doesn't do any URL decoding whatsoever.  There may
 * be security implications depending on how the methods are used.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UrlValidationUtils {

    /* the space in the following is intentional. */
    private static final String PATH_CHAR_WHITELIST = ValidationUtils.ALNUM + ".-_ /";
    /* NOTE: the following blacklist does not protect against all kinds of attacks. it's only there
     * to prevent the simplest forms of attacks. it's up to the request handling code to deal with
     * the application security. */
    private static final String[] PATH_SEQUENCE_BLACKLIST = {
        "..", "//", "./", "/."
    };
    private static final int PATH_MAX_LENGTH = 1024;

    private UrlValidationUtils() {
        /* not to be instantiated */
    }

    private static boolean containsOnly(final String s, final String chars) {
        for (int q = s.length() - 1; q >= 0; q--) {
            if (chars.indexOf(s.charAt(q)) < 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean doesNotContain(final String s, final String[] sequences) {
        for (int q = sequences.length - 1; q >= 0; q--) {
            if (s.contains(sequences[q])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Note that this method expects the path to start with a slash.
     */
    public static boolean isPathOk(final String path) {
        if (path == null) {
            return false;
        }
        if (path.length() == 0 || path.length() > PATH_MAX_LENGTH) {
            return false;
        }
        return path.charAt(0) == '/'
            && containsOnly(path, PATH_CHAR_WHITELIST)
            && doesNotContain(path, PATH_SEQUENCE_BLACKLIST);
    }

}
