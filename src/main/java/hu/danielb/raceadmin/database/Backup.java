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

    private static final String BACKUP_THREAD = "hu.danielb.raceadmin.database.Backup.BACKUP_THREAD";
    private final int backupInterval;
    private final String backupsPath;
    private final String fileName;
    private final String timeFormat;
    private Timer backupTimer;

    public Backup() {

        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        backupInterval = Integer.parseInt(properties.getProperty("backup-interval")) * 1000 * 60;
        backupsPath = properties.getProperty("backup-path", "backups");
        fileName = properties.getProperty("database", "adatok.db");
        timeFormat = properties.getProperty("backup-time-format", "_HH.mm.ss");
    }

    public Backup start() {
        backupTimer = new Timer(BACKUP_THREAD, true);
        backupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    backup(backupsPath, fileName, timeFormat);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, backupInterval, backupInterval);
        return this;
    }

    public Backup stop() {
        backupTimer.cancel();
        return this;
    }

    synchronized public void backup(String backupsPath, String fileName, String timeFormat) throws IOException {
        if (!Database.get().isBackedUp()) {
            DateFormat f = new SimpleDateFormat(timeFormat);
            File dir = new File(backupsPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("Cannot create backup directory on path:" + dir.getAbsolutePath());
                }
            }
            Files.copy(new File(fileName).toPath(), new File(backupsPath + File.separator + fileName + f.format(new Date())).toPath());
            Database.get().backedUp();
        }
    }
}
