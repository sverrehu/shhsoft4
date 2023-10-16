package no.shhsoft.phonetic;

import no.shhsoft.utils.StringUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractPhoneticEncoder
implements PhoneticEncoder {

    private static final String VOVELS = "AEIOUY";
    protected abstract String encodeSingleUpperCaseWord(String word);

    private static String replaceNonCharsWithSpace(final String s) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < s.length(); q++) {
            final char c = s.charAt(q);
            if (Character.isLetter(c)) {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    protected static boolean isUpperCaseVovel(final char c) {
        return VOVELS.indexOf(c) >= 0;
    }

    protected static String replaceFirst(final String s, final String match, final String replacement) {
        if (!s.startsWith(match)) {
            return s;
        }
        return replacement + s.substring(match.length());
    }

    protected static String replaceLast(final String s, final String match, final String replacement) {
        if (!s.endsWith(match)) {
            return s;
        }
        return s.substring(0, s.length() - match.length()) + replacement;
    }

    protected static String replaceLastUnlessAll(final String s, final String match, final String replacement) {
        if (!s.endsWith(match) || s.length() == match.length()) {
            return s;
        }
        return s.substring(0, s.length() - match.length()) + replacement;
    }

    protected final String replaceAll(final String s, final String match, final String replacement) {
        return StringUtils.replace(s, match, replacement);
    }

    protected static String removeDuplicateChars(final String s) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        if (s.length() > 0) {
            sb.append(s.charAt(0));
            for (int q = 1; q < s.length(); q++) {
                final char c = s.charAt(q);
                if (c != sb.charAt(sb.length() - 1)) {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    private static String replaceSelectedUpperCaseInternationalCharacters(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < s.length(); q++) {
            char c = s.charAt(q);
            if (c == '\u00dc') { // U with two dots
                c = 'Y';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public final String[] encode(final String wordsString) {
        final String[] words = StringUtils.split(replaceNonCharsWithSpace(wordsString),
                                                 " ".toCharArray(), true, false);
        final String[] encodedWords = new String[words.length];
        for (int q = encodedWords.length - 1; q >= 0; q--) {
            final String word = words[q];
            if (word == null || word.length() == 0) {
                encodedWords[q] = word;
            } else {
                final String s = replaceSelectedUpperCaseInternationalCharacters(word.toUpperCase());
                encodedWords[q] = encodeSingleUpperCaseWord(StringUtils.foldDiacritics(s));
            }
        }
        return encodedWords;
    }

}
