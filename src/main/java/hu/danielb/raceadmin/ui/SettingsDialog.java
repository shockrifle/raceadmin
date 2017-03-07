package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.sql.SQLException;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JList<String> mSettingsMenu;
    private JPanel mBasicSettings;
    private JPanel mAgeGroups;
    private JCheckBox hideDisqualifiedCheckBox;
    private JButton ageGroupCancelButton;
    private JButton ageGroupSaveButton;

    public SettingsDialog(Frame owner) {
        super(owner);
        setContentPane(contentPane);
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        mSettingsMenu.addListSelectionListener(this::listItemSelected);
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Általános");
        model.addElement("Korosztályok");
        model.addElement("Iskolák");
        model.addElement("Nyomtatási Fejléc");
        mSettingsMenu.setModel(model);
        mSettingsMenu.setSelectedIndex(0);

        boolean showDisqualified = false;
        try {
            showDisqualified = Database.get().getSettingDao().getShowDisqualified();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        hideDisqualifiedCheckBox.setSelected(showDisqualified);
        hideDisqualifiedCheckBox.addChangeListener(this::hideCheckboxChanged);

        pack();
    }

    private void hideCheckboxChanged(ChangeEvent e) {
        try {
            Database.get().getSettingDao().saveShowDisqualified(((JCheckBox) e.getSource()).isSelected());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void listItemSelected(ListSelectionEvent e) {
        int index = mSettingsMenu.getSelectedIndex();
        mBasicSettings.setVisible(false);
        mAgeGroups.setVisible(false);
        switch (index) {
            case 0:
                mBasicSettings.setVisible(true);
                break;
            case 1:
                mAgeGroups.setVisible(true);
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
}
