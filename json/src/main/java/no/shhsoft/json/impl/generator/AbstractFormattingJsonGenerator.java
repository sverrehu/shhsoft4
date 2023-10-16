package no.shhsoft.json.impl.generator;

import no.shhsoft.json.JsonException;
import no.shhsoft.json.JsonGenerator;
import no.shhsoft.json.model.*;

import java.util.Map;

/**
 * Mostly duplicated from JsonGeneratorImpl.  Don't want to add lots of seldom used
 * paremeter passing for potential performance loss.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractFormattingJsonGenerator
implements JsonGenerator {

    protected abstract String getNewline();

    protected abstract String getIndent(int level);

    protected abstract String getPreamble();

    protected abstract String getPostamble();

    @SuppressWarnings("SameParameterValue")
    protected abstract String decorateNull(String s);

    protected abstract String decorateKey(String s);

    protected abstract String decorateString(String s);

    protected abstract String decorateBoolean(String s);

    protected abstract String decorateNumber(String s);

    protected abstract String decorateOperator(String s);

    private String decorateOperator(final char c) {
        return decorateOperator(String.valueOf(c));
    }

    private void breakLine(final int indentLevel, final StringBuilder sb) {
        if (indentLevel < 0) {
            return;
        }
        sb.append(getNewline());
        sb.append(getIndent(indentLevel));
    }

    private void encodeValue(final int indentLevel, final StringBuilder sb, final JsonValue value) {
        if (value instanceof JsonContainer) {
            encodeContainer(indentLevel, sb, (JsonContainer) value);
        } else if (value instanceof JsonNumber) {
            sb.append(decorateNumber(JsonGeneratorImpl.encodeNumber((JsonNumber) value)));
        } else if (value instanceof JsonBoolean) {
            sb.append(decorateBoolean(JsonGeneratorImpl.encodeBoolean((JsonBoolean) value)));
        } else if (value instanceof JsonString) {
            sb.append(decorateString(JsonGeneratorImpl.encodeString((JsonString) value)));
        } else if (value instanceof JsonNull) {
            sb.append(decorateNull("null"));
        } else {
            throw new JsonException("Unhandled JsonValue type: " + value.getClass().getName());
        }
    }

    private void encodeObject(final int indentLevel, final StringBuilder sb, final JsonObject object) {
        sb.append(decorateOperator('{'));
        breakLine(indentLevel + 1, sb);
        boolean needComma = false;
        for (final Map.Entry<String, JsonValue> entry : object.entrySet()) {
            if (needComma) {
                sb.append(decorateOperator(','));
                breakLine(indentLevel + 1, sb);
            } else {
                needComma = true;
            }
            sb.append(decorateKey(JsonGeneratorImpl.toJsonString(entry.getKey())));
            sb.append(decorateOperator(" : "));
            encodeValue(indentLevel + 1, sb, entry.getValue());
        }
        breakLine(indentLevel, sb);
        sb.append(decorateOperator('}'));
    }

    private void encodeArray(final int indentLevel, final StringBuilder sb, final JsonArray array) {
        sb.append(decorateOperator('['));
        breakLine(indentLevel + 1, sb);
        boolean needComma = false;
        for (final JsonValue value : array) {
            if (needComma) {
                sb.append(decorateOperator(','));
                breakLine(indentLevel + 1, sb);
            } else {
                needComma = true;
            }
            encodeValue(indentLevel + 1, sb, value);
        }
        breakLine(indentLevel, sb);
        sb.append(decorateOperator(']'));
    }

    private void encodeContainer(final int indentLevel, final StringBuilder sb, final JsonContainer container) {
        if (container instanceof JsonObject) {
            encodeObject(indentLevel, sb, (JsonObject) container);
        } else if (container instanceof JsonArray) {
            encodeArray(indentLevel, sb, (JsonArray) container);
        } else {
            throw new JsonException("Unhandled JsonContainer type: " + container.getClass().toString());
        }
    }

    @Override
    public final String generate(final JsonContainer objectOrArray) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getPreamble());
        encodeContainer(0, sb, objectOrArray);
        sb.append(getPostamble());
        return sb.toString();
    }

}
