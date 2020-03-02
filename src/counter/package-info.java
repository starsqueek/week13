/**
 * A demonstration of concurrent processes in Java.
 *
 * A {@link counter.AbstractCounter} class is defined.  A counter is an object that counts from an initial value to (or just
 * past) a final value. Counters are defined as a subclass of {@link java.lang.Thread}, so counters can be run
 * concurrently.  The internal value of counters is shared between all counters, so concurrent counters will
 * "compete" to change the shared counter value.
 * <p>
 * The {@link counter.ThreadSet} interface extends {@link java.util.Set} to sets of {@link java.lang.Thread}, and adds
 * a {@link counter.ThreadSet#startSet()} method that will start all of the threads in the thread set, and a
 * {@link counter.ThreadSet#joinSet()} method that will wait for all of the threads to terminate.
 * An implementation of this interface is provided in {@link counter.ThreadHashSet}.
 * <p>
 * A demonstration of a possible use of these classes is provided in this package's {@link counter.Main} class.
 * For a visual demonstration please try the {@link ui} package's {@link ui.Main} main class.
 * <p>
 * <i>Note:</i> The {@link counter.AbstractCounter} and {@link counter.ThreadHashSet} classes are currently incomplete.
 *
 * @author Hugh Osborne
 * @version January 2020
 */
package counter;