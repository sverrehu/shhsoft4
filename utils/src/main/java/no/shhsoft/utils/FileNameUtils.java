package no.shhsoft.utils;

/**
 * BIG NOTE!
 * This class treats both slash and backslash as path separators, paying no attention to the platform.
 * This may lead to security problems if used without thinking!
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FileNameUtils {

    private static final String PATH_SEPARATOR_REPLACEMENT = "-";
    private static final String GENERIC_REPLACEMENT = "x";
    private static final char EXTENSION_SEPARATOR = '.';

    private FileNameUtils() {
    }

    public static String toFileNameWithoutPath(final String s) {
        String tmp = StringUtils.replace(s, "/", PATH_SEPARATOR_REPLACEMENT);
        tmp = StringUtils.replace(tmp, "\\", PATH_SEPARATOR_REPLACEMENT);
        final String[] parts = StringUtils.split(tmp, " \t\r\n".toCharArray(), true, false);
        final StringBuilder sb = new StringBuilder();
        for (final String part : parts) {
            for (int q = 0; q < part.length(); q++) {
                String replacement = massage(part.charAt(q));
                if (parts.length > 1 && q == 0) {
                    replacement = StringUtils.ucFirst(replacement);
                }
                sb.append(replacement);
            }
        }
        return sb.toString();
    }

    private static String massage(final char c) {
        if (c == '.' || c == '-' || (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return String.valueOf(c);
        }
        switch (c) {
            case '\u00e6':
                return "ae";
            case '\u00f8':
                return "oe";
            case '\u00e5':
                return "aa";
            case '\u00c6':
                return "Ae";
            case '\u00d8':
                return "Oe";
            case '\u00c5':
                return "Aa";
            default:
                return GENERIC_REPLACEMENT;
        }
    }

    /**
     * @return Extension without leading dot.
     */
    public static String getExtension(final String path) {
        if (path == null) {
            return null;
        }
        final String baseName = getBaseName(path);
        final int idx = baseName.lastIndexOf(EXTENSION_SEPARATOR);
        return idx >= 0 ? baseName.substring(idx + 1) : null;
    }

    /**
     * @param newExtension New extension with or without leading dot. Dot will be added if needed.
     */
    public static String addOrReplaceExtension(final String path, final String newExtension) {
        if (path == null) {
            return null;
        }
        String whatToAdd = "";
        if (newExtension != null && newExtension.length() > 0) {
            whatToAdd = newExtension.charAt(0) == EXTENSION_SEPARATOR ? newExtension : (EXTENSION_SEPARATOR + newExtension);
        }
        String prefix = stripTrailingPathSeparators(path);
        final int idx = getBaseName(prefix).lastIndexOf(EXTENSION_SEPARATOR);
        if (idx >= 0) {
            prefix = prefix.substring(0, idx);
        }
        return prefix + whatToAdd;
    }

    /**
     * As Unix.
     */
    public static String getDirName(final String path) {
        if (path == null) {
            return null;
        }
        final String p = stripTrailingPathSeparators(path);
        final int lastSep = getIndexOfLastSlash(p);
        if (lastSep < 0) {
            return "";
        }
        if (lastSep == 0) {
            return p.substring(0, 1);
        }
        return stripTrailingPathSeparators(p.substring(0, lastSep));
    }

    /**
     * As Unix.
     */
    public static String getBaseName(final String path) {
        if (path == null) {
            return null;
        }
        final String p = stripTrailingPathSeparators(path);
        final int lastSep = getIndexOfLastSlash(p);
        if (lastSep <= 0) {
            return p;
        }
        return p.substring(lastSep + 1);
    }

    public static String stripTrailingPathSeparators(final String path) {
        if (path == null) {
            return null;
        }
        String p = path;
        do {
            final int len = p.length();
            if (len == 1) {
                return p;
            }
            final int idx = getIndexOfLastSlash(p);
            if (idx < 0 || idx < len - 1) {
                return p;
            }
            p = p.substring(0, p.length() - 1);
        } while (path.length() > 1);
        return p;
    }

    private static int getIndexOfLastSlash(final String path) {
        return Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
    }

}
