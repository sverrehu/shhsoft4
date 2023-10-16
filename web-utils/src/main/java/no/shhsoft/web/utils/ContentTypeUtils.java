package no.shhsoft.web.utils;

import no.shhsoft.utils.StringUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ContentTypeUtils {

    private static final String CHARSET = "charset";
    public static final String DEFAULT_CHARSET = "UTF-8";

    private ContentTypeUtils() {
    }

    public static String[] splitParts(final String contentType) {
        return StringUtils.split(contentType, new char[] { ';' }, true, false);
    }

    public static String spliceParts(final String[] parts) {
        final StringBuilder sb = new StringBuilder();
        if (parts != null) {
            for (int q = 0; q < parts.length; q++) {
                if (q > 0) {
                    sb.append("; ");
                }
                sb.append(parts[q]);
            }
        }
        return sb.toString();
    }

    public static boolean isCharsetPart(final String part) {
        if (!part.toLowerCase().startsWith(CHARSET)) {
            return false;
        }
        int from = CHARSET.length();
        while (from < part.length() && Character.isWhitespace(part.charAt(from))) {
            ++from;
        }
        if (from == part.length() || part.charAt(from) != '=') {
            return false;
        }
        return true;
    }

    public static String getCharset(final String contentType) {
        if (contentType == null) {
            return null;
        }
        for (final String part : splitParts(contentType)) {
            if (!isCharsetPart(part)) {
                continue;
            }
            return part.substring(part.indexOf('=') + 1).trim();
        }
        return null;
    }

    public static String getContentTypePart(final String contentType) {
        if (contentType == null) {
            return null;
        }
        for (final String part : splitParts(contentType)) {
            if (!isCharsetPart(part)) {
                return part;
            }
        }
        return null;
    }

    public static String replaceCharset(final String contentType, final String newCharset) {
        final String newCharsetAssignment = CHARSET + "=" + newCharset;
        if (contentType == null) {
            return newCharsetAssignment;
        }
        final String[] parts = splitParts(contentType);
        boolean charsetFound = false;
        for (int q = 0; q < parts.length; q++) {
            if (isCharsetPart(parts[q])) {
                parts[q] = newCharsetAssignment;
                charsetFound = true;
                break;
            }
        }
        if (charsetFound) {
            return spliceParts(parts);
        }
        return contentType + "; " + newCharsetAssignment;
    }

    public static String replaceContentTypePart(final String contentType, final String newContentTypePart) {
        if (contentType == null) {
            return newContentTypePart;
        }
        final String[] parts = splitParts(contentType);
        boolean contentTypePartFound = false;
        for (int q = 0; q < parts.length; q++) {
            if (!isCharsetPart(parts[q])) {
                parts[q] = newContentTypePart;
                contentTypePartFound = true;
                break;
            }
        }
        if (contentTypePartFound) {
            return spliceParts(parts);
        }
        return newContentTypePart + "; " + contentType;
    }

    public static String defaultCharSetIfNull(final String charset) {
        if (charset == null) {
            return DEFAULT_CHARSET;
        }
        return charset;
    }

}
