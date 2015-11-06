package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import hu.danielb.raceadmin.database.dao.BaseDaoWithListener;

@DatabaseTable(tableName = "school", daoClass = BaseDaoWithListener.class)
public class School {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SHORT_NAME = "short_name";
    public static final String COLUMN_SETTLEMENT = "settlement";

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int id;
    @DatabaseField(columnName = COLUMN_NAME)
    private String name;
    @DatabaseField(columnName = COLUMN_SHORT_NAME)
    private String shortName;
    @DatabaseField(columnName = COLUMN_SETTLEMENT)
    private String settlement;

    public School() {
    }

    public School(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getShortNameWithSettlement(){
        return shortName + ", " + settlement;
    }

    @Override
    public String toString() {
        return name;
    }
}
