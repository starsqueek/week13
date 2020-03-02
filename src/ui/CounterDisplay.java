package ui;

import javax.swing.*;
import java.util.Hashtable;

/**
 * UI display for counter value.
 *
 * Undocumented.
 *
 * @author Hugh Osborne
 * @version January 2020
 */
public class CounterDisplay extends JSlider {
    private final static int MAX_MAJOR_TICKS = 25;

    public void rangeChanged(int minimum, int maximum) {
        int range = maximum - minimum;
        int majorTick = 1;
        do {
            majorTick *= 10;
        } while (majorTick < range/MAX_MAJOR_TICKS);
        setMajorTickSpacing(majorTick);
        if (majorTick >= 10) {
            setMinorTickSpacing(majorTick/10);
        }
        int startAt = 0;
        if (minimum < 0) {
            while (startAt > minimum) {
                startAt -= majorTick;
            }
        } else {
            while (startAt < minimum) {
                startAt += majorTick;
            }
            startAt -= majorTick;
        }
        int endAt = 0;
        if (maximum < 0) {
            while (endAt > maximum) {
                endAt -= majorTick;
            }
            endAt += majorTick;
        } else {
            while (endAt < maximum) {
                endAt += majorTick;
            }
        }
        setMinimum(startAt);
        setMaximum(endAt);
        Hashtable<Integer,JComponent> labels = createStandardLabels(majorTick);
        if (startAt < minimum) {
            labels.remove(startAt);
        }
        if (endAt > maximum) {
            labels.remove(endAt);
        }
        setLabelTable(labels);
        setValue(Counter.getCount());
    }

}
