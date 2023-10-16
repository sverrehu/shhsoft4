package no.shhsoft.web.utils;

import no.shhsoft.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UrlUtils {

    private static final char[] VALID_PROTOCOL_CHARACTERS
        = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+-_".toCharArray();
    private static String urlEncodeCharacterSet = "UTF-8";

    private UrlUtils() {
        /* not to be instantiated */
    }

    public static void setUrlEncodeCharacterSet(final String urlEncodeCharacterSet) {
        UrlUtils.urlEncodeCharacterSet = urlEncodeCharacterSet;
    }

    public static String getUrlEncodeCharacterSet() {
        return urlEncodeCharacterSet;
    }

    public static String encode(final String s, final String characterSet) {
        try {
            return URLEncoder.encode(s, characterSet);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding `" + characterSet + "'");
        }
    }

    public static String encode(final String s) {
        return encode(s, urlEncodeCharacterSet);
    }

    public static String decode(final String s, final String characterSet) {
        try {
            return URLDecoder.decode(s, characterSet);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding `" + characterSet + "'");
        }
    }

    public static String decode(final String s) {
        return decode(s, urlEncodeCharacterSet);
    }

    /**
     * Returns everything from (and including) the slash following the host and port part of a URL.
     */
    public static String extractPathAndQueryString(final String url) {
        int idx = url.indexOf("://");
        if (idx >= 0) {
            final String protocol = url.substring(0, idx);
            if (!StringUtils.containsOnly(protocol, VALID_PROTOCOL_CHARACTERS)) {
                return url;
            }
            idx = url.indexOf('/', idx + 3);
            if (idx >= 0) {
                return url.substring(idx);
            }
            return "/";
        }
        return url;
    }

    /**
     * Returns everything from (and including) the slash following the host and port part of a URL,
     * up to (and not including) any question mark that signals the start of the query-string.
     */
    public static String extractPath(final String url) {
        final String path = extractPathAndQueryString(url);
        final int idx = path.indexOf('?');
        if (idx >= 0) {
            return path.substring(0, idx);
        }
        return path;
    }

    public static String extractHost(final String url) {
        try {
            String s = new URL(url).getHost();
            if (s != null && s.trim().length() == 0) {
                s = null;
            }
            return s;
        } catch (final MalformedURLException e) {
            return null;
        }
    }

}
