package no.shhsoft.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractReloadingObjectHolder<T> {

    private static final Logger LOG = Logger.getLogger(AbstractReloadingObjectHolder.class.getName());
    private long updateIntervalMs = 60L * 1000L;
    private long lastChecked = -1L;
    private T object;

    private static void error(final String message, final Throwable cause,
                              final boolean throwException) {
        if (cause != null) {
            LOG.log(Level.WARNING, message, cause);
        } else {
            LOG.warning(message);
        }
        if (throwException) {
            throw new RuntimeException(message, cause);
        }
    }

    private void replaceObjectWithNewLoad(final boolean throwExceptionOnError) {
        LOG.fine("Loading " + getDescription() + " from " + getSourceDescription());
        final T newObject;
        try {
            newObject = load();
        } catch (final Throwable t) {
            error("Exception caught while trying to load " + getDescription() + " from "
                  + getSourceDescription() + ": " + t.getMessage(), t, throwExceptionOnError);
            return;
        }
        if (newObject == null) {
            error("Unable to load " + getDescription() + " from " + getSourceDescription()
                  + ", load result was null", null, throwExceptionOnError);
            return;
        }
        synchronized (this) {
            object = newObject;
        }
    }

    private void reloadIfUpdated() {
        if (isUpdated()) {
            replaceObjectWithNewLoad(false);
        }
    }

    private void conditionallyReloadIfUpdated() {
        final long now = System.currentTimeMillis();
        if (object == null) {
            replaceObjectWithNewLoad(true);
            lastChecked = now;
        } else if (now > lastChecked + updateIntervalMs) {
            reloadIfUpdated();
            lastChecked = now;
        }
    }

    protected abstract String getDescription();

    protected abstract String getSourceDescription();

    protected abstract T load();

    protected abstract boolean isUpdated();

    public final void setUpdateIntervalMs(final long upateIntervalMs) {
        this.updateIntervalMs = upateIntervalMs;
    }

    public final long getUpdateIntervalMs() {
        return updateIntervalMs;
    }

    public final synchronized T get() {
        conditionallyReloadIfUpdated();
        return object;
    }

}
