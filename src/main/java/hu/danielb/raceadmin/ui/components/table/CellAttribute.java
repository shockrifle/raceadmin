package hu.danielb.raceadmin.ui.components.table;

import java.awt.*;

public interface CellAttribute {

    void addColumn();

    void addRow();

    void insertRow(int row);

    Dimension getSize();

    void setSize(Dimension size);
}
