package hu.danielb.raceadmin.ui.components.table.tablemodels;

import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamResultsTableModel extends AttributiveCellTableModel<Team> {

    private final ArrayList<Integer> mTeamRows;

    public TeamResultsTableModel(List<Team> data) {
        super(Arrays.asList("Helyezés", "Pontszám", "Egyéni", "Rajtszám", "Név", "Iskola"), data,
                data.stream().flatMap(team -> team.getMembers().stream()).collect(Collectors.toList()).size());

        mTeamRows = new ArrayList<>();
        int row = 0;
        for (int i = 0; i < data.size(); i++) {
            Team team = data.get(i);
            for (int j = 0; j < team.getSize(); j++, row++) {
                mTeamRows.add(row, i);
            }
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        int teamRow = mTeamRows.get(row);
        int rowInTeam = row - mTeamRows.indexOf(teamRow);

        if (column == Column.POSITION.ordinal()) {
            return teamRow + 1;
        }
        Team team = mData.get(teamRow);
        if (column == Column.POINTS.ordinal()) {
            return team.getPoints();
        }
        Contestant member = team.getMembers().get(rowInTeam);
        if (column == Column.INDIVIDUAL_POSITION.ordinal()) {
            return member.getPositionString();
        }
        if (column == Column.NAME.ordinal()) {
            return member.getName();
        }
        if (column == Column.NUMBER.ordinal()) {
            return member.getNumber();
        }
        if (column == Column.SCHOOL_NAME.ordinal()) {
            return team.getName();
        }

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