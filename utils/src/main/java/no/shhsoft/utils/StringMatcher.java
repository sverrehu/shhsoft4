package no.shhsoft.utils;

import java.util.Set;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringMatcher {

    private Set<String> exactMatches;
    private Set<String> regexpMatches;

    public boolean matches(final String s) {
        if (exactMatches != null && exactMatches.contains(s)) {
            return true;
        }
        if (regexpMatches != null) {
            for (final String re : regexpMatches) {
                if (RegexCache.matches(re, s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setExactMatches(final Set<String> exactMatches) {
        this.exactMatches = exactMatches;
    }

    public void setRegexpMatches(final Set<String> regexpMatches) {
        this.regexpMatches = regexpMatches;
    }

}
