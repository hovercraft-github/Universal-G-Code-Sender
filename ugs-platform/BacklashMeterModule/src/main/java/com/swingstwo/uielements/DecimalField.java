package com.swingstwo.uielements;

import javax.swing.*;
import java.awt.Toolkit;

import java.text.*;

public class DecimalField extends /*JFormattedTextField*/ JTextField
{

    private NumberFormat m_format;
    JFormattedTextField.AbstractFormatter m_formatter;

    public DecimalField(Number value, int columns, NumberFormat f) {
        super(columns);
        setDocument(new DecimalsUseDotDocument(f));
        //super(f);
        m_format = f;
        if (value != null)
            setValue(value.doubleValue());
        //setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    }

    public Object getValue() {
        double retVal = 0.0;

        try {
            retVal = m_format.parse(getText()).doubleValue();
            //} catch (ParseException e) {
        } catch (Exception e) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
            Toolkit.getDefaultToolkit().beep();
            //System.err.println("getValue: could not parse: " + getText());
        }
        return retVal;
    }

    public void setValue(double value)
    {
        setText(m_format.format(value));
    }
}
