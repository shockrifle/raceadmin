package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsDialog extends BaseDialog {
    private JPanel mContentPane;
    private JList<String> mSettingsMenu;
    private JPanel mBasicSettings;
    private JPanel mAgeGroups;
    private JCheckBox hideDisqualifiedCheckBox;
    private JButton mAgeGroupCancelButton;
    private JButton mAgeGroupSaveButton;
    private JPanel mAgeGroupContainer;
    private JPanel mPrintHeader;
    private JTextField mTitleTextField;
    private JTextField mSubtitleTextField;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel mSettingsContent;
    private JButton mPrintHeaderSaveButton;
    private JButton mPrintHeaderCancelButton;
    private List<AgeGroup> mAgeGroupList;
    private boolean ageGroupsSaved = true;
    private ListSelectionListener mListItemSelectedListener;
    private boolean mPrintHeaderSaved = true;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    SettingsDialog(Frame owner) {
        super(owner);
        setContentPane(mContentPane);
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        mListItemSelectedListener = e -> listItemSelected();
        mSettingsMenu.addListSelectionListener(mListItemSelectedListener);
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Általános");
        model.addElement("Korosztályok");
        model.addElement("Nyomtatási Fejléc");
        mSettingsMenu.setModel(model);
        mSettingsMenu.setSelectedIndex(0);

        // basic
        hideDisqualifiedCheckBox.setSelected(loadHideDisqualified());
        hideDisqualifiedCheckBox.addChangeListener(this::hideCheckboxChanged);

        // age groups
        mAgeGroupCancelButton.addActionListener(e -> ageGroupCancelClicked());
        mAgeGroupSaveButton.addActionListener(e -> ageGroupSaveClicked());
        mAgeGroupContainer.setLayout(new BoxLayout(mAgeGroupContainer, BoxLayout.Y_AXIS));
        loadAgeGroups();

        //print header

        mTitleTextField.addKeyListener(new PrintHeaderEditListener());
        mSubtitleTextField.addKeyListener(new PrintHeaderEditListener());

        mPrintHeaderSaveButton.addActionListener(e -> printHeaderSaveClicked());
        mPrintHeaderCancelButton.addActionListener(e -> printHeaderCancelClicked());
        loadPrintHeader();

        pack();
    }

    private void loadAgeGroups() {
        try {
            mAgeGroupList = Database.get().getAgeGroupDao().getAll();
            loadAgeGroups(mAgeGroupList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAgeGroups(List<AgeGroup> ageGroups) {
        mAgeGroupContainer.removeAll();

        if (ageGroups != null) {
            ageGroups.forEach(this::createAgeGroupView);
        }
        if (ageGroups == null || ageGroups.isEmpty() || ageGroups.size() > 0 && ageGroups.get(ageGroups.size() - 1).getId() != 0) {
            JButton newAgeGroupButton = new JButton("Új korosztály");
            newAgeGroupButton.addActionListener(e -> newAgeGroupButtonClicked());
            mAgeGroupContainer.add(newAgeGroupButton);
        }

        mAgeGroupContainer.revalidate();
        mAgeGroupContainer.repaint();
    }

    private void ageGroupCancelClicked() {
        disableAgeGroupSaveAndCancel();
        loadAgeGroups();
    }

    private void ageGroupSaveClicked() {
        if (0 == JOptionPane.showOptionDialog(this, "Ha megválzotatja a korosztályt, az eddigi eredmények elvesznek!\nBiztos ezt akarja?", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Igen", "Nem"}, null)) {
            List<AgeGroup> overlaps = null;
            for (AgeGroup ageGroup : mAgeGroupList) {
                overlaps = mAgeGroupList.stream().filter(ageGroup2 ->
                        ageGroup2.getId() != ageGroup.getId() &&
                                (ageGroup.includes(ageGroup2.getMinimum()) ||
                                        ageGroup.includes(ageGroup2.getMaximum()))).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(overlaps)) break;
            }

            if (CollectionUtils.isEmpty(overlaps)) {
                LoadingDialog dialog = new LoadingDialog(this, "Mentés...");
                dialog.setMax(mAgeGroupList.size());
                new Thread(() -> {
                    try {
                        for (AgeGroup ageGroup : mAgeGroupList) {
                            if (ageGroup.getId() != 0) {
                                AgeGroup ageGroupOld = Database.get().getAgeGroupDao().queryForId(ageGroup.getId());
                                Database.get().getAgeGroupDao().createOrUpdate(ageGroup);
                                Database.get().getContestantDao().queryForAll().forEach(contestant -> {
                                    contestant.setPosition(0);
                                    updateContestantAgeGroup(contestant, ageGroupOld, ageGroup);
                                });

                            } else {
                                Database.get().getAgeGroupDao().createOrUpdate(ageGroup);
                                Database.get().getContestantDao().queryForAll().forEach(contestant -> updateContestantAgeGroup(contestant, null, ageGroup));
                            }
                            dialog.progress();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    dialog.dispose();
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            disableAgeGroupSaveAndCancel();
                            loadAgeGroups();
                        });
                    } catch (InterruptedException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }).start();
                dialog.setVisible(true);
            } else {
                StringBuilder buf = new StringBuilder("A beállított korhatár ütközik a következővel:\n");
                for (AgeGroup ageGroup1 : overlaps) {
                    buf.append(ageGroup1.getName()).append("\n");
                }
                warn(buf.toString());
            }
        }
    }

    private void updateContestantAgeGroup(Contestant contestant, AgeGroup ageGroupOld, AgeGroup ageGroupNew) {
        if (ageGroupOld != null && ageGroupOld.equals(contestant.getAgeGroup())) {
            contestant.setAgeGroup(null);
        }
        if (ageGroupNew != null && ageGroupNew.includes(contestant.getAge())) {
            contestant.setAgeGroup(ageGroupNew);
        }
        try {
            Database.get().getContestantDao().update(contestant);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void newAgeGroupButtonClicked() {
        enableAgeGroupSaveAndCancel();
        AgeGroup last = null;
        if (mAgeGroupList != null && !mAgeGroupList.isEmpty()) {
            last = mAgeGroupList.get(mAgeGroupList.size() - 1);
        }
        AgeGroup newAgeGroup = new AgeGroup(0, "Új korosztály", (last != null ? last.getMinimum() - 2 : 2000),
                (last != null ? last.getMinimum() - 1 : 2001), 4);
        mAgeGroupList.add(newAgeGroup);
        loadAgeGroups(mAgeGroupList);
    }

    private void enableAgeGroupSaveAndCancel() {
        ageGroupsSaved = false;
        mAgeGroupSaveButton.setEnabled(true);
        mAgeGroupCancelButton.setEnabled(true);
    }

    private void disableAgeGroupSaveAndCancel() {
        ageGroupsSaved = true;
        mAgeGroupSaveButton.setEnabled(false);
        mAgeGroupCancelButton.setEnabled(false);
    }

    private void createAgeGroupView(AgeGroup ageGroup) {
        JPanel ageGroupRow = new JPanel();

        JTextField name = new JTextField(ageGroup.getName());
        name.setPreferredSize(new Dimension(100, 22));

        name.addKeyListener(new AgeGroupNameEditListener());

        JSpinner minimum = new JSpinner(new SpinnerNumberModel(ageGroup.getMinimum(), 1900, 2100, 1));
        if (ageGroup.getId() != 0) {
            ((JSpinner.DefaultEditor) minimum.getEditor()).getTextField().setEditable(false);
            minimum.addChangeListener(e -> ageGroupMinimumChanged(e, ageGroup));
        } else {
            minimum.addChangeListener(e -> ageGroup.setMinimum((Integer) minimum.getValue()));
        }

        JSpinner maximum = new JSpinner(new SpinnerNumberModel(ageGroup.getMaximum(), 1900, 2100, 1));
        if (ageGroup.getId() != 0) {
            ((JSpinner.DefaultEditor) maximum.getEditor()).getTextField().setEditable(false);
            maximum.addChangeListener(e -> ageGroupMaximumChanged(e, ageGroup));
        } else {
            maximum.addChangeListener(e -> ageGroup.setMaximum((Integer) maximum.getValue()));
        }

        JSpinner teamHeadCount = new JSpinner(new SpinnerNumberModel(ageGroup.getTeamMaxMembers(), 1, 9999, 1));
        if (ageGroup.getId() != 0) {
            ((JSpinner.DefaultEditor) teamHeadCount.getEditor()).getTextField().setEditable(false);
        }
        teamHeadCount.addChangeListener(e -> ageGroupHeadCountChanged(e, ageGroup));
        teamHeadCount.setPreferredSize(new Dimension(40, 22));

        JButton delete = new JButton("Törlés");

        delete.addActionListener(e -> deleteAgeGroupClicked(ageGroup));

        ageGroupRow.add(name);
        ageGroupRow.add(minimum);
        ageGroupRow.add(maximum);
        ageGroupRow.add(teamHeadCount);
        ageGroupRow.add(delete);

        mAgeGroupContainer.add(ageGroupRow);
    }

    private void deleteAgeGroupClicked(AgeGroup ageGroup) {
        if (0 == JOptionPane.showOptionDialog(this, "Ha törli a korosztályt, az eddigi eredmények elvesznek!\nBiztos ezt akarja?", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Igen", "Nem"}, null)) {
            LoadingDialog dialog = new LoadingDialog(this, "Mentés...");
            new Thread(() -> {
                try {
                    Database.get().getAgeGroupDao().delete(ageGroup);
                    List<Contestant> contestants = Database.get().getContestantDao().queryForAll();
                    dialog.setMax(contestants.size());
                    contestants.forEach(contestant -> {
                        contestant.setPosition(0);
                        updateContestantAgeGroup(contestant, ageGroup, null);
                        dialog.progress();
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                dialog.dispose();
                try {
                    SwingUtilities.invokeAndWait(this::loadAgeGroups);
                } catch (InterruptedException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }).start();
            dialog.setVisible(true);
        }
    }

    private void ageGroupMinimumChanged(ChangeEvent e, AgeGroup ageGroup) {
        enableAgeGroupSaveAndCancel();
        int oldValue = ageGroup.getMinimum();
        Integer newValue = (Integer) ((JSpinner) e.getSource()).getValue();
        mAgeGroupList.forEach(ageGroupLocal -> {
            if (oldValue < newValue) {
                incrementValue(newValue);
            } else if (oldValue > newValue) {
                decrementValue(newValue);
            }
        });
        ageGroup.setMinimum(newValue);
        loadAgeGroups(mAgeGroupList);
    }

    private void ageGroupMaximumChanged(ChangeEvent e, AgeGroup ageGroup) {
        enableAgeGroupSaveAndCancel();
        int oldValue = ageGroup.getMaximum();
        Integer newValue = (Integer) ((JSpinner) e.getSource()).getValue();
        mAgeGroupList.forEach(ageGroupLocal -> {
            if (oldValue < newValue) {
                incrementValue(newValue);
            } else if (oldValue > newValue) {
                decrementValue(newValue);
            }
        });
        ageGroup.setMaximum(newValue);
        loadAgeGroups(mAgeGroupList);
    }

    private void incrementValue(int base) {
        for (AgeGroup ageGroup : mAgeGroupList) {
            if (ageGroup.getMinimum() == base) {
                incrementValue(base + 1);
                ageGroup.setMinimum(ageGroup.getMinimum() + 1);
            }
            if (ageGroup.getMaximum() == base) {
                incrementValue(base + 1);
                ageGroup.setMaximum(ageGroup.getMaximum() + 1);
            }
        }
    }

    private void decrementValue(int base) {
        for (AgeGroup ageGroup : mAgeGroupList) {
            if (ageGroup.getMinimum() == base) {
                decrementValue(base - 1);
                ageGroup.setMinimum(ageGroup.getMinimum() - 1);
            }
            if (ageGroup.getMaximum() == base) {
                decrementValue(base - 1);
                ageGroup.setMaximum(ageGroup.getMaximum() - 1);
            }
        }
    }

    private void ageGroupHeadCountChanged(ChangeEvent e, AgeGroup ageGroup) {
        enableAgeGroupSaveAndCancel();
        ageGroup.setTeamMaxMembers((Integer) ((JSpinner) e.getSource()).getValue());
    }

    private void enablePrintHeaderSaveAndCancel() {
        mPrintHeaderSaved = false;
        mPrintHeaderSaveButton.setEnabled(true);
        mPrintHeaderCancelButton.setEnabled(true);
    }

    private void disablePrintHeaderSaveAndCancel() {
        mPrintHeaderSaved = true;
        mPrintHeaderSaveButton.setEnabled(false);
        mPrintHeaderCancelButton.setEnabled(false);
    }

    private void loadPrintHeader() {
        try {
            String printHeaderTitle = Database.get().getSettingDao().getPrintHeaderTitle();
            titleLabel.setText(printHeaderTitle);
            mTitleTextField.setText(printHeaderTitle);
            String printHeaderSubtitle = Database.get().getSettingDao().getPrintHeaderSubtitle();
            subtitleLabel.setText(printHeaderSubtitle);
            mSubtitleTextField.setText(printHeaderSubtitle);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printHeaderSaveClicked() {
        try {
            Database.get().getSettingDao().savePrintHeaderTitle(mTitleTextField.getText());
            Database.get().getSettingDao().savePrintHeaderSubtitle(mSubtitleTextField.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        disablePrintHeaderSaveAndCancel();
    }

    private void printHeaderCancelClicked() {
        loadPrintHeader();
        disablePrintHeaderSaveAndCancel();
    }

    private boolean loadHideDisqualified() {
        boolean hideDisqualified = false;
        try {
            hideDisqualified = Database.get().getSettingDao().getHideDisqualified();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hideDisqualified;
    }

    private void hideCheckboxChanged(ChangeEvent e) {
        try {
            Database.get().getSettingDao().saveHideDisqualified(((JCheckBox) e.getSource()).isSelected());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void listItemSelected() {
        if (!ageGroupsSaved) {
            int answer = JOptionPane.showOptionDialog(this, "A korosztályok nincsenek elmentve!", "Figyelem!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Mentés", "Elvetés", "Mégse"}, null);
            switch (answer) {
                case 0:
                    ageGroupSaveClicked();
                    break;
                case 1:
                    ageGroupCancelClicked();
                    break;
                default:
                    mSettingsMenu.removeListSelectionListener(mListItemSelectedListener);
                    mSettingsMenu.setSelectedIndex(1);
                    mSettingsMenu.addListSelectionListener(mListItemSelectedListener);
                    return;
            }
        }

        if (!mPrintHeaderSaved) {
            int answer = JOptionPane.showOptionDialog(this, "A nyomtatási fejléc nincs elmentve!", "Figyelem!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Mentés", "Elvetés", "Mégse"}, null);
            switch (answer) {
                case 0:
                    printHeaderSaveClicked();
                    break;
                case 1:
                    printHeaderCancelClicked();
                    break;
                default:
                    mSettingsMenu.removeListSelectionListener(mListItemSelectedListener);
                    mSettingsMenu.setSelectedIndex(2);
                    mSettingsMenu.addListSelectionListener(mListItemSelectedListener);
                    return;
            }
        }

        for (Component component : mSettingsContent.getComponents()) {
            component.setVisible(false);
        }
        int index = mSettingsMenu.getSelectedIndex();
        switch (index) {
            case 0:
                mBasicSettings.setVisible(true);
                break;
            case 1:
                mAgeGroups.setVisible(true);
                break;
            case 2:
                mPrintHeader.setVisible(true);
                break;
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mContentPane = new JPanel();
        mContentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        mContentPane.setMaximumSize(new Dimension(540, 480));
        mContentPane.setMinimumSize(new Dimension(540, 480));
        mContentPane.setPreferredSize(new Dimension(540, 480));
        mContentPane.setRequestFocusEnabled(true);
        mSettingsContent = new JPanel();
        mSettingsContent.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        mSettingsContent.setVisible(true);
        mContentPane.add(mSettingsContent, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(50, -1), null, null, 0, false));
        mBasicSettings = new JPanel();
        mBasicSettings.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mBasicSettings.setVisible(true);
        mSettingsContent.add(mBasicSettings, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        hideDisqualifiedCheckBox = new JCheckBox();
        hideDisqualifiedCheckBox.setText("Csak a helyezettek mutatása");
        hideDisqualifiedCheckBox.setToolTipText("Elrejti a kizárt versenyzőket (pl nem ért be) az eredménylistából.");
        mBasicSettings.add(hideDisqualifiedCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        mBasicSettings.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        mBasicSettings.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mAgeGroups = new JPanel();
        mAgeGroups.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        mAgeGroups.setInheritsPopupMenu(false);
        mAgeGroups.setVisible(true);
        mSettingsContent.add(mAgeGroups, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        mAgeGroups.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        mAgeGroupCancelButton = new JButton();
        mAgeGroupCancelButton.setEnabled(false);
        mAgeGroupCancelButton.setText("Mégse");
        mAgeGroups.add(mAgeGroupCancelButton, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mAgeGroupSaveButton = new JButton();
        mAgeGroupSaveButton.setEnabled(false);
        mAgeGroupSaveButton.setText("Mentés");
        mAgeGroups.add(mAgeGroupSaveButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
        mAgeGroups.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mAgeGroupContainer = new JPanel();
        mAgeGroupContainer.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(mAgeGroupContainer, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 7, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Név");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Min. szül. év");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Max. szül. év");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Csapat létszám");
        panel1.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(0, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        mAgeGroups.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mPrintHeader = new JPanel();
        mPrintHeader.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        mSettingsContent.add(mPrintHeader, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mSubtitleTextField = new JTextField();
        mSubtitleTextField.setToolTipText("Alcím");
        mPrintHeader.add(mSubtitleTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-1));
        panel2.setName("Előnézet");
        panel2.setToolTipText("Előnézet");
        mPrintHeader.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Előnézet"));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(10, 10, 10, 0), -1, -1));
        panel3.setOpaque(false);
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        titleLabel.setText("");
        panel3.add(titleLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        subtitleLabel = new JLabel();
        subtitleLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        subtitleLabel.setText("");
        panel3.add(subtitleLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mTitleTextField = new JTextField();
        mTitleTextField.setToolTipText("Főcím");
        mPrintHeader.add(mTitleTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mPrintHeaderSaveButton = new JButton();
        mPrintHeaderSaveButton.setEnabled(false);
        mPrintHeaderSaveButton.setText("Mentés");
        mPrintHeader.add(mPrintHeaderSaveButton, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mPrintHeaderCancelButton = new JButton();
        mPrintHeaderCancelButton.setEnabled(false);
        mPrintHeaderCancelButton.setText("Mégse");
        mPrintHeader.add(mPrintHeaderCancelButton, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        mPrintHeader.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer9 = new com.intellij.uiDesigner.core.Spacer();
        mPrintHeader.add(spacer9, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mSettingsMenu = new JList();
        mSettingsMenu.setFont(new Font(mSettingsMenu.getFont().getName(), mSettingsMenu.getFont().getStyle(), 14));
        mSettingsMenu.setValueIsAdjusting(false);
        mContentPane.add(mSettingsMenu, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mContentPane;
    }

    private class AgeGroupNameEditListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_ENTER || e.getKeyCode() != KeyEvent.VK_TAB) {
                enableAgeGroupSaveAndCancel();
            }
        }
    }

    private class PrintHeaderEditListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_ENTER || e.getKeyCode() != KeyEvent.VK_TAB) {
                enablePrintHeaderSaveAndCancel();
                titleLabel.setText(mTitleTextField.getText());
                subtitleLabel.setText(mSubtitleTextField.getText());
            }
        }
    }
}
