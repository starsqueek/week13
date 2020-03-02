/**
 * This package implements a graphical interface for the {@link counter} package.
 * <p>
 * You do not <i>have</i> to use this package in order to complete the exercises.  This can be done using solely the code in
 * the {@link counter} package.  This package is provided <i>solely</i> in order to provide a basic visual
 * presentation of counters.  There should be no need for you to edit any code in this package.
 * </p>
 * <p>
 *     AbstractCounter configurations (sets of counters) can be constructed, loaded and saved using the Configuration menu.
 *     Run sets (of counters) can be constructed "by hand" by using the "Add counter", or "Remove counter" options.
 *     Under "Add counter" you are given the choice of using one of a small number of predefined counters,
 *     or of creating a new counter.  When a new counter is defined, you are given the option of either adding it
 *     to the current run set, or to the set of predefined counters.
 * </p>
 * <p>
 *     In the panel used to create a new counter the values for the step value, and for the counter's name will be
 *     entered automatically.  These automatic values can easily be overridden by editing the relevant fields in the panel.
 * </p>
 * <p>
 *     In the "Remove counter" menu, "Remove all" will remove all counters (i.e. clear the run set, or the set of
 *     predefined counters).
 * </p>
 * <p>
 *     The "Show ..." options will simply list all the counters in the current run set or predefined counters set.
 * </p>
 * <p>
 *     A configuration consists of the current run set, and the current set of predefined counters.
 *     Configurations can be saved to configuration files, using the "Save configuration" and "Save configuration as"
 *     options.  Once a configuration file has been saved it can be loaded into the interface using the "Load configuration"
 *     or "Reload last configuration" option.  A sample configuration file, <tt>config.cnt</tt>, is provided, which defines a
 *     configuration with two counters, one which counts from 5 to 10, in steps of one, and another which counts from
 *     5 to 0, in steps of minus one.  The predefined counters set of this configuration also contains these counters,
 *     plus two more counters, which count from 0 to 10, and from 10 to 0, in step of 1 and -1.
 * </p>
 * <p>
 *     The Trace menu allows you to switch tracing on or off (default is on), and to save the trace output to a file.
 * </p>
 * <p>
 *     The "Run" button will run the current run set, and the "Stop" button can be used to signal that all counters
 *     should stop, whether or not they have reached their limiting value.  The current value of the count is shown in
 *     the value bar.
 * </p>
 * <p>
 *     Counters are shown to the left and right of the trace window, with decrementing counters on the left, and
 *     incrementing counters on the right.  Counters that have not yet started are shown in orange.  Counters in yellow are
 *     active, but not actually accessing the shared count.  When a counter does access the shared count (to set, change,
 *     or check the value) it will be shown in green.  A counter that has finished its run will be grey, except if it
 *     was stopped by the "Stop" button, in which case it will be red.
 * </p>
 * <p>
 *     Note that once a run set has been run, it will need to be reloaded before it can be run again.
 * </p>
 * <p>
 *     The speed slider can be used to control the speed of execution.  Counters will not react <i>immediately</i> to
 *     any change in the speed slider.  Each counter will have to complete its current pause (if it is pausing) before
 *     the speed change takes effect.
 * </p>
 * <p>
 *     This code should definitely <i>not</i> be considered as an example of good coding practice.  Please report any bugs, or
 *     any suggestions for improvements to me.
 * </p>
 *
 * @author Hugh Osborne
 * @version January 2020
 */
package ui;