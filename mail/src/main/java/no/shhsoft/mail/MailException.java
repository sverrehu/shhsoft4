package no.shhsoft.mail;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class MailException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MailException() {
    }

    public MailException(final String message) {
        super(message);
    }

    public MailException(final Throwable cause) {
        super(cause);
    }

    public MailException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
