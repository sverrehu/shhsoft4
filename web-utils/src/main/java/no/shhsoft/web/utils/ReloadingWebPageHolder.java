package no.shhsoft.web.utils;

import no.shhsoft.utils.AbstractReloadingObjectHolder;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ReloadingWebPageHolder
extends AbstractReloadingObjectHolder<String> {

    private final String description;
    private final URL url;

    @Override
    protected String getDescription() {
        return description;
    }

    @Override
    protected String getSourceDescription() {
        return url.toString();
    }

    @Override
    protected boolean isUpdated() {
        /* no idea. leave it to the parent do decide when to reload. */
        return true;
    }

    @Override
    protected String load() {
        try {
            final URLConnection connection = url.openConnection();
            final String contentType = connection.getContentType();
            String charset = ContentTypeUtils.getCharset(contentType);
            if (charset == null) {
                charset = "UTF-8";
            }
            final byte[] content = IoUtils.read(connection.getInputStream());
            return new String(content, charset);
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    public ReloadingWebPageHolder(final String description, final URL url) {
        this.description = description;
        this.url = url;
    }

}
