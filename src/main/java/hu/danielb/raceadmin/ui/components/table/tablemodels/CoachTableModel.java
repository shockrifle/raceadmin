package hu.danielb.raceadmin.ui.components.table.tablemodels;

import hu.danielb.raceadmin.entity.Coach;

import java.util.Arrays;
import java.util.List;

public class CoachTableModel extends SortableAttributiveCellTableModel<Coach> {

    public static final List<ColumnModel<Coach>> COLUMN_MODELS = Arrays.asList(
            new ColumnModel.Builder<Coach>()
                    .setName("Név")
                    .setGetter(Coach::getName)
                    .setWidth(285)
                    .setComparator(o -> o.getName().toLowerCase())
                    .build(),
            new ColumnModel.Builder<Coach>()
                    .setOrdinal(1)
                    .setName("Iskola")
                    .setGetter(coach -> coach.getSchool().getNameWithSettlement())
                    .setWidth(400)
                    .setComparator(o -> o.getSchool().getNameWithSettlement().toLowerCase())
                    .build(),
            new ColumnModel.Builder<Coach>()
                    .setOrdinal(2)
                    .setGetter(data -> "Szerkesztés")
                    .setWidth(90)
                    .setButton(true)
                    .setSortable(false)
                    .build());

    public CoachTableModel(List<Coach> data) {
        super(COLUMN_MODELS, data);
    }

}
