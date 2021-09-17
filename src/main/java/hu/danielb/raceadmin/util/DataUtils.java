package hu.danielb.raceadmin.util;

import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.Supervisor;

public class DataUtils {
    private DataUtils() {
    }

    public static Supervisor getSupervisor(Contestant contestant) {

        if (contestant != null) {
            if (contestant.getSupervisor() != null) {
                return contestant.getSupervisor();
            }
            if (contestant.getSchool() != null && contestant.getSchool().getSupervisor() != null) {
                return contestant.getSchool().getSupervisor();
            }
        }

        return null;
    }
}
