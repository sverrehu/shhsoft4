package no.shhsoft.net;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class MessageTest
extends TestCase {

    public void testMisc() {
        final List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        final String[] array = new String[] {"foo", "bar"};
        final Date now = new Date();
        final Message m = new Message();
        m.putBoolean(true);
        m.putByte((byte) 0x80);
        m.putShort((short) 0x8040);
        m.putInt(0x80402010);
        m.putLong(0x8040201088442211L);
        m.putDate(null);
        m.putDate(now);
        m.putString(null);
        m.putString("foo");
        m.putStringList(null);
        m.putStringArray(null);
        m.putStringList(list);
        m.putStringArray(array);

        assertTrue(m.getBoolean());
        assertEquals((byte) 0x80, m.getByte());
        assertEquals((short) 0x8040, m.getShort());
        assertEquals(0x80402010, m.getInt());
        assertEquals(0x8040201088442211L, m.getLong());
        assertNull(m.getDate());
        assertEquals(now.getTime(), m.getDate().getTime());
        assertNull(m.getString());
        assertEquals("foo", m.getString());
        assertNull(m.getStringList());
        assertNull(m.getStringArray());
        final List<String> list2 = m.getStringList();
        assertEquals(2, list2.size());
        assertEquals("foo", list2.get(0));
        assertEquals("bar", list2.get(1));
        final String[] array2 = m.getStringArray();
        assertEquals(2, array2.length);
        assertEquals("foo", array2[0]);
        assertEquals("bar", array2[1]);
    }

}
