package com.swingstwo.uielements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class DateSpinnerCellEditor  extends DefaultCellEditor
{
    JSpinner spinner;
    JSpinner.DefaultEditor editor;
    JTextField textField;
    boolean valueSet;

    public DateSpinnerCellEditor() {
        super(new JTextField());
        spinner = new JSpinner(new SpinnerDateModel());
        editor = ((JSpinner.DateEditor)spinner.getEditor());
        textField = editor.getTextField();
        textField.addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent fe ) {
                SwingUtilities.invokeLater(() -> {
                    if ( valueSet ) {
                        textField.setCaretPosition(1);
                    }
                });
            }
            public void focusLost( FocusEvent fe ) {
            }
        });
        textField.addActionListener(ae -> stopCellEditing());
    }

    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column
    ) {
        if ( !valueSet ) {
            spinner.setValue(value);
        }
        SwingUtilities.invokeLater(() -> textField.requestFocus());
        return spinner;
    }

    public boolean isCellEditable( EventObject eo ) {
        if ( eo instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent)eo;
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
        } catch ( java.text.ParseException e ) {
            JOptionPane.showMessageDialog(null,
                    "Invalid value, discarding.");
        }
        return super.stopCellEditing();
    }
}
