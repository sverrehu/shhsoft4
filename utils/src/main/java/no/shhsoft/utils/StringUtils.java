package no.shhsoft.utils;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringUtils {

    private static final String ELLIPSIS = "...";
    public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    public static final Charset ISO_8859_1_CHARSET = Charset.forName("ISO-8859-1");
    private static final CharacterCondition LINEAR_WHITESPACE_CONDITION = c -> c == ' ' || c == '\t';
    private static final CharacterCondition ANY_WHITESPACE_CONDITION = Character::isWhitespace;

    private interface CharacterCondition {
        boolean satisifies(char c);
    }

    private StringUtils() {
        /* not to be instantiated */
    }

    public static boolean equals(final String s1, final String s2) {
        if (s1 == null) {
            return s2 == null;
        }
        return s1.compareTo(s2) == 0;
    }

    /**
     * Checks if two strings are equal when ignoring case, and folding all consecutive white space
     * to a single space character.
     *
     * @param string1
     *            one string.
     * @param string2
     *            another string.
     * @return <CODE>true</CODE> if the strings are equal when ignoring case and variations in
     *         white space, <CODE>false</CODE> otherwise.
     * @author Sverre H. Huseby
     */
    public static boolean equalsIgnoreCaseAndSpace(final String string1, final String string2) {
        if (string1 == null) {
            return string2 == null;
        }
        if (string2 == null) {
            return false;
        }
        final String s1 = string1.trim();
        final int len1 = s1.length();
        final String s2 = string2.trim();
        final int len2 = s2.length();
        int idx1 = 0;
        int idx2 = 0;
        for (;;) {
            /*
             * if any index is passed the length, both must be for the strings to be equal.
             */
            if (idx1 >= len1 || idx2 >= len2) {
                return (idx1 >= len1 && idx2 >= len2);
            }

            char c1 = s1.charAt(idx1++);
            if (Character.isWhitespace(c1)) {
                c1 = ' ';
                while (idx1 < len1 && Character.isWhitespace(s1.charAt(idx1))) {
                    ++idx1;
                }
            } else {
                c1 = Character.toLowerCase(c1);
            }

            char c2 = s2.charAt(idx2++);
            if (Character.isWhitespace(c2)) {
                c2 = ' ';
                while (idx2 < len2 && Character.isWhitespace(s2.charAt(idx2))) {
                    ++idx2;
                }
            } else {
                c2 = Character.toLowerCase(c2);
            }

            if (c1 != c2) {
                return false;
            }
        }
    }

    /**
     * Returns the index within this string of the first occurrence of the specified substring,
     * starting at the specified index, and ignoring case differences.
     *
     * @param s
     *            the string to search in.
     * @param what
     *            the substring to search for.
     * @param from
     *            the index to start the search from.
     * @return The index of the substring, or -1 if not found.
     */
    public static int indexOfIgnoreCase(final String s, final String what, final int from) {
        if (s == null || what == null) {
            return -1;
        }
        return s.toLowerCase().indexOf(what.toLowerCase(), from);
    }

    /**
     * Returns the index within this string of the first occurrence of the specified substring,
     * ignoring case differences.
     *
     * @param s
     *            the string to search in.
     * @param what
     *            the substring to search for.
     * @return The index of the substring, or -1 if not found.
     */
    public static int indexOfIgnoreCase(final String s, final String what) {
        return indexOfIgnoreCase(s, what, 0);
    }

    /**
     * Creates a string containing a given number of given characters.
     *
     * @param c
     *            the character to use.
     * @param n
     *            number of characters.
     * @return the string of characters.
     * @author Sverre H. Huseby
     */
    public static String stringOfChars(final char c, final int n) {
        final StringBuilder sb = new StringBuilder(n);
        for (int q = n - 1; q >= 0; q--) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Replaces all occurences of a string with another string inside a string.
     *
     * @param s
     *            the main string.
     * @param old
     *            substring that should be replaced.
     * @param nw
     *            what to replace the substring with.
     * @return a string where all replacement have taken place.
     * @author Sverre H. Huseby
     */
    public static String replace(final String s, final String old, final String nw) {
        if (s == null) {
            return null;
        }
        if (old == null || nw == null) {
            return s;
        }
        int from = 0;
        int idx = s.indexOf(old, from);
        if (idx < 0) {
            return s;
        }
        final int oldLen = old.length();
        final StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(s, from, idx);
            sb.append(nw);
            if ((from = idx + oldLen) >= s.length()) {
                break;
            }
            if ((idx = s.indexOf(old, from)) < 0) {
                sb.append(s.substring(from));
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Replaces all occurences of a string with another string inside a string. Ignores case when
     * searching for the original substring.
     *
     * @param s
     *            the main string.
     * @param old
     *            substring that should be replaced.
     * @param nw
     *            what to replace the substring with.
     * @return a string where all replacement have taken place.
     * @author Sverre H. Huseby
     */
    public static String replaceIgnoreCase(final String s, final String old, final String nw) {
        if (s == null) {
            return null;
        }
        if (old == null || nw == null) {
            return s;
        }
        int from = 0;
        int idx;
        if ((idx = indexOfIgnoreCase(s, old, from)) < 0) {
            return s;
        }
        final int oldLen = old.length();
        final StringBuilder sb = new StringBuilder();
        for (;;) {
            sb.append(s, from, idx);
            sb.append(nw);
            if ((from = idx + oldLen) >= s.length()) {
                break;
            }
            if ((idx = indexOfIgnoreCase(s, old, from)) < 0) {
                sb.append(s.substring(from));
                break;
            }
        }
        return sb.toString();
    }

    public static String rtrim(final String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (len == 0 || !Character.isWhitespace(s.charAt(len - 1))) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(s);
        while (len > 0 && Character.isWhitespace(sb.charAt(len - 1))) {
            sb.setLength(--len);
        }
        return sb.toString();
    }

    public static String ltrim(final String s) {
        if (s == null) {
            return null;
        }
        final int len = s.length();
        if (len == 0 || !Character.isWhitespace(s.charAt(0))) {
            return s;
        }
        int idx = 0;
        while (idx < len && Character.isWhitespace(s.charAt(idx))) {
            ++idx;
        }
        return s.substring(idx);
    }

    public static int findMatchingEndParen(final String s, final int from) {
        final char start = s.charAt(from);
        final char end;
        switch (start) {
            case '(':
                end = ')';
                break;
            case '[':
                end = ']';
                break;
            case '{':
                end = '}';
                break;
            case '<':
                end = '>';
                break;
            default:
                throw new RuntimeException("Current char `" + start + "' is not a recognized paren");
        }
        final int len = s.length();
        int idx = from + 1;
        int skipCount = 0;
        while (idx < len) {
            final char c = s.charAt(idx);
            if (c == start) {
                ++skipCount;
            } else if (c == end) {
                if (skipCount == 0) {
                    break;
                }
                --skipCount;
            }
            ++idx;
        }
        if (idx >= len) {
            idx = -1;
        }
        return idx;
    }

    public static boolean isInArray(final char c, final char[] chars) {
        if (chars == null) {
            return false;
        }
        for (int q = chars.length - 1; q >= 0; q--) {
            if (chars[q] == c) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsOnly(final String s, final char[] chars) {
        if (s == null) {
            return false;
        }
        final char[] schars = s.toCharArray();
        for (int q = schars.length - 1; q >= 0; q--) {
            if (!isInArray(schars[q], chars)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsOnly(final String s, final String chars) {
        return containsOnly(s, chars.toCharArray());
    }

    public static int indexOfAny(final String s, final char[] chars, final int from) {
        if (s == null || chars == null || chars.length == 0) {
            return -1;
        }
        int idx = from;
        final int len = s.length();
        while (idx < len) {
            final char c = s.charAt(idx);
            for (int q = chars.length - 1; q >= 0; q--) {
                if (c == chars[q]) {
                    return idx;
                }
            }
            ++idx;
        }
        return -1;
    }

    public static int indexOfAny(final String s, final char[] chars) {
        return indexOfAny(s, chars, 0);
    }

    private static void splitAdd(final String s, final int from, final int to,
                                 final List<String> strings,
                                 final boolean trimWhiteSpace, final boolean returnEmpty) {
        int f = from;
        int t = to;
        if (trimWhiteSpace) {
            while (f < t && Character.isWhitespace(s.charAt(f))) {
                ++f;
            }
            while (t > f && Character.isWhitespace(s.charAt(t - 1))) {
                --t;
            }
        }
        if (t > f || returnEmpty) {
            strings.add(s.substring(f, t));
        }
    }

    public static String[] split(final String s, final char[] separators,
                                 final boolean trimWhiteSpace, final boolean returnEmpty) {
        /* about 2.7 times faster than my old StringTokenizer-based method. */
        if (s == null || s.length() == 0 || separators == null || separators.length == 0) {
            return new String[0];
        }
        final List<String> strings = new ArrayList<>();
        final int len = s.length();
        int from = 0;
        for (;;) {
            final int idx = indexOfAny(s, separators, from);
            if (idx < 0) {
                splitAdd(s, from, len, strings, trimWhiteSpace, returnEmpty);
                break;
            }
            splitAdd(s, from, idx, strings, trimWhiteSpace, returnEmpty);
            from = idx + 1;
            if (from > len) {
                break;
            }
        }
        return strings.toArray(new String[0]);
    }

    /**
     * Splits a string into a <CODE>List</CODE> of smaller strings. The string is split on every
     * occurrence of one of the given separator characters, optionally trimming whitespace.
     * <P>
     * You should note the following:
     * <UL TYPE="square">
     * <LI>The separator characters will not be part of the returned strings.</LI>
     * <LI>Empty strings (two searator characters with no other character in between) will <B>not</B>
     * be returned.</LI>
     * </UL>
     *
     * @param s
     *            the string to split.
     * @param separatorChars
     *            a string of characters that individually mark a split point.
     * @param doTrim
     *            if <CODE>true</CODE>, trim whitespace characters from the returned strings. if
     *            <CODE>false</CODE>, keep all whitespace.
     * @return a <CODE>List</CODE> of substrings.
     * @author Sverre H. Huseby
     */
    public static List<String> split(final String s, final String separatorChars,
                                     final boolean doTrim) {
        final List<String> ret = new ArrayList<>();
        final StringTokenizer st = new StringTokenizer(s, separatorChars);
        while (st.hasMoreTokens()) {
            if (doTrim) {
                ret.add(st.nextToken().trim());
            } else {
                ret.add(st.nextToken());
            }
        }
        return ret;
    }

    /**
     * Splits a string into a <CODE>List</CODE> of smaller strings. The string is split on every
     * occurrence of one of the given separator characters, and whitespace is trimmed from the
     * returned strings.
     * <P>
     * You should note the following:
     * <UL TYPE="square">
     * <LI>The separator characters will not be part of the returned strings.</LI>
     * <LI>Empty strings (two searator characters with no other character in between) will <B>not</B>
     * be returned.</LI>
     * </UL>
     *
     * @param s
     *            the string to split.
     * @param separatorChars
     *            a string of characters that individually mark a split poing.
     * @return a <CODE>List</CODE> of substrings.
     * @author Sverre H. Huseby
     */
    public static List<String> split(final String s, final String separatorChars) {
        return split(s, separatorChars, true);
    }

    public static String join(final Object[] objects, final String separator) {
        if (objects == null) {
            return null;
        }
        final String sep = separator == null ? "" : separator;
        final StringBuilder sb = new StringBuilder();
        for (final Object object : objects) {
            if (sb.length() > 0) {
                sb.append(sep);
            }
            sb.append(object.toString());
        }
        return sb.toString();
    }

    public static String join(final Object[] objects, final char separator) {
        return join(objects, Character.toString(separator));
    }

    public static String join(final Collection<?> objects, final String separator) {
        if (objects == null) {
            return null;
        }
        return join(objects.toArray(new Object[0]), separator);
    }

    public static String join(final Collection<?> objects, final char separator) {
        return join(objects, Character.toString(separator));
    }

    public static String foldDiacritics(final String s) {
        final String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < n.length(); q++) {
            final char c = n.charAt(q);
            if (c > 0) {
                if (c <= 127) {
                    sb.append(c);
                } else if (c <= 255) {
                    switch (c) {
                        case 198:
                            sb.append('A');
                            break;
                        case 208:
                            sb.append('D');
                            break;
                        case 216:
                            sb.append('O');
                            break;
                        case 222:
                            sb.append('d');
                            break;
                        case 223:
                            sb.append('s');
                            break;
                        case 230:
                            sb.append('a');
                            break;
                        case 240:
                        case 248:
                            sb.append('o');
                            break;
                        default:
                            sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String trim(final String s) {
        if (s == null) {
            return null;
        }
        return s.trim();
    }

    public static String trimToNull(final String s) {
        if (s == null) {
            return null;
        }
        final String trimmed = s.trim();
        if (trimmed.length() == 0) {
            return null;
        }
        return trimmed;
    }

    public static String trimToEmpty(final String s) {
        if (s == null) {
            return "";
        }
        return s.trim();
    }

    public static boolean isEmpty(final String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isBlank(final String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String toString(final Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    /**
     * Folds repeated occurrences of linear white space into a single space character.
     * The returned string will contain no other linear white space than normal space
     * characters (ASCII 32).
     */
    public static String mergeLinearWhiteSpace(final String s) {
        return mergeWhiteSpace(s, LINEAR_WHITESPACE_CONDITION);
    }

    /**
     * Folds repeated occurrences of any white space into a single space character.
     * The returned string will contain no other linear space than normal space
     * characters (ASCII 32).
     */
    public static String mergeAnyWhiteSpace(final String s) {
        return mergeWhiteSpace(s, ANY_WHITESPACE_CONDITION);
    }

    private static String mergeWhiteSpace(final String s, final CharacterCondition condition) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean wsSeen = false;
        for (int q = 0; q < s.length(); q++) {
            final char c = s.charAt(q);
            if (condition.satisifies(c)) {
                if (!wsSeen) {
                    wsSeen = true;
                    sb.append(' ');
                }
            } else {
                wsSeen = false;
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String remove(final String s, final char charToRemove) {
        if (s == null) {
            return null;
        }
        if (s.indexOf(charToRemove) < 0) {
            return s;
        }
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < s.length(); q++) {
            final char c = s.charAt(q);
            if (c != charToRemove) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * @return true for the empty string (but not for null).
     */
    public static boolean isNumeric(final String s) {
        if (s == null) {
            return false;
        }
        for (int q = s.length() - 1; q >= 0; q--) {
            final char c = s.charAt(q);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true for the empty string (but not for null).
     */
    public static boolean isNumericSpace(final String s) {
        if (s == null) {
            return false;
        }
        for (int q = s.length() - 1; q >= 0; q--) {
            final char c = s.charAt(q);
            if (c != ' ' && (c < '0' || c > '9')) {
                return false;
            }
        }
        return true;
    }

    public static String escapeJavaLikeString(final String s,
                                              final boolean escapeSingleQuote, final boolean escapeForwardSlash) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < s.length(); q++) {
            final char c = s.charAt(q);
            switch (c) {
                case '\r':
                    sb.append("\\r");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '"':
                case '\\':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\'':
                    if (escapeSingleQuote) {
                        sb.append("\\'");
                    } else {
                        sb.append('\'');
                    }
                    break;
                case '/':
                    if (escapeForwardSlash) {
                        sb.append("\\/");
                    } else {
                        sb.append('/');
                    }
                    break;
                default:
                    if (c <= 0x1f || c >= 0x7f) {
                        sb.append("\\u" + HexUtils.to4DigitHex(c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static String lcFirst(final String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0 || !Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String ucFirst(final String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0 || !Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * If the string is too long, cuts it and adds ellipsis at the end.
     * If cut, the length of the resulting string will be the length given, including the ellipsis.
     */
    public static String trimToLength(final String s, final int len) {
        if (s == null || s.length() <= len) {
            return s;
        }
        return s.substring(0, len - ELLIPSIS.length()) + ELLIPSIS;
    }

    public static String removeAtStart(final String s, final String remove) {
        if (!s.startsWith(remove)) {
            return s;
        }
        return s.substring(remove.length()).trim();
    }

    public static String removeAtEnd(final String s, final String remove) {
        if (!s.endsWith(remove)) {
            return s;
        }
        return s.substring(0, s.length() - remove.length()).trim();
    }

    public static String newStringUtf8(final byte[] bytes) {
        return removeBom(new String(bytes, UTF_8_CHARSET));
    }

    public static String newStringUtf8(final byte[] bytes, final int offset, final int length) {
        return removeBom(new String(bytes, offset, length, UTF_8_CHARSET));
    }

    private static String removeBom(final String s) {
        if (s.length() > 0 && s.charAt(0) == '\ufeff') {
            return s.substring(1);
        }
        return s;
    }

    public static byte[] getBytesUtf8(final String s) {
        return s.getBytes(UTF_8_CHARSET);
    }

    public static String newStringIso8859_1(final byte[] bytes) {
        return new String(bytes, ISO_8859_1_CHARSET);
    }

    public static String newStringIso8859_1(final byte[] bytes, final int offset, final int length) {
        return new String(bytes, offset, length, ISO_8859_1_CHARSET);
    }

    public static byte[] getBytesIso8859_1(final String s) {
        return s.getBytes(ISO_8859_1_CHARSET);
    }

}
