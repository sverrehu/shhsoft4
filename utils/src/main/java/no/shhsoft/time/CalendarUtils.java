package no.shhsoft.time;

import no.shhsoft.validation.Validate;

import java.util.Calendar;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CalendarUtils {

    private CalendarUtils() {
    }

    /**
     * Calculates the first day of week for the week in which
     * the given date is found.  If the given date is already the
     * first day of the week, (a copy of) that day is returned.
     *
     * @param      cal     a day in the week.  this object is not
     *                     changed.
     * @return     a newly created <CODE>Calendar</CODE>.
     */
    public static Calendar firstDayOfWeek(final Calendar cal) {
        final Calendar ret = (Calendar) cal.clone();
        final int firstDay = ret.getFirstDayOfWeek();
        while (ret.get(Calendar.DAY_OF_WEEK) != firstDay) {
            ret.add(Calendar.DAY_OF_WEEK, -1);
        }
        return ret;
    }

    private static Calendar getMidDayCopy(final Calendar cal) {
        final Calendar calCopy = (Calendar) cal.clone();
        calCopy.set(Calendar.HOUR_OF_DAY, 12);
        calCopy.set(Calendar.MINUTE, 0);
        calCopy.set(Calendar.SECOND, 0);
        calCopy.set(Calendar.MILLISECOND, 0);
        return calCopy;
    }

    private static long getMsBetween(final Calendar cal1, final Calendar cal2) {
        final Calendar cal1copy = getMidDayCopy(cal1);
        final Calendar cal2copy = getMidDayCopy(cal2);
        final long t1 = cal1copy.getTime().getTime();
        final long t2 = cal2copy.getTime().getTime();
        return t2 - t1;
    }

    /**
     * Calculates the number of days one has to step to get from one
     * date to another.
     *
     * @param      cal1    the start date.
     * @param      cal2    the end date.
     * @return     <CODE>0</CODE> if the two dates represent the same day.
     *             <CODE>&gt;0</CODE> if the end date is in the future
     *             compared to the start date, or <CODE>&lt;0</CODE> if
     *             it is in the past.  the absolute value is the number
     *             of days to step.
     */
    public static int getDaysBetween(final Calendar cal1, final Calendar cal2) {
        final long diff = getMsBetween(cal1, cal2);
        long absDiff = Math.abs(diff);
        /* Learning the hard way, we now know that a day is not 24
         * hours when switching to or from daylight saving time.  To
         * avoid integer division that truncates "almost a day" to "no
         * day", we add a couple of hours to the difference. */
        absDiff += 2L * 60L * 60L * 1000L;
        final int days = (int) (absDiff / (24L * 60L * 60L * 1000L));
        return diff >= 0L ? days : -days;
    }

    /**
     * Calculates the number of weeks between two dates.  The calculation
     * is done on calendar week boundaries, thus there is one week between
     * a saturday and the following monday, even if the number of days
     * is less than seven.  The week boundary day is taken from
     * <CODE>Calendar.firstDayOfWeek()</CODE>, which is a <CODE>Locale</CODE>
     * specific value.
     *
     * @param      cal1    the start date.
     * @param      cal2    the end date.
     * @return     <CODE>0</CODE> if the two dates are within the same
     *             week.  <CODE>&gt;0</CODE> if the end date is in the future
     *             compared to the start date, or <CODE>&lt;0</CODE> if
     *             it is in the past.  the absolute value is the number
     *             of calendar week boundaries crossed.
     */
    public static int getWeeksBetween(final Calendar cal1, final Calendar cal2) {
        final long diff = getMsBetween(cal1, cal2);
        long absDiff = Math.abs(diff);
        /* Learning the hard way, we now know that a day is not 24
         * hours when switching to or from daylight saving time.  To
         * avoid integer division that truncates "almost a week" to
         * "no week", we add a couple of hours to the difference. */
        absDiff += 2L * 60L * 60L * 1000L;
        final int weeks = (int) (absDiff / (7L * 24L * 60L * 60L * 1000L));
        return diff >= 0L ? weeks : -weeks;
    }

    public static boolean isSameDay(final Calendar calendar1, final Calendar calendar2) {
        if (calendar1 == null || calendar2 == null) {
            return false;
        }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
            && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
            && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static YearWeek getIsoWeekOfYear(final Calendar cal) {
        Validate.notNull(cal);
        final Calendar c = (Calendar) cal.clone();
        /* Ensure ISO 8601 compatible calculations. Some locales will already do this. */
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(4);
        final int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
        int year = c.get(Calendar.YEAR);
        if (weekOfYear == 53 && c.get(Calendar.MONTH) == Calendar.JANUARY) {
            year -= 1;
        } else if (weekOfYear == 1 && c.get(Calendar.MONTH) == Calendar.DECEMBER) {
            year += 1;
        }
        return new YearWeek(year, weekOfYear);
    }

}
