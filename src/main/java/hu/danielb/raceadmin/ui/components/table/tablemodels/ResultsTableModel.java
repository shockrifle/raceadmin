package hu.danielb.raceadmin.ui.components.table.tablemodels;

import hu.danielb.raceadmin.entity.Contestant;

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
        super(COLUMN_MODELS, data);
    }
}