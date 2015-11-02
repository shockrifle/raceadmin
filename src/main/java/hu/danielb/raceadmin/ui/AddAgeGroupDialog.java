package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.DatabaseOld;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

class AddAgeGroupDialog extends BaseDialog {

    private AgeGroup ageGroup = null;

    private javax.swing.JSpinner spinnerMinimum;
    private javax.swing.JSpinner spinnerMaximum;
    private javax.swing.JTextField textName;

    public AddAgeGroupDialog(Dialog owner, AgeGroup ageGroup) {
        super(owner);
        this.ageGroup = ageGroup;
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {
        initComponents();
        if (ageGroup != null) {
            textName.setText(ageGroup.getName());
            spinnerMinimum.setValue(ageGroup.getMinimum());
            spinnerMaximum.setValue(ageGroup.getMaximum());
        }
    }

    private void initComponents() {

        JLabel labelName = new JLabel();
        JLabel labelMinimum = new JLabel();
        JLabel labelMaximum = new JLabel();
        textName = new javax.swing.JTextField();
        spinnerMinimum = new javax.swing.JSpinner();
        spinnerMaximum = new javax.swing.JSpinner();
        JButton buttonCancel = new JButton();
        JButton buttonSave = new JButton();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        labelName.setText("Korosztály neve:");

        labelMinimum.setText("Alsó határ:");

        labelMaximum.setText("Felső határ");

        spinnerMinimum.setModel(new SpinnerNumberModel(1900, 1900, Calendar.getInstance().get(Calendar.YEAR), 1));
        spinnerMinimum.addChangeListener(this::spinnerMinimumStateChanged);

        spinnerMaximum.setModel(new SpinnerNumberModel(1900, 1900, Calendar.getInstance().get(Calendar.YEAR), 1));
        spinnerMaximum.addChangeListener(this::spinnerMaximumStateChanged);

        buttonCancel.setText("Mégse");
        buttonCancel.addActionListener(this::buttonCancelActionPerformed);

        buttonSave.setText("Mentés");
        buttonSave.addActionListener(this::buttonSaveActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(buttonSave)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonCancel))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labelName)
                                                        .addComponent(labelMinimum)
                                                        .addComponent(labelMaximum))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(textName)
                                                        .addComponent(spinnerMinimum, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                                        .addComponent(spinnerMaximum))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelName)
                                        .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelMinimum)
                                        .addComponent(spinnerMinimum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelMaximum)
                                        .addComponent(spinnerMaximum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonCancel)
                                        .addComponent(buttonSave))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void spinnerMinimumStateChanged(javax.swing.event.ChangeEvent evt) {
        if ((Integer) spinnerMinimum.getValue() > (Integer) spinnerMaximum.getValue()) {
            spinnerMaximum.setValue(spinnerMinimum.getValue());
        }
    }

    private void spinnerMaximumStateChanged(javax.swing.event.ChangeEvent evt) {
        if ((Integer) spinnerMinimum.getValue() > (Integer) spinnerMaximum.getValue()) {
            spinnerMinimum.setValue(spinnerMaximum.getValue());
        }
    }

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ArrayList<AgeGroup> utkozes = new ArrayList<>();
            ResultSet rs = DatabaseOld.runSql("select * from " + AgeGroup.TABLE + " where (" +
                            AgeGroup.COLUMN_MINIMUM + " between ? and ? or " +
                            AgeGroup.COLUMN_MAXIMUM + " between ? and ?) and not " +
                            AgeGroup.COLUMN_ID + " = ?",
                    DatabaseOld.QUERRY,
                    String.valueOf(spinnerMinimum.getValue()),
                    String.valueOf(spinnerMaximum.getValue()),
                    String.valueOf(spinnerMinimum.getValue()),
                    String.valueOf(spinnerMaximum.getValue()),
                    ageGroup != null ? String.valueOf(ageGroup.getId()) : "0");
            while (rs.next()) {
                utkozes.add(new AgeGroup(rs.getInt(AgeGroup.COLUMN_ID), rs.getString(AgeGroup.COLUMN_NAME), rs.getInt(AgeGroup.COLUMN_MINIMUM), rs.getInt(AgeGroup.COLUMN_MAXIMUM)));
            }
            if (utkozes.isEmpty()) {
                if (ageGroup != null) {
                    if (0 == JOptionPane.showOptionDialog(this, "Ha megválzotatja a korosztályt, az eddigi eredmények elvesznek!\nBiztos ezt akarja?", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Igen", "Nem"}, null)) {
                        DatabaseOld.runSql("update " + AgeGroup.TABLE + " set " +
                                        AgeGroup.COLUMN_NAME + " = ?, " +
                                        AgeGroup.COLUMN_MINIMUM + " = ?, " +
                                        AgeGroup.COLUMN_MAXIMUM + " = ? where id = ?",
                                DatabaseOld.UPDATE, textName.getText(), String.valueOf(spinnerMinimum.getValue()), String.valueOf(spinnerMaximum.getValue()), String.valueOf(ageGroup.getId()));
                        DatabaseOld.runSql("update " + Contestant.TABLE + " set " +
                                        Contestant.COLUMN_POSITION + " = 0, ",
                                DatabaseOld.UPDATE);
                    }
                } else {
                    DatabaseOld.runSql("insert into " + AgeGroup.TABLE + " (" +
                                    AgeGroup.COLUMN_NAME + ", " +
                                    AgeGroup.COLUMN_MINIMUM + ", " +
                                    AgeGroup.COLUMN_MAXIMUM + ") values(?,?,?)",
                            DatabaseOld.UPDATE, textName.getText(), String.valueOf(spinnerMinimum.getValue()), String.valueOf(spinnerMaximum.getValue()));
                    DatabaseOld.runSql("update " + Contestant.TABLE + " set "
                                    + Contestant.COLUMN_POSITION + " = 0, "
                                    + "where " + Contestant.COLUMN_AGE_GROUP_ID + " = null or " +
                                    Contestant.COLUMN_AGE_GROUP_ID + " = '' or " +
                                    Contestant.COLUMN_AGE_GROUP_ID + " = 0",
                            DatabaseOld.UPDATE);
                }
                this.dispose();
            } else {
                String msg = "A beállított korhatár ütközik a következővel:\n";
                for (AgeGroup ageGroup1 : utkozes) {
                    msg += ageGroup1.getName() + "\n";
                }
                warn(msg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddAgeGroupDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
