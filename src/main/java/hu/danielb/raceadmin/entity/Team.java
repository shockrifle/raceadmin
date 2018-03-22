package hu.danielb.raceadmin.entity;

import java.util.ArrayList;
import java.util.List;

public class Team implements Comparable {

    private final int mMinMembers;
    private final int mMaxMembers;
    private String mName;
    private List<Contestant> mMembers;

    public Team(String name, int minMembers, int maxMembers) {
        mName = name;
        mMembers = new ArrayList<>();
        mMinMembers = minMembers;
        mMaxMembers = maxMembers;
    }

    public void addMember(Contestant contestant) {
        mMembers.add(contestant);
    }

    public List<Contestant> getMembers() {
        return mMembers;
    }

    public int getPoints() {
        return getPoints(mMinMembers);
    }

    private int getPoints(int to) {
        if (to > mMembers.size()) {
            to = mMembers.size();
        }
        int sum = 0;
        for (int i = 0; i < to; i++) {
            sum += mMembers.get(i).getPosition();
        }
        return sum;
    }

    public String getName() {
        return mName;
    }

    public boolean isValid() {
        return mMembers.size() >= mMinMembers;
    }

    public boolean isFull() {
        return mMembers.size() >= mMaxMembers;
    }

    public int getMinMembers() {
        return mMinMembers;
    }

    public int getMaxMembers() {
        return mMaxMembers;
    }

    public int getSize() {
        return mMembers.size();
    }

    @Override
    public int compareTo(Object o) {
        Team other = (Team) o;
        int result = Integer.compare(this.getPoints(), other.getPoints());
        if (result == 0) {
            return Integer.compare(this.getMembers().get(mMinMembers - 1).getPosition(), other.getMembers().get(mMinMembers - 1).getPosition());
        }
        return result;
    }
}
