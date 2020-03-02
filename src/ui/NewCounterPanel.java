package ui;

import counter.CounterException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Panel for specifying values for a new counter.
 *
 * Undocumented.
 *
 * @author Hugh Osborne
 * @version January 2020
 */
class NewCounterPanel extends JPanel implements FocusListener, DocumentListener {
    private JTextField fromField = new JTextField(), toField = new JTextField(), stepField = new JTextField(), nameField = new JTextField();
    private int from, to, step;
    private boolean stepEdited, nameEdited;
    private String name;

    NewCounterPanel() {
        super();
        setLayout(new GridLayout(4,1));
        add(new JLabel("From: "));
        add(fromField);
        fromField.addFocusListener(this);
        add(new JLabel("To: "));
        add(toField);
        toField.addFocusListener(this);
        add(new JLabel("Step: "));
        add(stepField);
        stepField.addFocusListener(this);
        stepField.getDocument().addDocumentListener(this);
        add(new JLabel("Name: "));
        add(nameField);
        nameField.getDocument().addDocumentListener(this);
        setVisible(false);
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getStep() {
        return step;
    }

    public String getName() {
        return name;
    }

    public NewCounterPanel read() throws CounterException {
        setVisible(true);
        boolean nameEdited=false, stepEdited=false;
        int result = JOptionPane.showConfirmDialog(null, this, "Create counter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                from = Integer.parseInt(fromField.getText());
                to = Integer.parseInt(toField.getText());
                step = Integer.parseInt(stepField.getText());
                name = nameField.getText();
                setVisible(false);
            } catch (NumberFormatException error) {
                throw new CounterException("Could not create counter.\nFormat error in input." + error.getMessage());
            }
        } else {
            throw new CounterException("Could not create counter.\n\"OK\" option not selected.");
        }
        return this;
    }

    public void focusGained(FocusEvent e) {
    }

    private int readIntField(JTextField field) throws NumberFormatException {
        if (field.getText().isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(field.getText());
        }
    }

    private boolean nonUserEdit = false;

    private void setFieldText(JTextField field,String text) {
        nonUserEdit = true;
        field.setText(text);
        nonUserEdit = false;
    }
    private boolean ignoreFocusLoss = false;

    @Override
    public void focusLost(FocusEvent e) {
        if (ignoreFocusLoss) return;
        try {
            from = readIntField(fromField);
            to = readIntField(toField);
            step = readIntField(stepField);
            if (to-from != 0) {
                if (!stepEdited) {
                    if (to > from) {
                        step = 1;
                        setFieldText(stepField,"1");
                    } else {
                        step = -1;
                        setFieldText(stepField,"-1");
                    }
                } else if ((to-from)*step < 0) {
                    ignoreFocusLoss = true;
                    JOptionPane.showMessageDialog(this, "Current step value, " + step + ", is in the wrong direction for current values of to (" + to + ") and from (" + from + ").\nIt will be changed to " + (-step), "Step change", JOptionPane.INFORMATION_MESSAGE);
                    ignoreFocusLoss = false;
                    step = -step;
                    setFieldText(stepField,"" + step);
                }
            }
            if (!nameEdited) {
                name = "AbstractCounter(" + from + "=[" + step + "]=>" + to + ")";
                setFieldText(nameField,name);
            }
        } catch (NumberFormatException error) {
            //ignore
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (nonUserEdit) return;
        if (e.getDocument()==stepField.getDocument() && !stepField.getText().isEmpty()) {
            stepEdited = true;
        }
        if (e.getDocument()==nameField.getDocument() && !nameField.getText().isEmpty()) {
            nameEdited = true;
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (nonUserEdit) return;
        if (e.getDocument()==stepField.getDocument() && !stepField.getText().isEmpty()) {
            stepEdited = true;
        }
        if (e.getDocument()==nameField.getDocument() && !nameField.getText().isEmpty()) {
            nameEdited = true;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (nonUserEdit) return;
        if (e.getDocument()==stepField.getDocument() && !stepField.getText().isEmpty()) {
            stepEdited = true;
        }
        if (e.getDocument()==nameField.getDocument() && !nameField.getText().isEmpty()) {
            nameEdited = true;
        }
    }
}
