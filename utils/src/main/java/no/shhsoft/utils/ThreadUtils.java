package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ThreadUtils {

    private ThreadUtils() {
    }

    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
    }

    public static void sleep(final long millis, final int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (final InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
    }

    public static boolean sleepOrInterrupt(final long millis) {
        try {
            Thread.sleep(millis);
            return false;
        } catch (final InterruptedException e) {
            return true;
        }
    }

    public static boolean sleepOrInterrupt(final long millis, final int nanos) {
        try {
            Thread.sleep(millis, nanos);
            return false;
        } catch (final InterruptedException e) {
            return true;
        }
    }

    public static void join(final Thread thread) {
        try {
            thread.join();
        } catch (final InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
    }

    public static void join(final Thread thread, final long millis) {
        try {
            thread.join(millis);
        } catch (final InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
    }

    public static void join(final Thread thread, final long millis, final int nanos) {
        try {
            thread.join(millis, nanos);
        } catch (final InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
    }

}
