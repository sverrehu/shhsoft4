package no.shhsoft.math;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ArrayFilters {

    private ArrayFilters() {
    }

    public static float[] medianFilter(final float[] a, final int from, final int len, final int windowSize) {
        final float[] filtered = new float[len];
        final float[] window = new float[windowSize];
        for (int q = filtered.length - 1; q >= 0; q--) {
            for (int w = window.length - 1; w >= 0; w--) {
                final int idx = from + q + w - window.length / 2;
                final float value;
                if (idx < from || idx >= len) {
                    value = 0.0f;
                } else {
                    value = a[idx];
                }
                window[w] = value;
            }
            filtered[q] = ArrayMathUtils.median(window);
        }
        return filtered;
    }

    public static float[] medianFilter(final float[] a, final int windowSize) {
        return medianFilter(a, 0, a.length, windowSize);
    }

    public static void destructiveMedianFilter(final float[] a, final int from, final int len, final int windowSize) {
        final float[] tmp = new float[len];
        System.arraycopy(a, from, tmp, 0, len);
        final float[] out = medianFilter(tmp, windowSize);
        System.arraycopy(out, 0, a, from, len);
    }

    public static void destructiveMedianFilter(final float[] a, final int windowSize) {
        destructiveMedianFilter(a, 0, a.length, windowSize);
    }

}
