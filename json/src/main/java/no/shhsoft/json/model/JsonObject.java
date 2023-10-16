package no.shhsoft.json.model;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonObject
extends LinkedHashMap<String, JsonValue>
implements JsonContainer {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
