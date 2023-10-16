package no.shhsoft.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CachingResourceLoader {

    private static final byte[] NULL = new byte[0];
    private static final Map<String, byte[]> cache = new HashMap<>();

    private CachingResourceLoader() {
        /* not to be instantiated */
    }

    public static synchronized byte[] load(final String resourceName) {
        byte[] data = cache.get(resourceName);
        /* the following _is_ supposed to be a reference comparison, not equals. */
        if (data == NULL) {
            return null;
        }
        if (data == null) {
            data = IoUtils.readResource(resourceName);
            if (data != null) {
                cache.put(resourceName, data);
            } else {
                cache.put(resourceName, NULL);
            }
        }
        return data;
    }

}
