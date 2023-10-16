package no.shhsoft.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LogUtils {

    private LogUtils() {
    }

    /**
     * @return <code>null</code> if no matching level is found.
     */
    public static Level levelFromString(final String s) {
        if (s == null) {
            return null;
        }
        final String levelString = s.trim().toUpperCase();
        if ("OFF".equals(levelString)) {
            return Level.OFF;
        }
        if ("SEVERE".equals(levelString)) {
            return Level.SEVERE;
        }
        if ("WARNING".equals(levelString)) {
            return Level.WARNING;
        }
        if ("INFO".equals(levelString)) {
            return Level.INFO;
        }
        if ("CONFIG".equals(levelString)) {
            return Level.CONFIG;
        }
        if ("FINE".equals(levelString)) {
            return Level.FINE;
        }
        if ("FINER".equals(levelString)) {
            return Level.FINER;
        }
        if ("FINEST".equals(levelString)) {
            return Level.FINEST;
        }
        if ("ALL".equals(levelString)) {
            return Level.ALL;
        }
        return null;
    }

    public static void setLogLevel(final Level level) {
        Logger.getLogger("").setLevel(level);
    }

}

