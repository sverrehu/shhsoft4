package no.shhsoft.web.utils;

import no.shhsoft.utils.StringUtils;

/**
 * Helper methods that may come in handy when working with HTML.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HtmlUtils {

    private HtmlUtils() {
        /* not to be instantiated. */
    }

    public static String encode(final String s) {
        /*
         * Cyclomatic complexity is 11, but this method is written this way for performance reasons,
         * so I don't care.
         */
        if (s == null) {
            return "(null)";
        }
        if (s.indexOf('"') < 0 && s.indexOf('&') < 0 && s.indexOf('<') < 0 && s.indexOf('>') < 0) {
            return s;
        }
        final int len = s.length();
        final StringBuilder sb = new StringBuilder(len + 10);
        for (int q = 0; q < len; q++) {
            final char c = s.charAt(q);
            switch (c) {
                case '"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String encodeKeepLineBreaks(final String s) {
        String ret = encode(s);
        ret = StringUtils.replace(ret, "\r", "");
        ret = StringUtils.replace(ret, "\n", "<br />\n");
        ret = StringUtils.replace(ret, "<br />\n<br />\n", "<br />\n<br />\n");
        return ret;
    }

    /**
     * Finds all URLs in a string, and encapsulates them in a (anchor) tags to create selectable
     * links.
     *
     * @param s
     *            the string to find URLs in.
     * @param attribs
     *            any attributes to add to the tag.
     * @return the string with URLs anchored.
     */
    public static String anchorUrls(final String s, final String attribs) {
        final String[] pres = { "http://", "ftp://", "https://", "nntp://", "telnet://", "mailto:",
                               "news:", "file://", "outlook://" };
        String text = s;
        for (final String pre : pres) {
            int len = text.length();
            int idx = StringUtils.indexOfIgnoreCase(text, pre, 0);
            while (idx >= 0) {
                int end = idx;
                while (end < len) {
                    /*
                     * SHH 1999-04-16: changed from isSpaceChar to isWhitespace, as the former
                     * didn't match on line breaks!
                     */
                    final char c = text.charAt(end);
                    if (Character.isWhitespace(c) || c == '<') {
                        break;
                    }
                    ++end;
                }
                char c;
                while (end > idx
                       && ((c = text.charAt(end - 1)) == '.' || c == ',' || c == ':' || c == ';'
                           || c == '!' || c == '"' || c == '\'' || c == '(' || c == ')' || c == '['
                           || c == ']' || c == '{' || c == '}' || c == '<' || c == '>')) {
                    --end;
                }
                final String url = text.substring(idx, end);
                String anchor = "<a href=\"" + url + "\"";
                if (attribs != null) {
                    anchor += " " + attribs;
                }
                anchor += ">" + url + "</a>";
                text = text.substring(0, idx) + anchor + text.substring(end);

                len = text.length();
                idx += anchor.length();
                idx = StringUtils.indexOfIgnoreCase(text, pre, idx);
            }
        }
        return text;
    }

    /**
     * Finds all URLs in a string, and encapsulates them in a (anchor) tags to create selectable
     * links.
     *
     * @param s
     *            the string to find URLs in.
     * @return the string with URLs anchored.
     */
    public static String anchorUrls(final String s) {
        return anchorUrls(s, null);
    }

    /**
     * Removes all HTML tags from a string. Also converts br and p tags to newlines, and decodes
     * selected character entities.
     *
     * @param html
     *            the string to remove HTML tags from.
     * @return the string with HTML tags removed.
     */
    public static String htmlToText(final String html) {
        String s = StringUtils.replaceIgnoreCase(html, "<br>", "\n");
        s = StringUtils.replaceIgnoreCase(s, "<p>", "\n");
        s = StringUtils.replaceIgnoreCase(s, "</p>", "");
        s = StringUtils.replace(s, "&nbsp;", " ");
        s = StringUtils.replace(s, "&endash;", "-");
        s = StringUtils.replace(s, "&emdash;", "--");

        s = StringUtils.replace(s, "&lt;", "<");
        s = StringUtils.replace(s, "&gt;", ">");
        s = StringUtils.replace(s, "&amp;", "&");
        s = StringUtils.replace(s, "&quot;", "\"");

        s = StringUtils.replace(s, "&aelig;", "\u00e6");
        s = StringUtils.replace(s, "&oslash;", "\u00f8");
        s = StringUtils.replace(s, "&aring;", "\u00e5");
        s = StringUtils.replace(s, "&AElig;", "\u00c6");
        s = StringUtils.replace(s, "&Oslash;", "\u00d8");
        s = StringUtils.replace(s, "&Aring;", "\u00c5");

        int from = 0;
        int idx;
        if ((idx = s.indexOf('<', from)) < 0) {
            return s;
        }
        final StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(s, from, idx);
            if ((from = s.indexOf('>', idx)) < 0) {
                break;
            }
            ++from;
            if ((idx = s.indexOf('<', from)) < 0) {
                sb.append(s.substring(from));
                break;
            }
        }
        return sb.toString();
    }

}
