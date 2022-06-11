package com.swingstwo.uielements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;

public class NumSpinnerCellEditor extends DefaultCellEditor {
    JSpinner spinner;
    JSpinner.NumberEditor editor;
    JFormattedTextField textField;
    SpinnerNumberModel model;
    boolean valueSet;

    public NumSpinnerCellEditor(Double stepSize, String formatPattern) {
        super(new JTextField());
        // Note: setting initial value as Double in SpinnerNumberModel is crucial to get fractional step sizes
        model = new SpinnerNumberModel(0.01d, null, null, stepSize);
        spinner = new JSpinner(model);
        editor = new JSpinner.NumberEditor(spinner, formatPattern);
        spinner.setEditor(editor);
        spinner.setBorder(null);
        textField = editor.getTextField();
        textField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent fe) {
                SwingUtilities.invokeLater(() -> {
                    if (valueSet) {
                        textField.setCaretPosition(1);
                    } else {
                        textField.selectAll();
                    }
                });
            }

            public void focusLost(FocusEvent fe) {
            }
        });
        textField.addActionListener(ae -> stopCellEditing());
    }

    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column
    ) {
        if (!valueSet) {
            spinner.setValue(value);
        }
        SwingUtilities.invokeLater(() -> textField.requestFocus());
        return spinner;
    }

    public boolean isCellEditable(EventObject eo) {
        if (eo instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) eo;
            textField.setText(String.valueOf(ke.getKeyChar()));
            valueSet = true;
        } else {
            valueSet = false;
        }
        return true;
    }

    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    public boolean stopCellEditing() {
        try {
            editor.commitEdit();
            spinner.commitEdit();
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid value, discarding.");
        }
        return super.stopCellEditing();
    }
}
