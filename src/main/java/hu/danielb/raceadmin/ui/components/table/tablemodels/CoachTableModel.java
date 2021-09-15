package hu.danielb.raceadmin.ui.components.table.tablemodels;

import java.util.Arrays;
import java.util.List;

import hu.danielb.raceadmin.entity.Coach;

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
                    .setGetter(coach -> coach.getSchool() != null ? coach.getSchool().getNameWithSettlement() : "")
                    .setWidth(310)
                    .setComparator(o -> o.getSchool() != null ? o.getSchool().getNameWithSettlement() : "")
                    .build(),
            new ColumnModel.Builder<Coach>()
                    .setOrdinal(2)
                    .setName("Típus")
                    .setGetter(coach -> coach.getType() != null ? coach.getType().getName() : "")
                    .setWidth(90)
                    .setComparator(o -> o.getType() != null ? o.getType().getName() : "")
                    .build(),
            new ColumnModel.Builder<Coach>()
                    .setOrdinal(3)
                    .setGetter(data -> "Szerkesztés")
                    .setWidth(90)
                    .setButton(true)
                    .setSortable(false)
                    .build());

    public CoachTableModel(List<Coach> data) {
        super(COLUMN_MODELS, data);
    }

}
