package hu.danielb.raceadmin.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import hu.danielb.raceadmin.database.dao.AgeGroupDao;

@DatabaseTable(tableName = "age_group", daoClass = AgeGroupDao.class)
public class AgeGroup implements Comparable<AgeGroup> {

    public static final String COLUMN_MINIMUM = "minimum";
    public static final String COLUMN_MAXIMUM = "maximum";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TEAM_MIN_MEMBERS = "team_min_members";
    private static final String COLUMN_TEAM_MAX_MEMBERS = "team_max_members";

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int mId;
    @DatabaseField(columnName = COLUMN_NAME)
    private String mName = "";
    @DatabaseField(columnName = COLUMN_MINIMUM)
    private int mMinimum;
    @DatabaseField(columnName = COLUMN_MAXIMUM)
    private int mMaximum;
    @DatabaseField(columnName = COLUMN_TEAM_MIN_MEMBERS, defaultValue = "4")
    private int mTeamMinMembers = 4;
    @DatabaseField(columnName = COLUMN_TEAM_MAX_MEMBERS, defaultValue = "4")
    private int mTeamMaxMembers = 4;

    @SuppressWarnings("WeakerAccess")
    public AgeGroup() {
    }

    public AgeGroup(int id, String name, int minimum, int maximum, int teamMinMembers, int teamMaxMembers) {
        this.mId = id;
        this.mName = name;
        this.mMinimum = minimum;
        this.mMaximum = maximum;
        this.mTeamMinMembers = teamMinMembers;
        this.mTeamMaxMembers = teamMaxMembers;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getMinimum() {
        return mMinimum;
    }

    public void setMinimum(int minimum) {
        this.mMinimum = minimum;
    }

    public int getMaximum() {
        return mMaximum;
    }

    public void setMaximum(int maximum) {
        this.mMaximum = maximum;
    }

    public boolean includes(int age) {
        return mMinimum <= age && age <= mMaximum;
    }

    public int getTeamMinMembers() {
        return mTeamMinMembers;
    }

    public void setTeamMinMembers(int teamMinMembers) {
        mTeamMinMembers = teamMinMembers;
    }

    public int getTeamMaxMembers() {
        return mTeamMaxMembers;
    }

    public void setTeamMaxMembers(Integer teamMaxMembers) {
        mTeamMaxMembers = teamMaxMembers;
    }

    @Override
    public String toString() {
        return mName;
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

    @Override
    public int compareTo(AgeGroup o) {
        return Integer.compare(o.getMinimum(), this.getMinimum());
    }
}
