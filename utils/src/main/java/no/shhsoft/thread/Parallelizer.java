package no.shhsoft.thread;

import no.shhsoft.utils.ThreadUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Parallelizer
implements Runnable {

    private static final Object INSTANCE_NUMBER_LOCK = new Object();
    private static int instanceNumber;
    private final Thread[] threads;
    private Runnable task;
    private volatile boolean shutdown;
    private final Object taskLock = new Object();

    private void taskLockWait() {
        try {
            taskLock.wait();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Parallelizer(final int numThreads) {
        final String name;
        synchronized (INSTANCE_NUMBER_LOCK) {
            name = "Parallelizer-" + ++instanceNumber;
        }
        threads = new Thread[numThreads];
        for (int q = threads.length - 1; q >= 0; q--) {
            final Thread thread = new Thread(this);
            thread.setName(name + "-" + (q + 1));
            thread.start();
            threads[q] = thread;
        }
    }

    public Parallelizer() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public int getNumThreads() {
        return threads.length;
    }

    public void shutdown() {
        shutdown = true;
        synchronized (taskLock) {
            taskLock.notifyAll();
        }
        for (int q = threads.length - 1; q >= 0; q--) {
            ThreadUtils.join(threads[q]);
        }
    }

    public void execute(final Runnable newTask) {
        synchronized (taskLock) {
            if (shutdown) {
                throw new RuntimeException("Parallelizer no longer valid.");
            }
            while (!shutdown && task != null) {
                taskLockWait();
            }
            if (!shutdown) {
                task = newTask;
                taskLock.notifyAll();
            }
            while (!shutdown && task != null) {
                taskLockWait();
            }
        }
    }

    @Override
    public void run() {
        while (!shutdown) {
            Runnable runnable = null;
            synchronized (taskLock) {
                while (!shutdown && task == null) {
                    taskLockWait();
                }
                if (!shutdown) {
                    runnable = task;
                    task = null;
                    taskLock.notifyAll();
                }
            }
            if (runnable != null) {
                synchronized (taskLock) {
                    taskLock.notifyAll();
                }
                runnable.run();
            }
        }
    }

}
