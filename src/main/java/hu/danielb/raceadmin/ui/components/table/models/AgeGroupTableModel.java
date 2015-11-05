package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.AgeGroup;

import java.util.Arrays;
import java.util.List;

public class AgeGroupTableModel extends BaseTableModel<AgeGroup> {

    public AgeGroupTableModel(List<AgeGroup> data) {
        super(Arrays.asList("Név", "Alsó határ", "Felső határ", ""), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == Column.NAME.ordinal())
            return data.get(row).getName();
        if (column == Column.MINIMUM.ordinal())
            return data.get(row).getMinimum();
        if (column == Column.MAXIMUM.ordinal())
            return data.get(row).getMaximum();
        if (column == Column.EDIT.ordinal())
            return "Szerkesztés";
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == Column.EDIT.ordinal();
    }

    public enum Column {
        NAME,
        MINIMUM,
        MAXIMUM,
        EDIT
    }
}