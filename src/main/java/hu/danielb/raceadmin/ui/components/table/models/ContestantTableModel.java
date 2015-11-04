package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.Contestant;

import java.util.Arrays;
import java.util.List;

public class ContestantTableModel extends BaseTableModel<Contestant> {

    public static final int COLUMN_CONTESTANT_ID = 0;
    public static final int COLUMN_SCHOOL_ID = 1;
    public static final int COLUMN_AGE_GROUP_ID = 2;
    public static final int COLUMN_POSITION = 3;
    public static final int COLUMN_NAME = 4;
    public static final int COLUMN_NUMBER = 5;
    public static final int COLUMN_AGE = 6;
    public static final int COLUMN_AGE_GROUP_NAME = 7;
    public static final int COLUMN_SCHOOL_NAME = 8;
    public static final int COLUMN_SEX = 9;
    public static final int COLUMN_EDIT = 10;

    private int sortBy = COLUMN_NAME;
    private boolean sortBackwards = false;

    public ContestantTableModel(List<Contestant> data) {
        super(Arrays.asList(
                "",
                "",
                "",
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
        switch (column) {
            case COLUMN_CONTESTANT_ID:
                return data.get(row).getId();
            case COLUMN_SCHOOL_ID:
                return data.get(row).getSchool().getId();
            case COLUMN_AGE_GROUP_ID:
                return data.get(row).getAgeGroup().getId();
            case COLUMN_POSITION:
                return data.get(row).getPosition();
            case COLUMN_NAME:
                return data.get(row).getName();
            case COLUMN_NUMBER:
                return data.get(row).getNumber();
            case COLUMN_AGE:
                return data.get(row).getAge();
            case COLUMN_AGE_GROUP_NAME:
                return data.get(row).getAgeGroup().getName();
            case COLUMN_SCHOOL_NAME:
                return data.get(row).getSchool().getName();
            case COLUMN_SEX:
                return data.get(row).getSex().equals("B") ? "Fiú" : "Lány";
            case COLUMN_EDIT:
                return "Szerkesztés";
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return super.getColumnName(column) + putSortMark(column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COLUMN_EDIT;
    }

    public ContestantTableModel setSortBy(int sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public ContestantTableModel setSortBackwards(boolean sortBackwards) {
        this.sortBackwards = sortBackwards;
        return this;
    }

    private String putSortMark(int i) {
        if (i == sortBy) {
            if (sortBackwards) {
                return "^";
            }
            return "ˇ";
        }
        return "";
    }
}