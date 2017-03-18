package hu.danielb.raceadmin.ui.components.table.models;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

public class BaseTableModel<T> extends DefaultTableModel {
    List<T> mData;

    BaseTableModel(List<String> columnIdentifiers, List<T> data) {
        this(columnIdentifiers, data, data.size());
    }

    BaseTableModel(List<String> columnIdentifiers, List<T> data, int dataSize) {
        super(new Vector<>(columnIdentifiers), dataSize);
        this.mData = data;
    }

    public T getDataAt(int row) {
        return mData.get(row);
    }
}