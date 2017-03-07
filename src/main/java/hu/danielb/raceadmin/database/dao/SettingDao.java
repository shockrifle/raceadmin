package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;
import hu.danielb.raceadmin.entity.Setting;

import java.sql.SQLException;

public class SettingDao extends BaseDaoWithListener<Setting, String> {
    private static final String SHOW_DISQUALIFIED = "show_disqualified";

    public SettingDao(ConnectionSource connectionSource, Class<Setting> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void putBoolean(String name, boolean value) {
        try {
            createOrUpdate(new Setting(name, String.valueOf(value)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        try {
            Setting setting = queryForId(name);
            if (setting != null) {
                return Boolean.parseBoolean(setting.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public void saveShowDisqualified(boolean shouldShow) {
        putBoolean(SHOW_DISQUALIFIED, shouldShow);
    }

    public boolean getShowDisqualified() {
        return getBoolean(SHOW_DISQUALIFIED);
    }
}
