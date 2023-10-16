package no.shhsoft.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ReloadingResourceHolder
extends AbstractReloadingObjectHolder<String> {

    private final String description;
    private final String resourceName;
    private final String charsetName;

    @Override
    protected String getDescription() {
        return description;
    }

    @Override
    protected String getSourceDescription() {
        return resourceName;
    }

    @Override
    protected boolean isUpdated() {
        return true;
    }

    @Override
    protected String load() {
        try {
            return new String(IoUtils.readResourceOrFile(resourceName), charsetName);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ReloadingResourceHolder(final String description, final String resourecName, final String charsetName) {
        this.description = description;
        this.resourceName = resourecName;
        this.charsetName = charsetName;
    }

}
