package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;
import hu.danielb.raceadmin.entity.Coach;

import java.sql.SQLException;
import java.util.List;

public class CoachDao extends BaseDaoWithListener<Coach, Integer> {

    public CoachDao(ConnectionSource connectionSource, Class<Coach> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Coach> getAll() throws SQLException {
        return queryBuilder().query();
    }

    public List<Coach> getBySchool(int id) throws SQLException {
        return queryBuilder().orderBy(Coach.COLUMN_NAME, true).where().eq(Coach.COLUMN_SCHOOL_ID, id).query();
    }
}
