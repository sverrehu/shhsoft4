package no.shhsoft.thread;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class PeriodicRunnableInvoker
implements Runnable {

    private final long msBetweenInvocations;
    private volatile boolean done;
    private final Runnable runnable;

    public PeriodicRunnableInvoker(final Runnable runnable, final long msBetweenInvocations) {
        this.runnable = runnable;
        this.msBetweenInvocations = msBetweenInvocations;
    }

    public void stop() {
        done = true;
    }

    @Override
    public void run() {
        while (!done) {
            final long nsTimeBefore = System.nanoTime();
            runnable.run();
            final long nsTimeSpent = System.nanoTime() - nsTimeBefore;
            final long msToWait = msBetweenInvocations - nsTimeSpent / 1000000L;
            if (msToWait > 0L) {
                try {
                    Thread.sleep(msToWait);
                } catch (final InterruptedException e) {
                    done = true;
                }
            } else {
                Thread.yield();
            }
        }
    }

}
