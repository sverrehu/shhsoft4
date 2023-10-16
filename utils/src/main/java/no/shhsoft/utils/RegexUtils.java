package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RegexUtils {

    private RegexUtils() {
    }

    public static String escape(final String s) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < s.length(); q++) {
            final char c = s.charAt(q);
            if (c == '.' || c == '\\' || c == '^' || c == '$' || c == '*' || c == '?'
                || c == '+' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}'
                || c == '|') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
