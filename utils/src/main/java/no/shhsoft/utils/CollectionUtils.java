package no.shhsoft.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CollectionUtils {

    private CollectionUtils() {
        /* not to be instantiated */
    }

    public static int size(final Collection<?> collection) {
        return collection.size();
    }

    public static List<String> convertSetOfStringsToList(final Set<String> set) {
        return new ArrayList<>(set);
    }

    public static boolean contains(final Collection<?> collection, final Object object) {
        return collection != null && collection.contains(object);
    }

}
