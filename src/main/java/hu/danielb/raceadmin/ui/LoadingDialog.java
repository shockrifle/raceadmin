package hu.danielb.raceadmin.ui;

import javax.swing.*;
import java.awt.*;

class LoadingDialog extends javax.swing.JDialog {

    private String mMessage = "";
    private JProgressBar mProgress;

    LoadingDialog(java.awt.Frame parent, String message) {
        super(parent, true);
        this.mMessage = message;
        initComponents();
        this.setLocationRelativeTo(parent);
    }

    LoadingDialog(Dialog parent, String message) {
        super(parent, true);
        this.mMessage = message;
        initComponents();
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {

        javax.swing.JLabel labelMessage = new javax.swing.JLabel();
        mProgress = new JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setModal(true);
        setResizable(false);
        setUndecorated(true);

        labelMessage.setText(mMessage);

        mProgress.setIndeterminate(true);
        mProgress.setMaximumSize(new java.awt.Dimension(145, 15));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(mProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(labelMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelMessage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    void setMax(int max) {
        mProgress.setIndeterminate(false);
        mProgress.setValue(0);
        mProgress.setMinimum(0);
        mProgress.setMaximum(max);
    }

    void progress() {
        progress(1);
    }

    void progress(int progressToAdd) {
        mProgress.setValue(mProgress.getValue() + progressToAdd);
    }
}
