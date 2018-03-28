package hu.danielb.raceadmin.ui.components.table.tablemodels;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.Contestant;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ResultsTableModel extends SortableAttributiveCellTableModel<Contestant> {

    public static final List<ColumnModel<Contestant>> COLUMN_MODELS = Arrays.asList(
            new ColumnModel.Builder<Contestant>()
                    .setName("Helyezés")
                    .setGetter(Contestant::getPositionString)
                    .setWidth(80)
                    .setSortable(false)
                    .build(),
            new ColumnModel.Builder<Contestant>()
                    .setOrdinal(1)
                    .setName("Rajtszám")
                    .setGetter(Contestant::getNumber)
                    .setWidth(80)
                    .setSortable(false)
                    .build(),
            new ColumnModel.Builder<Contestant>()
                    .setOrdinal(2)
                    .setName("Név")
                    .setGetter(Contestant::getName)
                    .setWidth(180)
                    .setSortable(false)
                    .build(),
            new ColumnModel.Builder<Contestant>()
                    .setOrdinal(3)
                    .setName("Iskola")
                    .setGetter(c -> c.getSchool() != null ? c.getSchool().getNameWithSettlement() : "")
                    .setSortable(false)
                    .build());

    public ResultsTableModel(List<Contestant> data) {
        super(COLUMN_MODELS, data, data.size() + 1);
    }

    @Override
    public Object getValueAt(int row, int tableColumn) {
        if (row == 0) {
            if (tableColumn == 3) {
                Contestant contestant = getDataAt(0);
                if (contestant.getCoach() != null) {
                    return contestant.getCoach().getName();
                } else {
                    if (contestant.getSchool().getCoachId() != 0) {
                        try {
                            return Database.get().getCoachDao().queryForId(contestant.getSchool().getCoachId()).getName();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return "";
        }
        return super.getValueAt(row - 1, tableColumn);
    }
}