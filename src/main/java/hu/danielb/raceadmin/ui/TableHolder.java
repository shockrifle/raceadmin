package hu.danielb.raceadmin.ui;

import javax.swing.*;

public class TableHolder {
    JTable mTable;
    JLabel mLabel;

    TableHolder(JTable currentTable, JLabel label) {
        mTable = currentTable;
        mLabel = label;
    }

    public JTable getTable() {
        return mTable;
    }

    public JLabel getSupervisor() {
        return mLabel;
    }
}
