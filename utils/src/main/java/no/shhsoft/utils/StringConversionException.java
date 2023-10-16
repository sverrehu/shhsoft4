package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringConversionException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StringConversionException() {
    }

    public StringConversionException(final String message) {
        super(message);
    }

    public StringConversionException(final Throwable cause) {
        super(cause);
    }

    public StringConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
