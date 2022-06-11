package com.swingstwo.tablemodel;

import java.sql.Connection;

public class SortableTableModel extends GenericTableModel {

    private static final long serialVersionUID = 76907414417488582L;
    int[] m_sortIndexes;

    public SortableTableModel(Connection con) throws Exception {
        super(con);
    }

	/*
	public void Populate(String query, Class<?>[] types, String[] names,
			int[] visColIX, int[] editableColIX) throws Exception {
	}
	*/

    @Override
    public Object getValueAt(int iSortedVisRow, int iVisCol)
    {
        return super.getValueAt(getTrueVisRowForSortedRow(iSortedVisRow), iVisCol);
    }

    @Override
    public void setValueAt(Object val, int iSortedVisRow, int iVisCol)
    {
        super.setValueAt(val, getTrueVisRowForSortedRow(iSortedVisRow), iVisCol);
    }

    public int getTrueVisRowForSortedRow(int iSortedVisRow)
    {
        int[] sortIndexes = getIndexes();
        int iTrueVisRow = sortIndexes[iSortedVisRow];
        return iTrueVisRow;
    }

    public int getInternalRowForTrueVisRow(int iUnSortedVisRow)
    {
        return super.getInternalRowForVisRow(iUnSortedVisRow);
    }

    public int getInternalRowForVisRow(int iSortedVisRow)
    {
        return super.getInternalRowForVisRow(getTrueVisRowForSortedRow(iSortedVisRow));
    }

    public int getVisRowForInternalRow(int iInternalRow)
    {
        int[] sortIndexes = getIndexes();
        int iTrueVisRow = super.getVisRowForInternalRow(iInternalRow);
        int iSortedVisRow = -1;
        for (int i = 0; i < sortIndexes.length; i++)
        {
            if (sortIndexes[i] == iTrueVisRow)
            {
                iSortedVisRow = i;
                break;
            }
        };
        return iSortedVisRow;
    }

    public int[] getIndexes()
    {
        int n = getRowCount();
        if (m_sortIndexes != null)
        {
            if (m_sortIndexes.length == n)
            {
                return m_sortIndexes;
            }
        }
        m_sortIndexes = new int[n];
        for (int i=0; i<n; i++)
        {
            m_sortIndexes[i] = i;
        }
        return m_sortIndexes;
    }

    protected void exchange(int[] indexes, int i, int j) {
        int temp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = temp;
    }

    public void quickSortByColumn(int column, boolean isAscent)
    {
        int n = getRowCount();
        if (n < 2)
            return;
        int[] indexes = getIndexes();
        quicksort(column, isAscent, indexes, 0, n-1);
        fireTableDataChanged();
    }

    protected void quicksort(int column, boolean isAscent, int[] indexes, int low, int high) {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        int pivot = indexes[low + (high-low)/2];
        //int pivot = low + (high-low)/2;

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller than the pivot
            // element then get the next element from the left list
            while (
                //indexes[i] < pivot
                    compare(column, isAscent, indexes[i], pivot) < 0
            )
            {
                i++;
            }
            // If the current value from the right list is larger than the pivot
            // element then get the next element from the right list
            while (
                //indexes[j] > pivot
                    compare(column, isAscent, indexes[j], pivot) > 0
            )
            {
                j--;
            }

            // If we have found a values in the left list which is larger than
            // the pivot element and if we have found a value in the right list
            // which is smaller than the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                exchange(indexes, i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quicksort(column, isAscent, indexes, low, j);
        if (i < high)
            quicksort(column, isAscent, indexes, i, high);
    }

    protected int compare(int column, boolean isAscent, int j, int k)
    {
        if (isAscent)
            return compare_unsorted(column, j, k);
        else
            return compare_unsorted(column, k, j);
    }

    protected int compare_unsorted(int column, int row1, int row2)
    {
        Object o1 = super.getValueAt(row1, column);
        Object o2 = super.getValueAt(row2, column);
        if (o1 == null && o2 == null)
        {
            return  0;
        }
        else if (o1 == null)
        {
            return -1;
        }
        else if (o2 == null)
        {
            return  1;
        }
        else
        {
            Class<?> type = getColumnClass(column);
            if (type.getSuperclass() == Number.class)
            {
                return compare((Number)o1, (Number)o2);
            }
            else if (type == String.class)
            {
                return ((String)o1).compareTo((String)o2);
            }
            else if (type == java.util.Date.class)
            {
                return compare((java.util.Date)o1, (java.util.Date)o2);
            }
            else if (type == java.sql.Timestamp.class)
            {
                return ((java.sql.Timestamp)o1).compareTo((java.sql.Timestamp)o2);
            }
            else if (type == Boolean.class)
            {
                return compare((Boolean)o1, (Boolean)o2);
            }
            else
            {
                return ((String)o1).compareTo((String)o2);
            }
        }
    }

	/*
	public void sortByColumn(int column, boolean isAscent)
  	{
	  int n = getRowCount();
	  int[] indexes = getIndexes();
	  for (int i=0; i<n-1; i++)
	  	{
		  int k = i;
		  for (int j=i+1; j<n; j++)
		  		{
			  	if (isAscent)
			  		{
			  		if (compare(column, j, k) < 0)
			  			{
			  			k = j;
			  			}
			  		}
			  	else
			  		{
			  		if (compare(column, j, k) > 0)
			  			{
			  			k = j;
			  			}
			  		}
			  	}
		  int tmp = indexes[i];
		  indexes[i] = indexes[k];
		  indexes[k] = tmp;
		  }
	  fireTableDataChanged();
	  }

	  // comparaters
	protected int compare(int column, int row1, int row2)
  	{
	  Object o1 = getValueAt(row1, column);
	  Object o2 = getValueAt(row2, column);
	  if (o1 == null && o2 == null)
	  	{
		  return  0;
		  }
	  else if (o1 == null)
	  	{
		  return -1;
		  }
	  else if (o2 == null)
	  	{
		  return  1;
		  }
	  else
	  	{
		  Class<?> type = getColumnClass(column);
		  if (type.getSuperclass() == Number.class)
		  	{
			  return compare((Number)o1, (Number)o2);
			  }
		  else if (type == String.class)
		  	{
			  return ((String)o1).compareTo((String)o2);
			  }
		  else if (type == java.util.Date.class)
		  	{
			  return compare((java.util.Date)o1, (java.util.Date)o2);
			  }
		  else if (type == Boolean.class)
		  	{
			  return compare((Boolean)o1, (Boolean)o2);
			  }
		  else
		  	{
			  return ((String)o1).compareTo((String)o2);
			  }
		  }
	  }
	  */

    public static int compare(Number o1, Number o2)
    {
        double n1 = o1.doubleValue();
        double n2 = o2.doubleValue();
        if (n1 < n2)
        {
            return -1;
        }
        else if (n1 > n2)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static int compare(java.util.Date o1, java.util.Date o2)
    {
        long n1 = o1.getTime();
        long n2 = o2.getTime();
        if (n1 < n2)
        {
            return -1;
        }
        else if (n1 > n2)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public static int compare(Boolean o1, Boolean o2)
    {
        boolean b1 = o1.booleanValue();
        boolean b2 = o2.booleanValue();
        if (b1 == b2)
        {
            return 0;
        }
        else if (b1)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

}
