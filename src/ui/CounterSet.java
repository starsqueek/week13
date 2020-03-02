package ui;

import counter.ThreadHashSet;

import javax.swing.*;
import java.awt.*;

public class CounterSet extends ThreadHashSet<Counter> {
    @Override
    public boolean add(Counter counter) {
        boolean add = super.add(counter);
        return add;
    }

    private static CounterLine line = new CounterLine();


    private static JFrame frame = new JFrame("Counters");

    private static void createAndShowGUI() {
        //Create and set up the window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        line.rangeChanged(-125,256);
        line.setPaintTicks(true);
        line.setPaintLabels(true);
        frame.getContentPane().add(line, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
