package no.shhsoft.json;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonException() {
        super();
    }

    public JsonException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JsonException(final String message) {
        super(message);
    }

    public JsonException(final Throwable cause) {
        super(cause);
    }

}
