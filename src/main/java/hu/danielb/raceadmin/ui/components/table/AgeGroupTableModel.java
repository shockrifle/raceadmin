package hu.danielb.raceadmin.ui.components.table;

import hu.danielb.raceadmin.entity.AgeGroup;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

public class AgeGroupTableModel extends DefaultTableModel {

    private List<AgeGroup> data;

    public AgeGroupTableModel(List<String> columnIdentifiers, List<AgeGroup> data) {
        this.columnIdentifiers = new Vector<>(columnIdentifiers);
        this.data = data;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return "";
    }
}
