package com.swingstwo.tablemodel;

import java.util.NoSuchElementException;

/**
 * {@code}
 * {@literal} Iterates over tables visible rows
 * @author pav
 * @see RecordListBase
 */
@SuppressWarnings("serial")
public class RecordListVisIter extends RecordListBase {

    public RecordListVisIter(RecordListBase src) {
        super(src, false);
    }

    public void toBegin() {
        m_iterationRow = -1;
    }

    public void toEnd() {
        m_iterationRow = this.getRowCount();
    }

    public boolean hasNext() {
        if (m_iterationRow < this.getRowCount()-1)
            return true;
        return false;
    }

    public TableRecord next() throws NoSuchElementException
    {
        TableRecord rec = null;
        if (m_iterationRow < this.getRowCount())
            m_iterationRow++;
        int iInternalRowIX = this.getInternalRowForVisRow(m_iterationRow);
        rec = getRecord(iInternalRowIX);
        if (rec == null)
            throw new NoSuchElementException("Iteration (next): there is no record " + Integer.valueOf(m_iterationRow).toString());
        return rec;
    }

    public void finalize()
    {
        //Free(); Prevents cleanup of referenced data.
    }
}
