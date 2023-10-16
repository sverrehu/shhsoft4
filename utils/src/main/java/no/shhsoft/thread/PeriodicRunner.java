package no.shhsoft.thread;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class PeriodicRunner {

    private final Thread thread;
    private final PeriodicRunnableInvoker runnableInvoker;
    private boolean started;

    public PeriodicRunner(final Runnable runnable, final String threadName, final long msBetweenInvocations) {
        runnableInvoker = new PeriodicRunnableInvoker(runnable, msBetweenInvocations);
        thread = new Thread(runnableInvoker, threadName);
        thread.setDaemon(true);
    }

    public synchronized void start() {
        if (started) {
            throw new RuntimeException("Can only be started and stopped once.");
        }
        started = true;
        thread.start();
    }

    public synchronized void stop() {
        if (!started) {
            return;
        }
        runnableInvoker.stop();
    }

}
