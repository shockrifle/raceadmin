package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

import hu.danielb.raceadmin.database.dao.SupervisorDao;

@DatabaseTable(tableName = "supervisor", daoClass = SupervisorDao.class)
public class Supervisor {

    public static final String COLUMN_SCHOOL_ID = "school_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ID = "id";

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int mId;
    @DatabaseField(columnName = COLUMN_NAME)
    private String mName = "";
    @DatabaseField(columnName = COLUMN_TYPE)
    private Type mType;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = COLUMN_SCHOOL_ID)
    private School school = new School();

    public Supervisor() {
    }

    public Supervisor(int id) {
        mId = id;
    }

    public Supervisor(int id, String name, Type type) {
        mId = id;
        mName = name;
        mType = type;
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

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Supervisor)) return false;
        Supervisor supervisor = (Supervisor) o;
        return mId == supervisor.mId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(mId);
    }

    public enum Type {
        COACH("Edző"),
        TEACHER("Tanár");

        private String mName;

        Type(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }
}
