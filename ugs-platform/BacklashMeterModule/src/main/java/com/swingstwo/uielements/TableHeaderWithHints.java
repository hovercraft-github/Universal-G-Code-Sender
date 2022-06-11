package com.swingstwo.uielements;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseEvent;

public class TableHeaderWithHints extends JTableHeader {
    String[] tooltips;

    public TableHeaderWithHints(TableColumnModel columnModel, String[] columnTooltips) {
        super(columnModel);//do everything a normal JTableHeader does
        this.tooltips = columnTooltips;//plus extra data
    }

    public String getToolTipText(MouseEvent e) {
        java.awt.Point p = e.getPoint();
        int index = columnModel.getColumnIndexAtX(p.x);
        int realIndex = columnModel.getColumn(index).getModelIndex();
        return this.tooltips[realIndex];
    }
}
