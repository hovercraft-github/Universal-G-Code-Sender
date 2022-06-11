package com.swingstwo.tablemodel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.Connection;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * Table model to represent a single row of DB data using diverse separate column UI components.
 */
public class SingleRowTableModel extends GenericTableModel {
    private static final Logger logger = Logger.getLogger(SingleRowTableModel.class.getName());
    private final Object[] m_colUIElements;
    private final Class<?>[] mTypes;
    private final String[] mQueryFieldNames;
    private final TreeSet<Integer> mVisColIX = new TreeSet<Integer>();
    private final TreeSet<Integer> mEditableColIX = new TreeSet<Integer>();

    public SingleRowTableModel( Connection con, Object[] colUIElements, String[] queryFieldNames ) throws Exception {
        super(con);
        assert (colUIElements.length == queryFieldNames.length);
        mQueryFieldNames = queryFieldNames;
        m_colUIElements = colUIElements;
        mTypes = new Class<?>[colUIElements.length];
        int ix = 0;
        for (Object uiElement : m_colUIElements) {
            if (uiElement instanceof JComboBox<?>) {
                mTypes[ix] = Integer.class;
                JComboBox<?> cb = (JComboBox<?>) uiElement;
                if (cb.isVisible())
                    mVisColIX.add(ix);
                if (cb.isEnabled()) {
                    mEditableColIX.add(ix);
                    final int finalIx = ix;
                    cb.addItemListener(new ItemListener() {
                        private final int m_colIx = finalIx;
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                Object item = e.getItem();
                                logger.info(String.format("Col %d JComboBox item=%s", m_colIx, item.toString()));
                                setValueAt(((JComboBox<?>)m_colUIElements[m_colIx]).getSelectedIndex(), 0, m_colIx);
                            }
                        }
                    });
                }
            } else if (uiElement instanceof JCheckBox) {
                mTypes[ix] = Boolean.class;
                JCheckBox cb = (JCheckBox) uiElement;
                if (cb.isVisible())
                    mVisColIX.add(ix);
                if (cb.isEnabled()) {
                    mEditableColIX.add(ix);
                    final int finalIx = ix;
                    cb.addItemListener(new ItemListener() {
                        private final int m_colIx = finalIx;
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            Boolean val = ((JCheckBox) m_colUIElements[m_colIx]).isSelected();
                            setValueAt(val, 0, m_colIx);
                        }
                    });
                }
            } else if (uiElement instanceof JSpinner) {
                JSpinner sp = (JSpinner) uiElement;
                if (sp.getModel() instanceof SpinnerNumberModel) {
                    mTypes[ix] = Double.class;
                    if (sp.isVisible())
                        mVisColIX.add(ix);
                    if (sp.isEnabled()) {
                        mEditableColIX.add(ix);
                        final int finalIx = ix;
                        sp.addChangeListener(new ChangeListener() {
                            private final int m_colIx = finalIx;
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                Double val = (Double) ((SpinnerNumberModel)((JSpinner)m_colUIElements[m_colIx]).getModel())
                                        .getNumber();
                                setValueAt(val, 0, m_colIx);
                            }
                        });
                    }
                } else {
                    throw new Exception("Only SpinnerNumberModel currently supported");
                }
            } else if (uiElement instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) uiElement;
                mTypes[ix] = String.class;
                if (tc.isVisible())
                    mVisColIX.add(ix);
                if (tc.isEditable()) {
                    mEditableColIX.add(ix);
                    final int finalIx = ix;
                    tc.addVetoableChangeListener(new VetoableChangeListener() {
                        private final int m_colIx = finalIx;
                        @Override
                        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                            String val = ((JTextComponent)m_colUIElements[m_colIx]).getText();
                            setValueAt(val, 0, m_colIx);
                        }
                    });
                }
            } else {
                throw new Exception(String.format("Unsupported UI class: %s", uiElement.getClass().toString()));
            }
            ix += 1;
        }
    }

    /**
     * Populates UI controls with data row from the SQL query
     * @param query
     * @throws Exception
     */
    public void Populate(String query) throws Exception {
        super.Populate(query, mTypes,
                mQueryFieldNames, mQueryFieldNames,
                mVisColIX.stream().mapToInt(i->i).toArray(),
                mEditableColIX.stream().mapToInt(i->i).toArray());
        first();
    }

    private int m_currentRowIx = 0;

    private boolean fetch(int rowIx) throws Exception {
        if ((super.getRowCount() == 0) || (rowIx < 0) || (rowIx >= super.getRowCount()))
            return false;
        super.disableAllListeners();
        int colIx = 0;
        try {
            for (Object uiElement : m_colUIElements) {
                Object val = super.getInternalValue(rowIx, colIx);
                if (uiElement instanceof JComboBox<?>) {
                    JComboBox<?> el = (JComboBox<?>) uiElement;
                    if (val != null) {
                        el.setSelectedIndex((Integer) val);
                    } else {
                        el.setSelectedIndex(-1);
                    }
                } else if (uiElement instanceof JCheckBox) {
                    JCheckBox el = (JCheckBox) uiElement;
                    if (val != null) {
                        el.setSelected((Boolean) val);
                    }
                } else if (uiElement instanceof JSpinner) {
                    JSpinner el = (JSpinner) uiElement;
                    if (val != null)
                        el.getModel().setValue(val);
                } else if (uiElement instanceof JTextComponent) {
                    JTextComponent el = (JTextComponent) uiElement;
                    if (val != null) {
                        el.setText((String) val);
                    } else {
                        el.setText("");
                    }
                } else {
                    throw new Exception(String.format("Unsupported UI class: %s", uiElement.getClass().toString()));
                }
                colIx += 1;
            }
        } finally {
            super.enableAllListeners();
        }
        return true;
    }

    public boolean first() throws Exception {
        if (!fetch(0))
            return false;
        m_currentRowIx = 0;
        return true;
    }

    public boolean last() throws Exception {
        int rowIx = super.getRowCount() - 1;
        if (!fetch(rowIx))
            return false;
        m_currentRowIx = rowIx;
        return true;
    }

    public boolean next() throws Exception {
        int rowIx = m_currentRowIx + 1;
        if (!fetch(rowIx))
            return false;
        m_currentRowIx = rowIx;
        return true;
    }

    public boolean previous() throws Exception {
        int rowIx = m_currentRowIx - 1;
        if (!fetch(rowIx))
            return false;
        m_currentRowIx = rowIx;
        return true;
    }
}
