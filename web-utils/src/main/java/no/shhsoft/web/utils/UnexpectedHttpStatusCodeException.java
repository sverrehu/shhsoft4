package no.shhsoft.web.utils;

import no.shhsoft.utils.StringUtils;
import no.shhsoft.utils.UncheckedIoException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UnexpectedHttpStatusCodeException
extends UncheckedIoException {

    private final int statusCode;
    private final byte[] messageBody;

    public UnexpectedHttpStatusCodeException(final int statusCode, final byte[] messageBody, final String message) {
        super("Unexpected HTTP status code: " + statusCode + "."
              + (message != null ? " " + message : "")
              + (messageBody != null && messageBody.length > 0 ? "\n" + StringUtils.newStringUtf8(messageBody) : ""));
        this.statusCode = statusCode;
        this.messageBody = messageBody;
    }

    public UnexpectedHttpStatusCodeException(final int statusCode, final byte[] messageBody) {
        this(statusCode, messageBody, null);
    }

    public UnexpectedHttpStatusCodeException(final int statusCode) {
        this(statusCode, null, null);
    }

    public UnexpectedHttpStatusCodeException(final int statusCode, final String message) {
        this(statusCode, null, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getMessageBody() {
        return messageBody;
    }

}
