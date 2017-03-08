package hu.danielb.raceadmin.ui;

import javax.swing.*;
import java.awt.*;

class LoadingDialog extends javax.swing.JDialog {

    private String message = "";
    private JProgressBar progress;

    LoadingDialog(java.awt.Frame parent, String message) {
        super(parent, true);
        this.message = message;
        initComponents();
        this.setLocationRelativeTo(parent);
    }

    LoadingDialog(Dialog parent, String message) {
        super(parent, true);
        this.message = message;
        initComponents();
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {

        javax.swing.JLabel labelMessage = new javax.swing.JLabel();
        progress = new JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setModal(true);
        setResizable(false);
        setUndecorated(true);

        labelMessage.setText(message);

        progress.setIndeterminate(true);
        progress.setMaximumSize(new java.awt.Dimension(145, 15));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(labelMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelMessage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    void setMax(int max) {
        progress.setIndeterminate(false);
        progress.setValue(0);
        progress.setMinimum(0);
        progress.setMaximum(max);
    }

    void progress() {
        progress.setValue(progress.getValue() + 1);
    }
}
