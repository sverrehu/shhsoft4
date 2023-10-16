package no.shhsoft.json.model;

import java.util.Objects;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonLong
implements JsonNumber {

    private final long value;
    private static final int NUM_CACHED = 2048;
    private static final int CACHE_OFFSET = NUM_CACHED / 2;
    private static final int CACHE_FROM = -(NUM_CACHED - CACHE_OFFSET - 1);
    private static final int CACHE_TO = CACHE_FROM + NUM_CACHED - 1;
    private static final JsonLong[] CACHE;
    public static final JsonLong ZERO;
    public static final JsonLong ONE;

    static {
        CACHE = new JsonLong[NUM_CACHED];
        for (int q = 0; q < NUM_CACHED; q++) {
            CACHE[q] = new JsonLong(CACHE_FROM + q);
        }
        ZERO = get(0);
        ONE = get(1);
    }

    private JsonLong(final long value) {
        this.value = value;
    }

    public static JsonLong get(final long i) {
        if (i >= CACHE_FROM && i <= CACHE_TO) {
            return CACHE[(int) i - CACHE_FROM];
        }
        return new JsonLong(i);
    }

    public long getValue() {
        return value;
    }

    @Override
    public double getValueAsDouble() {
        return value;
    }

    @Override
    public long getValueAsLong() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JsonLong jsonLong = (JsonLong) o;
        return value == jsonLong.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
