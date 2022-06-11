package com.swingstwo.tablemodel;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Iterates over editable rows
 * @author pav
 * @see RecordListBase
 */
@SuppressWarnings("serial")
public class RecordListEditableIter extends RecordListVisIter {
    protected Integer m_iFirstEditableColumn;
    protected Vector<Integer> m_iEditableVisibleRowIX = new Vector<Integer>(); //Contains internal indexes of editable rows

    /**
     * Builds row index to show.
     * Only the first editable column in a row now checked!
     * @param src
     * @throws Exception
     */
    public RecordListEditableIter(RecordListBase src) throws Exception
    {
        super(src);
        TableRecord rec = this.getRecord(this.getInternalRowForVisRow(0));
        boolean bFound = false;
        if (rec != null)
        {
            for (int visColIX = 0; visColIX < this.getVisColCount(); visColIX++)
                if (rec.isFieldEditable(this.getInternalColForVisCol(visColIX)))
                {
                    bFound = true;
                    m_iFirstEditableColumn = visColIX;
                    break;
                };
            if (!bFound)
                return;
            for (int i = 0; i < this.getRowCount(); i++)
            {
                if (this.isCellEditable(i, m_iFirstEditableColumn))
                {
                    m_iEditableVisibleRowIX.add(this.getInternalRowForVisRow(i));
                }
            };
        }
    }

    public void toBegin() {
        m_iterationRow = -1;
    }

    public void toEnd() {
        m_iterationRow = m_iEditableVisibleRowIX.size(); //this.getRowCount();
    }

    public boolean hasNext() {
        if (m_iterationRow < m_iEditableVisibleRowIX.size()-1)
            return true;
        return false;
    }

    public TableRecord next() throws NoSuchElementException
    {
        TableRecord rec = null;
        if (m_iterationRow < m_iEditableVisibleRowIX.size())
            m_iterationRow++;
        try {
            int iInternalRowIX = m_iEditableVisibleRowIX.elementAt(m_iterationRow);
            rec = getRecord(iInternalRowIX);
        }
        catch (ArrayIndexOutOfBoundsException ee)
        {
            throw new NoSuchElementException(ee.toString());
        };
        if (rec == null)
            throw new NoSuchElementException("Iteration (next): there is no record " + Integer.valueOf(m_iterationRow).toString());
        return rec;
    }
}
