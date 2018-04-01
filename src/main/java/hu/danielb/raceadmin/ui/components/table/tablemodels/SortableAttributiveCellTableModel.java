package hu.danielb.raceadmin.ui.components.table.tablemodels;

import java.util.List;
import java.util.stream.Collectors;

public class SortableAttributiveCellTableModel<T> extends AttributiveCellTableModel<T> {

    private List<ColumnModel<T>> mColumnModels;
    private int mSortBy = -1;
    private boolean mSortBackwards = false;

    SortableAttributiveCellTableModel(List<ColumnModel<T>> columnModels, List<T> data) {
        super(columnModels.stream().map(ColumnModel::getName).collect(Collectors.toList()), data);
        mColumnModels = columnModels;
    }

    SortableAttributiveCellTableModel(List<ColumnModel<T>> columnModels, List<T> data, int dataSize) {
        super(columnModels.stream().map(ColumnModel::getName).collect(Collectors.toList()), data, dataSize);
        mColumnModels = columnModels;
    }

    @Override
    public String getColumnName(int column) {
        return super.getColumnName(column) + putSortMark(mColumnModels.get(column).getOrdinal());
    }

    public SortableAttributiveCellTableModel setSortBy(int sortBy) {
        this.mSortBy = sortBy;
        return this;
    }

    public SortableAttributiveCellTableModel setSortBackwards(boolean sortBackwards) {
        this.mSortBackwards = sortBackwards;
        return this;
    }

    @Override
    public Object getValueAt(int row, int tableColumn) {
        return mColumnModels.get(tableColumn).getValue(mData.get(row));
    }

    private String putSortMark(int column) {
        if (column == mSortBy) {
            if (mSortBackwards) {
                return "^";
            }
            return "ˇ";
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return mColumnModels.get(column).isButton();
    }
}
