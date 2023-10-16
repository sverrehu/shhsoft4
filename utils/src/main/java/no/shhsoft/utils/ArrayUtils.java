package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ArrayUtils {

    private ArrayUtils() {
    }

    public static <T> boolean contains(final T[] haystack, final T needle) {
        if (needle == null) {
            for (final T t : haystack) {
                if (t == null) {
                    return true;
                }
            }
        } else {
            for (final T t : haystack) {
                if (needle.equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

}
