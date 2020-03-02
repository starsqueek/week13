package counter;

/**
 * A demonstration of the use and behaviour of Counters and ThreadSets.
 *
 * @author Hugh Osborne
 * @version January 2020
 */
public class Main {
    /**
     * Demonstrate the behaviour of counters and ThreadSets.  A thread set is populated with two counters, and
     * the thread set's runSet method is used to run the counters concurrently.
     *
     * @param args not used
     * @throws CounterException should not occur
     * @throws InterruptedException should not occur
     */
    public static void main(String[] args) throws CounterException, InterruptedException {
		/*
		 * Create two counters (in a thread set), and then run them with tracing on, so that their
		 * behaviour is visible.
		 */
        ThreadSet<Counter> counters = new ThreadHashSet<>();  // will contain the counters
        counters.add(new Counter(" up ",5,10)); // counter "up" counts from 5 to 10
        counters.add(new Counter("down",5,0)); // counter "down" counts from 5 to 0
        Counter.traceOn(); // switch tracing on
        Counter.setDelay(0,1); // set a delay from 0.0 to 0.1 seconds
        Counter.resetTraceLineNumber();
        counters.run();
    }
}
