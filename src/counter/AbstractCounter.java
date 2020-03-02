package counter;

import java.util.Random;

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

public abstract class AbstractCounter extends Thread
{
    /**
     * Counting starts at the value "from", takes place in increments of "step", and 
     * terminates when the counter passes the "limit" value.
     **/
    private int from,limit,step;
    
    /**
     * The counter is shared between all instances of AbstractCounter.
     **/
    private static int counter;

    /**
     * The most detailed constructor, allowing for specification of all parameters of the counter.
     *
     * @param name    the name of this counter
     * @param from the value at which the counter starts
     * @param limit   the limiting value (if the counter passes this value counting will stop)
     * @param step    the amount the counter will be incremented by
     * 
     * @throws CounterException if:
     * <ul>
     * <li> The increment is set to 0 ("step" == 0)
     * <li> The counter counts in the "wrong direction" (e.g. from 0 to 10 in steps of -1)
     *   <ul>
     *   <li> If "limit" &gt; "from" then "step" must be positive
     *   <li> If "limit" &lt; "from" then "step" must be negative
     *   <li> If "limit" == "from" then it doesn't matter
     *   </ul>
     * I.e. "step"*("limit"-"from") must always be &ge; 0
     * </ul>
     **/
    public AbstractCounter(String name, int from, int limit, int step) throws CounterException {
        super(name);
        this.from = from;
        this.limit = limit;
        if (step == 0) { // Increment is zero
            throw new CounterException("AbstractCounter's increment must be non-zero");
        } else if (step * (limit-from) < 0) { // Increment is in "the wrong direction"
            throw new CounterException("AbstractCounter does not count in the right direction to reach its limit");
        } else {
            this.step = step;
        }
    }
    
    /**
     * Constructor for a "nameless" counter.  All other parameters can be specified.
     * A name is generated for the AbstractCounter summarising the values of its parameters.
     *
     * @param from the value at which the counter starts
     * @param limit   the limiting value (if the counter passes this value counting will stop)
     * @param step    the amount the counter will be incremented by
     * @throws CounterException if:
     * <ul>
     * <li> The increment is set to 0 ("step" == 0)
     * <li> The counter counts in the "wrong direction" (e.g. from 0 to 10 in steps of -1)
     *   <ul>
     *   <li> If "limit" &gt; "from" then "step" must be positive
     *   <li> If "limit" &lt; "from" then "step" must be negative
     *   <li> If "limit" == "from" then it doesn't matter
     *   </ul>
     * I.e. "step"*("limit"-"from") must always be &ge; 0
     * </ul>
     **/
    AbstractCounter(int from, int limit, int step) throws CounterException {
        this("AbstractCounter(" + from + "=[" + (step >=0 ? "+" : "") + step + "]=>" + limit + ")",from,limit,step);
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
     * @param name    the name of this counter
     * @param from the value at which the counter starts
     * @param limit   the limiting value (if the counter passes this value counting will stop)
     **/
    AbstractCounter(String name, int from, int limit) {
        super(name);
        this.from = from;
        this.limit = limit;
        step = (limit > from ? 1 : -1);
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
     * @param from the value at which the counter starts
     * @param limit   the limiting value (if the counter passes this value counting will stop)
     **/
    public AbstractCounter(int from, int limit) {
        this("AbstractCounter(" + from + "=[" + (limit > from ? "+1" : "-1") + "]=>" + limit + ")",from,limit);
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
     * @param name    the name of this counter
     * @param limit   the limiting value (if the counter passes this value counting will stop)
     **/
    AbstractCounter(String name, int limit) {
        this(name,0,limit);
    }

    /**
     * A "nameless", "stepless" constructor with no initial count.
     * @param limit   the limiting value (if the counter passes this value counting will stop)
     * A name is generated for the AbstractCounter summarising the values of its parameters.
     * Counting will start at zero.
     * The step size is set to count by ones in the right direction. I.e.
     * <ul>
     * <li> If "limit" &gt; 0 then "step" is set to +1
     * <li> If "limit" &lt; then "step" is set to -1
     * <li> If "limit" == 0 then it doesn't matter what "step" is (here it is set to -1)
     * </ul>
     **/
    AbstractCounter(int limit) {
        this(0,limit);
    }

    /**
     * Get this counter's step size.
     * @return this counter's step size.
     */
    public int getStep() {
        return step;
    }

    /**
     * Get this counter's initial value.
     * @return this counter's initial value.
     */
    public int getFrom() {
        return from;
    }

    /**
     * Get this counter's limiting value (the value beyond which this counter will stop).
     * @return this counter's limiting value.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Get the current value of the shared count.
     * @return the current value of the shared count.
     */
    public static int getCount() {
        return counter;
    }

    /**
     * Provide a facility for switching tracing on/off.  All counters share the same tracing
     * toggle.
     **/
    private static boolean tracingOn = false;
    
    /**
     * Switch tracing on.
     **/
    public static void traceOn() {
        tracingOn = true;
    }
    
    /**
     * Switch tracing off.
     **/
    public static void traceOff() {
        tracingOn = false;
    }

    /**
     * Toggle tracing.  Switch from tracing on to tracing off, and vice versa.
     */
    public static void toggleTrace() {
        tracingOn = !tracingOn;
    }

    /**
     * It's helpful to have lines in the trace numbered.
     */
    private static int traceLineNo = 0;

    /**
     * Reset the trace line number.
     */
    public static void resetTraceLineNumber() {
        traceLineNo = 0;
    }

    /**
     * Add counter information (counter name, counter value) to a basic trace descriprion.
     * @param description a description of the trace step (started, stepped, checked, finished_/
     * @return the basic description with additional counter information.
     */
    protected String traceString(String description) {
        return (++traceLineNo) + ":\t [" + getName() + "] \t" + description + ": \t" + counter;
    }

    /**
     * Send the trace to stdout.
     * @param description The trace that will be output.
     */
    public void trace(String description) {
        System.out.println(traceString(description));
    }

    /**
     * A counter will pause, or delay, between each action (starting the count, checking the count's value, stepping
     * the count) by a random time between a minimum and a maximum delay.  Delay times are specified in milliseconds.
     **/
    private static int minimumDelay = 0, maximumDelay = 10;
    
    /**
     * Set the maximum delay in seconds.
     * @param maximum the maximum delay required, in seconds.
     * The minimum delay is set to zero.
     * @throws CounterException if the requested delay is less than zero seconds.
     **/
    public static void setDelay(double maximum) throws CounterException {
        if (maximum < 0) {
            throw new CounterException("Attempt to set the delay interval to 0" + "==>" + maximum + ". The maximum delay can not be less than the minimum");
        }
        minimumDelay = 0;
        maximumDelay = (int) (maximum*1000);
    }
    
    /**
     * Set both the minimum and maximum delay in seconds.
     * @param minimum the minimum delay desired, in seconds.
     * @param maximum the maximum delay desired, in seconds.
     * @throws CounterException if the requested maximum delay is not greater than or equal to the requested minimum, or
     *         if either of the specified delays is negative.
     */
    public static void setDelay(double minimum,double maximum) throws CounterException {
        if (minimum < 0 || maximum < minimum) {
            throw new CounterException("Attempt to set the delay interval to " + minimum + "==>" + maximum + ". The maximum delay can not be less than the minimum");
        }
        minimumDelay = (int) (minimum*1000);
        maximumDelay = (int) (maximum*1000);
    }

    /**
     * Used to generate random delays between the minimum and maximum delay values.
     */
    private static Random random = new Random();

    /**
     * Get a random delay.
     * @return a random delay (in milliseconds).
     */
    protected int getDelayTime() {
        int delay = 0;
        if (maximumDelay != 0) {
            delay = minimumDelay + random.nextInt(maximumDelay - minimumDelay);
        }
        return delay;
    }

    /**
     * Internal delay method for slowing down the counter
     **/
    protected void delay() {
        try {
            sleep(getDelayTime());
        } catch (InterruptedException ie) {}
    }

    /**
     * Wait, then start the counter by setting the count to this counter's initial value.
     **/
    public void startCount() {
        delay();
        doStartCount();
    }

    /**
     * Start the counter by setting the count to this counter's initial value (as an atomic action).
     */
    protected synchronized void doStartCount() {
        counter = from;
        if (tracingOn) trace("started");
    }

    /**
     * Wait, then step the count by this counter's step value.
     **/
    public void stepCount() {
        doStepCount();
        delay();
    }

    /**
     * Step the count by this counter's step value (as an atomic action).
     **/
    protected synchronized void doStepCount() {
        counter += step;
        if (tracingOn) trace("stepped");
    }

    /**
     * Wait, then check whether the count has reached, or passed this counter's limiting value.
     * @return true iff the count has reached, or passed this counter's limiting value.
     **/
    public boolean isCountFinished() {
        boolean check = doCheckIfCountFinished();
        delay();
        return check;
    }

    /**
     * Check whether the count has reached, or passed this counter's limiting value (as an atomic action).
     * The count has reached the limit if the count and the limit are equal.
     * If the increment is positive the count has passed the limit if it is greater than the limit.
     * If the increment is less than zero the count must be lower than its limit.
     * @return true iff the count has reached, or passed this counter's limiting value.
     **/
    protected synchronized boolean doCheckIfCountFinished() {
        boolean finished =
                (step > 0 && counter >= limit) || (step < 0 && counter <= limit);
        if (tracingOn) trace("checked");
        return finished;

    }

    /**
     * Wait, then end this counter's run,
     */
    public void endCount() {
        doEndCount();
    }

    /**
     * End this counter's run.
     */
    protected void doEndCount() {
        if (tracingOn) trace("finished");
    }

}
