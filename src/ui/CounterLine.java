package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class CounterLine extends JSlider implements CounterListener {
    private final static int MAX_MAJOR_TICKS = 25;

    @Override
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
        System.out.println("Major tick: " + majorTick);
        int startAt = 0;
        if (minimum < 0) {
            while (startAt > minimum) {
                startAt -= majorTick;
            }
        } else {
            while (startAt < minimum) {
                startAt += majorTick;
            }
        }
        System.out.println("Start at: " + startAt);
        int endAt = 0;
        if (maximum < 0) {
            while (endAt > maximum) {
                endAt -= majorTick;
            }
        } else {
            while (endAt < maximum) {
                endAt += majorTick;
            }
        }
        System.out.println("End at: " + endAt);
        setMinimum(startAt);
        setMaximum(endAt);
        Hashtable<Integer,JComponent> labels = createStandardLabels(majorTick);
        if (startAt < minimum) {
            labels.remove(startAt);
            labels.put(minimum,new JLabel(""+minimum));
        }
        if (endAt > maximum) {
            labels.remove(endAt);
            labels.put(maximum,new JLabel(""+maximum));
        }
        setLabelTable(labels);
        countChanged();
    }

    @Override
    public void countChanged() {
        setValue(Counter.getCount());
    }

}
