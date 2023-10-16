package no.shhsoft.time;

import no.shhsoft.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class DateAndTimeFormatterTest {

    @Test
    public void shouldGenerateCorrectDates() {
        final Date date = DateUtils.toDate(2019, 3, 19);
        Assert.assertEquals("2019-03-19", DateAndTimeFormatter.formatDate(DateAndTimeStyle.ISO_8601, date));
        Assert.assertEquals("19.03.2019", DateAndTimeFormatter.formatDate(DateAndTimeStyle.NORWEGIAN, date));
        Assert.assertEquals("19/3 2019", DateAndTimeFormatter.formatDate(DateAndTimeStyle.SWEDISH, date));
        Assert.assertEquals("19.3.2019", DateAndTimeFormatter.formatDate(DateAndTimeStyle.DANISH, date));
    }

    @Test
    public void shouldGenerateCorrectTimes() {
        final Date date = DateUtils.toDate(2019, 3, 19, 13, 1, 2);
        Assert.assertEquals("13:01", DateAndTimeFormatter.formatTime(DateAndTimeStyle.ISO_8601, date));
        Assert.assertEquals("1301", DateAndTimeFormatter.formatTime(DateAndTimeStyle.NORWEGIAN, date));
        Assert.assertEquals("13.01", DateAndTimeFormatter.formatTime(DateAndTimeStyle.SWEDISH, date));
        Assert.assertEquals("13.01", DateAndTimeFormatter.formatTime(DateAndTimeStyle.DANISH, date));
    }

    @Test
    public void shouldGenerateCorrectDatesAndTimes() {
        final Date date = DateUtils.toDate(2019, 3, 19, 13, 1, 2);
        Assert.assertEquals("2019-03-19 13:01", DateAndTimeFormatter.formatDateAndTime(DateAndTimeStyle.ISO_8601, date));
        Assert.assertEquals("19.03.2019 1301", DateAndTimeFormatter.formatDateAndTime(DateAndTimeStyle.NORWEGIAN, date));
        Assert.assertEquals("19/3 2019 13.01", DateAndTimeFormatter.formatDateAndTime(DateAndTimeStyle.SWEDISH, date));
        Assert.assertEquals("19.3.2019 13.01", DateAndTimeFormatter.formatDateAndTime(DateAndTimeStyle.DANISH, date));
    }

    @Test
    public void shouldParseDates() {
        assertDateParses("2019-03-19", "2019-03-19");
        assertDateParses("2019-03-19", "19.03.2019");
        assertDateParses("2019-03-19", "19.3.2019");
        assertDateParses("2019-03-19", "19/3 2019");
        assertDateParses("2019-03-19", "19/03 2019");
        assertDateParses("2019-03-19", "19/3-2019");
    }

    private void assertDateParses(final String expectedIsoDate, final String dateString) {
        final Date date = DateAndTimeFormatter.parseDate(dateString);
        Assert.assertNotNull(date);
        final String actualIsoDate = DateUtils.dateToString(date);
        Assert.assertEquals(expectedIsoDate, actualIsoDate);
    }

}
