package hu.danielb.raceadmin.ui;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class StartupScreen extends javax.swing.JDialog {

    private javax.swing.JLabel labelLoading;
    private javax.swing.JLabel labelVersion;
    private javax.swing.JPanel panelWithBackground;
    private javax.swing.JProgressBar progress;

    StartupScreen(java.awt.Frame parent, boolean modal, String version) {
        super(parent, modal);
        initComponents();
        labelVersion.setText(version);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        this.setLocation(x, y);
    }

    private void initComponents() {

        try {
            panelWithBackground = new BackgroundPanel(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/loading.jpg")));
            labelLoading = new javax.swing.JLabel();
            progress = new javax.swing.JProgressBar();
            labelVersion = new javax.swing.JLabel();

            setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            setMaximumSize(new java.awt.Dimension(300, 200));
            setMinimumSize(new java.awt.Dimension(300, 200));
            setModal(true);
            setUndecorated(true);
            setResizable(false);

        } catch (Exception ex) {
            Logger.getLogger(StartupScreen.class.getName()).log(Level.SEVERE, null, ex);
        }

        labelLoading.setText("Töltés...");

        progress.setIndeterminate(true);

        labelVersion.setText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(panelWithBackground);
        panelWithBackground.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(labelLoading)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(labelVersion)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(155, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelLoading)
                                        .addComponent(labelVersion))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelWithBackground, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelWithBackground, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }
}
