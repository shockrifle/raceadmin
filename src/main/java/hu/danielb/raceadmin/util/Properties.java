package hu.danielb.raceadmin.util;

import java.io.IOException;

public class Properties {

    private static final String DEFAULT_BACKUP_TIME_FORMAT = "_HH.mm.ss";
    private static final int DEFAULT_BACKUP_INTERVAL = 5;
    private static final String DEFAULT_BACKUP_PATH = "backups";
    private static final String DEFAULT_DATABASE = "adatok.db";
    private static final String DEFAULT_EXPORTS_PATH = "exports";
    private static final String DEFAULT_VERSION = "1.0";

    private static Properties instance = new Properties();

    private final java.util.Properties mProperties;

    private Properties() {
        mProperties = new java.util.Properties();
        try {
            mProperties.load(this.getClass().getResourceAsStream("/project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties getInstance() {
        if (instance == null) {
            instance = new Properties();
        }
        return instance;
    }

    public static String getVersion() {
        return isLoaded() ? getInstance().mProperties.getProperty("version", DEFAULT_VERSION) : DEFAULT_VERSION;
    }

    public static String getExportsPath() {
        return isLoaded() ? getInstance().mProperties.getProperty("exports-path", DEFAULT_EXPORTS_PATH) : DEFAULT_EXPORTS_PATH;
    }

    public static String getDatabase() {
        return isLoaded() ? getInstance().mProperties.getProperty("database", DEFAULT_DATABASE) : DEFAULT_DATABASE;
    }

    public static String getBackupPath() {
        return isLoaded() ? getInstance().mProperties.getProperty("backup-path", DEFAULT_BACKUP_PATH) : DEFAULT_BACKUP_PATH;
    }

    /**
     * Returns the backup interval in minutes.
     *
     * @return the interval
     */
    public static int getBackupInterval() {
        if (isLoaded()) {
            try {
                return Integer.parseInt(getInstance().mProperties.getProperty("backup-interval", "" + DEFAULT_BACKUP_INTERVAL));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
        return DEFAULT_BACKUP_INTERVAL;
    }

    public static String getBackupTimeFormat() {
        return isLoaded() ? getInstance().mProperties.getProperty("backup-time-format", DEFAULT_BACKUP_TIME_FORMAT) : DEFAULT_BACKUP_TIME_FORMAT;
    }

    private static boolean isLoaded() {
        return getInstance().mProperties != null;
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

}
