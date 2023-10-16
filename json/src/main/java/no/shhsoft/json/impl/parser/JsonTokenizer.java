package no.shhsoft.json.impl.parser;

import no.shhsoft.json.JsonException;
import no.shhsoft.json.model.JsonDouble;
import no.shhsoft.json.model.JsonLong;
import no.shhsoft.json.model.JsonNumber;
import no.shhsoft.json.model.JsonString;
import no.shhsoft.utils.HexUtils;

/**
 * Not thread safe.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class JsonTokenizer {

    private static final char EOF = Character.MIN_VALUE;
    private boolean allowComments = false;
    private final char[] chars;
    private int idx;
    private Token nextToken;

    private void error(final String message) {
        throw new JsonException(message);
    }

    private char nextChar() {
        if (idx >= chars.length - 1) {
            ++idx;
            return EOF;
        }
        return chars[++idx];
    }

    private char peekNextChar() {
        if (idx >= chars.length - 1) {
            return EOF;
        }
        return chars[idx + 1];
    }

    private char currChar() {
        if (idx >= chars.length) {
            return EOF;
        }
        return chars[idx];
    }

    private boolean isLiteralChar(final char c) {
        return Character.isLetter(c);
    }

    private void skipWhitespace() {
        for (;;) {
            final char c = currChar();
            if (allowComments && c == '/') {
                final char nc = peekNextChar();
                if (nc == '/') {
                    while (currChar() != EOF && currChar() != '\n') {
                        nextChar();
                    }
                } else if (nc == '*') {
                    nextChar();
                    nextChar();
                    while (currChar() != EOF) {
                        if (currChar() == '*' && peekNextChar() == '/') {
                            break;
                        }
                        nextChar();
                    }
                    nextChar();
                } else {
                    break;
                }
            } else if (!Character.isWhitespace(c)) {
                break;
            }
            nextChar();
        }
    }

    private String scanLiteral() {
        final StringBuilder sb = new StringBuilder();
        sb.append(currChar());
        char c;
        while ((c = nextChar()) != EOF) {
            if (!isLiteralChar(c)) {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private JsonNumber scanNumber() {
        double number = 0.0;
        double divider = 0.1;
        boolean dotSeen = false;
        boolean isReal = false;
        boolean isNegative = false;
        if (currChar() == '-') {
            isNegative = true;
            nextChar();
        }
        char c;
        while ((c = currChar()) != EOF) {
            if (c == '.') {
                dotSeen = true;
                isReal = true;
            } else if (c == 'e' || c == 'E') {
                isReal = true;
                c = nextChar();
                if (c == EOF) {
                    error("Dangling exponential character when parsing number.");
                }
                double sign = 1.0;
                if (c == '-') {
                    sign = -1.0;
                    c = nextChar();
                    if (c == EOF) {
                        error("Dangling negative exponential character when parsing number.");
                    }
                }
                final double exp = scanNumber().getValueAsDouble();
                number = number * Math.pow(10.0, sign * exp);
                break;
            } else if (c >= '0' && c <= '9') {
                final double digit = c - '0';
                if (dotSeen) {
                    number += digit * divider;
                    divider /= 10.0;
                } else {
                    number = number * 10.0 + digit;
                }
            } else {
                break;
            }
            nextChar();
        }
        if (!isReal && (number > Integer.MAX_VALUE || number < Integer.MIN_VALUE)) {
            isReal = true;
        }
        if (isNegative) {
            number = -number;
        }
        if (isReal) {
            return new JsonDouble(number);
        }
        return JsonLong.get((int) number);
    }

    private char scan4DigitHexChar() {
        int value = 0;
        for (int q = 4 - 1; q >= 0; q--) {
            final char c = nextChar();
            if (c == EOF) {
                error("Unexpected end of input.");
            }
            value = value * 16 + HexUtils.parseHexDigit(c);
        }
        return (char) value;
    }

    private JsonString scanString() {
        final StringBuilder sb = new StringBuilder();
        boolean endSeen = false;
        char c;
        while ((c = nextChar()) != EOF) {
            if (c == '\\') {
                c = nextChar();
                if (c == EOF) {
                    error("Unexpected end of input.");
                }
                switch (c) {
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        sb.append(scan4DigitHexChar());
                        break;
                    default:
                        sb.append(c);
                }
            } else if (c == '"') {
                endSeen = true;
                nextChar();
                break;
            } else if (c == '\r' || c == '\n') {
                error("Multi-line strings not supported.");
            } else {
                sb.append(c);
            }
        }
        if (!endSeen) {
            error("End of input in the middle of a string.");
        }
        return new JsonString(sb.toString());
    }

    private Token internalGetNextToken() {
        skipWhitespace();
        final char c = currChar();
        if (c == EOF) {
            return Token.INTERNAL_EOF;
        }
        if (isLiteralChar(c)) {
            final String word = scanLiteral();
            if ("true".compareTo(word) == 0) {
                return Token.TRUE;
            }
            if ("false".compareTo(word) == 0) {
                return Token.FALSE;
            }
            if ("null".compareTo(word) == 0) {
                return Token.NULL;
            }
            throw new JsonException("Unexpected literal value `" + word + "'");
        }
        if (c == '.' || (c >= '0' && c <= '9') || c == '-') {
            return new Token(scanNumber());
        }
        if (c == '"') {
            return new Token(scanString());
        }
        nextChar();
        if (c == '{') {
            return Token.BEGIN_OBJECT;
        }
        if (c == '}') {
            return Token.END_OBJECT;
        }
        if (c == '[') {
            return Token.BEGIN_ARRAY;
        }
        if (c == ']') {
            return Token.END_ARRAY;
        }
        if (c == ',') {
            return Token.COMMA;
        }
        if (c == ':') {
            return Token.COLON;
        }
        error("Unexpected character `" + c + "'");
        /* return statement just to please the compiler */
        return Token.INTERNAL_EOF;
    }

    public JsonTokenizer(final String s) {
        chars = s.toCharArray();
        idx = -1;
        nextChar();
    }

    public Token nextToken() {
        Token token = nextToken;
        if (token == null) {
            token = internalGetNextToken();
        } else {
            nextToken = null;
        }
        if (token == Token.INTERNAL_EOF) {
            return null;
        }
        return token;
    }

    public Token peekNextToken() {
        Token token = nextToken;
        if (token == null) {
            token = internalGetNextToken();
            nextToken = token;
        }
        if (token == Token.INTERNAL_EOF) {
            return null;
        }
        return token;
    }

    public void setAllowComments(final boolean allowComments) {
        this.allowComments = allowComments;
    }

}
