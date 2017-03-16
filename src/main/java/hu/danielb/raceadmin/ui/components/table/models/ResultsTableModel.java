package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.Contestant;

import java.util.Arrays;
import java.util.List;

public class ResultsTableModel extends AttributiveCellTableModel<Contestant> {


    public ResultsTableModel(List<Contestant> data) {
        super(Arrays.asList("Helyezés", "Rajtszám", "Név", "Iskola"), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == Column.POSITION.ordinal())
            return data.get(row).getPosition();
        if (column == Column.NAME.ordinal())
            return data.get(row).getName();
        if (column == Column.NUMBER.ordinal())
            return data.get(row).getNumber();
        if (column == Column.SCHOOL_NAME.ordinal())
            return data.get(row).getSchool().getShortNameWithSettlement();
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public enum Column {
        POSITION,
        NUMBER,
        NAME,
        SCHOOL_NAME
    }

}