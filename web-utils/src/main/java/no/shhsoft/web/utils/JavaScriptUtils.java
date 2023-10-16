package no.shhsoft.web.utils;

import no.shhsoft.utils.StringUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JavaScriptUtils {

    private JavaScriptUtils() {
    }

    public static String escapeString(final String s) {
        return StringUtils.escapeJavaLikeString(s, true, true);
    }

}
