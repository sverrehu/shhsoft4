package no.shhsoft.json.model;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonNull
implements JsonValue {

    public static final JsonNull NULL = new JsonNull();

    private JsonNull() {
    }

    public static JsonNull get() {
        return NULL;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof JsonNull;
    }

}
