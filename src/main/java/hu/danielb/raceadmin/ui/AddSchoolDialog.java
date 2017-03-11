package hu.danielb.raceadmin.ui;

import com.j256.ormlite.stmt.Where;
import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.School;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class AddSchoolDialog extends BaseDialog {

    private List<SaveListener> listeners = new ArrayList<>();

    private javax.swing.JTextField textName;
    private javax.swing.JTextField textShortName;
    private javax.swing.JTextField textSettlement;

    private School mSchool;

    AddSchoolDialog(Dialog owner) {
        super(owner);
        initComponents();
        setLocationRelativeTo(owner);
    }

    AddSchoolDialog(Dialog owner, School school) {
        this(owner);
        mSchool = school;
        init();
    }

    private void init() {
        textName.setText(mSchool.getName());
        textShortName.setText(mSchool.getShortName());
        textSettlement.setText(mSchool.getSettlement());
    }

    private void initComponents() {

        JLabel labelShortName = new JLabel();
        JLabel labelName = new JLabel();
        JLabel labelSettlement = new JLabel();
        textName = new javax.swing.JTextField();
        textShortName = new javax.swing.JTextField();
        textSettlement = new javax.swing.JTextField();
        JButton buttonCancel = new JButton();
        JButton buttonSave = new JButton();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        labelName.setText("Iskola név:");
        labelShortName.setText("Rövid név:");
        labelSettlement.setText("Település:");

        buttonCancel.setText("Mégse");
        buttonCancel.addActionListener(e -> buttonCancelActionPerformed());

        buttonSave.setText("Mentés");
        buttonSave.addActionListener(e -> buttonSaveActionPerformed());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        ).addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelShortName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textShortName, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                ).addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelSettlement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textSettlement, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelName)
                                        .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(labelShortName)
                                        .addComponent(textShortName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(labelSettlement)
                                        .addComponent(textSettlement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonCancel)
                                        .addComponent(buttonSave))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void buttonCancelActionPerformed() {
        this.dispose();
    }

    private void buttonSaveActionPerformed() {
        String[] names = textName.getText().split(" ");
        ArrayList<String> checked = new ArrayList<>();
        try {
            Where<School, Integer> where = Database.get().getSchoolDao().queryBuilder().where();

            for (int i = 0; i < names.length; i++) {
                String tempName = names[i];
                tempName = tempName.toLowerCase()
                        .replaceAll("\\.", "")
                        .replaceAll("iskola", "")
                        .replaceAll("általános", "");
                if (tempName.length() > 2) {
                    if (i > 0) {
                        where.or();
                    }
                    where.like(School.COLUMN_NAME, tempName);
                }
            }
            where.query().forEach(school1 -> checked.add(school1.getName()));

            if (checked.isEmpty()) {
                saveSchool();
            } else {
                String msg = "Hasonló névvel már léteznek a következő iskolák: \n";
                for (String aChecked : checked) {
                    msg += aChecked + "\n";
                }
                msg += "Ezek valamelyikére gondolt?";
                switch (JOptionPane.showOptionDialog(this, msg, "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Igen", "Mentés", "Javít"}, null)) {
                    case 0:
                        warn("Kerese meg a listában!");
                        dispose();
                        break;
                    case 1:
                        saveSchool();
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        listeners.forEach(listener -> listener.onSave(new School()));
    }

    private void saveSchool() {
        if (mSchool == null) {
            mSchool = new School();
        }
        mSchool.setName(textName.getText());
        mSchool.setShortName(textShortName.getText());
        mSchool.setSettlement(textSettlement.getText());
        try {
            Database.get().getSchoolDao().createOrUpdate(mSchool);
            message("Iskola mentve.");
            dispose();
        } catch (SQLException ex) {
            Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    AddSchoolDialog addSaveListener(SaveListener listener) {
        listeners.add(listener);
        return this;
    }

    public interface SaveListener {
        void onSave(School newSchool);
    }
}
