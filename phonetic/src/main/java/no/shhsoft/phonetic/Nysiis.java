package no.shhsoft.phonetic;

/**
 * TODO: Note that this implementation doesn't return the same values
 * as an Internet based encoder out there.  Don't know which one is correct.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Nysiis
extends AbstractPhoneticEncoder {

    @Override
    protected String encodeSingleUpperCaseWord(final String word) {
        String code = word;
        code = replaceFirst(code, "MAC", "MCC");
        code = replaceFirst(code, "KN", "N");
        code = replaceFirst(code, "K", "C");
        code = replaceFirst(code, "PH", "FF");
        code = replaceFirst(code, "PF", "FF");
        code = replaceFirst(code, "SCH", "SSS");
        code = replaceLast(code, "EE", "Y");
        code = replaceLast(code, "IE", "Y");
        code = replaceLast(code, "DT", "D");
        code = replaceLast(code, "RT", "D");
        code = replaceLast(code, "RD", "D");
        code = replaceLast(code, "NT", "D");
        code = replaceLast(code, "ND", "D");

        final StringBuilder sb = new StringBuilder();
        final char[] wordChars = code.toCharArray();
        final int len = wordChars.length;
        sb.append(wordChars[0]);
        int idx = 1;
        while (idx < len) {
            final char c = wordChars[idx];
            if (c == 'E' && idx < len - 1 && wordChars[idx + 1] == 'V') {
                sb.append("AF");
                idx += 2;
                continue;
            }
            if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
                sb.append('A');
                ++idx;
                continue;
            }
            if (c == 'Q') {
                sb.append('G');
                ++idx;
                continue;
            }
            if (c == 'Z') {
                sb.append('S');
                ++idx;
                continue;
            }
            if (c == 'M') {
                sb.append('N');
                ++idx;
                continue;
            }
            if (c == 'K') {
                if (idx < len - 1 && wordChars[idx + 1] == 'N') {
                    sb.append("N");
                    idx += 2;
                    continue;
                }
                sb.append('C');
                ++idx;
                continue;
            }
            if (c == 'S' && idx < len - 2 && wordChars[idx + 1] == 'C' && wordChars[idx + 1] == 'H') {
                sb.append("SSS");
                idx += 3;
                continue;
            }
            if (c == 'P' && idx < len - 1 && wordChars[idx + 1] == 'H') {
                sb.append("FF");
                idx += 2;
                continue;
            }
            if (c == 'H' && (!isUpperCaseVovel(wordChars[idx - 1]) || (idx < len - 1 && !isUpperCaseVovel(wordChars[idx + 1])))) {
                sb.append(wordChars[idx - 1]);
                ++idx;
                continue;
            }
            if (c == 'W' && isUpperCaseVovel(wordChars[idx - 1])) {
                sb.append(wordChars[idx - 1]);
                ++idx;
                continue;
            }
            if (c != sb.charAt(sb.length() - 1)) {
                sb.append(c);
            }
            ++idx;
        }
        code = sb.toString();
        code = replaceLastUnlessAll(code, "S", "");
        code = replaceLastUnlessAll(code, "AY", "Y");
        code = replaceLastUnlessAll(code, "A", "");
        return removeDuplicateChars(code);
    }

}
