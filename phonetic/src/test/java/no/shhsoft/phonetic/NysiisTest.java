package no.shhsoft.phonetic;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class NysiisTest {

    private static final Nysiis nysiis = new Nysiis();

    private static void encodeSingle(final String input, final String expected) {
        final String[] result = nysiis.encode(input);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(expected, result[0]);
    }

    @Test
    @Ignore("Doesn't work -- unfinished.")
    public void shouldEncodeStuff() {
        encodeSingle("John", "JAN");
        encodeSingle("Smith", "SNATH");
    }

}
