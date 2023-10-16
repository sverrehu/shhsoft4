package no.shhsoft.json.model;

import java.util.Objects;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonDouble
implements JsonNumber {

    private final double value;
    public static final JsonDouble ZERO = new JsonDouble(0.0);
    public static final JsonDouble ONE = new JsonDouble(1.0);

    public JsonDouble(final double value) {
        this.value = value;
    }

    public static JsonDouble get(final double value) {
        return new JsonDouble(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public double getValueAsDouble() {
        return value;
    }

    @Override
    public long getValueAsLong() {
        return (long) value;
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
        final JsonDouble that = (JsonDouble) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
