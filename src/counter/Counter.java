package counter;

/**
 * A simple, potentially shared concurrent, counter class which can print a trace of its behaviour.
 * The non-static methods publicly available are
 * <ul>
 * <li> void startCount(): start the counter
 * <li> void stepCount(): step the counter
 * <li> boolean isCountFinished(): has the counter passed its limit?
 * </ul>
 * The following static methods are also available which will modify the behaviour of all
 * Counters
 * <ul>
 * <li> void traceOn(): print a trace of changes to counters to stdout
 * <li> void traceOff(): do not print a trace
 * <li> void setDelay(int delay): slow down counters to increase the chance of time slicing
 * </ul>
 *
 * @author Hugh Osborne
 * @version January 2020
 **/

public class Counter extends AbstractCounter {

    //----------------------------------Constructors-------------------------------------------//

    /**
     * The most detailed constructor, allowing for specification of all parameters of the counter.
     *
     * @param name  the name of this counter
     * @param from  the value at which the counter starts
     * @param limit the limiting value (if the counter passes this value counting will stop)
     * @param step  the amount the counter will be incremented by
     * @throws CounterException if:
     *                          <ul>
     *                          <li> The increment is set to 0 ("step" == 0)
     *                          <li> The counter counts in the "wrong direction" (e.g. from 0 to 10 in steps of -1)
     *                          <ul>
     *                          <li> If "limit" &gt; "from" then "step" must be positive
     *                          <li> If "limit" &lt; "from" then "step" must be negative
     *                          <li> If "limit" == "from" then it doesn't matter
     *                          </ul>
     *                          I.e. "step"*("limit"-"from") must always be &ge; 0
     *                          </ul>
     **/
    public Counter(String name, int from, int limit, int step) throws CounterException {
        super(name, from, limit, step);
    }

    /**
     * Constructor for a "nameless" counter.  All other parameters can be specified.
     * A name is generated for the AbstractCounter summarising the values of its parameters.
     *
     * @param from  the value at which the counter starts
     * @param limit the limiting value (if the counter passes this value counting will stop)
     * @param step  the amount the counter will be incremented by
     * @throws CounterException if:
     *                          <ul>
     *                          <li> The increment is set to 0 ("step" == 0)
     *                          <li> The counter counts in the "wrong direction" (e.g. from 0 to 10 in steps of -1)
     *                          <ul>
     *                          <li> If "limit" &gt; "from" then "step" must be positive
     *                          <li> If "limit" &lt; "from" then "step" must be negative
     *                          <li> If "limit" == "from" then it doesn't matter
     *                          </ul>
     *                          I.e. "step"*("limit"-"from") must always be &ge; 0
     *                          </ul>
     **/
    Counter(int from, int limit, int step) throws CounterException {
        super(from, limit, step);
    }

    /**
     * Constructor where the step size is not specified.
     * The step size is set to count by ones in the right direction. I.e.
     * <ul>
     * <li> If "limit" &gt; "from" then "step" is set to +1
     * <li> If "limit" &lt; "from" then "step" is set to -1
     * <li> If "limit" == "from" then it doesn't matter what "step" is (here it is set to -1)
     * </ul>
     *
     * @param name  the name of this counter
     * @param from  the value at which the counter starts
     * @param limit the limiting value (if the counter passes this value counting will stop)
     **/
    Counter(String name, int from, int limit) {
        super(name, from, limit);
    }

    /**
     * "Nameless", "stepless" constructor.
     * A name is generated for the AbstractCounter summarising the values of its parameters.
     * The step size is set to count by ones in the right direction. I.e.
     * <ul>
     * <li> If "limit" &gt; "from" then "step" is set to +1
     * <li> If "limit" &lt; "from" then "step" is set to -1
     * <li> If "limit" == "from" then it doesn't matter what "step" is (here it is set to -1)
     * </ul>
     *
     * @param from  the value at which the counter starts
     * @param limit the limiting value (if the counter passes this value counting will stop)
     **/
    public Counter(int from, int limit) {
        super(from, limit);
    }

    /**
     * Constructor where neither the step size, nor the initial count is specified.
     * Counting will start at zero.
     * The step size is set to count by ones in the right direction. I.e.
     * <ul>
     * <li> If "limit" &gt; 0 then "step" is set to +1
     * <li> If "limit" &lt; 0 then "step" is set to -1
     * <li> If "limit" == 0 then it doesn't matter what "step" is (here it is set to -1)
     * </ul>
     *
     * @param name  the name of this counter
     * @param limit the limiting value (if the counter passes this value counting will stop)
     **/
    Counter(String name, int limit) {
        super(name, limit);
    }

    /**
     * A "nameless", "stepless" constructor with no initial count.
     *
     * @param limit the limiting value (if the counter passes this value counting will stop)
     *              A name is generated for the AbstractCounter summarising the values of its parameters.
     *              Counting will start at zero.
     *              The step size is set to count by ones in the right direction. I.e.
     *              <ul>
     *              <li> If "limit" &gt; 0 then "step" is set to +1
     *              <li> If "limit" &lt; then "step" is set to -1
     *              <li> If "limit" == 0 then it doesn't matter what "step" is (here it is set to -1)
     *              </ul>
     **/
    Counter(int limit) {
        super(limit);
    }


    //--------------------------------------Tracing--------------------------------------------//

    /**
     * Switch tracing on.
     **/
    public static void traceOn() {
        AbstractCounter.traceOn();
    }

    /**
     * Switch tracing off.
     **/
    public static void traceOff() {
        AbstractCounter.traceOff();
    }

    /**
     * Set the maximum delay in seconds.
     * @param maximum the maximum delay required, in seconds.
     * The minimum delay is set to zero.
     * @throws CounterException if the requested delay is less than zero seconds.
     **/

    //----------------------------------Speed control------------------------------------------//

    public static void setDelay(double maximum) throws CounterException {
        AbstractCounter.setDelay(maximum);
    }

    /**
     * Set both the minimum and maximum delay in seconds.
     * @param minimum the minimum delay desired, in seconds.
     * @param maximum the maximum delay desired, in seconds.
     * @throws CounterException if the requested maximum delay is not greater than or equal to the requested minimum, or
     *         if either of the specified delays is negative.
     */
    public static void setDelay(double minimum,double maximum) throws CounterException {
        AbstractCounter.setDelay(minimum,maximum);
     }

    //----------------------------------"User" methods----------------------------------------//

    /**
     * Wait, then start the counter by setting the count to this counter's initial value.
     **/
    public void startCount() {
        super.startCount();
    }

    /**
     * Wait, then step the count by this counter's step value.
     **/
    public void stepCount() {
        super.stepCount();
    }

    /**
     * Wait, then check whether the count has reached, or passed this counter's limiting value.
     * @return true iff the count has reached, or passed this counter's limiting value.
     **/
    public boolean isCountFinished() {
        return super.isCountFinished();
    }

    /**
     * Wait, then end this counter's run,
     */
    public void endCount() {
        super.endCount();
    }

    //------------------------------------Run method------------------------------------------//

    /**
     * Run this counter.
     */
    public void run() {
        startCount();
        while (!isCountFinished()) {
            stepCount();
        }
        endCount();
    }
}
