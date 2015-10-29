package hu.danielb.raceadmin.data;

public class AgeGroup {
    public static final String TABLE = "age_group";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MINIMUM = "minimum";
    public static final String COLUMN_MAXIMUM = "maximum";

    int id;
    String name;
    int minimum;
    int maximum;

    public AgeGroup(int id, String name, int minimum, int maximum) {
        this.id = id;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMin() {
        return minimum;
    }

    public int getMax() {
        return maximum;
    }

    @Override
    public String toString() {
        return name;
    }
}
