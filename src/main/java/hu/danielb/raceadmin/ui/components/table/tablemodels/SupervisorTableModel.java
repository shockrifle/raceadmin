package hu.danielb.raceadmin.ui.components.table.tablemodels;

import java.util.Arrays;
import java.util.List;

import hu.danielb.raceadmin.entity.Supervisor;

public class SupervisorTableModel extends SortableAttributiveCellTableModel<Supervisor> {

    public static final List<ColumnModel<Supervisor>> COLUMN_MODELS = Arrays.asList(
            new ColumnModel.Builder<Supervisor>()
                    .setName("Név")
                    .setGetter(Supervisor::getName)
                    .setWidth(285)
                    .setComparator(o -> o.getName().toLowerCase())
                    .build(),
            new ColumnModel.Builder<Supervisor>()
                    .setOrdinal(1)
                    .setName("Iskola")
                    .setGetter(supervisor -> supervisor.getSchool() != null ? supervisor.getSchool().getNameWithSettlement() : "")
                    .setWidth(310)
                    .setComparator(o -> o.getSchool() != null ? o.getSchool().getNameWithSettlement() : "")
                    .build(),
            new ColumnModel.Builder<Supervisor>()
                    .setOrdinal(2)
                    .setName("Típus")
                    .setGetter(supervisor -> supervisor.getType() != null ? supervisor.getType().getName() : "")
                    .setWidth(90)
                    .setComparator(o -> o.getType() != null ? o.getType().getName() : "")
                    .build(),
            new ColumnModel.Builder<Supervisor>()
                    .setOrdinal(3)
                    .setGetter(data -> "Szerkesztés")
                    .setWidth(90)
                    .setButton(true)
                    .setSortable(false)
                    .build());

    public SupervisorTableModel(List<Supervisor> data) {
        super(COLUMN_MODELS, data);
    }

}
