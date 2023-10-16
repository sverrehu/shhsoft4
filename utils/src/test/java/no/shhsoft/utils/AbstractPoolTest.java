package no.shhsoft.utils;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class AbstractPoolTest {

    static class MyThing {

        private boolean valid = true;

        public void setValid(final boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }

    }

    static class MyPool
    extends AbstractPool<MyThing> {

        @Override
        protected MyThing create() {
            return new MyThing();
        }

        @Override
        protected void destroy(final MyThing thing) {
        }

        @Override
        protected boolean isValid(final MyThing thing) {
            return thing.isValid();
        }

        @Override
        protected void prepareForAllocate(final MyThing thing) {
        }

    }

    @Test
    public void testPlainStuff() {
        final MyPool pool = new MyPool();
        final MyThing thing = pool.allocate();
        pool.release(thing);
        pool.allocate();
        pool.allocate();
        assertEquals(2, pool.getNumLive());
    }

    @Test
    public void testBlockWhenNoMoreAvailable() {
        final MyPool pool = new MyPool();
        pool.setMaxActive(1);
        pool.setMaxWaitTimeMs(300L);
        pool.allocate();
        try {
            pool.allocate();
            fail("Expected exception was not thrown.");
        } catch (final NoSuchElementException e) {
            assertTrue(e.getMessage().contains("Timeout"));
        }
        assertEquals(1, pool.getNumLive());
    }

    @Test
    public void testDontBlockWhenReleased() {
        final MyPool pool = new MyPool();
        pool.setMaxActive(1);
        pool.setMaxWaitTimeMs(300L);
        final MyThing thing = pool.allocate();
        pool.release(thing);
        pool.allocate();
        assertEquals(1, pool.getNumLive());
    }

    @Test
    public void testDontBlockWhenDiscarded() {
        final MyPool pool = new MyPool();
        pool.setMaxActive(1);
        pool.setMaxWaitTimeMs(300L);
        final MyThing thing = pool.allocate();
        pool.discard(thing);
        pool.allocate();
        assertEquals(1, pool.getNumLive());
    }

}
