package no.shhsoft.utils.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CacheManager {

    private static final Logger LOG = Logger.getLogger(CacheManager.class.getName());
    private static final List<Clearable> CLEARABLES = new ArrayList<>();

    private CacheManager() {
    }

    public static void addClearable(final Clearable clearable) {
        synchronized (CLEARABLES) {
            CLEARABLES.add(clearable);
        }
    }

    public static void clear() {
        LOG.fine("Clearing cache");
        synchronized (CLEARABLES) {
            for (final Clearable clearable : CLEARABLES) {
                clearable.clear();
            }
        }
    }

}
