package hu.danielb.raceadmin.ui;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.Supervisor;
import hu.danielb.raceadmin.ui.components.table.tablemodels.ColumnModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.SortableAttributiveCellTableModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.SupervisorTableModel;

public class SupervisorDialog extends BaseTableDialog<Supervisor> {

    SupervisorDialog(Frame owner) {
        super(owner);
    }

    @Override
    protected List<Supervisor> getData() {
        try {
            return Database.get().getSupervisorDao().getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected List<ColumnModel<Supervisor>> getColumnModels() {
        return SupervisorTableModel.COLUMN_MODELS;
    }

    @Override
    protected SortableAttributiveCellTableModel<Supervisor> getTableModel(List<Supervisor> data) {
        return new SupervisorTableModel(data);
    }

    @Override
    protected Predicate<Supervisor> getFilter(String filter) {
        return supervisor -> supervisor.getName().toLowerCase().contains(filter) ||
                supervisor.getSchool() != null && supervisor.getSchool().getNameWithSettlement() != null && supervisor.getSchool().getNameWithSettlement().toLowerCase().contains(filter);
    }

    @Override
    protected BaseDialog getEditDialog() {
        return new AddSupervisorDialog(this);
    }

    @Override
    protected BaseDialog getEditDialog(Supervisor data) {
        return new AddSupervisorDialog(this, data);
    }
}
