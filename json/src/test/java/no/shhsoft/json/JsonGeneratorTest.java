package no.shhsoft.json;

import no.shhsoft.json.impl.generator.JsonGeneratorImpl;
import no.shhsoft.json.model.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonGeneratorTest {

	private final JsonGenerator jsonGenerator = new JsonGeneratorImpl();

	@Test
	public void shouldEncodeVerySimpleObject() {
		final JsonObject object = new JsonObject();
		object.put("key", new JsonString("value"));
		final String string = jsonGenerator.generate(object);
		assertEquals("{\"key\":\"value\"}", string);
	}

	@Test
	public void shouldEncodeRatherSimpleArray() {
		final JsonArray array = new JsonArray();
		array.add(JsonBoolean.TRUE);
		array.add(JsonBoolean.FALSE);
		array.add(new JsonDouble(3.14));
		array.add(JsonLong.get(12345L));
		array.add(JsonNull.NULL);
		array.add(new JsonString("foo"));
		final String string = jsonGenerator.generate(array);
		assertEquals("[true,false,3.14,12345,null,\"foo\"]", string);
	}

	@Test
	public void shouldEncodeLessSimpleArray() {
		final JsonArray mainArray = new JsonArray();
		final JsonObject object = new JsonObject();
		object.put("k1", JsonLong.get(123));
		mainArray.add(object);
		final JsonArray array = new JsonArray();
		array.add(JsonBoolean.TRUE);
		mainArray.add(array);
		final String string = jsonGenerator.generate(mainArray);
		assertEquals("[{\"k1\":123},[true]]", string);
	}

}
