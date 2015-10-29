package hu.danielb.raceadmin.data;

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

    int id;
    int position;
    String name;
    String sex;
    int number;
    AgeGroup ageGroup;
    School school;
    int age;

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

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public int getNumber() {
        return number;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public School getSchool() {
        return school;
    }

    public int getAge() {
        return age;
    }
}
