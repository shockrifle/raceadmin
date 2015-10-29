/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.data;

import java.util.ArrayList;

/**
 * @author balu
 */
public class Team {

    String name;
    ArrayList<Versenyzo> members;

    public Team(String n) {
        name = n;
        members = new ArrayList<Versenyzo>();
    }

    public void addMember(Versenyzo m) {
        members.add(m);
    }

    public ArrayList<Versenyzo> getMembers() {
        return members;
    }

    public int getPoints() {
        return getPoints(4);
    }

    public int getPoints(int to) {
        if (to > members.size()) {
            to = members.size();
        }
        int sum = 0;
        for (int i = 0; i < to; i++) {
            sum += members.get(i).getHelyezes();
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
