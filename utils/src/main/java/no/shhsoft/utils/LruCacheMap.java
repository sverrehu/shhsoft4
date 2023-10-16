package no.shhsoft.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @param <K> key
 * @param <V> value
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LruCacheMap<K, V>
extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;
    private int maxSize = Integer.MAX_VALUE;

    public LruCacheMap() {
        super(16, 0.75f, true);
    }

    public LruCacheMap(final int maxSize) {
        super(maxSize, 0.75f, true);
        setMaxSize(maxSize);
    }

    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

}
