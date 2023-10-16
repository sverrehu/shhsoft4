package no.shhsoft.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringConversionUtilsTest {

    public StringConversionUtilsTest() {
        StringConversionUtils.setConverter(StringConversionUtilsTest.class,
                                           new StringConverter<StringConversionUtilsTest>() {
            @Override
            public boolean isValid(final String s) {
                return true;
            }

            @Override
            public StringConversionUtilsTest convertFromString(final String s) {
                return StringConversionUtilsTest.this;
            }

            @Override
            public String convertToString(final StringConversionUtilsTest t) {
                return "bar";
            }
        });
    }

    @SuppressWarnings("boxing")
    @Test
    public void testAllFromString() {
        assertEquals("s", StringConversionUtils.convertFromString("s", String.class));
        assertEquals(123, StringConversionUtils.convertFromString("123", Integer.class));
        assertEquals(123, StringConversionUtils.convertFromString("123", Integer.TYPE));
        assertEquals(123L, StringConversionUtils.convertFromString("123", Long.class));
        assertEquals((short) 123, StringConversionUtils.convertFromString("123", Short.class));
        assertEquals((byte) 123, StringConversionUtils.convertFromString("123", Byte.class));
        assertEquals(true, StringConversionUtils.convertFromString("true", Boolean.class));
        assertEquals(123.45, StringConversionUtils.convertFromString("123.45", Double.class));
        assertEquals(123.45f, StringConversionUtils.convertFromString("123.45", Float.class));
        assertEquals(this, StringConversionUtils.convertFromString(
                                                  "anything", StringConversionUtilsTest.class));
    }

    @Test
    public void testAllToString() {
        assertEquals("s", StringConversionUtils.convertToString("s"));
        assertEquals("123", StringConversionUtils.convertToString(123));
        assertEquals("123", StringConversionUtils.convertToString(123L));
        assertEquals("123", StringConversionUtils.convertToString((short) 123));
        assertEquals("123", StringConversionUtils.convertToString((byte) 123));
        assertEquals("true", StringConversionUtils.convertToString(Boolean.TRUE));
        assertEquals("123.45", StringConversionUtils.convertToString(123.45));
        assertEquals("123.45", StringConversionUtils.convertToString(123.45f));
        assertEquals("bar", StringConversionUtils.convertToString(this));
    }

}
