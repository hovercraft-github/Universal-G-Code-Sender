package com.swingstwo.tablemodel;

import java.io.Serializable;

/**
 * @author pav<p>
 * This class stores single row field values and its corresponding editable flags.
 * Simple method for getting/setting field values and flags supplied. Other classes,
 * e.g. {@link RecordListBase}, uses vector of such an objects.
 */
public class TableRecordBase implements Serializable, Cloneable {
    private static final long serialVersionUID = -2814703624337806172L;
    /**
     * Does this instance presents a reference or clone of original record.
     */
    protected boolean m_bOwnStorage;
    /**
     * @deprecated up coming improvements suppose field values would be
     * stored internally not as array of separate values but rather
     * as array of special Field class instances containing field value, edit flag members and more.
     */
    private Object[] m_field_values = null;
    /**
     * @deprecated up coming improvements suppose field values would be
     * stored internally not as array of separate values but rather
     * as array of special Field class instances containing field value, edit flag members and more.
     */
    private boolean[] m_is_field_editable = null;

    /** Constructor.
     * Caller must supply values & edit-able flags arrays created with new.
     * Instance will clean it up upon destruction.
     * @param values
     * @param editable
     */
    public TableRecordBase(Object[] values, boolean[] editable)
    {
        m_bOwnStorage = true;
        m_field_values = values;
        m_is_field_editable = editable;
    }

    /** Constructor.
     * Instance will clean up supplied arrays upon destruction if bOwnStorage is true
     * and left them alone otherwise.
     * @param values
     * @param editable
     * @param bOwnStorage
     */
    public TableRecordBase(Object[] values, boolean[] editable, boolean bOwnStorage)
    {
        m_bOwnStorage = bOwnStorage;
        m_field_values = values;
        m_is_field_editable = editable;
    }

    public Object clone() throws CloneNotSupportedException
    {
        //new TableRecordBase(m_field_values.clone(), m_is_field_editable.clone());
        // Fucked piece of shit is this default clone method! :
        TableRecordBase ret =  (TableRecordBase) super.clone();
        ret.m_bOwnStorage = true;
        ret.setValues(m_field_values.clone());
        ret.setEditables(m_is_field_editable.clone());
		/*
		int iFieldNum = m_field_values.length;
		ret.m_field_values = new Object[iFieldNum];
		ret.m_is_field_editable = new boolean[iFieldNum];
		for (int i = 0; i < iFieldNum; i++)
			{
			ret.m_field_values[i] = m_field_values[i];
			if (m_field_values[i] != null)
				{
				if (m_field_values[i].getClass().equals(Date.class))
					ret.m_field_values[i] = new java.util.Date(((java.util.Date)m_field_values[i]).getTime());
				else if (m_field_values[i].getClass().equals(String.class))
					ret.m_field_values[i] = new String(((String)m_field_values[i]));
				else if (m_field_values[i].getClass().equals(Integer.class))
					ret.m_field_values[i] = new Integer(((Integer)m_field_values[i]).intValue());
				else if (m_field_values[i].getClass().equals(Double.class))
					ret.m_field_values[i] = new Double(((Double)m_field_values[i]).doubleValue());
				};
			ret.m_is_field_editable[i] = new Boolean(m_is_field_editable[i]);
			};
		*/
        return ret;
    }

    /**
     * Field values array.
     * @return reference to the internally stored array
     * @deprecated up coming improvements suppose field values would be
     * stored internally not as array of separate values but rather
     * as array of special Field class instances containing field value, edit flag members and more.
     */
    public Object[] getValues()
    {
        return m_field_values;
    }

    /**
     * Field edit enabling flags array.
     * @return reference to the internally stored array
     * @deprecated up coming improvements suppose field values would be
     * stored internally not as array of separate values but rather
     * as array of special Field class instances containing field value, edit flag members and more.
     */
    public boolean[] getEditables()
    {
        return m_is_field_editable;
    }

    /**
     * Sets internal reference to the field values array.
     * @param values
     * @deprecated up coming improvements suppose field values would be
     * stored internally not as array of separate values but rather
     * as array of special Field class instances containing field value, edit flag members and more.
     */
    public void setValues(Object[] values)
    {
        m_bOwnStorage = false;
        m_field_values = values;
    }

    /**
     * Sets internal reference to the field edit-enable flags array.
     * @param editables
     * @deprecated up coming improvements suppose field values would be
     * stored internally not as array of separate values but rather
     * as array of special Field class instances containing field value, edit flag members and more.
     */
    public void setEditables(boolean[] editables)
    {
        m_bOwnStorage = false;
        m_is_field_editable = editables;
    }

    /**
     * Set field editing enable flag based on it's actual index
     * @throws java.util.NoSuchElementException
     */
    public void setFieldEditable(int col, boolean bEditable) throws java.util.NoSuchElementException
    {
        if (col < 0 || col >= m_is_field_editable.length)
            throw new java.util.NoSuchElementException("TableRecordBase: invalid column: " + Integer.valueOf(col).toString());
        m_is_field_editable[col] = bEditable;
    }

    /**
     * Get field editing enable flag based on it's actual index
     */
    public boolean isFieldEditable(int col)
    {
        if (col < 0 || col >= m_is_field_editable.length)
            return false;
        return m_is_field_editable[col];
    }

    public int getColCount()
    {
        return m_field_values.length;
    }

    /**
     * Set field value based on it's actual index
     * @throws java.util.NoSuchElementException
     */
    public void setFieldValue(Object val, int col) throws java.util.NoSuchElementException
    {
        if (col < 0 || col >= m_field_values.length)
            throw new java.util.NoSuchElementException("TableRecordBase: invalid column: " + Integer.valueOf(col).toString());
        //if (m_is_col_editable[col])
        m_field_values[col] = val;
    }

    /**
     * Get field value based on it's actual index
     * @throws java.util.NoSuchElementException
     */
    public Object getFieldValue(int col) throws java.util.NoSuchElementException
    {
        if (col < 0 || col >= m_field_values.length)
            throw new java.util.NoSuchElementException("TableRecordBase: invalid column: " + Integer.valueOf(col).toString());
        return m_field_values[col];
    }

    public void Free(boolean bClearValues)
    {
        if (bClearValues == true && m_field_values != null)
            for (int i = 0; i < m_field_values.length; i++)
                m_field_values[i] = null;
        m_field_values = null;
        m_is_field_editable = null;
    }

    public void finalize()
    {
        Free(m_bOwnStorage);
    }
}
