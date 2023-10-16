package no.shhsoft.time;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CalendarUtilsTest {

    /**
     * @param year Four digit year
     * @param month 1-12
     * @param day 1-31
     */
    private static Calendar day(final int year, final int month, final int day) {
        return new GregorianCalendar(year, month - 1, day);
    }

    private static int getDaysBetween(final Calendar cal1, final Calendar cal2) {
        return CalendarUtils.getDaysBetween(cal1, cal2);
    }

    private static int getWeeksBetween(final Calendar cal1, final Calendar cal2) {
        return CalendarUtils.getWeeksBetween(cal1, cal2);
    }

    private static void assertCorrectDayDiffBothWays(final int days, final Calendar cal1, final Calendar cal2) {
        assertEquals(days, getDaysBetween(cal1, cal2));
        assertEquals(-days, getDaysBetween(cal2, cal1));
    }

    @Test
    public void shouldCalculateNumberOfDaysBetween() {
        assertEquals(0, getDaysBetween(day(1968, 5, 29), day(1968, 5, 29)));
        assertCorrectDayDiffBothWays(1, day(2012, 6, 7), day(2012, 6, 8));
        /* Day count from timeanddate.com */
        assertCorrectDayDiffBothWays(16080, day(1968, 5, 29), day(2012, 6, 7));
        assertCorrectDayDiffBothWays(11005, day(1968, 5, 29), day(1998, 7, 16));
        /* Summer time */
        assertCorrectDayDiffBothWays(1, day(2012, 3, 24), day(2012, 3, 25));
        assertCorrectDayDiffBothWays(1, day(2012, 3, 25), day(2012, 3, 26));
        /* Winter time */
        assertCorrectDayDiffBothWays(1, day(2012, 10, 27), day(2012, 10, 28));
        assertCorrectDayDiffBothWays(1, day(2012, 10, 28), day(2012, 10, 29));
    }

    @Test
    public void shouldCalculateNumberOfWeeksBetween() {
        assertEquals(0, getWeeksBetween(day(1968, 5, 29), day(1968, 5, 29)));
        assertEquals(0, getWeeksBetween(day(1968, 5, 29), day(1968, 5, 30)));
        assertEquals(-1, getWeeksBetween(day(2012, 6, 8), day(2012, 6, 1)));
        assertEquals(1, getWeeksBetween(day(2012, 6, 1), day(2012, 6, 8)));
        /* Week count from timeanddate.com */
        assertEquals(2297, getWeeksBetween(day(1968, 5, 29), day(2012, 6, 7)));
        assertEquals(-2297, getWeeksBetween(day(2012, 6, 7), day(1968, 5, 29)));
        assertEquals(1572, getWeeksBetween(day(1968, 5, 29), day(1998, 7, 16)));
        assertEquals(-1572, getWeeksBetween(day(1998, 7, 16), day(1968, 5, 29)));
    }

    private static void assertExpectedYearAndWeek(final int expectedYear, final int expectedWeek, final int year, final int monthFromOne, final int day) {
        final Calendar cal = new GregorianCalendar(year, monthFromOne - 1, day);
        final YearWeek yearWeek = CalendarUtils.getIsoWeekOfYear(cal);
        assertEquals(expectedYear, yearWeek.getYear());
        assertEquals(expectedWeek, yearWeek.getWeek());
    }

    @Test
    public void shouldCalculateCorrectWeekNumbers() {
        assertExpectedYearAndWeek(2003, 52, 2003, 12, 28);
        assertExpectedYearAndWeek(2004, 1, 2003, 12, 29);
        assertExpectedYearAndWeek(2004, 1, 2003, 12, 30);
        assertExpectedYearAndWeek(2004, 1, 2003, 12, 31);
        assertExpectedYearAndWeek(2004, 1, 2004, 1, 1);
        assertExpectedYearAndWeek(2004, 1, 2004, 1, 2);
        assertExpectedYearAndWeek(2004, 1, 2004, 1, 3);
        assertExpectedYearAndWeek(2004, 1, 2004, 1, 4);
        assertExpectedYearAndWeek(2004, 2, 2004, 1, 5);

        assertExpectedYearAndWeek(2004, 52, 2004, 12, 26);
        assertExpectedYearAndWeek(2004, 53, 2004, 12, 27);
        assertExpectedYearAndWeek(2004, 53, 2004, 12, 28);
        assertExpectedYearAndWeek(2004, 53, 2004, 12, 29);
        assertExpectedYearAndWeek(2004, 53, 2004, 12, 30);
        assertExpectedYearAndWeek(2004, 53, 2004, 12, 31);
        assertExpectedYearAndWeek(2004, 53, 2005, 1, 1);
        assertExpectedYearAndWeek(2004, 53, 2005, 1, 2);
        assertExpectedYearAndWeek(2005, 1, 2005, 1, 3);
    }

}
