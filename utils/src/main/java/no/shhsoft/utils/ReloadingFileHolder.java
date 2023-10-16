package no.shhsoft.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ReloadingFileHolder
extends AbstractReloadingObjectHolder<String> {

    private final String description;
    private final String fileName;
    private long lastLoadTime;
    private final String charsetName;

    @Override
    protected String getDescription() {
        return description;
    }

    @Override
    protected String getSourceDescription() {
        return fileName;
    }

    @Override
    protected boolean isUpdated() {
        return new File(fileName).lastModified() > lastLoadTime;
    }

    @Override
    protected String load() {
        lastLoadTime = new File(fileName).lastModified();
        try {
            return new String(IoUtils.readFile(fileName), charsetName);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ReloadingFileHolder(final String description, final String fileName, final String charsetName) {
        this.description = description;
        this.fileName = fileName;
        this.charsetName = charsetName;
    }

}
