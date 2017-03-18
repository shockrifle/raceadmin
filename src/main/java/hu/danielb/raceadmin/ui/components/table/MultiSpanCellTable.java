package hu.danielb.raceadmin.ui.components.table;

import hu.danielb.raceadmin.ui.components.table.models.AttributiveCellTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Enumeration;

public class MultiSpanCellTable extends JTable {

    public MultiSpanCellTable(TableModel model) {
        super(model);
        setUI(new MultiSpanCellTableUI());
        getTableHeader().setReorderingAllowed(false);
        setCellSelectionEnabled(true);
        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    @Override
    public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        int rowLocal = row;
        int columnLocal = column;
        Rectangle sRect = super.getCellRect(rowLocal, columnLocal, includeSpacing);
        if ((rowLocal < 0) || (columnLocal < 0)
                || (getRowCount() <= rowLocal) || (getColumnCount() <= columnLocal)) {
            return sRect;
        }
        CellSpan cellAtt = (CellSpan) ((AttributiveCellTableModel) getModel()).getCellAttribute();
        if (!cellAtt.isVisible(rowLocal, columnLocal)) {
            int rowTemp = rowLocal;
            rowLocal += cellAtt.getSpan(rowLocal, columnLocal)[CellSpan.ROW];
            columnLocal += cellAtt.getSpan(rowTemp, columnLocal)[CellSpan.COLUMN];
        }
        int[] n = cellAtt.getSpan(rowLocal, columnLocal);

        int index = 0;
        int columnMargin = getColumnModel().getColumnMargin();
        Rectangle cellFrame = new Rectangle();
        int aCellHeight = rowHeight + rowMargin;
        cellFrame.y = rowLocal * aCellHeight;
        cellFrame.height = n[CellSpan.ROW] * aCellHeight;

        Enumeration enumeration = getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn) enumeration.nextElement();
            cellFrame.width = aColumn.getWidth() + columnMargin;
            if (index == columnLocal) {
                break;
            }
            cellFrame.x += cellFrame.width;
            index++;
        }
        for (int i = 0; i < n[CellSpan.COLUMN] - 1; i++) {
            TableColumn aColumn = (TableColumn) enumeration.nextElement();
            cellFrame.width += aColumn.getWidth() + columnMargin;
        }


        if (!includeSpacing) {
            Dimension spacing = getIntercellSpacing();
            cellFrame.setBounds(cellFrame.x + spacing.width / 2,
                    cellFrame.y + spacing.height / 2,
                    cellFrame.width - spacing.width,
                    cellFrame.height - spacing.height);
        }
        return cellFrame;
    }

    private int[] rowColumnAtPoint(Point point) {
        int[] retValue = {-1, -1};
        int row = point.y / (rowHeight + rowMargin);
        if ((row < 0) || (getRowCount() <= row)) {
            return retValue;
        }
        int column = getColumnModel().getColumnIndexAtX(point.x);

        CellSpan cellAtt = (CellSpan) ((AttributiveCellTableModel) getModel()).getCellAttribute();

        if (cellAtt.isVisible(row, column)) {
            retValue[CellSpan.COLUMN] = column;
            retValue[CellSpan.ROW] = row;
            return retValue;
        }
        retValue[CellSpan.COLUMN] = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
        retValue[CellSpan.ROW] = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
        return retValue;
    }

    @Override
    public int rowAtPoint(Point point) {
        return rowColumnAtPoint(point)[CellSpan.ROW];
    }

    @Override
    public int columnAtPoint(Point point) {
        return rowColumnAtPoint(point)[CellSpan.COLUMN];
    }

    @Override
    public void columnSelectionChanged(ListSelectionEvent e) {
        repaint();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        if (firstIndex == -1 && lastIndex == -1) { // Selection cleared.
            repaint();
        }
        Rectangle dirtyRegion = getCellRect(firstIndex, 0, false);
        int numCoumns = getColumnCount();
        int index = firstIndex;
        for (int i = 0; i < numCoumns; i++) {
            dirtyRegion.add(getCellRect(index, i, false));
        }
        index = lastIndex;
        for (int i = 0; i < numCoumns; i++) {
            dirtyRegion.add(getCellRect(index, i, false));
        }
        repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
    }
}
