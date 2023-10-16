package no.shhsoft.json;

import no.shhsoft.json.impl.generator.JsonGeneratorImpl;
import no.shhsoft.json.impl.parser.JsonParserImpl;
import no.shhsoft.json.model.JsonContainer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonParseAndGenerateTest {

    private final JsonGenerator jsonGenerator = new JsonGeneratorImpl();
    private final JsonParser jsonParser = new JsonParserImpl();

    private void assertParseAndGenerateEquals(final String json) {
        final JsonContainer container = jsonParser.parse(json);
        final String generated = jsonGenerator.generate(container);
        assertEquals(json, generated);
    }

    @Test
    public void test1() {
        assertParseAndGenerateEquals("[1]");
    }

    @Test
    public void test2() {
        assertParseAndGenerateEquals("[\"foo\\uabcdbar\"]");
        assertParseAndGenerateEquals("[\"foo\\u00cdbar\"]");
    }

}
