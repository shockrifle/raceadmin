package hu.danielb.raceadmin.ui.components.table.tablemodels;

import java.util.Arrays;
import java.util.List;

import hu.danielb.raceadmin.entity.Contestant;

public class ContestantTableModel extends BaseTableModel<Contestant> {

    private Column mSortBy = Column.NAME;
    private boolean mSortBackwards = false;

    public ContestantTableModel(List<Contestant> data) {
        super(Arrays.asList(
                "",
                "Helyezés",
                "Név",
                "Rajtszám",
                "Születési év",
                "Korosztály",
                "Iskola",
                "Csapat",
                "Nem",
                ""), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == Column.COUNTER.ordinal())
            return row;
        if (column == Column.POSITION.ordinal())
            return mData.get(row).getPositionString(false);
        if (column == Column.NAME.ordinal())
            return mData.get(row).getName();
        if (column == Column.NUMBER.ordinal())
            return mData.get(row).getNumber();
        if (column == Column.AGE.ordinal())
            return mData.get(row).getAge();
        if (column == Column.AGE_GROUP_NAME.ordinal()) {
            if (mData.get(row).getAgeGroup() != null) {
                return mData.get(row).getAgeGroup().getName();
            }
            return "";
        }
        if (column == Column.SCHOOL_NAME.ordinal())
            return mData.get(row).getSchool().getNameWithSettlement();
        if (column == Column.SEX.ordinal())
            return "B".equals(mData.get(row).getSex()) ? "Fiú" : "Lány";
        if (column == Column.TEAM.ordinal())
            return mData.get(row).isTeamEntry();
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

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == Column.TEAM.ordinal())
            return Boolean.class;
        return super.getColumnClass(columnIndex);
    }

    public ContestantTableModel setSortBy(Column sortBy) {
        mSortBy = sortBy;
        return this;
    }

    public ContestantTableModel setSortBackwards(boolean sortBackwards) {
        mSortBackwards = sortBackwards;
        return this;
    }

    private String putSortMark(Column i) {
        if (i.equals(mSortBy)) {
            if (mSortBackwards) {
                return "^";
            }
            return "ˇ";
        }
        return "";
    }

    public enum Column {
        COUNTER,
        POSITION,
        NAME,
        NUMBER,
        AGE,
        AGE_GROUP_NAME,
        SCHOOL_NAME,
        TEAM,
        SEX,
        EDIT
    }
}