package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import hu.danielb.raceadmin.database.listeners.DatabaseListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseDaoWithListener<T, ID> extends BaseDaoImpl<T, ID> {
    private List<DatabaseListener> listeners;

    public BaseDaoWithListener(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
        listeners = new ArrayList<>();
    }

    @Override
    public int create(T data) throws SQLException {
        notifyListeners();
        return super.create(data);
    }

    @Override
    public T createIfNotExists(T data) throws SQLException {
        notifyListeners();
        return super.createIfNotExists(data);
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(T data) throws SQLException {
        notifyListeners();
        return super.createOrUpdate(data);
    }

    @Override
    public int update(T data) throws SQLException {
        notifyListeners();
        return super.update(data);
    }

    @Override
    public int updateId(T data, ID newId) throws SQLException {
        notifyListeners();
        return super.updateId(data, newId);
    }

    @Override
    public int update(PreparedUpdate<T> preparedUpdate) throws SQLException {
        notifyListeners();
        return super.update(preparedUpdate);
    }

    @Override
    public int delete(T data) throws SQLException {
        notifyListeners();
        return super.delete(data);
    }

    @Override
    public int deleteById(ID ids) throws SQLException {
        notifyListeners();
        return super.deleteById(ids);
    }

    @Override
    public int delete(Collection<T> datas) throws SQLException {
        notifyListeners();
        return super.delete(datas);
    }

    @Override
    public int deleteIds(Collection<ID> ids) throws SQLException {
        notifyListeners();
        return super.deleteIds(ids);
    }

    @Override
    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
        notifyListeners();
        return super.delete(preparedDelete);
    }

    @Override
    public void commit(DatabaseConnection connection) throws SQLException {
        notifyListeners();
        super.commit(connection);
    }

    public BaseDaoWithListener addListener(DatabaseListener listener) {
        listeners.add(listener);
        return this;
    }

    private void notifyListeners() {
        listeners.forEach(DatabaseListener::onNotify);
    }
}
