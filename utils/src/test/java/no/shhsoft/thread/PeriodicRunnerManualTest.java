package no.shhsoft.thread;

import no.shhsoft.utils.ThreadUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class PeriodicRunnerManualTest
implements Runnable {

    @Override
    public void run() {
        System.out.println(System.currentTimeMillis() / 1000L);
    }

    public static void main(final String[] args) {
        final PeriodicRunner runner = new PeriodicRunner(new PeriodicRunnerManualTest(), "foo", 1000L);
        runner.start();
        ThreadUtils.sleep(5000L);
        runner.stop();
        System.out.println("Stopped the runner, still waiting some seconds...");
        ThreadUtils.sleep(5000L);
    }

}
