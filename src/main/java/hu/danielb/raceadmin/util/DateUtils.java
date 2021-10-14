package hu.danielb.raceadmin.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String YEAR_LONGMONTH_DAY = "yyyy. MMMM d.";

    private DateUtils() {
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat(YEAR_LONGMONTH_DAY, Locale.forLanguageTag("hu")).format(date);
    }

    public static String formatForFile(Date date) {
        return new SimpleDateFormat(Properties.getBackupTimeFormat()).format(date);
    }
}
