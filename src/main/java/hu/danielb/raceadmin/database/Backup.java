package hu.danielb.raceadmin.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class Backup {

    private static final String BACKUPTHREAD = "hu.danielb.raceadmin.database.Backup.BACKUPTHREAD";
    private String backupPath;

    public Backup() {

        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        backupPath = properties.getProperty("backupsPath");
        backupInterval = Integer.parseInt(properties.getProperty("backup-interval")) * 1000 * 60;

        start(backupInterval);
    }

    private void start(int backupInterval) {
        Timer backupTimer = new Timer(BACKUPTHREAD, true);
        backupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    backup();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, backupInterval, backupInterval);
    }

    synchronized public void backup() throws IOException {
        if (!Database.get().isBackedUp()) {
            DateFormat f = new SimpleDateFormat(BACKUP_TIME_FORMAT);
            File dir = new File(backupPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("Cannot create backup directory on path:" + dir.getAbsolutePath());
                }
            }
            Files.copy(new File(DATABASEFILE).toPath(), new File(backupPath + File.separator + DATABASEFILE + f.format(new Date())).toPath());
            Database.get().backedUp();
        }
    }
}
