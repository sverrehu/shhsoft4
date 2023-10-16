package no.shhsoft.time;

import no.shhsoft.validation.Validate;

import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class YearWeek
implements Comparable<YearWeek> {

    private final int year;
    private final int week;  /* 1 - 53 */

    public static YearWeek fromDate(final Date date) {
        Validate.notNull(date);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return fromCalendar(calendar);
    }

    public static YearWeek fromCalendar(final Calendar calendar) {
        return CalendarUtils.getIsoWeekOfYear(calendar);
    }

    public YearWeek(final int year, final int week) {
        if (week < 1 || week > 53) {
            throw new IllegalArgumentException("Week must be from 1 to 53");
        }
        this.year = year;
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public int getWeek() {
        return week;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + week;
        result = prime * result + year;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final YearWeek other = (YearWeek) obj;
        if (week != other.week) {
            return false;
        }
        if (year != other.year) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final YearWeek other) {
        final int yearDiff = year - other.year;
        if (yearDiff != 0) {
            return yearDiff;
        }
        return week - other.week;
    }

    @Override
    public String toString() {
        return String.format("%04d-W%02d", year, week);
    }

}
