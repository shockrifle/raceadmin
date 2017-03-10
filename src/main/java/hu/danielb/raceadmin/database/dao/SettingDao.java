package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;
import hu.danielb.raceadmin.entity.Setting;

import java.sql.SQLException;

public class SettingDao extends BaseDaoWithListener<Setting, String> {
    private static final String PRINT_HEADER_TITLE = "print_header_title";
    private static final String PRINT_HEADER_SUBTITLE = "print_header_subtitle";
    private static final String HIDE_DISQUALIFIED = "hide_disqualified";

    public SettingDao(ConnectionSource connectionSource, Class<Setting> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    private void putBoolean(String key, boolean value) {
        try {
            createOrUpdate(new Setting(key, String.valueOf(value)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        try {
            Setting setting = queryForId(key);
            if (setting != null) {
                return Boolean.parseBoolean(setting.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private void putString(String key, String value) {
        try {
            createOrUpdate(new Setting(key, value));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getString(String key) {
        return getString(key, "");
    }

    private String getString(String key, String defaultValue) {
        try {
            Setting setting = queryForId(key);
            if (setting != null) {
                return setting.getValue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public void saveHideDisqualified(boolean shouldHide) {
        putBoolean(HIDE_DISQUALIFIED, shouldHide);
    }

    public boolean getHideDisqualified() {
        return getBoolean(HIDE_DISQUALIFIED);
    }

    public void savePrintHeaderTitle(String title) {
        putString(PRINT_HEADER_TITLE, title);
    }

    public String getPrintHeaderTitle() {
        return getString(PRINT_HEADER_TITLE);
    }

    public void savePrintHeaderSubtitle(String subtitle) {
        putString(PRINT_HEADER_SUBTITLE, subtitle);
    }

    public String getPrintHeaderSubtitle() {
        return getString(PRINT_HEADER_SUBTITLE);
    }
}
