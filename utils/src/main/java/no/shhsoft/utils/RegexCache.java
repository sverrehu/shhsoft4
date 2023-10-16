package no.shhsoft.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RegexCache {

    private static final Map<String, Pattern> map = new LruCacheMap<>(1000);

    private RegexCache() {
        /* not to be instantiated */
    }

    private static Pattern getPattern(final String regex) {
        synchronized (map) {
            Pattern pattern = map.get(regex);
            if (pattern == null) {
                pattern = Pattern.compile(regex);
                map.put(regex, pattern);
            }
            return pattern;
        }
    }

    public static void setMaxSize(final int maxSize) {
        ((LruCacheMap<String, Pattern>) map).setMaxSize(maxSize);
    }

    public static boolean matches(final String regex, final String s) {
        return getPattern(regex).matcher(s).matches();
    }

    public static String[] getMatchGroups(final String regex, final String s) {
        final Matcher matcher = getPattern(regex).matcher(s);
        if (!matcher.matches()) {
            return new String[0];
        }
        final String[] groups = new String[matcher.groupCount()];
        for (int q = groups.length - 1; q >= 0; q--) {
            groups[q] = matcher.group(q + 1);
        }
        return groups;
    }

}
