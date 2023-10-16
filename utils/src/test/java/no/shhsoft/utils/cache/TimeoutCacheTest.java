package no.shhsoft.utils.cache;

import no.shhsoft.time.TestTimeProvider;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class TimeoutCacheTest {

    private static final long INITIAL_TIME = 100L;
    private static final long FUTURE = 101L;
    private static final long PAST = 99L;
    private static final String KEY1 = "key";
    private static final String VALUE1 = "value";
    private final TestTimeProvider timeProvider = new TestTimeProvider();

    @Test
    public void shouldAllowNullValue() {
        final TimeoutCache<String, String> cache = createCache();
        cache.put(KEY1, null, FUTURE);
        final TimeoutCache<String, String>.CacheResult<String> cacheResult = cache.getAsCacheResult(KEY1);
        assertNotNull(cacheResult);
        assertNull(cacheResult.getValue());
    }

    @Test
    public void shouldReturnNonExpiredValue() {
        final TimeoutCache<String, String> cache = createCache();
        cache.put(KEY1, VALUE1, FUTURE);
        assertEquals(VALUE1, cache.get(KEY1));
    }

    @Test
    public void shouldNotReturnExpiredValue() {
        final TimeoutCache<String, String> cache = createCache();
        cache.put(KEY1, VALUE1, PAST);
        assertNull(cache.get(KEY1));
    }

    private TimeoutCache<String, String> createCache() {
        timeProvider.set(INITIAL_TIME);
        return new TimeoutCache<>(timeProvider);
    }

}
