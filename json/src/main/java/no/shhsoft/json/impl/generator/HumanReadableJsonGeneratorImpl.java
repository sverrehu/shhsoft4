package no.shhsoft.json.impl.generator;

import no.shhsoft.utils.StringUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HumanReadableJsonGeneratorImpl
extends AbstractFormattingJsonGenerator {

    private static final int SPACES_PER_INDENT_LEVEL = 4;

    @Override
    protected String getNewline() {
        return "\r\n";
    }

    @Override
    protected String getIndent(final int level) {
        return StringUtils.stringOfChars(' ', level * SPACES_PER_INDENT_LEVEL);
    }

    @Override
    protected String getPreamble() {
        return "";
    }

    @Override
    protected String getPostamble() {
        return "";
    }

    @Override
    protected String decorateNull(final String s) {
        return s;
    }

    @Override
    protected String decorateKey(final String s) {
        return s;
    }

    @Override
    protected String decorateString(final String s) {
        return s;
    }

    @Override
    protected String decorateBoolean(final String s) {
        return s;
    }

    @Override
    protected String decorateNumber(final String s) {
        return s;
    }

    @Override
    protected String decorateOperator(final String s) {
        return s;
    }

}
