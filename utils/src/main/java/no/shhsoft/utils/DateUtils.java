package no.shhsoft.utils;

import no.shhsoft.validation.Validate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class DateUtils {

    private static final SimpleDateFormat ISO_DATE_FORMAT;
    private static final SimpleDateFormat ISO_TIME_FORMAT;
    private static final SimpleDateFormat ISO_DATE_TIME_FORMAT;
    private static final SimpleDateFormat ISO_DATE_TIME_FORMAT_WITH_MS;
    private static final SimpleDateFormat RFC_1123_DATE_FORMAT;
    private static final SimpleDateFormat RFC_822_DATE_FORMAT;

    private DateUtils() {
        /* not to be instantiated */
    }

    static {
        ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault(Locale.Category.FORMAT));
        ISO_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT));
        ISO_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT));
        ISO_DATE_TIME_FORMAT_WITH_MS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault(Locale.Category.FORMAT));
        /*
         * The RFC_1123_DATE_FORMAT has been tested on Sun JDK 1.1.7, 1.1.8, 1.2.2, 1.3beta1 and 1.3 on
         * Windows NT IBM JDK 1.1.8 on Windows and GNU/Linux Microsoft JView 5.00.3167 on Windows NT
         */
        /*
         * Microsoft's VM insisted on printing "GMT+00:00" when using the "zzz" formatting code for
         * timezone, so we must hardcode "GMT". Microsoft's VM also used the Norwegian language for
         * day and month names, so we force the use of the US locale.
         */
        RFC_1123_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        RFC_1123_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        RFC_822_DATE_FORMAT = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'zzz", Locale.US);
        RFC_822_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private static String toString(final SimpleDateFormat df, final Date d) {
        if (d == null) {
            return null;
        }
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (df) {
            return df.format(d);
        }
    }

    private static Date toDate(final SimpleDateFormat df, final String s) {
        if (s == null) {
            return null;
        }
        final String s2 = s.trim();
        if (s2.length() == 0) {
            return null;
        }
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (df) {
            try {
                return df.parse(s2);
            } catch (final ParseException e) {
                throw new UncheckedParseException(e);
            }
        }
    }

    public static Date toDate(final int year, final int month, final int day, final int hours, final int minutes, final int seconds) {
        final Date date = toDate(year, month, day);
        setTime(date, hours, minutes, seconds);
        return date;
    }

    public static Date toDate(final int year, final int month, final int day) {
        Validate.isTrue(year > 1582, "Year must be after 1582");
        Validate.isTrue(month >= 1 && month <= 12, "Month must be [1, 12]");
        Validate.isTrue(day >= 1 && day <= 31, "Day must be [1, 31]");
        return new Date(year - 1900, month - 1, day);
    }

    public static void setTime(final Date date, final int hours, final int minutes, final int seconds) {
        Validate.notNull(date);
        Validate.isTrue(hours >= 0 && hours <= 23, "Hours must be [0, 23]");
        Validate.isTrue(minutes >= 0 && minutes <= 59, "Minutes must be [0, 59]");
        Validate.isTrue(seconds >= 0 && seconds <= 61, "Seconds must be [0, 61]");
        date.setHours(hours);
        date.setMinutes(minutes);
        date.setSeconds(seconds);
    }

    /**
     * Checks if a year is a leap year.
     *
     * @param year the four digit year to test.
     */
    public static boolean isLeapYear(final int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /**
     * Returns the number of days in a month for the given year.
     *
     * @param year the four digit year.
     * @param month 1 is January, 12 is December.
     */
    public static int getDaysInMonth(final int year, final int month) {
        final int[] d = {-1, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        if (isLeapYear(year) && month == 2) {
            return d[month] + 1;
        }
        return d[month];
    }

    public static String dateToString(final Date d) {
        return toString(ISO_DATE_FORMAT, d);
    }

    public static Date stringToDate(final String s) {
        return toDate(ISO_DATE_FORMAT, s);
    }

    public static String timeToString(final Date d) {
        return toString(ISO_TIME_FORMAT, d);
    }

    public static Date stringToTime(final String s) {
        return toDate(ISO_TIME_FORMAT, s);
    }

    public static String dateTimeToString(final Date d) {
        return toString(ISO_DATE_TIME_FORMAT, d);
    }

    public static String dateTimeToStringWithMs(final Date d) {
        return toString(ISO_DATE_TIME_FORMAT_WITH_MS, d);
    }

    public static Date stringToDateTime(final String s) {
        return toDate(ISO_DATE_TIME_FORMAT, s);
    }

    public static Date stringWithMsToDateTime(final String s) {
        return toDate(ISO_DATE_TIME_FORMAT_WITH_MS, s);
    }

    public static String dateToRfc1123String(final Date d) {
        return toString(RFC_1123_DATE_FORMAT, d);
    }

    public static Date rfc1123StringToDate(final String s) {
        return toDate(RFC_1123_DATE_FORMAT, s);
    }

    public static String dateToRfc822String(final Date d) {
        return toString(RFC_822_DATE_FORMAT, d);
    }

    public static Date rfc822StringToDate(final String s) {
        return toDate(RFC_822_DATE_FORMAT, s);
    }

    /* month starts from 1 */
    public static Date getStartDate(final int year, final int month, final int day) {
        final Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year >= 0 ? year : cal.getActualMinimum(Calendar.YEAR));
        cal.set(Calendar.MONTH, month >= 0 ? month - 1 : cal.getActualMinimum(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, day >= 0 ? day : cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    /* month starts from 1 */
    public static Date getEndDate(final int year, final int month, final int day) {
        final Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year >= 0 ? year : cal.getActualMaximum(Calendar.YEAR));
        cal.set(Calendar.MONTH, month >= 0 ? month - 1 : cal.getActualMaximum(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, day >= 0 ? day : cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public static Date getStartDate(final Date date) {
        if (date == null) {
            return null;
        }
        return getStartDate(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    public static Date getEndDate(final Date date) {
        if (date == null) {
            return null;
        }
        return getEndDate(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    /**
     * @return Four digit year
     */
    public static int getYear(final Date date) {
        return 1900 + date.getYear();
    }

    /**
     * @return Month of year: 1-12
     */
    public static int getMonth(final Date date) {
        return 1 + date.getMonth();
    }

    /**
     * @return Day of month: 1-31
     */
    public static int getDay(final Date date) {
        return date.getDate();
    }

    public static Calendar toCalendar(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

}
