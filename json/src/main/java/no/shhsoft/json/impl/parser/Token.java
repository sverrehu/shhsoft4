package no.shhsoft.json.impl.parser;

import no.shhsoft.json.model.JsonBoolean;
import no.shhsoft.json.model.JsonNull;
import no.shhsoft.json.model.JsonValue;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class Token {

    public static final Token BEGIN_OBJECT = new Token(Structural.BEGIN_OBJECT);
    public static final Token END_OBJECT = new Token(Structural.END_OBJECT);
    public static final Token BEGIN_ARRAY = new Token(Structural.BEGIN_ARRAY);
    public static final Token END_ARRAY = new Token(Structural.END_ARRAY);
    public static final Token COMMA = new Token(Structural.COMMA);
    public static final Token COLON = new Token(Structural.COLON);
    public static final Token TRUE = new Token(JsonBoolean.TRUE);
    public static final Token FALSE = new Token(JsonBoolean.FALSE);
    public static final Token NULL = new Token(JsonNull.NULL);
    static final Token INTERNAL_EOF = new Token((Structural) null);
    private final TokenType type;
    private JsonValue value;
    private Structural structural;

    private Token(final Structural structural) {
        type = TokenType.STRUCTURAL;
        this.structural = structural;
    }

    public Token(final JsonValue value) {
        type = TokenType.VALUE;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public JsonValue getValue() {
        if (type != TokenType.VALUE) {
            throw new RuntimeException("getValue called for a token that is not a value.");
        }
        return value;
    }

    public Structural getStructural() {
        if (type != TokenType.STRUCTURAL) {
            throw new RuntimeException("getStructural called for a token that is not an structural ("
                                       + type.toString() + ").");
        }
        return structural;
    }

    @Override
    public String toString() {
        switch (getType()) {
            case VALUE:
                return "VALUE: " + getValue();
            case STRUCTURAL:
                return "STRUCTURAL: " + getStructural();
            default:
                throw new RuntimeException("Unhandled token type " + getType());
        }
    }

}
