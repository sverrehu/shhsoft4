package no.shhsoft.phonetic;

/**
 * Sverre's Phonetic Encoding for Norwegian Names.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Spenn
extends AbstractPhoneticEncoder {

    @Override
    protected String encodeSingleUpperCaseWord(final String word) {
        String code = word;
        code = replaceFirst(code, "CI", "CE");
        code = replaceFirst(code, "CECI", "SESI");
        code = replaceFirst(code, "CE", "SE");
        code = replaceFirst(code, "MAC", "MAK");
        code = replaceFirst(code, "MC", "MAK");
        code = replaceFirst(code, "KN", "N");
        code = replaceFirst(code, "KH", "K");
        code = replaceFirst(code, "KJE", "KE");
        code = replaceFirst(code, "SCH", "S");
        code = replaceFirst(code, "SKJ", "S");
        code = replaceFirst(code, "SJ", "S");
        code = replaceFirst(code, "EI", "E");
        code = replaceFirst(code, "OI", "OY");
        code = replaceFirst(code, "EI", "OY");
        code = replaceFirst(code, "EY", "OY");
        code = replaceFirst(code, "GJ", "J");
        code = replaceLast(code, "SSEN", "SEN");
        code = replaceLast(code, "SSON", "SEN");
        code = replaceLast(code, "SON", "SEN");
        code = replaceLast(code, "DT", "T");
        code = replaceLast(code, "RT", "T");
        code = replaceLast(code, "RD", "R");
        code = replaceLast(code, "NT", "T");
        code = replaceLast(code, "ND", "N");
        code = replaceLast(code, "AH", "A");
        code = replaceLast(code, "LD", "L");
        code = replaceLast(code, "MAD", "MED");
        code = replaceLast(code, "MET", "MED");
        code = replaceLast(code, "EW", "U");
        code = replaceLast(code, "SZ", "S");
        code = replaceLast(code, "Y", "I");
        code = replaceLast(code, "IE", "I");
        code = replaceLast(code, "OB", "OP");
        code = replaceLast(code, "UB", "OP");
        code = replaceLast(code, "CE", "SE");
        code = replaceLast(code, "ID", "I");
        code = replaceLast(code, "ELLE", "EL");
        code = replaceLast(code, "ELLA", "EL");
        code = replaceLast(code, "ETTE", "ET");
        code = replaceLast(code, "IA", "JA");
        code = replaceAll(code, "DTK", "K");
        code = replaceAll(code, "CH", "K");
        code = replaceAll(code, "CK", "K");
        code = replaceAll(code, "CA", "KA");
        code = replaceAll(code, "C", "K");
        code = replaceAll(code, "TH", "T");
        code = replaceAll(code, "DT", "T");
        code = replaceAll(code, "X", "KS");
        code = replaceAll(code, "Z", "S");
        code = replaceAll(code, "W", "V");
        code = replaceAll(code, "RN", "N");
        code = replaceAll(code, "AH", "A");
        code = replaceAll(code, "NG", "N");
        code = replaceAll(code, "PH", "F");
        code = replaceAll(code, "PF", "F");
        code = replaceAll(code, "OUI", "OVI");
        code = replaceAll(code, "QU", "KV");
        code = replaceAll(code, "IJ", "I");
        final StringBuilder sb = new StringBuilder();
        boolean lastWasVovel = false;
        for (int q = 0; q < code.length(); q++) {
            final char c = code.charAt(q);
            if (!isUpperCaseVovel(c)) {
                sb.append(c);
                lastWasVovel = false;
            } else {
                if (!lastWasVovel) {
                    sb.append(c);
                }
                lastWasVovel = true;
            }
        }
        code = sb.toString();
        return removeDuplicateChars(code);
    }

}
