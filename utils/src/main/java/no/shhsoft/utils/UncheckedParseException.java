package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UncheckedParseException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UncheckedParseException() {
    }

    public UncheckedParseException(final String message) {
        super(message);
    }

    public UncheckedParseException(final Throwable cause) {
        super(cause);
    }

    public UncheckedParseException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
