package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.config.Database;
import hu.danielb.raceadmin.entity.School;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class AddSchoolDialog extends BaseDialog {


    private javax.swing.JTextField textName;

    public AddSchoolDialog(Dialog owner) {
        super(owner);
        initComponents();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {

        JLabel labelName = new JLabel();
        textName = new javax.swing.JTextField();
        JButton buttonCancel = new JButton();
        JButton buttonSave = new JButton();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        labelName.setText("Iskola név:");

        buttonCancel.setText("Mégse");
        buttonCancel.addActionListener(AddSchoolDialog.this::buttonCancelActionPerformed);

        buttonSave.setText("Mentés");
        buttonSave.addActionListener(AddSchoolDialog.this::buttonSaveActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        String[] names = textName.getText().split(" ");
        ArrayList<String> toCheck = new ArrayList<>();
        ArrayList<String> checked = new ArrayList<>();
        String checkStatement = "select * from " + School.TABLE + " where " + School.COLUMN_NAME + " like ''";
        for (String name : names) {
            String tempName = name;
            tempName = tempName.replaceAll("iskola", "")
                    .replaceAll("Iskola", "")
                    .replaceAll("isk", "")
                    .replaceAll("Isk", "")
                    .replaceAll("ISK", "");
            if (tempName.length() > 2) {
                checkStatement += " or ";
                toCheck.add("%" + tempName + "%");
                toCheck.add(tempName);
                checkStatement += School.COLUMN_NAME + " like ? or ? like '%'||" + School.COLUMN_NAME + "||'%'";
            }
        }
        System.out.println(checkStatement);
        ResultSet rs;
        try {
            rs = Database.runSql(checkStatement, Database.QUERRY, toCheck);
            while (rs.next()) {
                checked.add(rs.getString(School.COLUMN_NAME));
            }
            if (checked.isEmpty()) {
                try {
                    Database.runSql("insert into " + School.TABLE + " (" + School.COLUMN_NAME + ") values (?)", Database.UPDATE, textName.getText());
                    message("Új iskola hozzáadva");
                    dispose();
                } catch (SQLException ex) {
                    Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String msg = "Hasonló névvel már léteznek a következő iskolák: \n";
                for (String aChecked : checked) {
                    msg += aChecked + "\n";
                }
                msg += "Ezek valamelyikére gondolt?";
                switch (JOptionPane.showOptionDialog(this, msg, "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Igen", "Újat hozzáad", "Javít"}, null)) {
                    case 0:
                        warn("Kerese meg a listában!");
                        dispose();
                        break;
                    case 1:
                        try {
                            Database.runSql("insert into " + School.TABLE + " (" + School.COLUMN_NAME + ") values (?)", Database.UPDATE, textName.getText());
                        } catch (SQLException ex) {
                            Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        message("Új iskola hozzáadva");
                        dispose();
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
