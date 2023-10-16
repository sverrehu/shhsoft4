package no.shhsoft.json.impl.generator;

import no.shhsoft.utils.StringUtils;
import no.shhsoft.web.utils.HtmlUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HtmlJsonGeneratorImpl
extends AbstractFormattingJsonGenerator {

    private static final int SPACES_PER_INDENT_LEVEL = 4;
    private boolean breakStringsAtNewlinesGeneratingInvalidJson = false;

    public HtmlJsonGeneratorImpl() {
    }

    public HtmlJsonGeneratorImpl(final boolean breakStringsAtNewlinesGeneratingInvalidJson) {
        this.breakStringsAtNewlinesGeneratingInvalidJson = breakStringsAtNewlinesGeneratingInvalidJson;
    }

    @Override
    protected String getNewline() {
        return "<br/>\r\n";
    }

    @Override
    protected String getIndent(final int level) {
        final StringBuilder sb = new StringBuilder();
        for (int q = 0; q < level * SPACES_PER_INDENT_LEVEL; q++) {
            sb.append("&nbsp;");
        }
        return sb.toString();
    }

    @Override
    protected String getPreamble() {
        return "<div style=\"font-family:Menlo,Monaco,Consolas,monospace;color:#000\">\r\n"
            + (breakStringsAtNewlinesGeneratingInvalidJson ? "/* Might contain invalid JSON strings, due to line breaks after \\n. */<br/>\r\n" : "");
    }

    @Override
    protected String getPostamble() {
        return "\r\n</div>\r\n";
    }

    @Override
    protected String decorateNull(final String s) {
        return decorate(s, "#0033b3");
    }

    @Override
    protected String decorateKey(final String s) {
        return decorate(s, "#871094");
    }

    @Override
    protected String decorateString(final String s) {
        String decorated = decorate(s, "#067017");
        if (breakStringsAtNewlinesGeneratingInvalidJson) {
            decorated = StringUtils.replace(decorated, "\\n", "\\n<br/>");
        }
        return decorated;
    }

    @Override
    protected String decorateBoolean(final String s) {
        return decorate(s, "#0033b3");
    }

    @Override
    protected String decorateNumber(final String s) {
        return decorate(s, "#1750eb");
    }

    @Override
    protected String decorateOperator(final String s) {
        return HtmlUtils.encode(s);
    }

    private static String decorate(final String s, final String color) {
        return "<span style=\"color:" + color + "\">" + HtmlUtils.anchorUrls(HtmlUtils.encode(s)) + "</span>";
    }

}
