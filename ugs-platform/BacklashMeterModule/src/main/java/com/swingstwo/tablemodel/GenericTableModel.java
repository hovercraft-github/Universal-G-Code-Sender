package com.swingstwo.tablemodel;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;


/**
 * @author pav<p>
 * This class extends the {@link ItemListBase} with the {@link javax.swing.table.TableModel} interface.
 * It doesn't implement the TableModel methods itself but uses an internal
 * AbstractTableModel subclass member to substitute them.
 * Also it does supply a powerful Populate methods utilizing the RecordListBase.LoadFromCursor
 * in conjunction with {@link ItemListBase}.setRecordList .
 * <p><b>Methods:</b><p>
 * {@link #Populate(String, Class[], String[], String[], int[], int[])}<p>
 * {@link #Populate(ResultSet, Class[], String[], String[], int[], int[])}<p>
 */
public abstract class GenericTableModel extends ItemListBase implements TableModel, Serializable
{
    private static final long serialVersionUID = 1676176246109691190L;
    private boolean listenersDisabled = false;

    public GenericTableModel(Connection con)
            throws Exception
    {
        super(con);
    }

    protected void disableAllListeners() {
        listenersDisabled = true;
    }

    protected void enableAllListeners() {
        listenersDisabled = false;
    }

	/*
	public GenericTableModel(ItemListBase original)
	{
	super(original);
	}
	*/

    /**
     * Populates the instance with data got from database (via m_con connection member) using SQL query.
     * Caller must supply result meta data (such as column data types and titles - see parameters list).
     * @param sQuery - SQL query
     * @param types - column data types
     * @param names - column visible headings
     * @param visColIX - array visible column indexes (internal numbers of visible columns)
     * @param editableColIX - array of editable column indexes
     * @throws Exception
     */
	/*
	public void Populate(
			String sQuery,
			Class<?>[] types,
			String[] names,
			int[] visColIX,
			int[] editableColIX
			) throws Exception
	{
		RecordListBase rl = new RecordListBase(types, names, visColIX, editableColIX);
		Statement st = null;
		ResultSet rs = null;
		try {
			st = m_con.createStatement();
			rs = st.executeQuery(sQuery);
			if (rs == null)
				{
				return;
				}
			rl.LoadFromCursor(rs);
			this.setRecordList(rl);
			}
		finally
			{
			try {
				if (rs != null)
					rs.close();
				} catch (Exception e) {};
			rs = null;
			try {
				if (st != null)
					st.close();
				} catch (Exception e) {};
			st = null;
			};
		}
	*/

    /**
     * Populates the instance with data got from database (via m_con connection member) using SQL query.
     * Caller must supply result meta data (such as column data types and titles - see parameters list).
     * @param query - SQL query
     * @param types - column data types
     * @param names - column visible headings
     * @param short_names - column actual (DB) names
     * @param visColIX - array visible column indexes (internal numbers of visible columns)
     * @param editableColIX - array of editable column indexes
     * @throws Exception
     */
    public void Populate(String query, Class<?>[] types, String[] names, String[] short_names,
                         int[] visColIX, int[] editableColIX) throws Exception
    {
        RecordListBase rl = new RecordListBase(types, names, visColIX, editableColIX, short_names);
        Statement st = null;
        ResultSet rs = null;
        try {
            st = m_con.createStatement();
            rs = st.executeQuery(query);
            if (rs == null) {
                return;
            }
            rl.LoadFromCursor(rs);
            this.setRecordList(rl);
        }
        finally
        {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {};
            try {
                if (st != null)
                    st.close();
            } catch (Exception e) {};
        };
    }

    /**
     * Populates the instance with data got from database via caller supplied result set object.
     * Result set will be closed on completion even if operation fails.
     * Caller also must supply result meta data (such as column data types and titles - see parameters list).
     * @param rs - caller supplied JDBC result set
     * @param types - column data types
     * @param names - column visible headings
     * @param short_names - column actual (DB) names
     * @param visColIX - array visible column indexes (internal numbers of visible columns)
     * @param editableColIX - array of editable column indexes
     * @throws Exception
     */
    public void Populate(ResultSet rs, Class<?>[] types, String[] names, String[] short_names,
                         int[] visColIX, int[] editableColIX) throws Exception
    {
        if (rs == null)
        {
            return;
        }
        RecordListBase rl = new RecordListBase(types, names, visColIX, editableColIX, short_names);
        try {
            rl.LoadFromCursor(rs);
            this.setRecordList(rl);
        }
        finally
        {
            try {
                rs.close();
            } catch (Exception e) {};
        };
    }

/*
	class InnerModel extends AbstractTableModel
	{
	private static final long serialVersionUID = -4193603779364658933L;
		public String getColumnName(int column) {
            return GenericTableModel.this.getColumnName(column);
        }
        public int getRowCount() {
            return GenericTableModel.this.getRowCount();
        }
        public int getColumnCount() {
            return GenericTableModel.this.getColumnCount();
        }
        public Object getValueAt(int row, int column) {
            return GenericTableModel.this.getValueAt(row, column);
        }
        public boolean isCellEditable(int row, int column) {
            return GenericTableModel.this.isCellEditable(row, column);
        }
        public void setValueAt(Object value, int row, int column) {
        	GenericTableModel.this.setValueAt(value, row, column);
        }
    };
private InnerModel tableModel = new InnerModel();
*/

    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

    protected void removeAllTableModelListeners() {
        listeners.clear();
    }

    public void addTableModelListener(TableModelListener l)
    {
        //tableModel.addTableModelListener(l);
        listeners.add(l);
    }
    public void removeTableModelListener(TableModelListener l)
    {
        //tableModel.removeTableModelListener(l);
        listeners.remove(l);
    }
    public void fireTableStructureChanged()
    {
        if (listenersDisabled)
            return;;
        //tableModel.fireTableStructureChanged();
        Iterator<TableModelListener> it = listeners.iterator();
        TableModelListener lstnr;
        while (it.hasNext())
        {
            lstnr = it.next();
            lstnr.tableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
        };
    }
    public void fireTableDataChanged()
    {
        if (listenersDisabled)
            return;;
        //tableModel.fireTableDataChanged();
        Iterator<TableModelListener> it = listeners.iterator();
        TableModelListener lstnr;
        while (it.hasNext())
        {
            lstnr = it.next();
            lstnr.tableChanged(new TableModelEvent(this));
        };
    }
    public void fireTableCellUpdated(int row, int col)
    {
        if (listenersDisabled)
            return;;
        //tableModel.fireTableCellUpdated(row, col);
        Iterator<TableModelListener> it = listeners.iterator();
        TableModelListener lstnr;
        while (it.hasNext())
        {
            lstnr = it.next();
            lstnr.tableChanged(new TableModelEvent(this, row, row, col, TableModelEvent.UPDATE));
        };
    }
    public void fireTableRowsUpdated(int row1, int row2)
    {
        if (listenersDisabled)
            return;;
        //tableModel.fireTableRowsUpdated(row1, row2);
        Iterator<TableModelListener> it = listeners.iterator();
        TableModelListener lstnr;
        while (it.hasNext())
        {
            lstnr = it.next();
            lstnr.tableChanged(new TableModelEvent(this, row1, row2));
        };
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
        super.setValueAt(val, row, col);
        int iInternalRow = super.getInternalRowForVisRow(row);
        int iInternalCol = super.getInternalColForVisCol(col);
        fireTableCellUpdated(iInternalRow, iInternalCol);
    }
}
