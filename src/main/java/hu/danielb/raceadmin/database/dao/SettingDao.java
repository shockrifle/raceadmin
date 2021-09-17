package hu.danielb.raceadmin.database.dao;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;

import hu.danielb.raceadmin.entity.Setting;

public class SettingDao extends BaseDaoWithListener<Setting, String> {
    private static final String PRINT_HEADER_TITLE = "print_header_title";
    private static final String PRINT_HEADER_SUBTITLE = "print_header_subtitle";
    private static final String HIDE_DISQUALIFIED = "hide_disqualified";
    private static final String ONLY_TEAM_ENTRIES = "only_team_entries";
    private static final String CONTESTANT_COUNTER = "contestant_counter";
    private static final String RACE_DATE = "race_date";

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

    @SuppressWarnings("SameParameterValue")
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

    @SuppressWarnings("SameParameterValue")
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

    private void putDate(String key, Date value) {
        try {
            createOrUpdate(new Setting(key, String.valueOf(value.getTime())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Date getDate(String key) {
        return getDate(key, new Date());
    }

    private Date getDate(String key, Date defaultValue) {
        try {
            Setting setting = queryForId(key);
            if (setting != null) {
                return new Date(Long.parseLong(setting.getValue()));
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

    public void saveOnlyTeamEntries(boolean enabled) {
        putBoolean(ONLY_TEAM_ENTRIES, enabled);
    }

    public boolean getOnlyTeamEntries() {
        return getBoolean(ONLY_TEAM_ENTRIES, true);
    }

    public void saveContestantCounter(boolean enabled) {
        putBoolean(CONTESTANT_COUNTER, enabled);
    }

    public boolean getContestantCounter() {
        return getBoolean(CONTESTANT_COUNTER, true);
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

    public void saveRaceDate(Date date) {
        putDate(RACE_DATE, date);
    }

    public Date getRaceDate() {
        return getDate(RACE_DATE);
    }
}
