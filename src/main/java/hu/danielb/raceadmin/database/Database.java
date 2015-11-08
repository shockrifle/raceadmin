package hu.danielb.raceadmin.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import hu.danielb.raceadmin.database.dao.BaseDaoWithListener;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.PrintHeader;
import hu.danielb.raceadmin.entity.School;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static Database database;
    @SuppressWarnings("FieldCanBeLocal")
    private final Backup backup;
    private Dao<AgeGroup, Integer> ageGroupDao;
    private Dao<Contestant, Integer> contestantDao;
    private Dao<PrintHeader, Integer> printHeaderDao;
    private Dao<School, Integer> schoolDao;
    private boolean backedUp = true;

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
        printHeaderDao = DaoManager.createDao(connectionSource, PrintHeader.class);
        schoolDao = DaoManager.createDao(connectionSource, School.class);

        ((BaseDaoWithListener) ageGroupDao).addListener(() -> backedUp = false);
        ((BaseDaoWithListener) contestantDao).addListener(() -> backedUp = false);
        ((BaseDaoWithListener) printHeaderDao).addListener(() -> backedUp = false);
        ((BaseDaoWithListener) schoolDao).addListener(() -> backedUp = false);
    }

    private void createTables(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, AgeGroup.class);
        TableUtils.createTableIfNotExists(connectionSource, Contestant.class);
        TableUtils.createTableIfNotExists(connectionSource, PrintHeader.class);
        TableUtils.createTableIfNotExists(connectionSource, School.class);
    }

    public Dao<AgeGroup, Integer> getAgeGroupDao() {
        return ageGroupDao;
    }

    public Dao<Contestant, Integer> getContestantDao() {
        return contestantDao;
    }

    public Dao<PrintHeader, Integer> getPrintHeaderDao() {
        return printHeaderDao;
    }

    public Dao<School, Integer> getSchoolDao() {
        return schoolDao;
    }

    public void backedUp() {
        this.backedUp = true;
    }

    public boolean isBackedUp() {
        return backedUp;
    }
}
