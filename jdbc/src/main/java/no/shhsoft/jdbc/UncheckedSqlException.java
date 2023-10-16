package no.shhsoft.jdbc;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public class UncheckedSqlException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UncheckedSqlException() {
    }

    public UncheckedSqlException(final String message) {
        super(message);
    }

    public UncheckedSqlException(final Throwable cause) {
        super(cause);
    }

    public UncheckedSqlException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
