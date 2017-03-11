package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.Contestant;

import java.util.Arrays;
import java.util.List;

public class ContestantTableModel extends BaseTableModel<Contestant> {

    private Column sortBy = Column.NAME;
    private boolean sortBackwards = false;

    public ContestantTableModel(List<Contestant> data) {
        super(Arrays.asList(
                "Helyezés",
                "Név",
                "Rajtszám",
                "Születési év",
                "Korosztály",
                "Iskola",
                "Nem",
                ""), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == Column.POSITION.ordinal())
            return data.get(row).getPosition();
        if (column == Column.NAME.ordinal())
            return data.get(row).getName();
        if (column == Column.NUMBER.ordinal())
            return data.get(row).getNumber();
        if (column == Column.AGE.ordinal())
            return data.get(row).getAge();
        if (column == Column.AGE_GROUP_NAME.ordinal()) {
            if (data.get(row).getAgeGroup() != null) {
                return data.get(row).getAgeGroup().getName();
            }
            return "";
        }
        if (column == Column.SCHOOL_NAME.ordinal())
            return data.get(row).getSchool().getName();
        if (column == Column.SEX.ordinal())
            return data.get(row).getSex().equals("B") ? "Fiú" : "Lány";
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

    public ContestantTableModel setSortBy(Column sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public ContestantTableModel setSortBackwards(boolean sortBackwards) {
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
        POSITION,
        NAME,
        NUMBER,
        AGE,
        AGE_GROUP_NAME,
        SCHOOL_NAME,
        SEX,
        EDIT
    }
}