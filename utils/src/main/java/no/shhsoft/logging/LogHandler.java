package no.shhsoft.logging;

import java.util.logging.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LogHandler
extends Handler {

    public LogHandler(final LogUserNameProvider userNameProvider) {
        setFormatter(new LogFormatter(userNameProvider));
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(final LogRecord record) {
        final Formatter formatter = getFormatter();
        final String message;
        if (formatter != null) {
            message = formatter.format(record);
        } else {
            message = record.getMessage();
        }
        System.out.println(message);
    }

    public static synchronized void install(final LogUserNameProvider userNameProvider) {
        final Logger rootLogger = Logger.getLogger("");
        final Handler[] handlers = rootLogger.getHandlers();
        boolean found = false;
        for (final Handler handler : handlers) {
            if (handler instanceof LogHandler) {
                if (userNameProvider != null) {
                    handler.setFormatter(new LogFormatter(userNameProvider));
                }
                found = true;
            } else {
                handler.setLevel(Level.OFF);
            }
        }
        if (!found) {
            rootLogger.addHandler(new LogHandler(userNameProvider));
        }
    }

    public static void install() {
        install(null);
    }

}
