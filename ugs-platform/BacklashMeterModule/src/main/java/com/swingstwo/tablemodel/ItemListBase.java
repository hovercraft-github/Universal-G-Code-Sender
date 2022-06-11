package com.swingstwo.tablemodel;

import java.io.Serializable;
import java.sql.Connection;


/**
 * @author pav<p>
 * This class serves as base for the table data model classes hierarchy.
 * Although it dosn't implement the {@link javax.swing.table.TableModel} interface,
 * it is implemented by its direct descendant, the {@link GenericTableModel} class.
 * It utilize an internal descendant of the {@link RecordListBase} class
 * to store table data in its m_RecordList member rather then inherit RecordListBase directly.
 * Methods {@link #setRecordList} and {@link #getRecordList} used to substitute and get record list.
 * Also it supports cloning.
 * <p><b>Methods:</b><p>
 * {@link #setRecordList}<p>{@link #getRecordList}
 */
public class ItemListBase implements Cloneable, Serializable {
    private static final long serialVersionUID = 3963303434804330122L;
    protected static Connection m_con;
    private RecordListBase m_RecordList = null;

	/*
	class TableRecord extends TableRecordBase {
		private static final long serialVersionUID = 8831427497150496358L;
		TableRecord(Object[] values) {
			super(values);
			// TODO Auto-generated constructor stub
			}
		TableRecord(Object[] values, boolean[] editable)
			{
			super(values, editable);
			}
		}
		*/

	/*
	class ILRecordList extends RecordListBase {
		private static final long serialVersionUID = 5095544900818397825L;
		public ILRecordList(RecordListBase src, boolean bOwnStorage)
			{
			super(src, bOwnStorage);
			}
		public ILRecordList(Class<?>[] types, String[] names) throws Exception
			{
			super(types, names);
			// TODO Auto-generated constructor stub
			}
		public ILRecordList(Class<?>[] types, String[] names, int[] visColIX) throws Exception
			{
			super(types, names, visColIX);
			}
		*/
		/*
		public ILRecordList(Class<?>[] types, String[] names, int[] visColIX, int[] editableColIX) throws Exception
			{
			super(types, names, visColIX, editableColIX);
			}
		*/
    //}

    public void Free()
    {
        //TODO: check to move this to the setRecordList method
        //*
        if (m_RecordList != null)
        {
            m_RecordList.finalize();
            m_RecordList = null;
        };
        //*/
        setRecordList(null);
    }

    protected void finalize()
    {
        Free();
    }

/* ??
public ItemListBase copy()
{
int i;
ItemListBase ret = new ItemListBase();
return ret;
}
*/

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            return null;
        }
    }

    /**
     * Default constructor
     */
    public ItemListBase()
    {
        setRecordList(null);
    }

/*
 * Copy constructor
 * @param original: source ItemListBase
public ItemListBase(ItemListBase original)
{
	setRecordList(null); //original.m_RecordList; 	???
}
 */

    public ItemListBase(Connection con) throws Exception
    {
        m_con = con;
    }

    /**
     * Assigns the record list. Resulting instance just references the source record list data.
     * <p>Instance will clear referenced RecordList object upon destruction.
     * @param srcRecordList
     */
    public void setRecordList(RecordListBase srcRecordList) {
	/* TODO: check to see is it safe to do m_RecordList.finalize() there:
	if (m_RecordList != null)
		{
		m_RecordList.finalize();
		};
		*/
        m_RecordList = null;
        if (srcRecordList != null)
            m_RecordList = new RecordListBase(srcRecordList, true);
    }

    /**
     * Assigns the record list. Resulting instance just references the source record list data.
     * @param srcRecordList
     * @param bOwnStorage - if true, instance will clear referenced RecordList object upon destruction.
     */
    public void setRecordList(RecordListBase srcRecordList, boolean bOwnStorage) {
	/* TODO: check to see is it safe to do m_RecordList.finalize() there:
	if (m_RecordList != null)
		{
		m_RecordList.finalize();
		};
		*/
        m_RecordList = null;
        if (srcRecordList != null)
            m_RecordList = new RecordListBase(srcRecordList, bOwnStorage);
    }

    /**
     * @return the RecordListBase object which represents the actual container of all model data records.
     */
    public RecordListBase getRecordList() {
        return m_RecordList;
    }

    /**
     * Returns visible columns number.
     * @return
     */
    public int getColumnCount()
    {
        if (getRecordList() == null)
            return 0;
        return getRecordList().getVisColCount();
    }

    /**
     * Return internal (actual) columns number.
     * @return
     */
    public int getInternalColumnCount()
    {
        if (getRecordList() == null)
            return 0;
        return getRecordList().getColCount();
    }

    /**
     * Return column title for it's visible index
     * @param col - visual column index
     * @return null if column has no title or index out of range
     */
    public String getColumnName(int col)
    {
        if (getRecordList() == null)
            return null;
        return getRecordList().getColTitle(col);
    }

    public String getColumnShortName(int col)
    {
        if (getRecordList() == null)
            return null;
        return getRecordList().getColShortName(col);
    }

    /**
     * Get visible rows number
     * @return
     */
    public int getRowCount()
    {
        if (getRecordList() == null)
            return 0;
        return getRecordList().getRowCount();
    }

    /**
     * Get actual (visible, invisible, any) rows number.
     * @return
     */
    public int getInternalRowCount()
    {
        if (getRecordList() == null)
            return 0;
        return getRecordList().getInternalRowCount();
    }

    /**
     * Maps corresponding indexes.
     * @param iVisRow
     * @return -1 if the record list is unassigned.
     */
    public int getInternalRowForVisRow(int iVisRow)
    {
        if (getRecordList() == null)
            return -1;
        return getRecordList().getInternalRowForVisRow(iVisRow);
    }

    /**
     * Maps corresponding indexes if possible.
     * @param iInternalRow
     * @return -1 if there is no mapping index or the record list is unassigned.
     */
    public int getVisRowForInternalRow(int iInternalRow)
    {
        if (getRecordList() == null)
            return -1;
        for (int iVisRow = 0; iVisRow < getRecordList().getRowCount(); iVisRow++)
        {
            if (getRecordList().getInternalRowForVisRow(iVisRow) == iInternalRow)
                return iVisRow;
        }
        return -1;
        //getRecordList().getInternalRowForVisRow(iVisRow);
    }

    /**
     * Return column class for it's visible index.
     * @param col
     * @return
     */
    public Class<?> getColumnClass(int col)
    {
        if (getRecordList() == null)
        {
            System.out.println("Get class :"
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return null;
        }
        return getRecordList().getVisColumnClass(col);
    }

    /**
     * Return cell value for it's actual row & column index
     * @param row
     * @param col
     * @return null if the record list is not assigned.
     * @throws java.util.NoSuchElementException if the cell is not found
     */
    public Object getInternalValue(int row, int col) throws Exception
    {
        if (getRecordList() == null)
        {
            System.out.println("Get cell :intRow="
                    +Integer.valueOf(row).toString()
                    +",intCol="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return null;
        }
        return getRecordList().getInternalCellValue(row, col);
    }

    /**
     * Return cell value for it's actual row index & column name
     * @param row
     * @param sColShortName
     * @return null if the record list is not assigned.
     * @throws java.util.NoSuchElementException if the cell is not found
     */
    public Object getInternalValue(int row, String sColShortName) throws Exception
    {
        if (getRecordList() == null)
        {
            System.out.println("Get cell :intRow="
                    +Integer.valueOf(row).toString()
                    +",col="+sColShortName+"\n\t" + "getRecordList==null");
            return null;
        }
        return getRecordList().getInternalCellValue(row, getRecordList().getColIXByShortName(sColShortName));
    }

    /**
     * Set cell value for it's actual row & column index.
     * @param val
     * @param row
     * @param col
     * @throws Exception
     */
    public void setInternalValue(Object val, int row, int col) throws Exception
    {
        String sError = "";
        if (getRecordList() == null)
        {
            sError = "Set cell :intRow="
                    +Integer.valueOf(row).toString()
                    +",intCol="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null";
            System.out.println(sError);
            throw new Exception(sError);
        }
        getRecordList().setInternalCellValue(val, row, col);
    }

    /**
     * Set cell value for it's actual row index & column name.
     * @param val
     * @param row
     * @param sColShortName
     * @throws Exception
     */
    public void setInternalValue(Object val, int row, String sColShortName) throws Exception
    {
        String sError = "";
        if (getRecordList() == null)
        {
            sError = "Set cell :intRow="
                    +Integer.valueOf(row).toString()
                    +",col="+sColShortName+"\n\t" + "getRecordList==null";
            System.out.println(sError);
            throw new Exception(sError);
        }
        getRecordList().setInternalCellValue(val, row, getRecordList().getColIXByShortName(sColShortName));
    }

    /**
     * {@code}
     * Returns internal column number
     * @param sColShortName - the database column name
     * @return Index of column of given name
     */
    public int getColIX(String sColShortName)
    {
        return getRecordList().getColIXByShortName(sColShortName);
    }

    /**
     * {@literal}
     * Returns internal column number
     * @param sColShortName - the database column name
     * @return Index of column of given name
     */
    public int getInternalCol(String sColShortName)
    {
        return getRecordList().getColIXByShortName(sColShortName);
    }

    /**
     * {@literal}
     * @param sColShortName - the database column name
     * @return Visible index of column name
     */
    public int getVisCol(String sColShortName)
    {
        return getVisColForInternalCol(getColIX(sColShortName));
    }

    public int getVisColForInternalCol(int colIX)
    {
        return getRecordList().getVisColForInternalCol(colIX);
    }

    public int getInternalColForVisCol(int colVisIX)
    {
        return getRecordList().getInternalColForVisCol(colVisIX);
    }

    /**
     * Set cell value for it's visible row & column index.
     * Only prints error into the standard error stream if cell is not found or other error occur.
     * @param val
     * @param row
     * @param col
     */
    public void setValueAt(Object val, int row, int col)
    {
        if (getRecordList() == null)
        {
            System.out.println("Set cell :row="
                    +Integer.valueOf(row).toString()
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return;
        }
        try {
            getRecordList().setCellValue(val, row, col);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get cell effective edit enable flag e by it's visible row and column numbers.
     * This accounts entire column edit enabling implemented at the RecordListBase level as well as cell intrinsic edit enabling implemented at the TableRecordBase level.
     * @param row
     * @param col
     * @return false if cell is not found
     */
    public boolean isCellEditable(int row, int col)
    {
        if (getRecordList() == null)
        {
            System.out.println("isCellEditable :row="
                    +Integer.valueOf(row).toString()
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return false;
        }
        return getRecordList().isCellEditable(row, col);
    }

    /**
     * Get effective edit enable flag of cell specified by its actual (internal) row and column indexes.
     * This accounts entire column edit enabling implemented at the RecordListBase level as well as cell intrinsic edit enabling implemented at the TableRecordBase level.
     * @param row
     * @param col
     * @return false if cell is not found
     */
    public boolean isInternalCellEditable(int row, int col)
    {
        if (getRecordList() == null)
        {
            System.out.println("isInternalCellEditable :row="
                    +Integer.valueOf(row).toString()
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return false;
        }
        return getRecordList().isInternalCellEditable(row, col);
    }

    /**
     * Set cell edit enable by it's visible row and column indexes
     * @param bEditable
     * @param row
     * @param col
     * @throws Exception
     */
    public void setCellEditable(boolean bEditable, int row, int col) throws Exception
    {
        if (getRecordList() == null)
        {
            System.out.println("setCellEditable :row="
                    +Integer.valueOf(row).toString()
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return;
        }
        getRecordList().setCellEditable(bEditable, row, col);
    }

    /**
     * Set cell edit enable flag by it's internal (actual) row and column
     * @param bEditable
     * @param row
     * @param col
     * @throws Exception
     */
    public void setInternalCellEditable(boolean bEditable, int row, int col) throws Exception
    {
        if (getRecordList() == null)
        {
            System.out.println("setCellEditable :row="
                    +Integer.valueOf(row).toString()
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return;
        }
        getRecordList().setInternalCellEditable(bEditable, row, col);
    }

    /**
     * Return cell value for it's visible row & column index
     * @param row
     * @param col
     * @return null if cell is not found
     */
    public Object getValueAt(int row, int col)
    {
        if (getRecordList() == null)
        {
            System.out.println("Get cell:row="
                    +Integer.valueOf(row).toString()
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + "getRecordList==null");
            return null;
        }
        try {
            return getRecordList().getValueAt(row, col);
        }
        catch (Exception e)
        {
            System.out.println("Get cell:row="
                    +Integer.valueOf(row).toString()
                    +",col="+Integer.valueOf(col).toString()+"\n\t" + e.toString());
		/*
		JOptionPane.showMessageDialog(null,
			    e.toString(),
			    "Ошибка",
			    JOptionPane.ERROR_MESSAGE
			    );
		*/
        }
        return null;
    }
}
