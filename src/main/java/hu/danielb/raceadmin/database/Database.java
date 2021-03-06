package hu.danielb.raceadmin.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import hu.danielb.raceadmin.database.dao.AgeGroupDao;
import hu.danielb.raceadmin.database.dao.BaseDaoWithListener;
import hu.danielb.raceadmin.database.dao.SchoolDao;
import hu.danielb.raceadmin.database.dao.SettingDao;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.entity.Setting;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static Database database;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Backup backup;
    private AgeGroupDao ageGroupDao;
    private Dao<Contestant, Integer> contestantDao;
    private SchoolDao schoolDao;
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

        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String databaseUrl = "jdbc:sqlite:";
        String databaseFile = properties.getProperty("database");

        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl + databaseFile);

        initDaos(connectionSource);
        createTables(connectionSource);
    }

    private void initDaos(ConnectionSource connectionSource) throws SQLException {
        ageGroupDao = DaoManager.createDao(connectionSource, AgeGroup.class);
        contestantDao = DaoManager.createDao(connectionSource, Contestant.class);
        schoolDao = DaoManager.createDao(connectionSource, School.class);
        settingDao = DaoManager.createDao(connectionSource, Setting.class);


        ageGroupDao.addListener(() -> backedUp = false);
        ((BaseDaoWithListener) contestantDao).addListener(() -> backedUp = false);
        schoolDao.addListener(() -> backedUp = false);
        settingDao.addListener(() -> backedUp = false);
    }

    private void createTables(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, AgeGroup.class);
        TableUtils.createTableIfNotExists(connectionSource, Contestant.class);
        TableUtils.createTableIfNotExists(connectionSource, School.class);
        TableUtils.createTableIfNotExists(connectionSource, Setting.class);
    }

    public AgeGroupDao getAgeGroupDao() {
        return ageGroupDao;
    }

    public Dao<Contestant, Integer> getContestantDao() {
        return contestantDao;
    }

    public SchoolDao getSchoolDao() {
        return schoolDao;
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
