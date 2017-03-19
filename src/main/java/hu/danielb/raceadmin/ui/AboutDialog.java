package hu.danielb.raceadmin.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class AboutDialog extends BaseDialog {
    private JPanel contentPane;
    private JPanel mStartupImagePanel;
    private JLabel mLabelVersion;

    public AboutDialog(Frame owner) {
        super(owner);
        initContent();
        setLocationRelativeTo(owner);
    }

    private void initContent() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        mStartupImagePanel.add(new BackgroundPanel(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/loading.jpg"))), BorderLayout.CENTER);
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mLabelVersion.setText(properties.getProperty("version"));

        pack();
    }
}
