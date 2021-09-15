package hu.danielb.raceadmin.util;

import hu.danielb.raceadmin.entity.Coach;
import hu.danielb.raceadmin.entity.Contestant;

public class DataUtils {
    private DataUtils() {
    }

    public static Coach getCoach(Contestant contestant) {

        if (contestant != null) {
            if (contestant.getCoach() != null) {
                return contestant.getCoach();
            }
            if (contestant.getSchool() != null && contestant.getSchool().getCoach() != null) {
                return contestant.getSchool().getCoach();
            }
        }

        return null;
    }
}
