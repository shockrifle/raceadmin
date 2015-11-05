package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.Contestant;

import java.util.Arrays;
import java.util.List;

public class ResultsTableModel extends BaseTableModel<Contestant> {

    public static final int COLUMN_CONTESTANT_ID = 0;
    public static final int COLUMN_POSITION = 1;
    public static final int COLUMN_NUMBER = 2;
    public static final int COLUMN_NAME = 3;
    public static final int COLUMN_SCHOOL_NAME = 4;

    public ResultsTableModel(List<Contestant> data) {
        super(Arrays.asList("", "Helyezés", "Rajtszám", "Név", "Iskola"), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case COLUMN_CONTESTANT_ID:
                return data.get(row).getId();
            case COLUMN_POSITION:
                return data.get(row).getPosition();
            case COLUMN_NAME:
                return data.get(row).getName();
            case COLUMN_NUMBER:
                return data.get(row).getNumber();
            case COLUMN_SCHOOL_NAME:
                return data.get(row).getSchool().getName();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}