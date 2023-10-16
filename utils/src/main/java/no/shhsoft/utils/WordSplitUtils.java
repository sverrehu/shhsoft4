package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WordSplitUtils {

    private enum Type {
        NONE, LETTER, DIGIT, SPACE, OTHER
    }

    private WordSplitUtils() {
    }

    private static Type getType(final char c) {
        if (Character.isLetter(c)) {
            return Type.LETTER;
        }
        if (Character.isDigit(c)) {
            return Type.DIGIT;
        }
        if (Character.isWhitespace(c)) {
            return Type.SPACE;
        }
        return Type.OTHER;
    }

    private static String[] append(final String[] strings, final String string) {
        if (string == null || string.length() == 0) {
            return strings;
        }
        final String[] newStrings = new String[strings.length + 1];
        System.arraycopy(strings, 0, newStrings, 0, strings.length);
        newStrings[strings.length] = string;
        return newStrings;
    }

    public static String[] splitByWords(final String string) {
        if (string == null) {
            return null;
        }
        String[] strings = new String[0];
        Type lastType = Type.NONE;
        final StringBuilder curr = new StringBuilder();
        for (int q = 0; q < string.length(); q++) {
            final char c = string.charAt(q);
            final Type type = getType(c);
            if (type == lastType) {
                curr.append(c);
            } else {
                strings = append(strings, curr.toString());
                lastType = type;
                curr.setLength(0);
                curr.append(c);
            }
        }
        strings = append(strings, curr.toString());
        return strings;
    }

}
