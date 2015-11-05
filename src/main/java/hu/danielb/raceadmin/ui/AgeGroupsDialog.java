package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.ui.components.ButtonEditor;
import hu.danielb.raceadmin.ui.components.ButtonRenderer;
import hu.danielb.raceadmin.ui.components.table.models.AgeGroupTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

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
        new AddAgeGroupDialog(this, ((AgeGroupTableModel) tableAgeGroups.getModel()).getDataAt(evt.getID())).setVisible(true);
    }

    private void loadData() {

        try {
            tableAgeGroups.setModel(new AgeGroupTableModel(Database.get().getAgeGroupDao().queryForAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableAgeGroups.getColumnModel().getColumn(AgeGroupTableModel.Column.EDIT.ordinal()).setCellRenderer(new ButtonRenderer());
        tableAgeGroups.getColumnModel().getColumn(AgeGroupTableModel.Column.EDIT.ordinal()).setCellEditor(new ButtonEditor(
                AgeGroupsDialog.this::editButtonActionPerformed).addEditingStoppedListener(
                AgeGroupsDialog.this::loadData));
        tableAgeGroups.getTableHeader().setReorderingAllowed(false);
        tableAgeGroups.getTableHeader().setResizingAllowed(false);
        tableAgeGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


}
