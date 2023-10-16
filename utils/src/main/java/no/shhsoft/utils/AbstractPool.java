package no.shhsoft.utils;

import java.util.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractPool<T> {

    private int maxActive = 0;
    private long maxWaitTimeMs = 0L;
    private boolean checkValidityOnAllocate = true;
    private boolean checkValidityOnRelease = false;
    private final List<T> available = new ArrayList<>();
    private final Set<T> lended = new HashSet<>();

    private T lend(final T thing) {
        lended.add(thing);
        prepareForAllocate(thing);
        return thing;
    }

    protected abstract T create();

    protected abstract void destroy(T thing);

    protected abstract boolean isValid(T thing);

    protected abstract void prepareForAllocate(T thing);

    public final synchronized T allocate() {
        final long timeAtStartOfAllocate = System.currentTimeMillis();
        for (;;) {
            final int numAvailable = available.size();
            if (numAvailable > 0) {
                final T thing = available.remove(0);
                if (checkValidityOnAllocate) {
                    if (isValid(thing)) {
                        return lend(thing);
                    }
                    destroy(thing);
                } else {
                    return lend(thing);
                }
            }
            if (maxActive <= 0 || numAvailable + lended.size() < maxActive) {
                return lend(create());
            }
            try {
                if (maxWaitTimeMs > 0L) {
                    final long timeToWait = System.currentTimeMillis() - (timeAtStartOfAllocate + maxWaitTimeMs);
                    if (timeToWait <= 0L) {
                        throw new NoSuchElementException("Timeout waiting for free object");
                    }
                    wait(maxWaitTimeMs);
                } else {
                    wait();
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new UncheckedInterruptedException(e);
            }
        }
    }

    public final synchronized void release(final T thing) {
        if (!lended.remove(thing)) {
            return;
        }
        if (checkValidityOnRelease) {
            if (isValid(thing)) {
                available.add(thing);
            } else {
                destroy(thing);
            }
        } else {
            available.add(thing);
        }
        notifyAll();
    }

    public final synchronized void discard(final T thing) {
        if (!lended.remove(thing)) {
            return;
        }
        destroy(thing);
        notifyAll();
    }

    public final synchronized void setMaxActive(final int maxActive) {
        this.maxActive = maxActive;
    }

    public final synchronized int getMaxActive() {
        return maxActive;
    }

    public final synchronized void setMaxWaitTimeMs(final long maxWaitTimeMs) {
        this.maxWaitTimeMs = maxWaitTimeMs;
    }

    public final synchronized long getMaxWaitTimeMs() {
        return maxWaitTimeMs;
    }

    public final synchronized void setCheckValidityOnAllocate(final boolean checkValidityOnAllocate) {
        this.checkValidityOnAllocate = checkValidityOnAllocate;
    }

    public final synchronized boolean isCheckValidityOnAllocate() {
        return checkValidityOnAllocate;
    }

    public final synchronized void setCheckValidityOnRelease(final boolean checkValidityOnRelease) {
        this.checkValidityOnRelease = checkValidityOnRelease;
    }

    public final synchronized boolean isCheckValidityOnRelease() {
        return checkValidityOnRelease;
    }

    public final synchronized int getNumLive() {
        return available.size() + lended.size();
    }

}
