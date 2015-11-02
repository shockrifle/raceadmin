package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.config.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.ui.components.ButtonEditor;
import hu.danielb.raceadmin.ui.components.ButtonRenderer;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgeGroupsDialog extends BaseDialog {

    private javax.swing.JTable tableAgeGroups;

    public AgeGroupsDialog(Frame owner) {
        super(owner);
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {
        initComponents();
    }

    private void initComponents() {

        JScrollPane scrollPaneTableAgeGroups = new JScrollPane();
        tableAgeGroups = new javax.swing.JTable();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        loadData();
        scrollPaneTableAgeGroups.setViewportView(tableAgeGroups);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPaneTableAgeGroups, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPaneTableAgeGroups, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );

        pack();
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int row = evt.getID();
        new AddAgeGroupDialog(this,
                new AgeGroup(Integer.parseInt((String) tableAgeGroups.getValueAt(row, 0)),
                        (String) tableAgeGroups.getValueAt(row, 1),
                        Integer.parseInt((String) tableAgeGroups.getValueAt(row, 2)),
                        Integer.parseInt((String) tableAgeGroups.getValueAt(row, 3)))).setVisible(true);
    }

    private void loadData() {
        Vector<Vector<String>> data = new Vector<>();
        try {
            ResultSet rs = Database.runSql("select * from " + AgeGroup.TABLE);
            while (rs.next()) {
                data.add(new Vector<>(Arrays.asList(new String[]{
                        String.valueOf(rs.getInt(AgeGroup.COLUMN_ID)),
                        rs.getString(AgeGroup.COLUMN_NAME),
                        String.valueOf(rs.getInt(AgeGroup.COLUMN_MINIMUM)),
                        String.valueOf(rs.getInt(AgeGroup.COLUMN_MAXIMUM)),
                        "Szerkeszt"})));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AgeGroupsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableAgeGroups.setModel(new javax.swing.table.DefaultTableModel(data, new Vector<>(Arrays.asList(new String[]{"", "Név", "Alsó határ", "Felső határ", ""}))) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        });
        tableAgeGroups.getColumnModel().getColumn(0).setMaxWidth(0);
        tableAgeGroups.getColumnModel().getColumn(0).setMinWidth(0);
        tableAgeGroups.getColumnModel().getColumn(0).setPreferredWidth(0);
        tableAgeGroups.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        tableAgeGroups.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(
                AgeGroupsDialog.this::editButtonActionPerformed).addEditingStoppedListener(
                AgeGroupsDialog.this::loadData));
        tableAgeGroups.getTableHeader().setReorderingAllowed(false);
        tableAgeGroups.getTableHeader().setResizingAllowed(false);
        tableAgeGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

}
