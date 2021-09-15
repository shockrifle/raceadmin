package hu.danielb.raceadmin.util;

import hu.danielb.raceadmin.entity.Contestant;

public class DataUtils {
    private DataUtils() {
    }

    public static String getCoachName(Contestant contestant) {

        if (contestant != null) {
            if (contestant.getCoach() != null) {
                return contestant.getCoach().getName();
            }
            if (contestant.getSchool() != null && contestant.getSchool().getCoach() != null) {
                return contestant.getSchool().getCoach().getName();
            }
        }

        return "";
    }
}
