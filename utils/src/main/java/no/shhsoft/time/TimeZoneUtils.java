package no.shhsoft.time;

import java.util.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class TimeZoneUtils {

    private static Set<String> timeZoneIds;

    private TimeZoneUtils() {
    }

    private static synchronized void populateTimeZoneIds() {
        if (timeZoneIds != null) {
            return;
        }
        timeZoneIds = new HashSet<>();
        synchronized (timeZoneIds) {
            timeZoneIds.addAll(Arrays.asList(TimeZone.getAvailableIDs()));
        }
    }

    public static boolean isValidTimeZoneId(final String timeZoneId) {
        if (timeZoneId == null) {
            return false;
        }
        populateTimeZoneIds();
        synchronized (timeZoneIds) {
            return timeZoneIds.contains(timeZoneId);
        }
    }

    public static TimeZone getTimeZone(final String timeZoneId) {
        if (!isValidTimeZoneId(timeZoneId)) {
            return null;
        }
        return TimeZone.getTimeZone(timeZoneId);
    }

    public static Calendar getNowForTimeZone(final String timeZoneId) {
        final TimeZone timeZone = getTimeZone(timeZoneId);
        if (timeZone == null) {
            return null;
        }
        final Calendar calendar = new GregorianCalendar(timeZone);
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar;
    }

}
