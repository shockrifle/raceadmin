package hu.danielb.raceadmin.entity;

import java.util.ArrayList;

public class Team {

    String name;
    ArrayList<Contestant> members;

    public Team(String n) {
        name = n;
        members = new ArrayList<>();
    }

    public void addMember(Contestant m) {
        members.add(m);
    }

    public ArrayList<Contestant> getMembers() {
        return members;
    }

    public int getPoints(int to) {
        if (to > members.size()) {
            to = members.size();
        }
        int sum = 0;
        for (int i = 0; i < to; i++) {
            sum += members.get(i).getPosition();
        }
        return sum;
    }

    public boolean lowerThan(Team t) {
        if (t.getPoints(4) > this.getPoints(4)) {
            return true;
        } else if (t.getPoints(4) == this.getPoints(4)) {
            if (t.getPoints(3) > this.getPoints(3)) {
                return true;
            } else if (t.getPoints(3) == this.getPoints(3)) {
                if (t.getPoints(2) > this.getPoints(2)) {
                    return true;
                } else if (t.getPoints(2) == this.getPoints(2)) {
                    if (t.getPoints(1) > this.getPoints(1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isFull() {
        return members.size() >= 4;
    }
}
