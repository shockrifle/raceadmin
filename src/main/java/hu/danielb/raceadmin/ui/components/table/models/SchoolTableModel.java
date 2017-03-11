package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.School;

import java.util.Arrays;
import java.util.List;

public class SchoolTableModel extends AttributiveCellTableModel<School> {

    private Column sortBy = Column.NAME;
    private boolean sortBackwards = false;

    public SchoolTableModel(List<School> data) {
        super(Arrays.asList("Név", "Rövidített név", "Település", ""), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == Column.NAME.ordinal())
            return data.get(row).getName();
        if (column == Column.SHORT_NAME.ordinal())
            return data.get(row).getShortName();
        if (column == Column.SETTLEMENT.ordinal())
            return data.get(row).getSettlement();
        if (column == Column.EDIT.ordinal())
            return "Szerkesztés";
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return super.getColumnName(column) + putSortMark(Column.values()[column]);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == Column.EDIT.ordinal();
    }

    public SchoolTableModel setSortBy(Column sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public SchoolTableModel setSortBackwards(boolean sortBackwards) {
        this.sortBackwards = sortBackwards;
        return this;
    }

    private String putSortMark(Column i) {
        if (i == sortBy) {
            if (sortBackwards) {
                return "^";
            }
            return "ˇ";
        }
        return "";
    }

    public enum Column {
        NAME,
        SHORT_NAME,
        SETTLEMENT,
        EDIT
    }

}