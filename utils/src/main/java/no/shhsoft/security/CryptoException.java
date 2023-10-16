package no.shhsoft.security;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public class CryptoException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CryptoException() {
    }

    public CryptoException(final String message) {
        super(message);
    }

    public CryptoException(final Throwable cause) {
        super(cause);
    }

    public CryptoException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
