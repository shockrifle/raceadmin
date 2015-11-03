package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import hu.danielb.raceadmin.database.dao.BaseDaoWithListener;

@DatabaseTable(tableName = "age_group", daoClass = BaseDaoWithListener.class)
public class AgeGroup {
    public static final String TABLE = "age_group";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MINIMUM = "minimum";
    public static final String COLUMN_MAXIMUM = "maximum";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private int minimum;
    @DatabaseField
    private int maximum;

    public AgeGroup() {
    }

    public AgeGroup(int id, String name, int minimum, int maximum) {
        this.id = id;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
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

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public boolean includes(int age) {
        return minimum <= age && age <= maximum;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgeGroup)) return false;

        AgeGroup ageGroup = (AgeGroup) o;

        return getId() == ageGroup.getId();

    }

    @Override
    public int hashCode() {
        return getId();
    }
}
