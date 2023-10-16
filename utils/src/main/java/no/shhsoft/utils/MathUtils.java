package no.shhsoft.utils;

import java.util.Random;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class MathUtils {

    private static final Random RND = new Random();

    private MathUtils() {
    }

    public static double degToRad(final double deg) {
        return 2.0 * Math.PI * (deg / 360.0);
    }

    public static double radToDeg(final double rad) {
        return 360.0 * rad / (2.0 * Math.PI);
    }

    public static int randomInclusive(final int min, final int max) {
        final int fromInclusive = Math.min(min, max);
        final int toInclusive = Math.max(min, max);
        final int n = toInclusive - fromInclusive + 1;
        return fromInclusive + RND.nextInt(n);
    }

    public static boolean randomBoolean() {
        return RND.nextBoolean();
    }

    public static double randomDoubleLessThanOne() {
        return RND.nextDouble();
    }

}
