package com.swingstwo.tablemodel;

/**
 *
 * @author pav<p>
 * Extends the {@link TableRecordBase} class with column meta data and some functionality.
 * It's mainly for extracting single record from record list and do something with it.
 * This class is able to operate on its own clone of data or originative {@link RecordListBase} instance record as well,
 * depending on clone parameter of the constructor ({@link #TableRecord}).
 * <p><b>Members</b><p>
 * {@link #m_bOwnStorage}<p>
 * {@link #m_rlSource}<p>
 * {@link #m_iRowIX}<p>
 * {@link #m_types}<p>
 * {@link #m_sColShortNames}<p>
 * {@link #m_sColumnTitles}<p>
 * {@link #m_bIsColVisible}<p>
 */
@SuppressWarnings("serial")
public class TableRecord extends TableRecordBase implements Cloneable
{

    /**
     * Column titles (visible headings). Always cloned.
     */
    private String[] m_sColumnTitles; //i.e. Titles
    /**
     * Actual (DB) column names. Cloned or referenced.
     */
    private String[] m_sColShortNames; //i.e. actual column names
    /**
     * Field data types. Cloned or referenced.
     */
    private Class<?>[] m_types; // Data types
    /**
     * Effective field visibility flags. Always cloned.
     */
    private boolean[] m_bIsColVisible;
    //private boolean[] m_bIsColEditable;
    //private int m_iFieldsCnt; // Contains actual (internal) number of fields in record
    /**
     * Contains actual (internal) row index of this record in the source recordset.
     */
    private int m_iRowIX;
    /**
     * Record set whereof this record originates from.
     */
    private RecordListBase m_rlSource;

    public void finalize()
    {
        for (int i = 0; i < m_sColShortNames.length; i++)
        {
            if (m_bOwnStorage)
            {
                m_sColShortNames[i] = null;
                m_types[i] = null;
                if (m_sColumnTitles != null)
                    m_sColumnTitles[i] = null;
            };
        };
        m_sColumnTitles = null;
        m_sColShortNames = null;
        m_types = null;
        m_bIsColVisible = null;
        m_rlSource = null;
		/*
		if (m_bOwnStorage)
			super.finalize();
		else
			super.Free(false);*/
        super.finalize();
    }

    public Object clone() throws CloneNotSupportedException
    {
        TableRecord ret = (TableRecord)super.clone(); //new TableRecord();
        //ret.m_bOwnStorage = true; super.clone did it
        ret.m_rlSource = null;
        ret.m_iRowIX = m_iRowIX;
        ret.m_types = m_types.clone();
        ret.m_sColShortNames = m_sColShortNames.clone();
        ret.m_bIsColVisible = m_bIsColVisible.clone();
        ret.m_sColumnTitles = m_sColumnTitles.clone();
		/*
		int iFieldsNum = this.getColCount(); //m_types.length;
		boolean[] editable = new boolean[iFieldsNum];
		Object[] values = new Object[iFieldsNum];
		Class<?>[] types = new Class<?>[iFieldsNum];
		String[] sColShortNames = new String[iFieldsNum];
		String[] sTitles = new String[iFieldsNum];
		boolean[] visible = new boolean[iFieldsNum];

		for (int col = 0; col < iFieldsNum; col++)
			{
			types[col] = m_types[col];
			if (m_sColShortNames[col] != null)
				sColShortNames[col] = new String(m_sColShortNames[col]);
			visible[col] = new Boolean(this.m_bIsColVisible[col]);
			if (m_sColumnTitles[col] != null)
				sTitles[col] = new String(this.m_sColumnTitles[col]);
			editable[col] = new Boolean(this.isFieldEditable(col));
			values[col] = this.getFieldValue(col);
			if (values[col] != null)
				{
				if (values[col].getClass().equals(String.class))
					values[col] = new String((String)values[col]);
				else if (values[col].getClass().equals(Integer.class))
					values[col] = new Integer((Integer)values[col]);
				else if (values[col].getClass().equals(Double.class))
					values[col] = new Double((Double)values[col]);
				else if (values[col].getClass().equals(java.util.Date.class))
					values[col] = new java.util.Date(((java.util.Date)values[col]).getTime());
				};
			};
		ret.m_types = types;
		ret.m_sColShortNames = sColShortNames;
		ret.m_bIsColVisible = visible;
		ret.m_sColumnTitles = sTitles;
		ret.setEditables(editable);
		ret.setValues(values);
		*/
        return ret;
    }

    /**
     * Constructor. Fills in the instance from the given record list.
     * @param src - source {@link RecordListBase}
     * @param row - an actual (internal) row of this record in the source record list
     * @param clone - whether to do cloning or referencing
     * @throws Exception
     */
    public TableRecord(RecordListBase src, int row, boolean clone) throws Exception
    {
        super(null, null);
        m_rlSource = src;
        m_iRowIX = row;
        m_bOwnStorage = clone;
        if (src == null || row < 0 || row >= src.getInternalRowCount())
            throw new Exception("TableRecord: source cannot be null. Row must be valid.");
        int iFieldsNum = src.getColCount();
        { // These are always cloned due to their nature.
            m_bIsColVisible = src.getCellEffVisibleFlags(row);
            m_sColumnTitles = src.getColTitles();
        }
        if (m_bOwnStorage)
        { // Cloning values and meta data
            boolean[] got_editable = src.getRowEditFlags(row); //Get cell intrinsic edit enable flags, preserving meaning inherited from the base class.
            Object[] got_values = src.getRowValues(row); //new Object[iFieldsNum];
            TableRecordBase rec_clone = (TableRecordBase)(new TableRecordBase(got_values, got_editable, false)).clone();
            super.setEditables(rec_clone.getEditables());
            super.setValues(rec_clone.getValues());
            m_types = src.getColumnClasses().clone();
            m_sColShortNames = src.getColShortNames().clone();
			/*
			m_types = new Class<?>[iFieldsNum];
			m_sColShortNames = new String[iFieldsNum];
			for (int col = 0; col < iFieldsNum; col++)
				{
				m_types[col] = src.getColumnClass(col);
				if (src.getColShortName(col) != null)
					m_sColShortNames[col] = new String(src.getColShortName(col));
				};
			*/
        }
        else // just reference originative instance data
        {
            super.setEditables(src.getRowEditFlags(row)); //TableRecordBase edit enable flags has cell intrinsic meaning, so we preserve it there.
            super.setValues(src.getRowValues(row));
            m_types = src.getColumnClasses();
            m_sColShortNames = src.getColShortNames();
        }

    }

    /**
     * Copy constructor
     * @param src
     */
    public TableRecord(TableRecord src)
    {
        super(src.getValues(), src.getEditables());
        m_sColShortNames = src.m_sColShortNames;
        //m_iFieldsCnt = m_sColShortNames.length;
        m_types = src.m_types;
        m_sColumnTitles = src.m_sColumnTitles;
        m_bIsColVisible = src.m_bIsColVisible;
        m_iRowIX = src.m_iRowIX;
        m_rlSource = src.m_rlSource;
        m_bOwnStorage = false; //src.m_bIsClone;
    }

    public boolean isClone() {
        return m_bOwnStorage;
    }

    /**
     * Returns internal row index of this record in the source recordset
     * @return
     */
    public int getRowIX()
    {
        return m_iRowIX;
    }

    /**
     * Returns internal column index by its actual (DB) name
     * @param sFieldName
     * @return
     */
    public int getColIX(String sFieldName)
    {
        if (sFieldName != null && m_sColShortNames != null)
            for (int i = 0; i < m_sColShortNames.length; i++)
            {
                if (sFieldName.equalsIgnoreCase(m_sColShortNames[i]))
                    return i;
            };
        return -1;
    }

    public Class<?> getColClass(String sFieldName)
    {
        int ix = getColIX(sFieldName);
        if (ix < 0)
            return null;
        return m_types[ix];
    }

    public String getColTitle(String sFieldName)
    {
        int ix = getColIX(sFieldName);
        if (ix < 0 || m_sColumnTitles == null)
            return null;
        return m_sColumnTitles[ix];
    }

    public boolean isColVisible(String sFieldName)
    {
        int ix = getColIX(sFieldName);
        if (ix < 0 || m_bIsColVisible == null)
            return false;
        return m_bIsColVisible[ix];
    }

    /**
     * Sets field value based on it's actual index.
     * Changes are stored locally or in the originating record list depending on the clone mode specified during construction.
     * @throws java.util.NoSuchElementException
     */
    public void setFieldValue(Object val, int col) throws java.util.NoSuchElementException
    {
        //TODO: throw exceptions likewise the setFieldEditable method
        //if (m_rlSource != null) m_rlSource.setColValue(val, getRowIX(), col);
        super.setFieldValue(val, col);
    }

    /**
     * Sets field value based on it's actual (DB) name.
     * Changes are stored locally or in the originating record list depending on the clone mode specified during construction.
     * @throws java.util.NoSuchElementException
     */
    public void setFieldValue(String sFieldName, Object val) throws java.util.NoSuchElementException
    {
        //TODO: throw exceptions likewise the setFieldEditable method
        int col = getColIX(sFieldName);
        //if (m_rlSource != null) m_rlSource.setColValue(val, getRowIX(), col);
        super.setFieldValue(val, col);
    }

    /**
     * Gets the field value based on its actual (DB) name.
     * @param sFieldName
     * @return field value by it's name
     * @throws java.util.NoSuchElementException
     */
    public Object getFieldValue(String sFieldName) throws java.util.NoSuchElementException
    {
        int ix = getColIX(sFieldName);
        //if (ix < 0)
        //	return null;
        return super.getFieldValue(ix);
    }

    public boolean isFieldEditable(String sFieldName)
    {
        int ix = getColIX(sFieldName);
        if (ix < 0)
            return false;
        return super.isFieldEditable(ix);
    }

    /**
     * Sets field intrinsic (not effective) editing enable flag based on it's actual (internal) index
     * Note: this has no effect if entire column is disabled for editing.
     * Changes are stored locally or in the originating record list depending on the clone mode specified during construction.
     * @throws java.util.NoSuchElementException
     */
    public void setFieldEditable(int col, boolean edit) throws java.util.NoSuchElementException
    {
        if (col < 0 || col >= m_sColShortNames.length)
            throw new java.util.NoSuchElementException("TableRecord: invalid column: " + Integer.valueOf(col).toString());
        //if (m_rlSource != null) m_rlSource.setInternalCellEditable(edit, getRowIX(), col);
        super.setFieldEditable(col, edit);
    }

    /**
     * Sets field intrinsic (not effective) editing enable flag based on it's actual (DB) name.
     * Note: this has no effect if entire column is disabled for editing.
     * Changes are stored locally or in the originating record list depending on the clone mode specified during construction.
     * @param sFieldName
     * @param edit
     * @throws java.util.NoSuchElementException
     */
    public void setFieldEditable(String sFieldName, boolean edit) throws java.util.NoSuchElementException
    {
        int ix = getColIX(sFieldName);
        if (ix < 0) // ?? || m_sColumnTitles == null)
            throw new java.util.NoSuchElementException("TableRecord: invalid column: " + sFieldName);
        //if (m_rlSource != null) m_rlSource.setInternalCellEditable(edit, getRowIX(), ix);
        super.setFieldEditable(ix, edit);
    }
}
