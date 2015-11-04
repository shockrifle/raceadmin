package hu.danielb.raceadmin.ui.components.table.models;

import hu.danielb.raceadmin.entity.AgeGroup;

import java.util.Arrays;
import java.util.List;

public class AgeGroupTableModel extends BaseTableModel<AgeGroup> {

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_MINIMUM = 2;
    public static final int COLUMN_MAXIMUM = 3;
    public static final int COLUMN_EDIT = 4;

    public AgeGroupTableModel(List<AgeGroup> data) {
        super(Arrays.asList("", "Név", "Alsó határ", "Felső határ", ""), data);
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case COLUMN_ID:
                return data.get(row).getId();
            case COLUMN_NAME:
                return data.get(row).getName();
            case COLUMN_MINIMUM:
                return data.get(row).getMinimum();
            case COLUMN_MAXIMUM:
                return data.get(row).getMaximum();
            case COLUMN_EDIT:
                return "Szerkesztés";
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COLUMN_EDIT;
    }
}