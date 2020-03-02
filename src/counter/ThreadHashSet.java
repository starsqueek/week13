package counter;

import java.util.HashSet;

/**
 * An implementation of thread sets.
 * A thread set is a set of Threads which provides a facility for running all the threads in the set concurrently.
 *
 * A thread set is also a Runnable, so can, itself, be run as a Thread.
 *
 * @author Hugh Osborne
 * @version January 2020
 */

public class ThreadHashSet<T extends Thread> extends HashSet<T> implements ThreadSet<T>, Runnable {

    @Override
    /**
     * Start all the threads in this set.
     */
    public void startSet() {
        // Implement startSet here.
    }

    /**
     * Wait for all the threads in this set to finish.
     * @throws InterruptedException if any thread has interrupted the current thread.
     */
    public void joinSet() throws InterruptedException {
        // Implement joinSet here.
    }

    /**
     * Run this Runnable.
     * The run method will start all threads, and then wait for them to finish.
     * Perhaps unadvisedly, it ignores any InterruptedExceptions.
     */
    public void run() {
        startSet();
        try {
            joinSet();
        } catch (InterruptedException ie) {}
    }
}
