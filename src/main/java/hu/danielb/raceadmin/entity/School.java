package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import hu.danielb.raceadmin.database.dao.SchoolDao;

@DatabaseTable(tableName = "school", daoClass = SchoolDao.class)
public class School {

    public static final String COLUMN_NAME = "name";
    private static final String COLUMN_SHORT_NAME = "short_name";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SETTLEMENT = "settlement";
    private static final String COLUMN_SUPERVISOR_ID = "coach_id";

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int id;
    @DatabaseField(columnName = COLUMN_NAME, defaultValue = "")
    private String name = "";
    @DatabaseField(columnName = COLUMN_SHORT_NAME, defaultValue = "")
    private String shortName = "";
    @DatabaseField(columnName = COLUMN_SETTLEMENT, defaultValue = "")
    private String settlement = "";
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = COLUMN_SUPERVISOR_ID)
    private Supervisor supervisor;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public String getNameWithSettlement() {
        if (shortName != null && shortName.length() > 0) {
            return shortName + (settlement != null && settlement.length() > 0 ? ", " + settlement : "");
        }
        return name + (settlement != null && settlement.length() > 0 ? ", " + settlement : "");
    }

    @Override
    public String toString() {
        return name;
    }
}
