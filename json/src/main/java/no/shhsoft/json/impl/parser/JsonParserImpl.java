package no.shhsoft.json.impl.parser;

import no.shhsoft.json.JsonException;
import no.shhsoft.json.JsonParser;
import no.shhsoft.json.model.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonParserImpl
implements JsonParser {

    private static Token nextExistingToken(final JsonTokenizer tokenizer) {
        final Token token = tokenizer.nextToken();
        if (token == null) {
            throw new JsonException("Unexpected end of input.");
        }
        return token;
    }

    private static Token peekNextExistingToken(final JsonTokenizer tokenizer) {
        final Token token = tokenizer.peekNextToken();
        if (token == null) {
            throw new JsonException("Unexpected end of input.");
        }
        return token;
    }

    private static JsonObject parseObject(final JsonTokenizer tokenizer) {
        final JsonObject object = new JsonObject();
        for (;;) {
            Token token = nextExistingToken(tokenizer);
            if (token == Token.END_OBJECT) {
                break;
            }
            if (token.getType() != TokenType.VALUE) {
                throw new JsonException("Expected value token for object member name.");
            }
            final JsonValue nameValue = token.getValue();
            if (!(nameValue instanceof JsonString)) {
                throw new JsonException("Expected string value token for object member name.");
            }
            final String name = ((JsonString) nameValue).getValue();
            token = nextExistingToken(tokenizer);
            if (token != Token.COLON) {
                throw new JsonException("Expected `:' after object member name");
            }
            final JsonValue value = parseValue(tokenizer);
            object.put(name, value);
            token = nextExistingToken(tokenizer);
            if (token == Token.END_OBJECT) {
                break;
            }
            if (token != Token.COMMA) {
                throw new JsonException("Unexpected token `" + token + "'.");
            }
        }
        return object;
    }

    private static JsonArray parseArray(final JsonTokenizer tokenizer) {
        final JsonArray array = new JsonArray();
        for (;;) {
            if (peekNextExistingToken(tokenizer) == Token.END_ARRAY) {
                tokenizer.nextToken();
                break;
            }
            final JsonValue value = parseValue(tokenizer);
            array.add(value);
            if (peekNextExistingToken(tokenizer) == Token.END_ARRAY) {
                tokenizer.nextToken();
                break;
            }
            final Token token = tokenizer.nextToken();
            if (token != Token.COMMA) {
                throw new JsonException("Unexpected token `" + token + "'.");
            }
        }
        return array;
    }

    private static JsonValue parseValue(final JsonTokenizer tokenizer) {
        final Token token = tokenizer.nextToken();
        if (token.getType() == TokenType.VALUE) {
            return token.getValue();
        }
        if (token == Token.BEGIN_OBJECT) {
            return parseObject(tokenizer);
        } else if (token == Token.BEGIN_ARRAY) {
            return parseArray(tokenizer);
        }
        throw new JsonException("Unexpected token `" + token + "'.");
    }

    @Override
    public JsonContainer parse(final String s) {
        final JsonTokenizer tokenizer = new JsonTokenizer(s);
        final JsonValue value = parseValue(tokenizer);
        if (value == null) {
            throw new JsonException("There's nothing to parse.");
        }
        if (!(value instanceof JsonContainer)) {
            throw new JsonException("Text must contain either an object or an array.");
        }
        return (JsonContainer) value;
    }

}
