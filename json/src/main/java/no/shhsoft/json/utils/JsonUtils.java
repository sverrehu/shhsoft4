package no.shhsoft.json.utils;

import no.shhsoft.json.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonUtils {

    private static final SimpleDateFormat TIMESTAMP_FORMAT_WITH_MS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static final SimpleDateFormat TIMESTAMP_FORMAT_WITHOUT_MS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    private static final SimpleDateFormat TIMESTAMP_FORMAT_WITH_MS_WITHOUT_TZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final SimpleDateFormat TIMESTAMP_FORMAT_WITHOUT_MS_WITHOUT_TZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat[] TIMESTAMP_FORMATS_TO_TRY;
    private static final Object DATE_FORMAT_LOCK = new Object();

    static {
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        TIMESTAMP_FORMAT_WITH_MS.setTimeZone(utc);
        TIMESTAMP_FORMAT_WITHOUT_MS.setTimeZone(utc);
        TIMESTAMP_FORMAT_WITH_MS_WITHOUT_TZ.setTimeZone(utc);
        TIMESTAMP_FORMAT_WITHOUT_MS_WITHOUT_TZ.setTimeZone(utc);
        TIMESTAMP_FORMATS_TO_TRY = new SimpleDateFormat[] {
            TIMESTAMP_FORMAT_WITH_MS, TIMESTAMP_FORMAT_WITHOUT_MS, TIMESTAMP_FORMAT_WITH_MS_WITHOUT_TZ, TIMESTAMP_FORMAT_WITHOUT_MS_WITHOUT_TZ
        };
    }
    private JsonUtils() {
    }

    public static JsonObject asJsonObject(final JsonValue value) {
        if (value == null || value instanceof JsonNull) {
            return null;
        }
        if (!(value instanceof JsonObject)) {
            throw new RuntimeException("JsonValue is not a JsonObject");
        }
        return (JsonObject) value;
    }

    public static JsonArray asJsonArray(final JsonValue value) {
        if (value == null || value instanceof JsonNull) {
            return null;
        }
        if (!(value instanceof JsonArray)) {
            throw new RuntimeException("JsonValue is not a JsonArray");
        }
        return (JsonArray) value;
    }

    public static String asString(final JsonValue value) {
        return asString(value, true, null);
    }

    public static String asString(final JsonValue value, final String defaultValue) {
        return asString(value, false, defaultValue);
    }

    public static long asLong(final JsonValue value) {
        return asLong(value, true, 0L);
    }

    public static long asLong(final JsonValue value, final long defaultValue) {
        return asLong(value, false, defaultValue);
    }

    public static int asInt(final JsonValue value) {
        return (int) asLong(value, true, 0L);
    }

    public static int asInt(final JsonValue value, final int defaultValue) {
        return (int) asLong(value, false, defaultValue);
    }

    public static double asDouble(final JsonValue value) {
        return asDouble(value, true, 0.0);
    }

    public static double asDouble(final JsonValue value, final double defaultValue) {
        return asDouble(value, false, defaultValue);
    }

    public static boolean asBoolean(final JsonValue value) {
        return asBoolean(value, true, false);
    }

    public static boolean asBoolean(final JsonValue value, final boolean defaultValue) {
        return asBoolean(value, false, defaultValue);
    }

    public static JsonValue objectLookup(final JsonValue value, final Object key) {
        return asJsonObject(value).get(key);
    }

    public static String objectLookupAsString(final JsonValue value, final Object key) {
        return asString(asJsonObject(value).get(key));
    }

    public static String objectLookupAsString(final JsonValue value, final Object key, final String defaultValue) {
        return asString(asJsonObject(value).get(key), defaultValue);
    }

    public static long objectLookupAsLong(final JsonValue value, final Object key) {
        return asLong(asJsonObject(value).get(key));
    }

    public static long objectLookupAsLong(final JsonValue value, final Object key, final long defaultValue) {
        return asLong(asJsonObject(value).get(key), defaultValue);
    }

    public static int objectLookupAsInt(final JsonValue value, final Object key) {
        return asInt(asJsonObject(value).get(key));
    }

    public static int objectLookupAsInt(final JsonValue value, final Object key, final int defaultValue) {
        return asInt(asJsonObject(value).get(key), defaultValue);
    }

    public static double objectLookupAsDouble(final JsonValue value, final Object key) {
        return asDouble(asJsonObject(value).get(key));
    }

    public static double objectLookupAsDouble(final JsonValue value, final Object key, final double defaultValue) {
        return asDouble(asJsonObject(value).get(key), defaultValue);
    }

    public static boolean objectLookupAsBoolean(final JsonValue value, final Object key) {
        return asBoolean(asJsonObject(value).get(key));
    }

    public static boolean objectLookupAsBoolean(final JsonValue value, final Object key, final boolean defaultValue) {
        return asBoolean(asJsonObject(value).get(key), defaultValue);
    }

    public static JsonArray objectLookupAsJsonArray(final JsonValue value, final Object key) {
        return asJsonArray(asJsonObject(value).get(key));
    }

    public static JsonObject objectLookupAsJsonObject(final JsonValue value, final Object key) {
        return asJsonObject(asJsonObject(value).get(key));
    }

    private static String asString(final JsonValue value, final boolean required, final String defaultValue) {
        if (value == null || value instanceof JsonNull) {
            if (required) {
                throw new RuntimeException("Missing required String");
            }
            return defaultValue;
        }
        if (!(value instanceof JsonString)) {
            throw new RuntimeException("JsonValue is not a JsonString");
        }
        return ((JsonString) value).getValue();
    }

    private static long asLong(final JsonValue value, final boolean required, final long defaultValue) {
        if (value == null || value instanceof JsonNull) {
            if (required) {
                throw new RuntimeException("Missing required number");
            }
            return defaultValue;
        }
        if (!(value instanceof JsonNumber)) {
            throw new RuntimeException("JsonValue is not a JsonNumber");
        }
        return ((JsonNumber) value).getValueAsLong();
    }

    private static double asDouble(final JsonValue value, final boolean required, final double defaultValue) {
        if (value == null || value instanceof JsonNull) {
            if (required) {
                throw new RuntimeException("Missing required number");
            }
            return defaultValue;
        }
        if (!(value instanceof JsonNumber)) {
            throw new RuntimeException("JsonValue is not a JsonNumber");
        }
        return ((JsonNumber) value).getValueAsDouble();
    }

    private static boolean asBoolean(final JsonValue value, final boolean required, final boolean defaultValue) {
        if (value == null || value instanceof JsonNull) {
            if (required) {
                throw new RuntimeException("Missing required boolean");
            }
            return defaultValue;
        }
        if (!(value instanceof JsonBoolean)) {
            throw new RuntimeException("JsonValue is not a JsonBoolean");
        }
        return ((JsonBoolean) value).getValue();
    }

    public static String toTimestamp(final Date date) {
        if (date == null) {
            return null;
        }
        synchronized (DATE_FORMAT_LOCK) {
            return TIMESTAMP_FORMAT_WITH_MS.format(date);
        }
    }

    public static Date fromTimestamp(final String timestampString) {
        if (timestampString == null) {
            return null;
        }
        synchronized (DATE_FORMAT_LOCK) {
            for (final SimpleDateFormat timestampFormat : TIMESTAMP_FORMATS_TO_TRY) {
                try {
                    return timestampFormat.parse(timestampString);
                } catch (final ParseException ignored) {
                }
            }
            throw new RuntimeException("Unparsable timestamp: " + timestampString);
        }
    }

}
