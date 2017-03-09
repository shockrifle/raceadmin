package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Integer, JPanel> mAgeGroupViews = new HashMap<>();
    private List<AgeGroup> mAgeGroupList;
    private boolean ageGroupsSaved = true;
    private ListSelectionListener mListItemSelectedListener;

    SettingsDialog(Frame owner) {
        super(owner);
        setContentPane(mContentPane);
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        mListItemSelectedListener = this::listItemSelected;
        mSettingsMenu.addListSelectionListener(mListItemSelectedListener);
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Általános");
        model.addElement("Korosztályok");
        model.addElement("Iskolák");
        model.addElement("Nyomtatási Fejléc");
        mSettingsMenu.setModel(model);
        mSettingsMenu.setSelectedIndex(0);

        // basic
        hideDisqualifiedCheckBox.setSelected(loadShowDisqualified());
        hideDisqualifiedCheckBox.addChangeListener(this::hideCheckboxChanged);

        // age groups
        mAgeGroupCancelButton.addActionListener(e -> cancelClicked());
        mAgeGroupSaveButton.addActionListener(e -> ageGroupSaveClicked());
        mAgeGroupContainer.setLayout(new BoxLayout(mAgeGroupContainer, BoxLayout.Y_AXIS));
        loadAgeGroups();

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
        if (ageGroups == null || ageGroups.size() > 0 && ageGroups.get(ageGroups.size() - 1).getId() != 0) {
            JButton newAgeGroupButton = new JButton("Új korosztály");
            newAgeGroupButton.addActionListener(this::newAgeGroupButtonClicked);
            mAgeGroupContainer.add(newAgeGroupButton);
        }

        mAgeGroupContainer.revalidate();
        mAgeGroupContainer.repaint();
    }

    private void cancelClicked() {
        disableSaveAndCancel();
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
                    disableSaveAndCancel();
                    loadAgeGroups();
                }).start();
                dialog.setVisible(true);
            } else {
                String msg = "A beállított korhatár ütközik a következővel:\n";
                for (AgeGroup ageGroup1 : overlaps) {
                    msg += ageGroup1.getName() + "\n";
                }
                warn(msg);
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

    private void newAgeGroupButtonClicked(ActionEvent e) {
        enableSaveAndCancel();
        AgeGroup last = mAgeGroupList.get(mAgeGroupList.size() - 1);
        AgeGroup newAgeGroup = new AgeGroup(0, "Új korosztály", last.getMinimum() - 2, last.getMinimum() - 1);
        mAgeGroupList.add(newAgeGroup);
        loadAgeGroups(mAgeGroupList);
    }

    private void enableSaveAndCancel() {
        ageGroupsSaved = false;
        mAgeGroupSaveButton.setEnabled(true);
        mAgeGroupCancelButton.setEnabled(true);
    }

    private void disableSaveAndCancel() {
        ageGroupsSaved = true;
        mAgeGroupSaveButton.setEnabled(false);
        mAgeGroupCancelButton.setEnabled(false);
    }

    private void createAgeGroupView(AgeGroup ageGroup) {
        JPanel ageGroupRow = new JPanel();

        JTextField name = new JTextField(ageGroup.getName());
        name.setPreferredSize(new Dimension(120, 22));

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

        JButton delete = new JButton("Törlés");

        delete.addActionListener(e -> deleteAgeGroupClicked(ageGroup));

        ageGroupRow.add(name);
        ageGroupRow.add(minimum);
        ageGroupRow.add(maximum);
        ageGroupRow.add(delete);

        mAgeGroupViews.put(ageGroup.getId(), ageGroupRow);
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
                loadAgeGroups();
            }).start();
            dialog.setVisible(true);
        }
    }

    private void ageGroupMinimumChanged(ChangeEvent e, AgeGroup ageGroup) {
        enableSaveAndCancel();
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
        enableSaveAndCancel();
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

    private boolean loadShowDisqualified() {
        boolean showDisqualified = false;
        try {
            showDisqualified = Database.get().getSettingDao().getShowDisqualified();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showDisqualified;
    }

    private void hideCheckboxChanged(ChangeEvent e) {
        try {
            Database.get().getSettingDao().saveShowDisqualified(((JCheckBox) e.getSource()).isSelected());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void listItemSelected(ListSelectionEvent e) {
        if (!ageGroupsSaved) {
            int answer = JOptionPane.showOptionDialog(this, "A korosztályok nincsenek elmentve!", "Figyelem!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Mentés", "Elvetés", "Mégse"}, null);
            switch (answer) {
                case 0:
                    ageGroupSaveClicked();
                    break;
                case 1:
                    cancelClicked();
                    break;
                default:
                    mSettingsMenu.removeListSelectionListener(mListItemSelectedListener);
                    mSettingsMenu.setSelectedIndex(1);
                    mSettingsMenu.addListSelectionListener(mListItemSelectedListener);
                    return;
            }
        }
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

    private class AgeGroupNameEditListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_ENTER || e.getKeyCode() != KeyEvent.VK_TAB) {
                enableSaveAndCancel();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
