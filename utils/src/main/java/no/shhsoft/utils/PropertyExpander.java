package no.shhsoft.utils;

import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class PropertyExpander {

    public static final String DEFAULT_PLACEHOLDER_START_MARKER = "${";
    public static final String DEFAULT_PLACEHOLDER_END_MARKER = "}";
    private String placeholderStartMarker = DEFAULT_PLACEHOLDER_START_MARKER;
    private String placeholderEndMarker = DEFAULT_PLACEHOLDER_END_MARKER;
    private Properties[] propertiesArray;
    private MissingPropertyPolicy missingPropertyPolicy = MissingPropertyPolicy.THROW_EXCEPTION;

    public PropertyExpander() {
    }

    public PropertyExpander(final Properties properties) {
        setProperties(properties);
    }

    public PropertyExpander(final Properties[] propertiesArray) {
        setProperties(propertiesArray);
    }

    public PropertyExpander(final List<Properties> propertiesList) {
        setProperties(propertiesList);
    }

    private String getProperty(final String name) {
        if (propertiesArray != null) {
            for (final Properties properties : propertiesArray) {
                final String value = properties.getProperty(name);
                if (value != null) {
                    return value;
                }
            }
        }
        switch (missingPropertyPolicy) {
            case THROW_EXCEPTION:
                throw new MissingPropertyException("Property `" + name + "' not found.");
            case REPLACE_WITH_EMPTY:
                return "";
            case KEEP_PLACEHOLDER:
                return placeholderStartMarker + name + placeholderEndMarker;
            case KEEP_TEXT:
                return name;
            default:
                throw new RuntimeException("Unhandled MissingPropertyPolicy `"
                                           + missingPropertyPolicy + "'");
        }
    }

    public enum MissingPropertyPolicy {

        THROW_EXCEPTION,
        REPLACE_WITH_EMPTY,
        KEEP_PLACEHOLDER,
        KEEP_TEXT

    }

    public static final class MissingPropertyException
    extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public MissingPropertyException(final String message) {
            super(message);
        }

    }

    public static final class UnterminatedPlaceholderException
    extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public UnterminatedPlaceholderException(final String message) {
            super(message);
        }

    }

    public void setProperties(final Properties properties) {
        propertiesArray = new Properties[1];
        propertiesArray[0] = properties;
    }

    public void setProperties(final Properties[] propertiesArray) {
        this.propertiesArray = propertiesArray;
    }

    public void setProperties(final List<Properties> propertiesList) {
        propertiesArray = new Properties[propertiesList.size()];
        propertiesArray = propertiesList.toArray(propertiesArray);
    }

    public void setPlaceholderStartMarker(final String placeholderStartMarker) {
        this.placeholderStartMarker = placeholderStartMarker;
    }

    public String getPlaceholderStartMarker() {
        return placeholderStartMarker;
    }

    public void setPlaceholderEndMarker(final String placeholderEndMarker) {
        this.placeholderEndMarker = placeholderEndMarker;
    }

    public String getPlaceholderEndMarker() {
        return placeholderEndMarker;
    }

    public void setMissingPropertyPolicy(final MissingPropertyPolicy missingPropertyPolicy) {
        this.missingPropertyPolicy = missingPropertyPolicy;
    }

    public MissingPropertyPolicy getMissingPropertyPolicy() {
        return missingPropertyPolicy;
    }

    public String expandProperties(final String string) {
        if (string == null) {
            return null;
        }
        if (!string.contains(placeholderStartMarker)) {
            return string;
        }
        final StringBuilder sb = new StringBuilder();
        int from = 0;
        for (;;) {
            int idx = string.indexOf(placeholderStartMarker, from);
            if (idx < 0) {
                sb.append(string.substring(from));
                break;
            }
            sb.append(string, from, idx);
            idx += placeholderStartMarker.length();
            final int idx2 = string.indexOf(placeholderEndMarker, idx);
            if (idx2 < 0) {
                String description = placeholderStartMarker + string.substring(idx);
                if (description.length() > 20) {
                    description = description.substring(0, 17) + "...";
                }
                throw new UnterminatedPlaceholderException(
                    "Cannot find end of property placeholder for `" + description + "'");
            }
            from = idx2 + placeholderEndMarker.length();
            final String macro = string.substring(idx, idx2);
            final String expansion = getProperty(macro);
            if (expansion != null) {
                sb.append(expansion);
            }
        }
        return sb.toString();
    }

}
