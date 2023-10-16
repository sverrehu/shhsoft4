package no.shhsoft.utils;

import no.shhsoft.utils.PropertyExpander.MissingPropertyPolicy;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class PropertyExpanderTest {

    private final PropertyExpander simplePropertyExpander;
    private final PropertyExpander multiPropertyExpander;

    public PropertyExpanderTest() {
        final Properties simpleProperties = new Properties();
        simpleProperties.put("foo", "bar");
        simpleProperties.put("foobar", "xyzzy");
        simplePropertyExpander = new PropertyExpander(simpleProperties);
        final Properties otherProperties = new Properties();
        otherProperties.put("foo", "gazonk");
        otherProperties.put("xyzzy", "foobar");
        multiPropertyExpander = new PropertyExpander(new Properties[] {otherProperties,
        simpleProperties});
    }

    @Test(expected = PropertyExpander.UnterminatedPlaceholderException.class)
    public void shouldTrowExceptionOnUnterminatedPlaceholder() {
        simplePropertyExpander.expandProperties("${xxx");
    }

    @Test(expected = PropertyExpander.MissingPropertyException.class)
    public void shouldTrowExceptionOnUnknownProperty() {
        final PropertyExpander expander = new PropertyExpander();
        expander.expandProperties("${xxx}");
    }

    @Test(expected = PropertyExpander.MissingPropertyException.class)
    public void shouldTrowExceptionOnUnknownProperty2() {
        final PropertyExpander expander = new PropertyExpander();
        expander.setMissingPropertyPolicy(MissingPropertyPolicy.THROW_EXCEPTION);
        expander.expandProperties("${xxx}");
    }

    @Test
    public void shouldReplaceWithEmptyWhenMissingProperty() {
        final PropertyExpander expander = new PropertyExpander();
        expander.setMissingPropertyPolicy(MissingPropertyPolicy.REPLACE_WITH_EMPTY);
        assertEquals("foobar", expander.expandProperties("foo${xxx}bar"));
    }

    @Test
    public void shouldKeepWhenMissingProperty() {
        final PropertyExpander expander = new PropertyExpander();
        expander.setMissingPropertyPolicy(MissingPropertyPolicy.KEEP_PLACEHOLDER);
        assertEquals("foo${xxx}bar", expander.expandProperties("foo${xxx}bar"));
    }

    @Test
    public void shouldNotExpand() {
        assertEquals("$}", simplePropertyExpander.expandProperties("$}"));
    }

    @Test
    public void shouldExpandSimple() {
        assertEquals("xbarbary", simplePropertyExpander.expandProperties("x${foo}${foo}y"));
    }

    @Test
    public void shouldExpandWithOtherPlaceholderMarkers() {
        try {
            simplePropertyExpander.setPlaceholderStartMarker("[[");
            simplePropertyExpander.setPlaceholderEndMarker("]]");
            assertEquals("${qwe}bar", simplePropertyExpander.expandProperties("${qwe}[[foo]]"));
        } finally {
            simplePropertyExpander.setPlaceholderStartMarker(
                PropertyExpander.DEFAULT_PLACEHOLDER_START_MARKER);
            simplePropertyExpander.setPlaceholderEndMarker(
                PropertyExpander.DEFAULT_PLACEHOLDER_END_MARKER);
        }
    }

    @Test
    public void shouldFindPropertiesFromTwoSources() {
        assertEquals("xyzzy", multiPropertyExpander.expandProperties("${foobar}"));
        assertEquals("foobar", multiPropertyExpander.expandProperties("${xyzzy}"));
    }

    @Test
    public void shouldFindPrioritizedProperty() {
        assertEquals("gazonk", multiPropertyExpander.expandProperties("${foo}"));
    }

}
