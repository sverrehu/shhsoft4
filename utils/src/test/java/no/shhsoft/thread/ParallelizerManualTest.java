package no.shhsoft.thread;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ParallelizerManualTest {

    private static final AtomicLong n = new AtomicLong();

    private void calculate() {
        println(Thread.currentThread().getName() + " starting");
        final byte[] source = new byte[50000];
        final byte[] destination = new byte[source.length];
        final long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < 5000L) {
            for (int q = 0; q < source.length; q++) {
                source[q] = 1;
            }
            System.arraycopy(source, 0, destination, 0, source.length);
            for (int q = 0; q < source.length; q++) {
                source[q] = destination[q];
            }
            n.incrementAndGet();
        }
        println(Thread.currentThread().getName() + " ran for " + (System.currentTimeMillis() - t) + " ms");
    }

    private void doit() {
        calculate();
        final long singleThreadResult = n.get();
        System.out.println("Single thread: " + singleThreadResult);
        n.set(0);
        final Parallelizer parallelizer = new Parallelizer(5);
        for (int q = 0; q < parallelizer.getNumThreads(); q++) {
            println("Scheduling job " + (q + 1));
            parallelizer.execute(this::calculate);
        }
        parallelizer.shutdown();
        final long multiThreadResult = n.get();
        println(parallelizer.getNumThreads() + " threads: " + multiThreadResult);
        println("Speedup factor: " + (double) multiThreadResult / singleThreadResult);
    }

    private synchronized void println(final String s) {
        System.out.println(s);
        System.out.flush();
    }

    public static void main(final String[] args) {
        new ParallelizerManualTest().doit();
    }

}
