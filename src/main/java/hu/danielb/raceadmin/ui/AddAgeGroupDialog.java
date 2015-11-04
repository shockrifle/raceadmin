package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        } else {
            ageGroup = new AgeGroup();
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
            AgeGroup ageGroupOld = new AgeGroup(ageGroup);
            ageGroup.setName(textName.getText());
            ageGroup.setMinimum((Integer) spinnerMinimum.getValue());
            ageGroup.setMaximum((Integer) spinnerMaximum.getValue());

            List<AgeGroup> overlaps = Database.get().getAgeGroupDao().queryForAll().stream().filter(ageGroup2 ->
                    !(ageGroup != null && ageGroup2.getId() == ageGroup.getId()) &&
                            (ageGroup.includes(ageGroup2.getMinimum()) ||
                                    ageGroup.includes(ageGroup2.getMaximum()))).collect(Collectors.toList());

            if (overlaps.isEmpty()) {
                if (ageGroup.getId() != 0) {
                    if (0 == JOptionPane.showOptionDialog(this, "Ha megválzotatja a korosztályt, az eddigi eredmények elvesznek!\nBiztos ezt akarja?", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Igen", "Nem"}, null)) {
                        Database.get().getAgeGroupDao().createOrUpdate(ageGroup);
                        Database.get().getContestantDao().queryForAll().forEach(contestant -> {
                            contestant.setPosition(0);
                            updateContestantAgeGroup(contestant, ageGroupOld, ageGroup);
                        });
                    }
                } else {
                    Database.get().getAgeGroupDao().createOrUpdate(ageGroup);
                    Database.get().getContestantDao().queryForAll().forEach(contestant -> updateContestantAgeGroup(contestant, ageGroupOld, ageGroup));
                }
                this.dispose();
            } else {
                String msg = "A beállított korhatár ütközik a következővel:\n";
                for (AgeGroup ageGroup1 : overlaps) {
                    msg += ageGroup1.getName() + "\n";
                }
                warn(msg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddAgeGroupDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void updateContestantAgeGroup(Contestant contestant, AgeGroup ageGroupOld, AgeGroup ageGroupNew){
        if (ageGroupOld.equals(contestant.getAgeGroup()) && !ageGroupNew.includes(contestant.getAge())) {
            contestant.setAgeGroup(null);
        } else if (!ageGroupOld.equals(contestant.getAgeGroup()) && ageGroupNew.includes(contestant.getAge())) {
            contestant.setAgeGroup(ageGroupNew);
        }
        try {
            Database.get().getContestantDao().update(contestant);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
