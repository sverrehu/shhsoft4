package no.shhsoft.time;

import no.shhsoft.utils.StringUtils;
import no.shhsoft.validation.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class DateAndTimeFormatter {

    private static final DateAndTimeStyle DEFAULT_STYLE = DateAndTimeStyle.NORWEGIAN;
    private static final Map<DateAndTimeStyle, SimpleDateFormat> DATE_FORMATS = new HashMap<>();
    private static final Map<DateAndTimeStyle, SimpleDateFormat> TIME_FORMATS = new HashMap<>();
    private static final Map<DateAndTimeStyle, SimpleDateFormat> DATE_AND_TIME_FORMATS = new HashMap<>();

    static {
        DATE_FORMATS.put(DateAndTimeStyle.ISO_8601, new SimpleDateFormat("yyyy-MM-dd"));
        TIME_FORMATS.put(DateAndTimeStyle.ISO_8601, new SimpleDateFormat("HH:mm"));
        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.ISO_8601, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
        DATE_FORMATS.put(DateAndTimeStyle.NORWEGIAN, new SimpleDateFormat("dd.MM.yyyy"));
        TIME_FORMATS.put(DateAndTimeStyle.NORWEGIAN, new SimpleDateFormat("HHmm"));
        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.NORWEGIAN, new SimpleDateFormat("dd.MM.yyyy HHmm"));
        DATE_FORMATS.put(DateAndTimeStyle.DANISH, new SimpleDateFormat("d.M.yyyy"));
        TIME_FORMATS.put(DateAndTimeStyle.DANISH, new SimpleDateFormat("HH.mm"));
        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.DANISH, new SimpleDateFormat("d.M.yyyy HH.mm"));
        DATE_FORMATS.put(DateAndTimeStyle.SWEDISH, new SimpleDateFormat("d/M yyyy"));
        TIME_FORMATS.put(DateAndTimeStyle.SWEDISH, new SimpleDateFormat("HH.mm"));
        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.SWEDISH, new SimpleDateFormat("d/M yyyy HH.mm"));

        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.ISO_8601_SHORT_TIME, new SimpleDateFormat("yyyy-MM-dd HHmm"));
        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.ISO_8601_DOT_TIME, new SimpleDateFormat("yyyy-MM-dd HH.mm"));
        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.NORWEGIAN_COLON_TIME, new SimpleDateFormat("dd.MM.yyyy HH:mm"));
        DATE_AND_TIME_FORMATS.put(DateAndTimeStyle.NORWEGIAN_DOT_TIME, new SimpleDateFormat("dd.MM.yyyy HH.mm"));
        TIME_FORMATS.put(DateAndTimeStyle.NORWEGIAN_DOT_TIME, new SimpleDateFormat("HH.mm"));
        DATE_FORMATS.put(DateAndTimeStyle.SWEDISH_WITH_DASH_BEFORE_YEAR, new SimpleDateFormat("d/M-yyyy"));
    }

    private DateAndTimeFormatter() {
    }

    @SuppressWarnings("deprecation")
    private static boolean hasNoTime(final Date date) {
        if (date == null) {
            return false;
        }
        return date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0;
    }

    private static SimpleDateFormat get(final DateAndTimeStyle style, final Map<DateAndTimeStyle, SimpleDateFormat> map) {
        synchronized (map) {
            return map.get(style != null ? style : DEFAULT_STYLE);
        }
    }

    private static String format(final SimpleDateFormat format, final Date date) {
        if (date == null) {
            return null;
        }
        synchronized (format) {
            return format.format(date);
        }
    }

    private static Date parseToNull(final SimpleDateFormat format, final String dateAndOrTimeString) {
        synchronized (format) {
            try {
                return format.parse(dateAndOrTimeString);
            } catch (final ParseException e) {
                return null;
            }
        }
    }

    private static Date parseToNull(final Map<DateAndTimeStyle, SimpleDateFormat> formats, final String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }
        final String ds = dateString.trim();
        synchronized (formats) {
            for (final SimpleDateFormat format : formats.values()) {
                final Date date = parseToNull(format, ds);
                if (date != null) {
                    return date;
                }
            }
        }
        return null;
    }

    private static Date parseDateToNull(final String dateString) {
        return parseToNull(DATE_FORMATS, dateString);
    }

    private static Date parseTimeToNull(final String timeString) {
        return parseToNull(TIME_FORMATS, timeString);
    }

    private static Date parseDateAndTimeToNull(final String dateAndTimeString) {
        return parseToNull(DATE_AND_TIME_FORMATS, dateAndTimeString);
    }

    private static boolean hasAcceptedYear(final Date date) {
        if (date == null) {
            return false;
        }
        @SuppressWarnings("deprecation")
        final int year = date.getYear() + 1900;
        return year >= 1900 && year <= 2200;
    }

    public static boolean isValidDate(final String dateString) {
        return hasAcceptedYear(parseDateToNull(dateString));
    }

    public static void assertValidDate(final String dateString) {
        if (!isValidDate(dateString)) {
            throw new ValidationException("Invalid date `" + dateString + "'");
        }
    }

    public static Date parseDate(final String dateString) {
        assertValidDate(dateString);
        return parseDateToNull(dateString);
    }

    public static boolean isValidTime(final String timeString) {
        return parseTimeToNull(timeString) != null;
    }

    public static void assertValidTime(final String timeString) {
        if (!isValidTime(timeString)) {
            throw new ValidationException("Invalid time `" + timeString + "'");
        }
    }

    public static Date parseTime(final String timeString) {
        assertValidTime(timeString);
        return parseTimeToNull(timeString);
    }

    public static boolean isValidDateAndTime(final String dateAndTimeString) {
        return hasAcceptedYear(parseDateAndTimeToNull(dateAndTimeString));
    }

    public static void assertValidDateAndTime(final String dateAndTimeString) {
        if (!isValidDateAndTime(dateAndTimeString)) {
            throw new ValidationException("Invalid date and time `" + dateAndTimeString + "'");
        }
    }

    public static Date parseDateAndTime(final String dateAndTimeString) {
        assertValidDateAndTime(dateAndTimeString);
        return parseDateAndTimeToNull(dateAndTimeString);
    }

    public static String formatDate(final DateAndTimeStyle style, final Date date) {
        return format(get(style, DATE_FORMATS), date);
    }

    public static String formatTime(final DateAndTimeStyle style, final Date date) {
        return format(get(style, TIME_FORMATS), date);
    }

    public static String formatDateAndTime(final DateAndTimeStyle style, final Date date) {
        if (hasNoTime(date)) {
            return formatDate(style, date);
        }
        return format(get(style, DATE_AND_TIME_FORMATS), date);
    }

}
