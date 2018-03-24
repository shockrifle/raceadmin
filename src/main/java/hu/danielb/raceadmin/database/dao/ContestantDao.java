package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;
import hu.danielb.raceadmin.entity.Contestant;

import java.sql.SQLException;
import java.util.List;

public class ContestantDao extends BaseDaoWithListener<Contestant, Integer> {

    public ContestantDao(ConnectionSource connectionSource, Class<Contestant> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Contestant> getAll() throws SQLException {
        return queryBuilder().query();
    }
}
