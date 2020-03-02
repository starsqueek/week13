package ui;

import counter.AbstractCounter;
import counter.CounterException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * AbstractCounter UI class.
 *
 * Undocumented.
 *
 * @author Hugh Osborne
 * @version January 2020
 */
public class Counter extends counter.Counter {

    private static NewCounterPanel panel = new NewCounterPanel();
    private static CountersUI setPanel = new CountersUI();

    private JButton icon = new JButton();
    private JMenuItem removeFromRunSet, addFromPredefined, removeFromPredefineds, showInRunSet, showInPredefined;

    public Counter() throws CounterException {
        super(panel.read().getName(),panel.getFrom(), panel.getTo(), panel.getStep());
        initialiseButtons();
    }

    public Counter(int from,int to) throws CounterException {
        super(from,to);
        initialiseButtons();
    }

    public Counter(String name,int from,int to,int step) throws CounterException {
        super(name,from,to,step);
        initialiseButtons();
    }

    private final static Color
        READY = Color.ORANGE,
        RUNNING = Color.YELLOW,
        ACCESSING_COUNT = Color.GREEN,
        FINISHED = Color.LIGHT_GRAY,
        STOPPED = Color.RED;

    private void accessingCount(String label) {
        icon.setBackground(ACCESSING_COUNT);
        icon.setText(label);
    }

    private void running() {
        shortDelay();
        icon.setBackground(RUNNING);
        icon.setText("=>");
    }

    private void initialiseButtons() {
        Counter counter = this;

        icon.setOpaque(true);
        icon.setBackground(READY);
        icon.setEnabled(false);
        icon.setPreferredSize(new Dimension(50,50));
        icon.setToolTipText(getName());

        removeFromRunSet = new JMenuItem(getName());
        removeFromRunSet.setEnabled(true);
        removeFromRunSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CountersUI.removeFromRunSet(counter,true);
            }
        });

        addFromPredefined = new JMenuItem(getName());
        addFromPredefined.setEnabled(true);
        addFromPredefined.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CountersUI.addToRunSet(counter,true);
                CountersUI.getRemoveFromRunSet().add(removeFromRunSet);
            }
        });

        removeFromPredefineds = new JMenuItem(getName());
        removeFromPredefineds.setEnabled(true);
        removeFromPredefineds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CountersUI.removeFromPredefineds(counter,true);
            }
        });

        showInRunSet = new JMenuItem(getName());
        showInRunSet.setEnabled(false);

        showInPredefined = new JMenuItem(getName());
        showInPredefined.setEnabled(false);
    }

    public JComponent getIcon() {
        return icon;
    }
    public JMenuItem getRemoveFromRunSet() { return removeFromRunSet;}
    public JMenuItem getAddFromPredefined() { return addFromPredefined;}
    public JMenuItem getRemoveFromPredefineds() { return removeFromPredefineds;}
    public JMenuItem getShowInRunSet() {return showInRunSet;}
    public JMenuItem getShowInPredefined() {return showInPredefined;}

    private void shortDelay() {
        try {
            sleep(getDelayTime()/10);
        } catch (InterruptedException exception) {}
    }

    public void doStartCount() {
        accessingCount("=" + getFrom());
        super.doStartCount();
        CountersUI.counterStarted();
        running();
    }

    public void doStepCount() {
        accessingCount(getStep() > 0 ? "+" + getStep() : "" + getStep());
        super.doStepCount();
        running();
    }

    private boolean stopped = false;

    public void stopCounter() {
        stopped = true;
    }

    public boolean doCheckIfCountFinished() {
        accessingCount("?");
        boolean check = super.doCheckIfCountFinished();
        running();
        return check || stopped;
    }

    public void doEndCount() {
        if (stopped) {
            icon.setBackground(STOPPED);
            icon.setText("!");
            trace("stopped");
        } else {
            icon.setBackground(FINISHED);
            icon.setText("X");
            trace("finished");
        }
        CountersUI.counterEnded();
    }

    public void trace(String description) {
        CountersUI.traceArea.append(traceString(description) + "\n");
    }

}
