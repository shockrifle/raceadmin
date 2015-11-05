package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.ui.components.table.CellAttribute;
import hu.danielb.raceadmin.ui.components.table.DefaultCellAttribute;

import javax.swing.event.TableModelEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class AttributiveCellTableModel<T> extends BaseTableModel<T> {

    private CellAttribute cellAtt;

    public AttributiveCellTableModel(List<String> columnIdentifiers, List<T> data) {
        super(columnIdentifiers, data);
    }

    @Override
    public void setDataVector(Vector newData, Vector columnNames) {
        dataVector = new Vector(0);
        columnIdentifiers = columnNames;
        dataVector = newData;

        cellAtt = new DefaultCellAttribute(dataVector == null ? 0 : dataVector.size(),
                columnIdentifiers == null ? 0 : columnIdentifiers.size());

        newRowsAdded(new TableModelEvent(this, 0, getRowCount() - 1,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addColumn(Object columnName, Vector columnData) {
        if (columnName == null) {
            throw new IllegalArgumentException("addColumn() - null parameter");
        }
        columnIdentifiers.addElement(columnName);
        int index = 0;
        Enumeration enumeration = dataVector.elements();
        while (enumeration.hasMoreElements()) {
            Object value;
            if ((columnData != null) && (index < columnData.size())) {
                value = columnData.elementAt(index);
            } else {
                value = null;
            }
            ((Vector) enumeration.nextElement()).addElement(value);
            index++;
        }

        cellAtt.addColumn();

        fireTableStructureChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addRow(Vector rowData) {
        Vector newData = null;
        if (rowData == null) {
            newData = new Vector(getColumnCount());
        } else {
            rowData.setSize(getColumnCount());
        }
        dataVector.addElement(newData);

        cellAtt.addRow();

        newRowsAdded(new TableModelEvent(this, getRowCount() - 1, getRowCount() - 1,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void insertRow(int row, Vector rowData) {
        if (rowData == null) {
            rowData = new Vector(getColumnCount());
        } else {
            rowData.setSize(getColumnCount());
        }

        dataVector.insertElementAt(rowData, row);

        cellAtt.insertRow(row);

        newRowsAdded(new TableModelEvent(this, row, row,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    public CellAttribute getCellAttribute() {
        return cellAtt;
    }

}
