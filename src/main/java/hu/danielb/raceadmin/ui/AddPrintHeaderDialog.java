package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.config.Database;
import hu.danielb.raceadmin.entity.PrintHeader;

import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class AddPrintHeaderDialog extends BaseDialog {

    private javax.swing.JTextArea textText;
    private javax.swing.JTextField textName;

    public AddPrintHeaderDialog(Frame owner) {
        super(owner);
        initComponents();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        textName = new javax.swing.JTextField();
        javax.swing.JScrollPane scrollText = new javax.swing.JScrollPane();
        textText = new javax.swing.JTextArea();
        javax.swing.JButton buttonCancel = new javax.swing.JButton();
        javax.swing.JButton buttonSave = new javax.swing.JButton();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);

        jLabel1.setText("Fejléc neve:");

        jLabel2.setText("Fejléc szövege:");

        textText.setColumns(20);
        textText.setFont(new java.awt.Font("Arial", 0, 12));
        textText.setRows(5);
        scrollText.setViewportView(textText);

        buttonCancel.setText("Mégse");
        buttonCancel.addActionListener(AddPrintHeaderDialog.this::buttonCancelActionPerformed);

        buttonSave.setText("Mentés");
        buttonSave.addActionListener(AddPrintHeaderDialog.this::buttonSaveActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(scrollText)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(textName))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 244, Short.MAX_VALUE)
                                                .addComponent(buttonSave)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonCancel)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollText, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonCancel)
                                        .addComponent(buttonSave))
                                .addContainerGap())
        );

        pack();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (textName.getText().trim().isEmpty()) {
                throw new Exception("Nem adott meg nevet!");
            }
            if (textText.getText().trim().isEmpty()) {
                throw new Exception("Nem adott meg szöveget!");
            }

            Database.runSql("insert into " + PrintHeader.TABLE + " (" + PrintHeader.COLUMN_NAME + "," + PrintHeader.COLUMN_TEXT + ") values(?,?)", Database.UPDATE, textName.getText().trim(), textText.getText().trim());
            this.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(AddPrintHeaderDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            warn(ex.getMessage());
        }
    }

}
