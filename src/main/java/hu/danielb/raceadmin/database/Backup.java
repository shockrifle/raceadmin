package hu.danielb.raceadmin.database;

import hu.danielb.raceadmin.util.DateUtils;
import hu.danielb.raceadmin.util.Properties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Backup {

    private static final String BACKUP_THREAD = "hu.danielb.raceadmin.database.Backup.BACKUP_THREAD";
    private final int mBackupInterval;
    private final String mBackupsPath;
    private final String mFileName;
    private Timer mBackupTimer;

    Backup() {
        mBackupInterval = Properties.getBackupInterval() * 1000 * 60;
        mBackupsPath = Properties.getBackupPath();
        mFileName = Properties.getDatabase();
    }

    Backup start() {
        mBackupTimer = new Timer(BACKUP_THREAD, true);
        mBackupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    backup(mBackupsPath, mFileName);
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, mBackupInterval);
        return this;
    }

    public Backup stop() {
        mBackupTimer.cancel();
        return this;
    }

    private synchronized void backup(String backupsPath, String fileName) throws IOException, SQLException {
        if (!Database.get().isBackedUp()) {
            File dir = new File(backupsPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("Cannot create backup directory on path:" + dir.getAbsolutePath());
                }
            }
            Files.copy(new File(fileName).toPath(), new File(backupsPath + File.separator + fileName + DateUtils.formatForFile(new Date())).toPath());
            Database.get().backedUp();
        }
    }
}
