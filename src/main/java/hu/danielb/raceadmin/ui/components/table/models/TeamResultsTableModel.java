package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.Team;

import java.util.Arrays;
import java.util.List;

public class TeamResultsTableModel extends AttributiveCellTableModel<Team> {

    public TeamResultsTableModel(List<Team> data) {
        super(Arrays.asList("Helyezés", "Pontszám", "Egyéni", "Rajtszám", "Név", "Iskola"), data, data.size() * Team.MAX_MEMBERS);
    }

    @Override
    public Object getValueAt(int row, int column) {
        int teamRow = (int) Math.floor(row / Team.MAX_MEMBERS);

        if (column == Column.POSITION.ordinal())
            return (int) Math.floor(row / Team.MAX_MEMBERS) + 1;
        if (column == Column.POINTS.ordinal())
            return data.get(teamRow).getPoints();
        if (column == Column.INDIVIDUAL_POSITION.ordinal())
            return data.get(teamRow).getMembers().get(row % Team.MAX_MEMBERS).getPosition();
        if (column == Column.NAME.ordinal())
            return data.get(teamRow).getMembers().get(row % Team.MAX_MEMBERS).getName();
        if (column == Column.NUMBER.ordinal())
            return data.get(teamRow).getMembers().get(row % Team.MAX_MEMBERS).getNumber();
        if (column == Column.SCHOOL_NAME.ordinal())
            return data.get(teamRow).getMembers().get(row % Team.MAX_MEMBERS).getSchool().getName();

        return null;

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public enum Column {
        POSITION,
        POINTS,
        INDIVIDUAL_POSITION,
        NUMBER,
        NAME,
        SCHOOL_NAME
    }

}