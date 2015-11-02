package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import hu.danielb.raceadmin.database.dao.BaseDaoWithListener;

@DatabaseTable(tableName = "school", daoClass = BaseDaoWithListener.class)
public class School {

    public static final String TABLE = "school";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField(columnName = "short_name")
    private String shortName;
    @DatabaseField(columnName = "settlement")
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

    @Override
    public String toString() {
        return name;
    }
}
