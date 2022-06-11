package com.swingstwo.tablemodel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * @author pav<p>
 * This class presents an actual storage for tabular data, for using in table data model
 * classes in particular (see {@link ItemListBase}).
 * Data is stored as a Vector (see {@link java.util.Vector}) of {@link TableRecordBase} records.
 * Powerful {@link #LoadFromCursor} method supplied for populating from database.
 * It also supports iteration over stored records and acts as the base class for
 * iterators hierarchy (see {@link RecordListVisIter}, {@link RecordListEditableIter}).
 * <p><b>Methods:</b><p>
 * {@link #LoadFromCursor}
 */
public class RecordListBase implements Cloneable, Serializable, RestartableIterator<TableRecord>, Iterable<TableRecord>
{
    private static final long serialVersionUID = 1274454490557569869L;
    private String[] m_sColumnNames = null;
    private String[] m_sColShortNames = null;
    private Class<?>[] m_types = null;
    private int[] m_iVisColIX; // Contains internal indexes of visible columns
    private int[] m_iColVisIX; // Values are visible indexes of internal columns
    private Vector<Integer> m_iVisRowIX;
    private boolean[] m_bIsColEditable;
    private Vector<TableRecordBase> m_records;
    private String[] m_sColFormat = null;

    /**
     * Does this instance presents a reference or clone of original data.
     */
    protected boolean m_bOwnStorage;

    /**
     * Get array of specified row values as Objects
     * @param row - actual (internal) row index
     * @return - reference to the internally stored array
     * @throws ArrayIndexOutOfBoundsException
     */
    public Object[] getRowValues(int row) throws ArrayIndexOutOfBoundsException
    {
        if (m_records == null)
            return null;
        return m_records.elementAt(row).getValues();
    }

    /**
     * Get self (intrinsic) edit ability flags of every cell of specified row as booleans array.
     * Note that cell editing could be disabled on a column level also, which isn't taken into account there.
     * @param row - actual (internal) row index
     * @return - reference to the internally stored array
     * @throws ArrayIndexOutOfBoundsException
     */
    public boolean[] getRowEditFlags(int row) throws ArrayIndexOutOfBoundsException
    {
        if (m_records == null)
            return null;
        return m_records.elementAt(row).getEditables();
    }

    public Object clone() throws CloneNotSupportedException
    {
        //new RecordListBase(null, true); //
        RecordListBase ret = (RecordListBase)super.clone();
        ret.m_bOwnStorage = true;
        ret.m_sColumnNames = m_sColumnNames.clone();
        ret.m_sColShortNames = m_sColShortNames.clone();
        ret.m_types = m_types.clone();
        ret.m_iVisColIX = m_iVisColIX.clone(); // Contains internal indexes of visible columns
        ret.m_iColVisIX = m_iColVisIX.clone(); // Values are visible indexes of internal columns
        ret.m_bIsColEditable = m_bIsColEditable.clone();
        ret.m_iVisRowIX = (Vector<Integer>)m_iVisRowIX.clone();
        ret.m_records = (Vector<TableRecordBase>)m_records.clone();

        //ret.m_bOwnStorage = true;
		/*
		if (m_sColumnNames != null)
			{
			ret.m_sColumnNames = new String[m_sColumnNames.length];
			for (int i = 0; i < m_sColumnNames.length; i++)
				ret.m_sColumnNames[i] = new String(m_sColumnNames[i]);
			};
		if (m_sColShortNames != null)
			{
			ret.m_sColShortNames = new String[m_sColShortNames.length];
			for (int i = 0; i < m_sColShortNames.length; i++)
				ret.m_sColShortNames[i] = new String(m_sColShortNames[i]);
			};
		if (m_types != null)
			{
			ret.m_types = new Class<?>[m_types.length];
			for (int i = 0; i < m_types.length; i++)
				ret.m_types[i] = m_types[i];
			};
		if (m_iVisColIX != null)
			{
			ret.m_iVisColIX = new int[m_iVisColIX.length];
			for (int i = 0; i < m_iVisColIX.length; i++)
				ret.m_iVisColIX[i] = new Integer(m_iVisColIX[i]);
			};
		if (m_iColVisIX != null)
			{
			ret.m_iColVisIX = new int[m_iColVisIX.length];
			for (int i = 0; i < m_iColVisIX.length; i++)
				ret.m_iColVisIX[i] = new Integer(m_iColVisIX[i]);
			};
		if (m_bIsColEditable != null)
			{
			ret.m_bIsColEditable = new boolean[m_bIsColEditable.length];
			for (int i = 0; i < m_bIsColEditable.length; i++)
				ret.m_bIsColEditable[i] = new Boolean(m_bIsColEditable[i]);
			};
		if (m_iVisRowIX != null)
			{
			ret.m_iVisRowIX = new Vector<Integer>();
			ret.m_iVisRowIX.setSize(m_iVisRowIX.size());
			for (int i = 0; i < m_iVisRowIX.size(); i++)
				ret.m_iVisRowIX.add(new Integer(m_iVisRowIX.elementAt(i)));
			};
		*/
        if (m_records != null)
        {
            ret.m_records = new Vector<TableRecordBase>();
            for (int i = 0; i < m_records.size(); i++)
                ret.m_records.add((TableRecordBase) m_records.elementAt(i).clone());
            ret.m_records.setSize(m_records.size());
            ret.m_records.trimToSize();
        };
        return ret;
    }

    /**
     * Copy constructor. Fill in the instance with references to original data.
     *
     * @param src
     * @param bOwnStorage - if true, the instance will clean up arrays contents on instance destruction.
     */
    public RecordListBase(RecordListBase src, boolean bOwnStorage)
    {
        Free();
        m_bOwnStorage = bOwnStorage;
        if (src == null)
        {
            m_iVisRowIX = new Vector<Integer>();
            return;
        }
        m_sColumnNames = src.m_sColumnNames;
        m_sColShortNames = src.m_sColShortNames;
        m_types = src.m_types;
        m_iVisColIX = src.m_iVisColIX;
        m_iColVisIX = src.m_iColVisIX;
        m_bIsColEditable = src.m_bIsColEditable;
        m_records = new Vector<TableRecordBase>(src.m_records);
        m_iVisRowIX = new Vector<Integer>(src.m_iVisRowIX);
    }

	/*
	public RecordListBase(Class<?>[] types, String[] names) throws Exception
		{
		int i;
		if (names.length != types.length)
			throw new Exception("Number of column names in record doesn't match the number of columns.");
		m_iVisRowIX = new Vector<Integer>();
		m_types = types;
		m_sColumnNames = names;
		m_records = new Vector<TableRecordBase>();
		m_bIsColEditable = new boolean[types.length];
		m_iVisColIX = new int[names.length];
		m_iColVisIX = new int[types.length];
		for (i = 0; i < names.length; i++)
			{
			m_iVisColIX[i] = i;
			m_iColVisIX[i] = i;
			}
		}

	public RecordListBase(Class<?>[] types, String[] names, int[] visColIX) throws Exception
		{
		int i;
		if (names.length != visColIX.length)
			throw new Exception("Number of column names in record doesn't match the number of visible columns.");
		if (types.length < visColIX.length)
			throw new Exception("Not all column types specified for the record.");
		m_iVisRowIX = new Vector<Integer>();
		m_types = types;
		m_sColumnNames = names;
		m_iVisColIX = visColIX;
		m_records = new Vector<TableRecordBase>();
		m_bIsColEditable = new boolean[types.length];
		for (i = 0; i < m_bIsColEditable.length; i++)
			m_bIsColEditable[i] = false;
		m_iColVisIX = new int[types.length];
		for (i = 0; i < types.length; i++)
			{
			m_iColVisIX[i] = getVisColForInternalColPrivate(i);
			}
		}

	public RecordListBase(Class<?>[] types, String[] names, int[] visColIX, int[] editableColIX) throws Exception
		{
		int i;
		if (names.length != visColIX.length)
			throw new Exception("Number of column names in record doesn't match the number of visible columns.");
		if (types.length < visColIX.length)
			throw new Exception("Not all column types specified for the record.");
		m_iVisRowIX = new Vector<Integer>();
		m_types = types;
		m_sColumnNames = names;
		m_iVisColIX = visColIX;
		m_records = new Vector<TableRecordBase>();
		m_bIsColEditable = new boolean[types.length];
		for (i = 0; i < m_bIsColEditable.length; i++)
			m_bIsColEditable[i] = false;
		for (i = 0; i < editableColIX.length; i++)
			m_bIsColEditable[editableColIX[i]] = true;
		m_iColVisIX = new int[types.length];
		for (i = 0; i < types.length; i++)
			{
			m_iColVisIX[i] = getVisColForInternalColPrivate(i);
			}
		}
	*/

    /**
     * Constructor. Clones supplied arrays. Resulting  instance owns its internally stored data.
     */
    public RecordListBase(Class<?>[] types, String[] names, int[] visColIX, int[] editableColIX, String colShortNames[]) throws Exception
    {
        int i;
        m_bOwnStorage = true;
        if (names.length != visColIX.length)
            throw new Exception("Number of column names in record doesn't match the number of visible columns.");
        if (types.length < visColIX.length)
            throw new Exception("Not all column types specified for the record.");
        if (types.length != colShortNames.length)
            throw new Exception("Number of column name abreviations in record doesn't match the number of columns.");
        m_iVisRowIX = new Vector<Integer>();
        m_types = new Class<?>[types.length];
        for (i = 0; i < types.length; i++)
            m_types[i] = types[i];
        m_sColumnNames = new String[names.length];
        for (i = 0; i < names.length; i++)
            m_sColumnNames[i] = names[i];
        m_iVisColIX = new int[visColIX.length];
        for (i = 0; i < visColIX.length; i++)
            m_iVisColIX[i] = visColIX[i];
        m_records = new Vector<TableRecordBase>();
        m_bIsColEditable = new boolean[types.length];
        m_sColShortNames = new String[types.length]; //colShortNames;
        for (i = 0; i < m_bIsColEditable.length; i++)
            m_bIsColEditable[i] = false;
        for (i = 0; i < editableColIX.length; i++)
            m_bIsColEditable[editableColIX[i]] = true;
        for (i = 0; i < types.length; i++)
            m_sColShortNames[i] = colShortNames[i];
        m_iColVisIX = new int[types.length];
        for (i = 0; i < types.length; i++)
        {
            m_iColVisIX[i] = getVisColForInternalColPrivate(i);
        }
    }

    /**
     * Returns mutable array of actual (DB) column names.
     * @return
     */
    public String[] getColShortNames() {
        return m_sColShortNames;
    }

    /**
     * Gives each column title array (contains null for columns without a title).
     * Do using immutable approach.
     * @return
     */
    public String[] getColTitles() {
        String[] titles = new String[m_types.length];
        //return ;
        for (int col = 0; col < m_types.length; col ++)
        {
            titles[col] = getColTitle(getVisColForInternalCol(col));
            if (titles[col] != null)
                titles[col] = new String(titles[col]);
        }
        return titles;
    }

    /**
     * Get all columns visibility flags (using immutable approach).
     * Note, that the specific cell could be invisible despite its column is visible due to row been invisble.
     * @return
     */
    public boolean[] getColVisibleFlags()
    {
        boolean[] visible = new boolean[m_types.length];
        for (int col = 0; col < m_types.length; col ++)
        {
            visible[col] = false;
            for (int i = 0; i < m_iVisColIX.length; i++)
                if (m_iVisColIX[i] == col)
                {
                    visible[col] = true;
                    break;
                };
        };
        return visible;
    }

    /**
     * Get every cell effective visibility flags for the specified row - i.e. if row is invisible then all cell returned false;
     * if specific column is invisible then only that column cell return false.
     * Do using immutable approach.
     * @param row - actual (internal) row index
     * @return
     */
    public boolean[] getCellEffVisibleFlags(int row)
    {
        boolean[] visible = new boolean[m_types.length];
        for (int col = 0; col < m_types.length; col ++)
        {
            visible[col] = false;
            if (this.isInternalCellVisible(row, col))
                visible[col] = true;
        }
        return visible;
    }

    /**
     * Get every cell effective edit ability flags for the specified row - i.e. if entire column is disabled then this column cell of any row returns false;
     * otherwise cell returns true or false on its own.
     * Do using immutable approach.
     * @param row - actual (internal) row index
     * @return
     */
    public boolean[] getCellEffEditableFlags(int row)
    {
        boolean[] editable = new boolean[m_types.length];
        for (int col = 0; col < m_types.length; col ++)
        {
            editable[col] = false;
            if (this.isInternalCellEditable(row, col))
                editable[col] = true;
        }
        return editable;
    }

    /**
     * Gets column actual (internal) index by its actual (DB) name.
     * @param abr
     * @return -1 if there is no such column
     */
    public int getColIXByShortName(String abr) //throws Exception
    {
        if (m_sColShortNames == null)
            //throw new Exception("Column short names not defined");
            return -1;
        for (int i = 0; i < m_sColShortNames.length; i++)
        {
            if (m_sColShortNames[i] != null && m_sColShortNames[i].compareToIgnoreCase(abr) == 0)
                return i;
        };
        return -1;
    }

    /**
     * Sets column edit enabling flag by its actual (DB) name.
     * @param sColDBName
     * @param bEditable
     */
    public void setColEditable(String sColDBName, boolean bEditable)
    {
        int col = getColIXByShortName(sColDBName);
        if (col >= 0)
            m_bIsColEditable[col] = bEditable;
    }

    /**
     * Appends record to the record list.
     * @param record
     * @throws Exception if record column number doesn't match with this instance types number.
     */
    public void addRecord(TableRecordBase record) throws Exception
    {
        if (m_types.length != record.getColCount())
            throw new Exception("Invalid column count in the given record.");
        m_records.addElement(record);
        m_iVisRowIX.add(m_records.size()-1);
    }

    /**
     * Substitutes the record at given internal row index.
     * @param record
     * @throws Exception if record column number doesn't match with this instance types number.
     */
    public void setRecord(int row, TableRecordBase record) throws Exception
    {
        if (m_types.length != record.getColCount())
            throw new Exception("Invalid column count in the given record.");
        m_records.set(row, record);
    }

    public void addHiddenRecord(TableRecordBase record) throws Exception
    {
        if (m_types.length != record.getColCount())
            throw new Exception("Invalid column count in the given record.");
        m_records.addElement(record);
        //m_iVisRowIX.add(m_records.size()-1);
    }

    /**
     * Set cell edit enable flag by it's visible row and column indexes.
     * <p>Note: this will have no effect if entire column is disabled for editing.
     * @param bEditable
     * @param visRow
     * @param visCol
     * @throws java.util.NoSuchElementException
     */
    public void setCellEditable(boolean bEditable, int visRow, int visCol) throws java.util.NoSuchElementException
    {
        TableRecordBase record = null;
        if (m_bIsColEditable[m_iVisColIX[visCol]] == false)
            return;
        if (m_records != null)
            record = m_records.elementAt(m_iVisRowIX.elementAt(visRow));
        if (record != null)
        {
            record.setFieldEditable(m_iVisColIX[visCol], bEditable);
        };
    }

    /**
     * Set cell edit enable flag by it's internal (actual) row and column.
     * <p>Note: this will have no effect if entire column is disabled for editing.
     * @param bEditable
     * @param row
     * @param col
     * @throws java.util.NoSuchElementException
     */
    public void setInternalCellEditable(boolean bEditable, int row, int col) throws java.util.NoSuchElementException
    {
        TableRecordBase record = null;
        if (m_bIsColEditable[col] == false)
            return;
        if (m_records != null)
            record = m_records.elementAt(row);
        if (record != null)
        {
            record.setFieldEditable(col, bEditable);
        };
    }

    public int getInternalColForVisCol(int iVisCol)
    {
        if (iVisCol >= 0 && iVisCol < m_iVisColIX.length)
            return m_iVisColIX[iVisCol];
        return -1;
    }

    public int getVisColForInternalCol(int iInternalCol)
    {
        if (iInternalCol >= 0 && iInternalCol < m_iColVisIX.length)
            return m_iColVisIX[iInternalCol];
        return -1;
    }

    private int getVisColForInternalColPrivate(int iInternalCol)
    {
        boolean bFound = false;
        int i;
        for (i = 0; i < m_iVisColIX.length; i++)
            if (m_iVisColIX[i] == iInternalCol)
            {
                bFound = true;
                break;
            };
        if (!bFound)
            return -1;
        return i;
    }

    /**
     * Get effective visibility of the specified cell.
     * This accounts column visibility as wall as row visibility.
     * @param row - actual (internal) row index
     * @param col - actual (internal) column index
     * @return
     */
    public boolean isInternalCellVisible(int row, int col)
    {
        boolean bFound = false;
        for (int i = 0; i < m_iVisColIX.length; i++)
            if (m_iVisColIX[i] == col)
            {
                bFound = true;
                break;
            };
        if (!bFound)
            return false;
        for (int i = 0; i < m_iVisRowIX.size(); i++)
            if (m_iVisRowIX.elementAt(i) == row)
            {
                bFound = true;
                break;
            };
        if (!bFound)
            return false;
        return true;
    }

    /**
     * Get reference to the record given by its internal row index via {@link TableRecord} object.
     * @param row
     * @return null if no such a record
     */
    public TableRecord getRecord(int row) //throws Exception
    {
        TableRecord rec = null;
        if (m_records == null || row < 0 || row > m_records.size())
            return null;
        try {
            rec = new TableRecord(this, row, false); //names, types, values, titles, visible, editable);
        }
        catch (Exception e)
        {
            rec = null;
        };
        return rec;
    }

    /**
     * Get reference (bound=true) or clone (bound=false) of the record given by its internal row index via {@link TableRecord} object.
     * @param row
     * @param bound
     * @return null if no such a record
     */
    public TableRecord getRecord(int row, boolean bound) //throws Exception
    {
        TableRecord rec = null;
        if (m_records == null || row < 0 || row > m_records.size())
            return null;
        try {
            rec = new TableRecord(this, row, !bound); //names, types, values, titles, visible, editable);
        }
        catch (Exception e)
        {
            rec = null;
        };
        return rec;
    }

    /**
     * Get effective edit enable flag of cell specified by its actual (internal) row and column indexes.
     * This accounts entire column edit enabling implemented at the RecordListBase level
     * as well as cell intrinsic edit enabling implemented at the TableRecordBase level.
     * @param row
     * @param col
     * @return
     */
    public boolean isInternalCellEditable(int row, int col)
    {
        TableRecordBase record = null;
        if (m_bIsColEditable[col] == false)
            return false;
        if (m_records != null)
            record = m_records.elementAt(row);
        if (record != null)
        {
            return record.isFieldEditable(col);
        };
        return false;
    }

    /**
     * Get cell effective edit enable flag e by it's visible row and column numbers.
     * @param iVisRow
     * @param iVisCol
     * @return
     */
    public boolean isCellEditable(int iVisRow, int iVisCol)
    {
        String sError = "isCellEditable: ";
        TableRecordBase record = null;

        if ((iVisCol < 0) || (iVisCol >= m_iVisColIX.length))
        {
            System.out.println(sError + "case 1");
            return false;
        };
        if ((m_iVisColIX[iVisCol] < 0) || (m_iVisColIX[iVisCol] >= m_bIsColEditable.length))
        {
            System.out.println(sError + "case 2");
            return false;
        };
        if (m_bIsColEditable[m_iVisColIX[iVisCol]] == false)
            return false;
        if (m_records != null)
        {
            if ((iVisRow < 0) || (iVisRow >= m_iVisRowIX.size()))
            {
                System.out.println(sError + "case 3");
                return false;
            };
            int iInternalRow = m_iVisRowIX.elementAt(iVisRow);
            if ((iInternalRow < 0) || (iInternalRow >= m_records.size()))
            {
                System.out.println(sError + "case 4");
                return false;
            };
            record = m_records.elementAt(iInternalRow);
        }
        else
        {
            sError = "isCellEditable trecord==null: row="
                    +Integer.valueOf(iVisRow).toString()
                    +",col="+Integer.valueOf(iVisCol).toString();
            //throw new Exception(sError);
            System.out.println(sError);
        }
        if (record != null)
        {
            return record.isFieldEditable(m_iVisColIX[iVisCol]);
        };
        return false;
    }

    public int getInternalRowForVisRow(int iVisRow)
    {
        String sError = "getInternalRowForVisRow: ";
        if ((iVisRow < 0) || (iVisRow >= m_iVisRowIX.size()))
        {
            System.out.println(sError + "case 1");
            return -1;
        }
        return m_iVisRowIX.elementAt(iVisRow);
    }

    /**
     * Set cell value for it's actual row & column index
     * @param val - new cell value
     * @param row - internal row index
     * @param col - internal column index
     * @throws java.util.NoSuchElementException
     */
    public void setInternalCellValue(Object  val, int row, int col) throws java.util.NoSuchElementException
    {
        String sError;
        TableRecordBase record = null;
        if (m_records != null)
        {
            int iSize = m_records.size();
            if ((row < 0) || (row >= m_records.size()))
            {
                sError = "setColValue wrong row: intRow="
                        +Integer.valueOf(row).toString()
                        +",intCol="+Integer.valueOf(col).toString()+",m_records.size()==" + iSize;
                throw new java.util.NoSuchElementException(sError);
                //System.out.println(sError);
            }
            if ((col < 0) || (col >= m_bIsColEditable.length))
            {
                sError = "setColValue wrong col: intRow="
                        +Integer.valueOf(row).toString()
                        +",intCol="+Integer.valueOf(col).toString()+",m_records.size()==" + iSize;
                throw new java.util.NoSuchElementException(sError);
                //System.out.println(sError);
            }
            record = m_records.elementAt(row);
        }
        else
        {
            sError = "setColValue trecord==null: intRow="
                    +Integer.valueOf(row).toString()
                    +",intCol="+Integer.valueOf(col).toString();
            //throw new Exception(sError);
            System.out.println(sError);
        }
        if (record != null)
        {
            record.setFieldValue(val, col);
			/* This is not appropriate there. Visual components disables editing by user based on the call to isCellEditable.
			if (m_bIsColEditable[col])
				{
				}
			else
				{
				sError = "setColValue col not editable: intRow="
					+Integer.valueOf(row).toString()
					+",intCol="+Integer.valueOf(col).toString();
				System.out.println(sError);
				}
			*/
        }
        else
        {
            sError = "setColValue wrong row (trecord==null): intRow="
                    +Integer.valueOf(row).toString()
                    +",intCol="+Integer.valueOf(col).toString();
            //throw new Exception(sError);
            System.out.println(sError);
        }
        ;
        //TODO: Throw exceptions there?
    }

    /**
     * Set cell value for it's actual row index & column name
     * @param val - new cell value
     * @param row - internal row index
     * @param colAbrName - column DB name
     * @throws java.util.NoSuchElementException
     */
    public void setInternalCellValue(Object  val, int row, String colAbrName) throws java.util.NoSuchElementException
    {
        TableRecordBase record = null;
        int col = getColIXByShortName(colAbrName);
        if (m_records != null)
            record = m_records.elementAt(row);
        if (record != null)
        {
            if (m_bIsColEditable[col])
                record.setFieldValue(val, col);
        };
    }

    /**
     * Sets cell value for it's visible row & column index.
     * <p>Note: only edit-enabled cells are set.
     * @param val - new cell value
     * @param visRow - visible row index
     * @param visCol - visible column index
     * @throws java.util.NoSuchElementException
     */
    public void setCellValue(Object  val, int visRow, int visCol) throws java.util.NoSuchElementException
    {
        TableRecordBase record = null;
        if (m_records != null)
            record = m_records.elementAt(m_iVisRowIX.elementAt(visRow));
        if (record != null)
        {
            if (m_bIsColEditable[m_iVisColIX[visCol]])
                record.setFieldValue(val, m_iVisColIX[visCol]);
        }
    }

    /**
     * Return formated value for cell's actual row & column index
     * @param row - internal row index
     * @param col - internal column index
     * @return
     * @throws java.util.NoSuchElementException
     */
    public String getInternalCellFormated(int row, int col) throws java.util.NoSuchElementException
    {
        Object val = getInternalCellValue(row, col);
        if (val == null)
            return "";
        String ret = val.toString();
        Class<?> type = m_types[col];
        String sFormat = null;
        if (m_sColFormat != null)
            sFormat = m_sColFormat[col];
        if (sFormat == null)
            sFormat = "";
        if (type == String.class)
            return (String)val;
        else if (type == java.util.Date.class || type == java.sql.Timestamp.class)
        {
            SimpleDateFormat df = null;
            if (sFormat != "")
                df = new SimpleDateFormat(sFormat);
            else
                df = new SimpleDateFormat();
            ret = df.format(val);
        }
        else if (type == Integer.class || type == Double.class || type == Float.class || type == Number.class)
        {
            if (sFormat != "")
                sFormat = "#########.##";
            DecimalFormat df = new DecimalFormat(sFormat, new DecimalFormatSymbols(Locale.US));
            ret = df.format(val);
        }
        return ret;
    }

    /**
     * Return cell value for it's actual row & column index
     * @param row - internal row index
     * @param col - internal column index
     * @return
     * @throws java.util.NoSuchElementException
     */
    public Object getInternalCellValue(int row, int col) throws java.util.NoSuchElementException
    {
        String sError;
        TableRecordBase record = null;
        Object ret = null;

        if (m_records != null)
        {
            if ((row < 0) || (row >= m_records.size()))
            {
                sError = "getColValue wrong row: intrn row=" + Integer.valueOf(row).toString()
                        + ",m_records.size()=" + Integer.valueOf(m_records.size()).toString();
                //System.out.println(sError);
                throw new java.util.NoSuchElementException(sError);
            }
            record = m_records.elementAt(row);
        }
        else
        {
            sError = "getVisColValue: m_records==null: intrn row=" + Integer.valueOf(row).toString()
                    + ",intrn col=" + Integer.valueOf(col).toString();
            System.out.println(sError);
            //throw new java.util.NoSuchElementException(sError);
        };
        if (record != null)
            ret = record.getFieldValue(col);
        return ret;
    }

    /**
     * Return cell value for it's actual row index and DB column name
     * @param row - internal row index
     * @param colAbrName - column name
     * @throws java.util.NoSuchElementException
     */
    public Object getInternalCellValue(int row, String colAbrName) throws java.util.NoSuchElementException
    {
        String sError;
        int col = getColIXByShortName(colAbrName);
        TableRecordBase record = null;
        Object ret = null;
        if (m_records != null)
        {
            if ((row < 0) || (row >= m_records.size()))
            {
                sError = "getColValue wrong row: intrn row=" + Integer.valueOf(row).toString()
                        + ",m_records.size()=" + Integer.valueOf(m_records.size()).toString();
                //System.out.println(sError);
                throw new java.util.NoSuchElementException(sError);
            }
            record = m_records.elementAt(row);
        }
        else
        {
            sError = "getVisColValue: m_records==null: intrn row=" + Integer.valueOf(row).toString()
                    + ",intrn col=" + Integer.valueOf(col).toString();
            System.out.println(sError);
        };
        if (record != null)
            ret = record.getFieldValue(col);
        return ret;
    }

    /**
     * Return cell value for it's visible row & column index
     * @param row - visible row index
     * @param col - visible column index
     * @throws java.util.NoSuchElementException
     */
    public Object getValueAt(int row, int col) throws java.util.NoSuchElementException
    {
        TableRecordBase record = null;
        Object ret = null;
        String sError;
        if (m_records != null)
        {
            if ((row >= m_iVisRowIX.size()) || (row < 0))
                throw new java.util.NoSuchElementException("Invalid vis.row index=" + Integer.valueOf(row).toString()
                        + " not in bounds: 0.." + Integer.valueOf(m_iVisRowIX.size()).toString()
                );
            int iActualRow = m_iVisRowIX.elementAt(row);
            if ((iActualRow >= m_records.size()) || (iActualRow < 0))
                throw new java.util.NoSuchElementException("Invalid row index=" + Integer.valueOf(iActualRow).toString()
                        + " not in bounds: 0.." + Integer.valueOf(m_records.size()).toString()
                );
            record = m_records.elementAt(iActualRow);
        }
        else
        {
            sError = "getVisColValue: m_records==null: vis row=" + Integer.valueOf(row).toString()
                    + ",vis col=" + Integer.valueOf(col).toString();
            System.out.println(sError);
        };
        if (record != null)
        {
            int iVisIXSize = m_iVisColIX.length;
            if ((col < 0) || (col >= iVisIXSize))
            {
                sError = "Invalid vis col index=" + Integer.valueOf(col).toString()
                        + " not in bounds: 0.." + Integer.valueOf(iVisIXSize).toString();
                System.out.println(sError);
            };
            int iInrCol = m_iVisColIX[col];
            if ((iInrCol < 0) || (iInrCol >= record.getColCount()))
            {
                sError = "Invalid intern col index=" + Integer.valueOf(iInrCol ).toString()
                        + " not in bounds: 0.." + Integer.valueOf(record.getColCount()).toString();
                System.out.println(sError);
            };
            ret = record.getFieldValue(iInrCol);
        }
        else
            throw new java.util.NoSuchElementException("getVisColValue: Cell not found: row=" + Integer.valueOf(row).toString()
                    + ",col=" + Integer.valueOf(col).toString());
        return ret;
    }

    /**
     * Return column class for it's internal index
     * @param col - internal (actual) column index
     * @return
     */
    public Class<?> getColumnClass(int col)
    {
        return m_types[col];
    }

    public Class<?>[] getColumnClasses()
    {
        return m_types;
    }

    /**
     * Set format string for specified actual column number
     * @param internCol
     * @return
     */
    public void setColumnFormat(int internCol, String sFormat)
    {
        if (m_sColFormat == null)
            m_sColFormat = new String[m_types.length];
        m_sColFormat[internCol] = sFormat;
    }

    /**
     * Set format strings as array for every column. Array size must match the actual column number.
     * @param sFormats
     * @throws Exception
     */
    public void setColumnFormats(String sFormats[]) throws Exception
    {
        if (m_sColFormat == null)
            m_sColFormat = new String[m_types.length];
        if (sFormats == null || sFormats.length != m_types.length)
            throw new Exception("Number of format specifications doesn't match the number of columns.");
        for (int i = 0; i < sFormats.length; i++)
            m_sColFormat[i] = sFormats[i];
    }

    /**
     * Get format string for specified actual column number
     * @param internCol
     * @return
     */
    public String getColumnFormat(int internCol)
    {
        if (m_sColFormat == null || internCol < 0 || internCol >= m_sColFormat.length)
            return null;
        return m_sColFormat[internCol];
    }

    /**
     * Return column class for it's visible index
     * @param col - visible column index
     * @return
     */
    public Class<?> getVisColumnClass(int col)
    {
        return m_types[m_iVisColIX[col]];
    }

    /**
     * Return internal (actual) columns number
     * @return
     */
    public int getColCount()
    {
        if (m_types != null)
            return m_types.length;
        return 0;
    }

    /**
     * Return visible columns number
     * @return
     */
    public int getVisColCount()
    {
        if (m_iVisColIX != null)
            return m_iVisColIX.length;
        return 0;
    }

    /**
     * Return column title for it's visible index
     * @param visCol - visual column index
     * @return null if column has no title or index out of range
     */
    public String getColTitle(int visCol)
    {
        String ret = null;
        if (m_sColumnNames != null && visCol >= 0 && visCol < m_sColumnNames.length)
            ret = m_sColumnNames[visCol];
        return ret;
    }

    /**
     * Return actual (DB) column name for it's internal index
     * @param col - internal (actual) column index
     * @return
     */
    public String getColShortName(int col)
    {
        String ret = null;
        if (m_sColShortNames != null)
            ret = m_sColShortNames[col];
        return ret;
    }

    /**
     * Get visible rows number
     * @return
     */
    public int getRowCount()
    {
        int ret = 0;
        //if (m_records != null)
        //ret = m_records.size();
        if (m_iVisRowIX != null)
            ret = m_iVisRowIX.size();
        return ret;
    }

    /**
     * Get actual (visible, invisible, any) rows number
     * @return
     */
    public int getInternalRowCount()
    {
        int ret = 0;
        if (m_records != null)
            ret = m_records.size();
        return ret;
    }

    /**
     * Fills the instance with data got from the JDBC result set.
     * Column data types treated based on specifications obtained via constructor rather then using result set meta data.
     * It doesn't close the result set on completion.
     * @param rs
     * @throws Exception
     */
    public void LoadFromCursor(ResultSet rs) throws Exception
    {
        int i;

        while (rs.next())
        {
            Object values[] = new Object[m_types.length];
            boolean field_editable[] = new boolean[m_types.length];
            for (i = 0; i < m_types.length; i++)
            {
                String sColName = m_sColShortNames[i].toUpperCase();
                values[i] = null;
                field_editable[i] = m_bIsColEditable[i]; // Set default field edit enable flag to its column flag.
                if (m_types[i] == Integer.class) {
                    values[i] = rs.getInt(sColName);
                } else if (m_types[i] == Long.class) {
                    values[i] = rs.getLong(sColName);
                } else if (m_types[i] == BigDecimal.class) {
                    values[i] = rs.getBigDecimal(sColName);
                } else if (m_types[i] == java.sql.Timestamp.class || m_types[i] == java.util.Date.class) {
                    values[i] = rs.getTimestamp(sColName);
                } else if (m_types[i] == Double.class) {
                    values[i] = rs.getDouble(sColName);
                } else if (m_types[i] == Boolean.class) {
                    Boolean bNull = null;
                    int ixCol = rs.findColumn(sColName);
                    String sColType = rs.getMetaData().getColumnTypeName(ixCol).toUpperCase();
                    if (sColType == "BOOLEAN") {
                        values[i] = rs.getBoolean(sColName);
                    } else if (sColType.startsWith("V") || sColType.startsWith("C")) {
                        String sVal = rs.getString(sColName);
                        if (rs.wasNull())
                            sVal = null;
                        if (sVal == null)
                            sVal = "Q";
                        sVal = sVal.toUpperCase();
                        if (sVal.startsWith("Y") || sVal.startsWith("Д") || sVal.startsWith("1") || sVal.startsWith("T")) {
                            values[i] = Boolean.TRUE;
                        } else if (sVal.startsWith("N") || sVal.startsWith("Н") || sVal.startsWith("0") || sVal.startsWith("F")) {
                            values[i] = Boolean.FALSE;
                        } else {
                            values[i] = bNull;
                        }
                    } else if (sColType.startsWith("N") || sColType.startsWith("I")) {
                        Double dVal = rs.getDouble(sColName);
                        if (rs.wasNull())
                            dVal = null;
                        if (dVal == null)
                            dVal = (double) -1;
                        if (dVal > 0)
                            values[i] = Boolean.TRUE;
                        else if (dVal == 0)
                            values[i] = Boolean.FALSE;
                        else
                            values[i] = bNull;
                    }
                } else if (m_types[i] == String.class) {
                    values[i] = rs.getString(sColName);
                };
                if (rs.wasNull())
                    values[i] = null;
            };
            TableRecordBase rec = new TableRecordBase(values, field_editable);
            this.addRecord(rec);
        };
    }

    public void Free()
    {
        if (m_bOwnStorage)
        {
            if (m_records != null)
                m_records.clear();
            if (m_iVisRowIX != null)
                m_iVisRowIX.clear();
        }
        m_records = null;
        m_types = null;
        m_sColumnNames = null;
        m_sColShortNames = null;
        m_iVisColIX = null;
        m_iVisRowIX = null;
    }

    public void finalize()
    {
        Free();
    }

    protected int m_iterationRow = -1;

    public void toBegin() {
        m_iterationRow = -1;
    }

    public void toEnd() {
        m_iterationRow = getInternalRowCount();
    }

    public boolean hasNext() {
        if (m_iterationRow < this.getInternalRowCount()-1)
            return true;
        return false;
    }

    public Iterator<TableRecord> iterator() {
        m_iterationRow = -1;
        return this;
    }

    public TableRecord next() throws NoSuchElementException
    {
        TableRecord rec = null;
        if (m_iterationRow < this.getInternalRowCount())
            m_iterationRow++;
        rec = getRecord(m_iterationRow);
        if (rec == null)
        {
            throw new NoSuchElementException("Iteration (next): there is no record " + Integer.valueOf(m_iterationRow).toString());
        };
        return rec;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
