package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FlagUtils {

    private FlagUtils() {
    }

    public static int setFlag(final int allFlags, final int flagMask, final boolean status) {
        /* If more than one flag bit is given, all will be set according to status. */
        if (status) {
            return allFlags | flagMask;
        }
        return allFlags & ~flagMask;
    }

    public static boolean isFlagSet(final int allFlags, final int flagMask) {
        /* If more than one flag bit is given, all must be set to receive true. */
        return (allFlags & flagMask) == flagMask;
    }

}
