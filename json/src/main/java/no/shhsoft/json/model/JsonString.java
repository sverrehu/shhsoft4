package no.shhsoft.json.model;

import no.shhsoft.json.utils.JsonUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonString
implements JsonValue {

    private final String value;

    public JsonString(final String value) {
        this.value = value;
    }

    public JsonString(final Date dateValue) {
        this.value = JsonUtils.toTimestamp(dateValue);
    }

    public static JsonString get(final String value) {
        return new JsonString(value);
    }

    public static JsonString get(final Date dateValue) {
        return new JsonString(dateValue);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JsonString that = (JsonString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
