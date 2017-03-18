package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import hu.danielb.raceadmin.database.dao.SettingDao;

@DatabaseTable(tableName = "setting", daoClass = SettingDao.class)
public class Setting {
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VALUE = "value";

    @DatabaseField(id = true, columnName = COLUMN_NAME)
    private String name = "";
    @DatabaseField(columnName = COLUMN_VALUE)
    private String value = "";

    public Setting() {
    }

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
