package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.ui.components.table.tablemodels.ColumnModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.SchoolTableModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.SortableAttributiveCellTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

public class SchoolsDialog extends BaseTableDialog<School> {

    SchoolsDialog(Frame owner) {
        super(owner);
    }

    @Override
    protected List<School> getData() {
        try {
            return Database.get().getSchoolDao().getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected List<ColumnModel<School>> getColumnModels() {
        return SchoolTableModel.COLUMN_MODELS;
    }

    @Override
    protected SortableAttributiveCellTableModel<School> getTableModel(List<School> data) {
        return new SchoolTableModel(data);
    }

    @Override
    protected Predicate<School> getFilter(String filter) {
        return school -> school.getName().toLowerCase().contains(filter) ||
                school.getShortName() != null && school.getShortName().toLowerCase().contains(filter) ||
                school.getSettlement() != null && school.getSettlement().toLowerCase().contains(filter);
    }

    @Override
    protected BaseDialog getEditDialog() {
        return new AddSchoolDialog(this);
    }

    @Override
    protected BaseDialog getEditDialog(School data) {
        return new AddSchoolDialog(this, data);
    }
}
