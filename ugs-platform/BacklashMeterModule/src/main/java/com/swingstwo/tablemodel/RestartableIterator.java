package com.swingstwo.tablemodel;

import java.util.Iterator;

public interface RestartableIterator<E> extends Iterator<E> {
    /**
     * Set current position just before the very first collection element
     */
    public void toBegin();
    /**
     * Set current position just after the very last collection element
     */
    public void toEnd();
}
