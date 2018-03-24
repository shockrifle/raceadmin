package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import hu.danielb.raceadmin.database.dao.CoachDao;

@DatabaseTable(tableName = "coach", daoClass = CoachDao.class)
public class Coach {

    public static final String COLUMN_SCHOOL_ID = "school_id";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int mId;
    @DatabaseField(columnName = COLUMN_NAME)
    private String mName = "";
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, columnName = COLUMN_SCHOOL_ID)
    private School school = new School();

    public Coach() {
    }

    public Coach(int id, String name, School school) {
        mId = id;
        mName = name;
        this.school = school;
    }

    public Coach(Coach other) {
        mId = other.getId();
        mName = other.getName();
        this.school = other.getSchool();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}
