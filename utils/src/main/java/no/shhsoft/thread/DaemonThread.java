package no.shhsoft.thread;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class DaemonThread
implements Runnable {

    private static final Logger LOG = Logger.getLogger(DaemonThread.class.getName());
    private boolean started;
    private Thread thread;
    private boolean vmShouldWaitForThisThread = false;
    private volatile boolean done;

    private Thread getOrCreateThread() {
        if (thread == null) {
            thread = new Thread(this);
            thread.setUncaughtExceptionHandler((thread, t) -> {
                LOG.log(Level.WARNING, "Uncaught exception in thread " + thread.getName(), t);
                throw new RuntimeException(t);
            });
        }
        return thread;
    }

    protected boolean implShouldStop() {
        /* Subclasses may override */
        return false;
    }

    protected final boolean shouldStop() {
        return implShouldStop() || done;
    }

    protected void beforeStart() {
        /* Subclasses may override */
    }

    protected void afterStart() {
        /* Subclasses may override */
    }

    protected void beforeStop() {
        /* Subclasses may override */
    }

    protected void afterStop() {
        /* Subclasses may override */
    }

    public final synchronized boolean isStarted() {
        return started;
    }

    public final synchronized void setVmShouldWaitForThisThread(final boolean status) {
        if (isStarted()) {
            throw new RuntimeException("setVmShouldWaitForThisThread may not be called after the thread is started.");
        }
        vmShouldWaitForThisThread = status;
    }

    public final synchronized void start() {
        beforeStart();
        if (isStarted()) {
            return;
        }
        thread = getOrCreateThread();
        thread.setDaemon(!vmShouldWaitForThisThread);
        done = false;
        thread.start();
        started = true;
        afterStart();
    }

    public final synchronized void stop() {
        beforeStop();
        if (!isStarted()) {
            afterStop();
            return;
        }
        done = true;
        thread.interrupt();
        started = false;
        thread = null;
        afterStop();
    }

    public final synchronized void interrupt() {
        if (!isStarted()) {
            return;
        }
        thread.interrupt();
    }

    public final synchronized void setPriority(final int pri) {
        getOrCreateThread().setPriority(pri);
    }

    public final synchronized int getPriority() {
        return getOrCreateThread().getPriority();
    }

    public final synchronized void setName(final String name) {
        getOrCreateThread().setName(name);
    }

    public final synchronized String getName() {
        return getOrCreateThread().getName();
    }

    public final synchronized void setUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler eh) {
        getOrCreateThread().setUncaughtExceptionHandler(eh);
    }

    /* Must be implemented by the child class. */
    @Override
    public abstract void run();

}
