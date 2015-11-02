package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "contestant")
public class Contestant {

    public static final String TABLE = "contestant";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SEX = "sex";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_AGE_GROUP_ID = "age_group_id";
    public static final String COLUMN_SCHOOL_ID = "school_id";
    public static final String COLUMN_AGE = "age";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private int position;
    @DatabaseField
    private String name;
    @DatabaseField
    private String sex;
    @DatabaseField
    private int number;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "age_group_id")
    private AgeGroup ageGroup;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, columnName = "school_id")
    private School school;
    @DatabaseField
    private int age;

    public Contestant(int id, int position, String name, String sex, int number, AgeGroup ageGroup, School school, int age) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.sex = sex;
        this.number = number;
        this.ageGroup = ageGroup;
        this.school = school;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
