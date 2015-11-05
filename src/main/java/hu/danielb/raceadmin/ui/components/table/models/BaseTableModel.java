package hu.danielb.raceadmin.ui.components.table.models;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

public class BaseTableModel<T> extends DefaultTableModel {
    List<T> data;

    public BaseTableModel(List<String> columnIdentifiers, List<T> data) {
        this(columnIdentifiers, data, data.size());
    }

    public BaseTableModel(List<String> columnIdentifiers, List<T> data, int dataSize) {
        super(new Vector<>(columnIdentifiers), dataSize);
        this.data = data;
    }

    public T getDataAt(int row) {
        return data.get(row);
    }
}