package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.Team;

import java.util.Arrays;
import java.util.List;

public class TeamResultsTableModel extends AttributiveCellTableModel<Team> {

    public static final int COLUMN_CONTESTANT_ID = 0;
    public static final int COLUMN_SCHOOL_ID = 1;
    public static final int COLUMN_POSITION = 2;
    public static final int COLUMN_POINTS = 3;
    public static final int COLUMN_INDIVIDUAL_POSITION = 4;
    public static final int COLUMN_NUMBER = 5;
    public static final int COLUMN_NAME = 6;
    public static final int COLUMN_SCHOOL_NAME = 7;

    public TeamResultsTableModel(List<Team> data) {
        super(Arrays.asList("", "", "Helyezés", "Pontszám", "Egyéni", "Rajtszám", "Név", "Iskola"), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case COLUMN_CONTESTANT_ID:
                return data.get(row).getMembers().get(row % Team.MAX_MEMBERS).getId();
            case COLUMN_SCHOOL_ID:
                return data.get(row).getMembers().get(row % Team.MAX_MEMBERS).getSchool().getId();
            case COLUMN_POSITION:
                return Math.ceil(row / Team.MAX_MEMBERS);
            case COLUMN_POINTS:
                return data.get(row).getPoints();
            case COLUMN_INDIVIDUAL_POSITION:
                return data.get(row).getMembers().get(row % Team.MAX_MEMBERS).getPosition();
            case COLUMN_NAME:
                return data.get(row).getName();
            case COLUMN_NUMBER:
                return data.get(row).getMembers().get(row % Team.MAX_MEMBERS).getNumber();
            case COLUMN_SCHOOL_NAME:
                return data.get(row).getMembers().get(row % Team.MAX_MEMBERS).getSchool().getName();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}