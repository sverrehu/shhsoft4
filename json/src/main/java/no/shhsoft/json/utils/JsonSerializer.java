package no.shhsoft.json.utils;

import no.shhsoft.json.model.*;
import no.shhsoft.utils.ReflectionUtils;
import no.shhsoft.validation.Validate;

import java.beans.Transient;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonSerializer {

    private JsonSerializer() {
    }

    public static JsonContainer toJsonContainer(final Object value) {
        Validate.notNull(value);
        final JsonValue jsonValue = toJson(value);
        if (!(jsonValue instanceof JsonContainer)) {
            throw new RuntimeException("Value is not a JsonContainer, but " + jsonValue.getClass().getName());
        }
        return (JsonContainer) jsonValue;
    }

    private static JsonValue toJsonObject(final Object value) {
        if (value == null) {
            return JsonNull.get();
        }
        final JsonObject jsonObject = new JsonObject();
        for (final Method getter : ReflectionUtils.findAllGetters(value.getClass())) {
            if (getter.getAnnotation(Transient.class) != null) {
                continue;
            }
            final String propertyName = ReflectionUtils.getPropertyNameFromGetterMethodName(getter.getName());
            final Object propertyValue;
            try {
                propertyValue = ReflectionUtils.callGetter(value, propertyName);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            final JsonValue jsonValue = toJson(propertyValue);
            jsonObject.put(propertyName, jsonValue);
        }
        return jsonObject;
    }

    private static JsonValue toJsonArray(final Object[] values) {
        if (values == null) {
            return JsonNull.get();
        }
        final JsonArray jsonArray = new JsonArray();
        for (final Object value : values) {
            jsonArray.add(toJson(value));
        }
        return jsonArray;
    }

    private static JsonValue toJson(final Object value) {
        if (value == null) {
            return JsonNull.get();
        } else if (value instanceof String) {
            return JsonString.get((String) value);
        } else if (value instanceof Integer) {
            return JsonLong.get((Integer) value);
        } else if (value instanceof Long) {
            return JsonLong.get((Long) value);
        } else if (value instanceof Boolean) {
            return JsonBoolean.get((Boolean) value);
        } else if (value instanceof Double) {
            return JsonDouble.get((Double) value);
        } else if (value instanceof Float) {
            return JsonDouble.get((Float) value);
        } else if (value instanceof Date) {
            return JsonString.get((Date) value);
        } else if (value.getClass().isArray()) {
            return toJsonArray((Object[]) value);
        } else {
            return toJsonObject(value);
        }
    }

}
