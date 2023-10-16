package no.shhsoft.math;

import java.util.Arrays;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ArrayMathUtils {

    private ArrayMathUtils() {
    }

    public static final class MinAndMax {

        private final float min;
        private final float max;

        public MinAndMax(final float min, final float max) {
            this.min = min;
            this.max = max;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

    }

    private static void assertNotNull(final float[] a) {
        if (a == null) {
            throw new RuntimeException("Array cannot be null");
        }
    }

    private static void assertNotEmpty(final float[] a) {
        assertNotNull(a);
        if (a.length < 1) {
            throw new RuntimeException("Array cannot be empty");
        }
    }

    public static float min(final float[] a) {
        assertNotEmpty(a);
        float min = Float.MAX_VALUE;
        for (int q = a.length - 1; q >= 0; q--) {
            final float d = a[q];
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

    public static float max(final float[] a) {
        assertNotEmpty(a);
        float max = -Float.MAX_VALUE;
        for (int q = a.length - 1; q >= 0; q--) {
            final float d = a[q];
            if (d > max) {
                max = d;
            }
        }
        return max;
    }

    public static MinAndMax minAndMax(final float[] a) {
        assertNotEmpty(a);
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;
        for (int q = a.length - 1; q >= 0; q--) {
            final float d = a[q];
            if (d < min) {
                min = d;
            }
            if (d > max) {
                max = d;
            }
        }
        return new MinAndMax(min, max);
    }

    public static float[] copy(final float[] a) {
        assertNotNull(a);
        final float[] a2 = new float[a.length];
        System.arraycopy(a, 0, a2, 0, a2.length);
        return a2;
    }

    public static float[] copy(final float[] a, final int from, final int len) {
        assertNotNull(a);
        final float[] a2 = new float[len];
        System.arraycopy(a, from, a2, 0, a2.length);
        return a2;
    }

    public static float[] sort(final float[] a) {
        final float[] a2 = copy(a);
        Arrays.sort(a2);
        return a2;
    }

    public static float sum(final float[] a) {
        assertNotEmpty(a);
        float sum = 0.0f;
        for (int q = a.length - 1; q >= 0; q--) {
            sum += a[q];
        }
        return sum;
    }

    public static float mean(final float[] a) {
        return sum(a) / a.length;
    }

    public static float median(final float[] a) {
        assertNotEmpty(a);
        final float[] sorted = sort(a);
        final int middle = a.length / 2;
        if ((a.length & 1) == 1) {
            return sorted[middle];
        }
        return (sorted[middle - 1] + sorted[middle]) / 2.0f;
    }

}
