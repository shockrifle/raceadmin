package hu.danielb.raceadmin.database;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import hu.danielb.raceadmin.database.dao.AgeGroupDao;
import hu.danielb.raceadmin.database.dao.ContestantDao;
import hu.danielb.raceadmin.database.dao.SchoolDao;
import hu.danielb.raceadmin.database.dao.SettingDao;
import hu.danielb.raceadmin.database.dao.SupervisorDao;
import hu.danielb.raceadmin.database.listeners.DatabaseListener;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.entity.Setting;
import hu.danielb.raceadmin.entity.Supervisor;
import hu.danielb.raceadmin.util.Properties;

public class Database {

    private static Database database;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Backup backup;
    private AgeGroupDao ageGroupDao;
    private ContestantDao contestantDao;
    private SchoolDao schoolDao;
    private SupervisorDao supervisorDao;
    private SettingDao settingDao;
    private boolean backedUp = false;

    private Database() throws SQLException {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        connect();
        backup = new Backup().start();
    }

    public static Database get() throws SQLException {
        if (database == null) database = new Database();
        return database;
    }

    private void connect() throws SQLException {

        String databaseUrl = "jdbc:sqlite:";
        String databaseFile = Properties.getDatabase();

        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl + databaseFile);

        initDaos(connectionSource);
        createTables(connectionSource);
    }

    private void initDaos(ConnectionSource connectionSource) throws SQLException {
        ageGroupDao = DaoManager.createDao(connectionSource, AgeGroup.class);
        contestantDao = DaoManager.createDao(connectionSource, Contestant.class);
        schoolDao = DaoManager.createDao(connectionSource, School.class);
        supervisorDao = DaoManager.createDao(connectionSource, Supervisor.class);
        settingDao = DaoManager.createDao(connectionSource, Setting.class);


        DatabaseListener listener = () -> backedUp = false;
        ageGroupDao.addListener(listener);
        contestantDao.addListener(listener);
        schoolDao.addListener(listener);
        supervisorDao.addListener(listener);
        settingDao.addListener(listener);
    }

    private void createTables(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, AgeGroup.class);
        TableUtils.createTableIfNotExists(connectionSource, Contestant.class);
        TableUtils.createTableIfNotExists(connectionSource, School.class);
        TableUtils.createTableIfNotExists(connectionSource, Supervisor.class);
        TableUtils.createTableIfNotExists(connectionSource, Setting.class);
    }

    public AgeGroupDao getAgeGroupDao() {
        return ageGroupDao;
    }

    public ContestantDao getContestantDao() {
        return contestantDao;
    }

    public SchoolDao getSchoolDao() {
        return schoolDao;
    }

    public SupervisorDao getSupervisorDao() {
        return supervisorDao;
    }

    public SettingDao getSettingDao() {
        return settingDao;
    }

    void backedUp() {
        this.backedUp = true;
    }

    boolean isBackedUp() {
        return backedUp;
    }
}
