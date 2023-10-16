package no.shhsoft.json.impl.generator;

import no.shhsoft.json.JsonException;
import no.shhsoft.json.JsonGenerator;
import no.shhsoft.json.model.*;
import no.shhsoft.utils.StringUtils;

import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonGeneratorImpl
implements JsonGenerator {

    static String toJsonString(final String s) {
        if (s == null) {
            return "null";
        }
        return "\"" + StringUtils.escapeJavaLikeString(s, false, false) + '"';
    }

    static void encodeString(final StringBuilder sb, final JsonString string) {
        sb.append(toJsonString(string.getValue()));
    }

    static String encodeString(final JsonString string) {
        final StringBuilder sb = new StringBuilder();
        encodeString(sb, string);
        return sb.toString();
    }

    static void encodeBoolean(final StringBuilder sb, final JsonBoolean bool) {
        if (bool.getValue()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
    }

    static String encodeBoolean(final JsonBoolean bool) {
        final StringBuilder sb = new StringBuilder();
        encodeBoolean(sb, bool);
        return sb.toString();
    }

    static void encodeNumber(final StringBuilder sb, final JsonNumber number) {
        if (number instanceof JsonLong) {
            sb.append(number.getValueAsLong());
        } else if (number instanceof JsonDouble) {
            sb.append(number.getValueAsDouble());
        } else {
            throw new JsonException("Unhandled JsonNumber type: " + number.getClass().getName());
        }
    }

    static String encodeNumber(final JsonNumber number) {
        final StringBuilder sb = new StringBuilder();
        encodeNumber(sb, number);
        return sb.toString();
    }

    private static void encodeValue(final StringBuilder sb, final JsonValue value) {
        if (value instanceof JsonContainer) {
            encodeContainer(sb, (JsonContainer) value);
        } else if (value instanceof JsonNumber) {
            encodeNumber(sb, (JsonNumber) value);
        } else if (value instanceof JsonBoolean) {
            encodeBoolean(sb, (JsonBoolean) value);
        } else if (value instanceof JsonString) {
            encodeString(sb, (JsonString) value);
        } else if (value instanceof JsonNull) {
            sb.append("null");
        } else {
            throw new JsonException("Unhandled JsonValue type: " + value.getClass().getName());
        }
    }

    private static void encodeObject(final StringBuilder sb, final JsonObject object) {
        sb.append('{');
        boolean needComma = false;
        for (final Map.Entry<String, JsonValue> entry : object.entrySet()) {
            if (needComma) {
                sb.append(',');
            } else {
                needComma = true;
            }
            sb.append(toJsonString(entry.getKey()));
            sb.append(':');
            encodeValue(sb, entry.getValue());
        }
        sb.append('}');
    }

    private static void encodeArray(final StringBuilder sb, final JsonArray array) {
        sb.append('[');
        boolean needComma = false;
        for (final JsonValue value : array) {
            if (needComma) {
                sb.append(',');
            } else {
                needComma = true;
            }
            encodeValue(sb, value);
        }
        sb.append(']');
    }

    private static void encodeContainer(final StringBuilder sb, final JsonContainer container) {
        if (container instanceof JsonObject) {
            encodeObject(sb, (JsonObject) container);
        } else if (container instanceof JsonArray) {
            encodeArray(sb, (JsonArray) container);
        } else {
            throw new JsonException("Unhandled JsonContainer type: " + container.getClass().toString());
        }
    }

    @Override
    public String generate(final JsonContainer objectOrArray) {
        final StringBuilder sb = new StringBuilder();
        encodeContainer(sb, objectOrArray);
        return sb.toString();
    }

}
