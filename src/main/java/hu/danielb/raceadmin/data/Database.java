package hu.danielb.raceadmin.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Database {

    public static final int QUERRY = 1;
    public static final int UPDATE = 2;
    private static final String BACKUPTHREAD = "hu.danielb.raceadmin.Database.BACKUPTHREAD";
    private static final int BACKUPINTERVAL = 1000 * 60 * 5;
    private static final String DATABASEFILE = "adatok.db";
    private static final String BACKUP_TIME_FORMAT = "_HH.mm.ss";
    private static Database database;
    private Connection connection = null;
    private static String backupPath;
    private boolean backedUp = true;

    private Database() throws SQLException {
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        backupPath = properties.getProperty("backupsPath");

        // create a database connection
        connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASEFILE);
        Timer backupTimer = new Timer(BACKUPTHREAD, true);
        backupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    backup();
                } catch (IOException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, BACKUPINTERVAL, BACKUPINTERVAL);
    }

    public static void connect() throws SQLException {
        database = new Database();
        runSql("create table if not exists "+AgeGroup.TABLE+" ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' string, 'minimum' integer, 'maximum' integer)", UPDATE);
        runSql("create table if not exists "+Contestant.TABLE+" ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'position' integer, 'name' string, 'sex' string, 'number' integer, 'age_group_id' integer, 'school_id' integer, 'age' integer)", UPDATE);
        runSql("create table if not exists "+School.TABLE+" ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' string)", UPDATE);
        runSql("create table if not exists "+PrintHeader.TABLE+" ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' string, 'text' string)", UPDATE);
        runSql("insert or ignore into "+AgeGroup.TABLE+" (id,name,'minimum','maximum') values(?,?,?,?)", UPDATE, "1", "II. korcsoport", "2003", "2007");
        runSql("insert or ignore into "+AgeGroup.TABLE+" (id,name,'minimum','maximum') values(?,?,?,?)", UPDATE, "2", "III. korcsoport", "2001", "2002");
        runSql("insert or ignore into "+AgeGroup.TABLE+" (id,name,'minimum','maximum') values(?,?,?,?)", UPDATE, "3", "IV. korcsoport", "1999", "2000");
        runSql("insert or ignore into "+AgeGroup.TABLE+" (id,name,'minimum','maximum') values(?,?,?,?)", UPDATE, "4", "V. korcsoport", "1997", "1998");
        runSql("insert or ignore into "+AgeGroup.TABLE+" (id,name,'minimum','maximum') values(?,?,?,?)", UPDATE, "5", "VI. korcsoport", "1994", "1996");

    }

    synchronized public static ResultSet runSql(String statement) throws SQLException {
        return runSql(statement, QUERRY);
    }

    synchronized public static ResultSet runSql(String statement, int type, String... params) throws SQLException {
        return runSql(statement, type, Arrays.asList(params));
    }

    synchronized public static ResultSet runSql(String statement, int type, List<String> params) throws SQLException {

        ResultSet rs = null;

        PreparedStatement st = database.getConnection().prepareStatement(statement);
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                String string = params.get(i);
                try {
                    int num = Integer.parseInt(string);
                    st.setInt(i + 1, num);
                } catch (NumberFormatException ex) {
                    st.setString(i + 1, string);
                }
            }
        }
        switch (type) {
            case 1:
                rs = st.executeQuery();
                break;
            case 2:
                st.executeUpdate();
                database.edited();
                break;
            default:
                throw new AssertionError();
        }

        return rs;
    }

    synchronized public void backup() throws IOException {
        if (!database.isBackedUp()) {
            DateFormat f = new SimpleDateFormat(BACKUP_TIME_FORMAT);
            File dir = new File(backupPath);
            if (!dir.exists()) {
                if(!dir.mkdirs()){
                    throw new IOException("Cannot create backup directory on path:" + dir.getAbsolutePath() );
                }
            }
            Files.copy(new File(DATABASEFILE).toPath(), new File(backupPath + File.separator + DATABASEFILE + f.format(new Date())).toPath());
            database.backedUp();
        }
    }

    private Connection getConnection() {
        return connection;
    }

    private void edited() {
        this.backedUp = false;
    }

    private void backedUp() {
        this.backedUp = true;
    }

    private boolean isBackedUp() {
        return backedUp;
    }
}
