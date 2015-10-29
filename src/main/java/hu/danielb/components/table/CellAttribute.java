/*
 * (swing1.1beta3)
 * 
 */
package hu.danielb.components.table;

import java.awt.*;

/**
 * @version 1.0 11/22/98
 */
public interface CellAttribute {

    void addColumn();

    void addRow();

    void insertRow(int row);

    Dimension getSize();

    void setSize(Dimension size);
}
