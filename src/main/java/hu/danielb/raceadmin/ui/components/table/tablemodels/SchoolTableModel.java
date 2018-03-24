package hu.danielb.raceadmin.ui.components.table.tablemodels;

import hu.danielb.raceadmin.entity.School;

import java.util.Arrays;
import java.util.List;

public class SchoolTableModel extends SortableAttributiveCellTableModel<School> {

    public static final List<ColumnModel<School>> COLUMN_MODELS = Arrays.asList(
            new ColumnModel.Builder<School>()
                    .setName("Név")
                    .setGetter(School::getName)
                    .setWidth(280)
                    .setComparator(o -> o.getName().toLowerCase())
                    .build(),
            new ColumnModel.Builder<School>()
                    .setOrdinal(1)
                    .setName("Megjelenítendő név")
                    .setGetter(School::getShortName)
                    .setWidth(210)
                    .setComparator(o -> o.getShortName().toLowerCase())
                    .build(),
            new ColumnModel.Builder<School>()
                    .setOrdinal(2)
                    .setName("Település")
                    .setGetter(School::getSettlement)
                    .setWidth(180)
                    .setComparator(o -> o.getSettlement().toLowerCase())
                    .build(),
            new ColumnModel.Builder<School>()
                    .setOrdinal(3)
                    .setGetter(data -> "Szerkesztés")
                    .setWidth(90)
                    .setButton(true)
                    .setSortable(false)
                    .build());

    public SchoolTableModel(List<School> data) {
        super(COLUMN_MODELS, data);
    }

}
