package hu.danielb.raceadmin.entity;

import java.util.ArrayList;
import java.util.List;

public class Team implements Comparable {

    private final int mMaxMembers;
    private String mName;
    private List<Contestant> mMembers;

    public Team(String name, int maxMembers) {
        mName = name;
        mMembers = new ArrayList<>();
        mMaxMembers = maxMembers;
    }

    public void addMember(Contestant contestant) {
        mMembers.add(contestant);
    }

    public List<Contestant> getMembers() {
        return mMembers;
    }

    public int getPoints() {
        return getPoints(mMaxMembers);
    }

    private int getPoints(int to) {
        int toLocal = to;
        if (toLocal > mMembers.size()) {
            toLocal = mMembers.size();
        }
        int sum = 0;
        for (int i = 0; i < toLocal; i++) {
            sum += mMembers.get(i).getPosition();
        }
        return sum;
    }

    public String getName() {
        return mName;
    }

    public boolean isFull() {
        return mMembers.size() >= mMaxMembers;
    }

    public int getMaxMembers() {
        return mMaxMembers;
    }

    @Override
    public int compareTo(Object o) {
        Team other = (Team) o;
        return compareTo(other, mMaxMembers);
    }

    private int compareTo(Team other, int memberCount) {
        if (memberCount <= 0) {
            return 0;
        }
        if (other.getPoints(memberCount) > this.getPoints(memberCount)) {
            return -1;
        } else if (other.getPoints(memberCount) == this.getPoints(memberCount)) {
            return compareTo(other, memberCount - 1);
        }
        return 1;
    }
}
