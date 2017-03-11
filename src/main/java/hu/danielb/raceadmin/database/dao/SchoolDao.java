package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;
import hu.danielb.raceadmin.entity.School;

import java.sql.SQLException;
import java.util.List;

public class SchoolDao extends BaseDaoWithListener<School, Integer> {

    public SchoolDao(ConnectionSource connectionSource, Class<School> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<School> getAll() throws SQLException {
        return queryBuilder().orderBy(School.COLUMN_NAME, false).query();
    }
}
