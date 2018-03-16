package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.Team;

import java.util.Arrays;
import java.util.List;

public class TeamResultsTableModel extends AttributiveCellTableModel<Team> {

    private final int mTeamMaxMembers;

    public TeamResultsTableModel(List<Team> data) {
        super(Arrays.asList("Helyezés", "Pontszám", "Egyéni", "Rajtszám", "Név", "Iskola"), data, data.size() *
                (data.size() > 0 && data.get(0) != null ? data.get(0).getMaxMembers() : 0));
        mTeamMaxMembers = (data.size() > 0 && data.get(0) != null ? data.get(0).getMaxMembers() : 0);
    }

    @Override
    public Object getValueAt(int row, int column) {
        int teamRow = (int) Math.floor((double) row / (double) mTeamMaxMembers);

        if (column == Column.POSITION.ordinal())
            return teamRow + 1;
        if (column == Column.POINTS.ordinal())
            return mData.get(teamRow).getPoints();
        if (column == Column.INDIVIDUAL_POSITION.ordinal())
            return mData.get(teamRow).getMembers().get(row % mTeamMaxMembers).getPositionString();
        if (column == Column.NAME.ordinal())
            return mData.get(teamRow).getMembers().get(row % mTeamMaxMembers).getName();
        if (column == Column.NUMBER.ordinal())
            return mData.get(teamRow).getMembers().get(row % mTeamMaxMembers).getNumber();
        if (column == Column.SCHOOL_NAME.ordinal())
            return mData.get(teamRow).getMembers().get(row % mTeamMaxMembers).getSchool().getNameWithSettlement();

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