package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import hu.danielb.raceadmin.entity.Supervisor;

public class SupervisorDao extends BaseDaoWithListener<Supervisor, Integer> {

    public SupervisorDao(ConnectionSource connectionSource, Class<Supervisor> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Supervisor> getAll() throws SQLException {
        return queryBuilder().query();
    }

    public List<Supervisor> getBySchool(int id) throws SQLException {
        return queryBuilder().orderBy(Supervisor.COLUMN_NAME, true).where().eq(Supervisor.COLUMN_SCHOOL_ID, id).query();
    }
}
