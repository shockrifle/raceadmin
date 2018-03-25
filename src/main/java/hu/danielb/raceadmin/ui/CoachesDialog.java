package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.Coach;
import hu.danielb.raceadmin.ui.components.table.tablemodels.CoachTableModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.ColumnModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.SortableAttributiveCellTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

public class CoachesDialog extends BaseTableDialog<Coach> {

    CoachesDialog(Frame owner) {
        super(owner);
    }

    @Override
    protected List<Coach> getData() {
        try {
            return Database.get().getCoachDao().getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected List<ColumnModel<Coach>> getColumnModels() {
        return CoachTableModel.COLUMN_MODELS;
    }

    @Override
    protected SortableAttributiveCellTableModel<Coach> getTableModel(List<Coach> data) {
        return new CoachTableModel(data);
    }

    @Override
    protected Predicate<Coach> getFilter(String filter) {
        return coach -> coach.getName().toLowerCase().contains(filter) ||
                coach.getSchool().getNameWithSettlement() != null && coach.getSchool().getNameWithSettlement().toLowerCase().contains(filter);
    }

    @Override
    protected BaseDialog getEditDialog() {
        return null;
    }

    @Override
    protected BaseDialog getEditDialog(Coach data) {
        return null;
    }
}
