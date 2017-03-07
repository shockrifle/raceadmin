package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;
import hu.danielb.raceadmin.entity.AgeGroup;

import java.sql.SQLException;
import java.util.List;

public class AgeGroupDao extends BaseDaoWithListener<AgeGroup, Integer> {

    public AgeGroupDao(ConnectionSource connectionSource, Class<AgeGroup> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<AgeGroup> getAll() throws SQLException {
        return queryBuilder().orderBy(AgeGroup.COLUMN_MINIMUM, false).query();
    }
}
