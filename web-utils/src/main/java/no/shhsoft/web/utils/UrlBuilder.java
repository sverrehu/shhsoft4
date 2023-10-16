package no.shhsoft.web.utils;

/**
 * Immutable.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UrlBuilder {

    private final String url;

    private UrlBuilder(final String baseUrl) {
        url = baseUrl;
    }

    private UrlBuilder(final String baseUrl, final String name, final String value) {
        final StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        if (baseUrl.indexOf('?') >= 0) {
            sb.append('&');
        } else {
            sb.append('?');
        }
        sb.append(UrlUtils.encode(name));
        sb.append('=');
        sb.append(UrlUtils.encode(value));
        url = sb.toString();
    }

    public static UrlBuilder url(final String baseUrl) {
        return new UrlBuilder(baseUrl);
    }

    public UrlBuilder param(final String name, final String value) {
        return new UrlBuilder(url, name, value);
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public boolean equals(final Object obj) {
        return url.equals(obj);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

}
