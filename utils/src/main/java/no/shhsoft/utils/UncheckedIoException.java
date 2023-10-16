package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public class UncheckedIoException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UncheckedIoException() {
    }

    public UncheckedIoException(final String message) {
        super(message);
    }

    public UncheckedIoException(final Throwable cause) {
        super(cause);
    }

    public UncheckedIoException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
