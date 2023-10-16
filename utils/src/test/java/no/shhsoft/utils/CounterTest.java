package no.shhsoft.utils;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CounterTest {

    private static final String V1 = "v1";
    private static final String V2 = "v2";

    @Test
    public void shouldReturnZeroForNoCounts() {
        assertEquals(0, new Counter<String>().getCount(V1));
    }

    @Test
    public void shouldReturnCorrectCounts() {
        final Counter<String> counter = new Counter<>();
        counter.increase(V1);
        counter.increase(V2);
        counter.increase(V1);
        counter.increase(V2);
        counter.increase(V1);
        assertEquals(3, counter.getCount(V1));
        assertEquals(2, counter.getCount(V2));
    }

    @Test
    public void shouldReturnEmptyMaxSetForNoCounts() {
        assertEquals(0, new Counter<String>().findHighestCountValues().size());
    }

    @Test
    public void shouldReturnCorrectMaxSetForJustOne() {
        final Counter<String> counter = new Counter<>();
        counter.increase(V1);
        counter.increase(V2);
        counter.increase(V1);
        counter.increase(V2);
        counter.increase(V1);
        final Set<String> expected = new HashSet<>();
        expected.add(V1);
        assertEquals(expected, counter.findHighestCountValues());
    }

    @Test
    public void shouldReturnCorrectMaxSetForMultiple() {
        final Counter<String> counter = new Counter<>();
        counter.increase(V1);
        counter.increase(V2);
        counter.increase(V1);
        counter.increase(V2);
        final Set<String> expected = new HashSet<>();
        expected.add(V1);
        expected.add(V2);
        assertEquals(expected, counter.findHighestCountValues());
    }

}
