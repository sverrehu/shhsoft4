package no.shhsoft.json.manualtest;

import no.shhsoft.json.JsonParser;
import no.shhsoft.json.impl.parser.JsonParserImpl;
import no.shhsoft.json.model.*;
import org.junit.Assert;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonPerformanceTest {

    private JsonPerformanceTest() {
    }

    public static void main(final String[] args) {
        final String json = "{\n"
        + " \"a\": [ \"a\", 1, -31.4e-1, true, false, null ],"
        + " \"b\": null,"
        + " \"c\": 123,"
        + " \"d\": \"foo\","
        + " \"e\": { \"foo\" : \"bar\", \"gazonk\": -123 }"
        + " }";
        long t = System.currentTimeMillis();
        for (int q = 5000000; q >= 0; q--) {
            final JsonParser decoder = new JsonParserImpl();
            final JsonContainer container = decoder.parse(json);
            final JsonArray array = (JsonArray) ((JsonObject) container).get("a");
            Assert.assertEquals("a", ((JsonString) array.get(0)).getValue());
            Assert.assertEquals(1L, ((JsonNumber) array.get(1)).getValueAsLong());
            Assert.assertEquals(-3.14, ((JsonNumber) array.get(2)).getValueAsDouble(), 0.01);
            Assert.assertTrue(((JsonBoolean) array.get(3)).getValue());
            Assert.assertFalse(((JsonBoolean) array.get(4)).getValue());
            Assert.assertTrue(array.get(5) instanceof JsonNull);
            final JsonObject object = (JsonObject) ((JsonObject) container).get("e");
            Assert.assertEquals(-123L, ((JsonNumber) object.get("gazonk")).getValueAsLong());
        }
        t = System.currentTimeMillis() - t;
        System.out.println("Done in " + t + " ms");
    }

}
