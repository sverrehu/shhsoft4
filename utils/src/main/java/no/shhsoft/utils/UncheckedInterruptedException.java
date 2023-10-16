package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UncheckedInterruptedException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UncheckedInterruptedException() {
        super();
    }

    public UncheckedInterruptedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UncheckedInterruptedException(final String message) {
        super(message);
    }

    public UncheckedInterruptedException(final Throwable cause) {
        super(cause);
    }

}
