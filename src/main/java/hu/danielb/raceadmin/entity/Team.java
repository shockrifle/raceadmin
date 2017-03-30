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
        int result = Integer.compare(this.getPoints(), other.getPoints());
        if (result == 0) {
            return Integer.compare(this.getMembers().get(mMaxMembers - 1).getPosition(), other.getMembers().get(mMaxMembers - 1).getPosition());
        }
        return result;
    }
}
