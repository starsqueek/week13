package ui;

import javax.swing.*;

/**
 * Run a counter set demonstration.
 *
 * Undocumented.
 *
 * @author Hugh Osborne
 * @version January 2020
 */
public class Main {
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CountersUI.setUpPanel();
            }
        });
    }

}
