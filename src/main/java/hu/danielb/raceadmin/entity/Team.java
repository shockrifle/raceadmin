package hu.danielb.raceadmin.entity;

import java.util.ArrayList;

public class Team implements Comparable {

    public static final int MAX_MEMBERS = 4;

    private String name;
    private ArrayList<Contestant> members;

    public Team(String name) {
        this.name = name;
        members = new ArrayList<>();
    }

    public void addMember(Contestant contestant) {
        members.add(contestant);
    }

    public ArrayList<Contestant> getMembers() {
        return members;
    }

    public int getPoints() {
        return getPoints(MAX_MEMBERS);
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

    public String getName() {
        return name;
    }

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    @Override
    public int compareTo(Object o) {
        Team other = (Team) o;
        return compareTo(other, MAX_MEMBERS);
    }

    private int compareTo(Team other, int memberCount) {
        if (memberCount <= 0) {
            return 0;
        }
        if (other.getPoints(memberCount) > this.getPoints(memberCount)) {
            return 1;
        } else if (other.getPoints(memberCount) == this.getPoints(memberCount)) {
            return compareTo(other, memberCount - 1);
        }
        return -1;
    }
}
