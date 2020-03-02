package counter;

import java.util.Set;

/**
 * A thread set is a set of Threads which provides a facility for running all the threads in the set concurrently.
 * 
 * @author Hugh Osborne 
 * @version January 2020
 */
public interface ThreadSet<T extends Thread> extends Set<T>, Runnable
{
	/**
	 * Start all the threads in this set.
	 */
	public void startSet();

	/**
	 * Wait for all the threads in this set to finish.
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 */
	public void joinSet() throws InterruptedException;

}
