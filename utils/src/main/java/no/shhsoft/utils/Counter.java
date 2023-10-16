package no.shhsoft.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Counter<T> {

    private final Map<T, Integer> map = new HashMap<>();

    public void increase(final T t) {
        add(t, 1);
    }

    public void add(final T t, final int n) {
        synchronized (map) {
            map.merge(t, n, Integer::sum);
        }
    }

    public int getCount(final T t) {
        synchronized (map) {
            final Integer count = map.get(t);
            if (count == null) {
                return 0;
            }
            return count;
        }
    }

    public Set<T> findHighestCountValues() {
        final Set<T> set = new HashSet<>();
        synchronized (map) {
            final int max = findHighestCount();
            for (final Map.Entry<T, Integer> entry : map.entrySet()) {
                if (entry.getValue().equals(max)) {
                    set.add(entry.getKey());
                }
            }
        }
        return set;
    }

    private int findHighestCount() {
        synchronized (map) {
            int highest = 0;
            for (final Integer count : map.values()) {
                if (count > highest) {
                    highest = count;
                }
            }
            return highest;
        }
    }

    public void clear() {
        synchronized (map) {
            map.clear();
        }
    }

    public Set<Map.Entry<T, Integer>> entrySet() {
        synchronized (map) {
            return map.entrySet();
        }
    }

}
