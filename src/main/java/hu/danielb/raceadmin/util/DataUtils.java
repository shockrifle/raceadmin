package hu.danielb.raceadmin.util;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.Contestant;

import java.sql.SQLException;

public class DataUtils {
    private DataUtils() {
    }

    public static String getCoachName(Contestant contestant) {

        if (contestant != null) {
            if (contestant.getCoach() != null) {
                return contestant.getCoach().getName();
            }
            if (contestant.getSchool() != null && contestant.getSchool().getCoachId() != 0) {
                try {
                    return Database.get().getCoachDao().queryForId(contestant.getSchool().getCoachId()).getName();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }
}
