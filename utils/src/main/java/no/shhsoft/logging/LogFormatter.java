package no.shhsoft.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LogFormatter
extends Formatter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String NEWLINE = System.getProperty("line.separator", "\n");
    private final LogUserNameProvider userNameProvider;

    private static String getLogSourceDescription(final LogRecord record) {
        final StringBuilder sb = new StringBuilder();
        String className = record.getSourceClassName();
        final int idx = className.lastIndexOf('.');
        if (idx >= 0) {
            className = className.substring(idx + 1);
        }
        sb.append(className);
        sb.append('.');
        sb.append(record.getSourceMethodName());
        return sb.toString();
    }

    private static String getStackTraceDetails(final Throwable t) {
        if (t == null) {
            return "";
        }
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        printWriter.close();
        return NEWLINE + stringWriter.toString();
    }

    public LogFormatter(final LogUserNameProvider userNameProvider) {
        this.userNameProvider = userNameProvider;
    }

    @Override
    public String format(final LogRecord record) {
        final StringBuilder sb = new StringBuilder();
        synchronized (DATE_FORMAT) {
            sb.append(DATE_FORMAT.format(new Date()));
        }
        if (userNameProvider != null) {
            sb.append(" [");
            String userName = userNameProvider.getUserName();
            if (userName == null) {
                userName = "-";
            }
            sb.append(userName);
            sb.append(']');
        }
        sb.append(' ');
        sb.append(record.getLevel().getName());
        sb.append(" ");
        sb.append(getLogSourceDescription(record));
        sb.append(": ");
        sb.append(record.getMessage());
        sb.append(getStackTraceDetails(record.getThrown()));
        return sb.toString();
    }

}
