package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.util.Constants;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

class AddContestantDialog extends BaseDialog {

    private Contestant contestant = null;

    private javax.swing.ButtonGroup buttonGroupSex;
    private JComboBox<AgeGroup> comboAgeGroup;
    private javax.swing.JComboBox<School> comboSchool;
    private javax.swing.JLabel labelPosition;
    private javax.swing.JRadioButton radioBoy;
    private javax.swing.JRadioButton radioGirl;
    private javax.swing.JSpinner spinnerNumber;
    private javax.swing.JSpinner spinnerAge;
    private javax.swing.JSpinner spinnerPosition;
    private javax.swing.JTextField textName;

    AddContestantDialog(Frame owner) {
        super(owner);
        init();
        setLocationRelativeTo(owner);
    }

    AddContestantDialog(Dialog owner, Contestant contestant) {
        super(owner);
        this.contestant = contestant;
        init();
        setLocationRelativeTo(owner);
    }

    private void init() {
        initComponents();
        if (contestant != null) {
            labelPosition.setVisible(true);
            labelPosition.setEnabled(true);
            spinnerPosition.setVisible(true);
            spinnerPosition.setEnabled(true);

            spinnerPosition.setValue(contestant.getPosition());
            textName.setText(contestant.getName());
            comboSchool.setSelectedItem(contestant.getSchool());
            spinnerAge.setValue(contestant.getAge());
            comboAgeGroup.setSelectedItem(contestant.getAgeGroup());
            spinnerNumber.setValue(contestant.getNumber());
            if (Constants.BOY.equals(contestant.getSex())) {
                radioBoy.setSelected(true);
            } else {
                radioGirl.setSelected(true);
            }
        } else {
            contestant = new Contestant();
        }
        spinnerAge.addChangeListener(e -> spinnerAgeStateChanged());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                textName.requestFocus();
            }
        });

        ((JSpinner.DefaultEditor) spinnerNumber.getEditor()).getTextField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (e.getSource() instanceof JTextComponent) {
                    final JTextComponent textComponent = ((JTextComponent) e.getSource());
                    SwingUtilities.invokeLater(textComponent::selectAll);
                }
            }
        });

    }

    private void initComponents() {

        buttonGroupSex = new javax.swing.ButtonGroup();
        textName = new javax.swing.JTextField();
        comboSchool = new javax.swing.JComboBox<>();
        spinnerAge = new javax.swing.JSpinner();
        spinnerNumber = new javax.swing.JSpinner();
        radioBoy = new javax.swing.JRadioButton();
        radioGirl = new javax.swing.JRadioButton();
        labelPosition = new javax.swing.JLabel();
        spinnerPosition = new javax.swing.JSpinner();
        comboAgeGroup = new javax.swing.JComboBox<>();
        JButton buttonSave = new javax.swing.JButton();
        JButton buttonDelete = new JButton();
        JButton buttonEnd = new JButton();
        JButton buttonNew = new JButton();
        JLabel labelName = new JLabel();
        JLabel labelSchool = new JLabel();
        JLabel labelAge = new JLabel();
        JLabel labelNumber = new JLabel();
        JLabel labelSex = new JLabel();
        JLabel labelAgeGroup = new JLabel();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(300, 225));
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        AutoCompleteDecorator.decorate(comboSchool, new ObjectToStringConverter() {
            @Override
            public String getPreferredStringForItem(Object item) {
                if (item instanceof School) {
                    return ((School) item).getNameWithSettlement();
                }
                return null;
            }
        });
        comboSchool.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof School) {
                    value = ((School) value).getNameWithSettlement();
                }
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                return this;
            }
        });
        refreshSchools();

        int min = 9999;
        int max = 0;
        try {
            String[] minMax = Database.get().getAgeGroupDao().queryBuilder().selectRaw("MIN(" + AgeGroup.COLUMN_MINIMUM + ")", "MAX(" + AgeGroup.COLUMN_MAXIMUM + ")").queryRawFirst();
            min = Integer.parseInt(minMax[0]) - 5;
            max = Integer.parseInt(minMax[1]) + 5;
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        SpinnerNumberModel spinnerModelNumber = new SpinnerNumberModel(min + 5, min, max, 1);
        spinnerAge.setModel(spinnerModelNumber);

        spinnerModelNumber = new SpinnerNumberModel(1, 1, 99999, 1);
        spinnerNumber.setModel(spinnerModelNumber);
        spinnerNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                spinnerNumberFocusGained(evt);
            }
        });

        buttonGroupSex.add(radioBoy);
        radioBoy.setText("Fiu");
        radioBoy.setActionCommand(Constants.BOY);
        buttonGroupSex.add(radioBoy);

        buttonGroupSex.add(radioGirl);
        radioGirl.setText("Lány");
        radioGirl.setActionCommand(Constants.GIRL);
        buttonGroupSex.add(radioGirl);

        buttonSave.setText("Mentés");
        buttonSave.addActionListener(e -> buttonSaveActionPerformed());

        buttonDelete.setText("Törlés");
        buttonDelete.addActionListener(e -> buttonDeleteActionPerformed());
        if (contestant == null) {
            buttonDelete.setVisible(false);
        }

        buttonEnd.setText("Vége");
        buttonEnd.addActionListener(e -> buttonEndActionPerformed());

        buttonNew.setText("Új");
        buttonNew.setFocusable(false);
        buttonNew.addActionListener(e -> buttonNewActionPerformed());

        labelName.setText("Név:");

        labelSchool.setText("Iskola:");

        labelAge.setText("Születési év:");

        labelNumber.setText("Rajtszám:");

        labelSex.setText("Nem:");

        labelPosition.setText("Helyezés:");
        labelPosition.setEnabled(false);
        labelPosition.setVisible(false);

        spinnerModelNumber = new SpinnerNumberModel(0, 0, 9999, 1);
        spinnerPosition.setModel(spinnerModelNumber);
        spinnerPosition.setEnabled(false);
        spinnerPosition.setVisible(false);

        comboAgeGroup.setModel(new javax.swing.DefaultComboBoxModel<>(new AgeGroup[]{new AgeGroup(0, "", 0, 0, 0, 0)}));
        try {
            Database.get().getAgeGroupDao().queryForAll().forEach(comboAgeGroup::addItem);
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        labelAgeGroup.setText("Korosztály:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(buttonSave)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonDelete)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonEnd))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labelName)
                                                        .addComponent(labelSchool)
                                                        .addComponent(labelPosition)
                                                        .addComponent(labelAge, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(labelNumber)
                                                        .addComponent(labelSex)
                                                        .addComponent(labelAgeGroup))
                                                .addGap(10, 10, 10)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(spinnerAge)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(comboSchool, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(buttonNew))
                                                        .addComponent(textName, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(spinnerPosition, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(spinnerNumber)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(radioBoy)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(radioGirl)
                                                                .addGap(0, 148, Short.MAX_VALUE))
                                                        .addComponent(comboAgeGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelPosition)
                                        .addComponent(spinnerPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelName)
                                        .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelSchool, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboSchool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonNew))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(spinnerAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelAge))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(comboAgeGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelAgeGroup))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(7, 7, 7)
                                                .addComponent(labelNumber))
                                        .addComponent(spinnerNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(radioBoy)
                                        .addComponent(labelSex)
                                        .addComponent(radioGirl))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonEnd)
                                        .addComponent(buttonSave)
                                        .addComponent(buttonDelete))
                                .addContainerGap())
        );

        pack();
    }

    private void refreshSchools() {
        comboSchool.setModel(new DefaultComboBoxModel<>(new School[]{new School(0, "")}));
        try {
            Database.get().getSchoolDao().queryForAll().stream().sorted(Comparator.comparing(o -> o.getNameWithSettlement().toLowerCase()))
                    .forEach(comboSchool::addItem);
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buttonSaveActionPerformed() {
        Contestant contestantOld = new Contestant(contestant);

        contestant.setName((textName.getText()).trim());
        contestant.setSchool((School) comboSchool.getSelectedItem());
        contestant.setAge((Integer) spinnerAge.getValue());
        contestant.setAgeGroup((AgeGroup) comboAgeGroup.getSelectedItem());
        contestant.setNumber((Integer) spinnerNumber.getValue());
        contestant.setPosition((Integer) spinnerPosition.getValue());
        contestant.setSex("");
        if (buttonGroupSex.getSelection() != null) {
            contestant.setSex(buttonGroupSex.getSelection().getActionCommand());
        }
        try {
            if (contestant.getName().length() < 3) {
                throw new Exception("Adjon meg nevet!");
            }
            if (contestant.getSchool() == null) {
                throw new Exception("Válasszon iskolát!");
            }
            if (contestant.getNumber() < 1) {
                throw new Exception("Adjon meg rajtszámot!");
            }
            if (contestant.getAgeGroup() == null) {
                throw new Exception("Nem választott korcsoportot!");
            }

            Contestant numberConflict = Database.get().getContestantDao().queryBuilder().where()
                    .eq(Contestant.COLUMN_NUMBER, contestant.getNumber()).and()
                    .ne(Contestant.COLUMN_ID, contestant.getId())
                    .queryForFirst();
            if (numberConflict != null) {
                throw new Exception("Ez a rajtszám már ki van osztva!\nNév: " + numberConflict.getName());
            }

            if (contestant.getSex().isEmpty()) {
                throw new Exception("Nem választott nemet!");
            }

            if (contestant.getId() == 0) {
                Database.get().getContestantDao().create(contestant);
                contestant = new Contestant();
                textName.setText("");
                spinnerNumber.setValue(((int) spinnerNumber.getValue()) + 1);
            } else {
                if (contestant.getAge() != contestantOld.getAge() || !contestant.getSex().equals(contestantOld.getSex()) || !contestant.getAgeGroup().equals(contestantOld.getAgeGroup())) {
                    if (0 == JOptionPane.showOptionDialog(this, "Ha megváltoztatja a versenyző korát, vagy nemét, az eredménye törlődik!", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Rendben", "Mégsem"}, null)) {
                        contestant.setPosition(0);
                    } else {
                        throw new Exception("Nem lettek mentve az adatok.");
                    }
                }

                if (contestant.getPosition() != contestantOld.getPosition()) {
                    if ((0 < contestant.getPosition() && contestant.getPosition() < contestantOld.getPosition()) || contestantOld.getPosition() == 0) {
                        moveForward(contestantOld, contestant.getPosition());
                    } else if ((0 < contestantOld.getPosition() && contestantOld.getPosition() < contestant.getPosition()) || contestant.getPosition() == 0) {
                        if (contestantOld.getAgeGroup() != null) {
                            moveBack(contestantOld, contestant.getPosition());
                        }
                    }
                }

                Database.get().getContestantDao().createOrUpdate(contestant);
                this.dispose();
            }
            textName.requestFocus();

        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            warn(ex.getMessage());
        }
    }

    private void moveBack(Contestant contestantOld, int newPosition) throws SQLException {
        newPosition = newPosition == 0 ? Integer.MAX_VALUE : newPosition;
        Database.get().getContestantDao().queryBuilder()
                .where()
                .eq(Contestant.COLUMN_SEX, contestantOld.getSex()).and()
                .eq(Contestant.COLUMN_AGE_GROUP_ID, contestantOld.getAgeGroup().getId()).and()
                .gt(Contestant.COLUMN_POSITION, contestantOld.getPosition()).and()
                .le(Contestant.COLUMN_POSITION, newPosition).and()
                .ne(Contestant.COLUMN_POSITION, 0)
                .query().forEach(contestant1 -> {
            contestant1.setPosition(contestant1.getPosition() - 1);
            try {
                Database.get().getContestantDao().update(contestant1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void moveForward(Contestant contestantOld, int newPosition) throws SQLException {
        int oldPosition = contestantOld.getPosition() == 0 ? Integer.MAX_VALUE : contestantOld.getPosition();
        Database.get().getContestantDao().queryBuilder()
                .where()
                .eq(Contestant.COLUMN_SEX, contestantOld.getSex()).and()
                .eq(Contestant.COLUMN_AGE_GROUP_ID, contestantOld.getAgeGroup().getId()).and()
                .ge(Contestant.COLUMN_POSITION, newPosition).and()
                .lt(Contestant.COLUMN_POSITION, oldPosition).and()
                .ne(Contestant.COLUMN_POSITION, 0)
                .query().forEach(contestant1 -> {
            contestant1.setPosition(contestant1.getPosition() + 1);
            try {
                Database.get().getContestantDao().update(contestant1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void buttonDeleteActionPerformed() {
        if (0 == JOptionPane.showOptionDialog(this, "Biztosan törli?", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Igen", "Mégsem"}, null)) {
            Contestant contestantOld = new Contestant(contestant);
            try {
                if (contestantOld.getAgeGroup() != null) {
                    moveBack(contestantOld, 0);
                }
                Database.get().getContestantDao().delete(contestant);
                this.dispose();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void buttonEndActionPerformed() {
        this.dispose();
    }

    private void buttonNewActionPerformed() {
        new AddSchoolDialog(this).addSaveListener(newSchool -> {
            refreshSchools();
            comboSchool.setSelectedItem(newSchool);
        }).setVisible(true);

    }

    @SuppressWarnings("UnusedParameters")
    private void spinnerNumberFocusGained(java.awt.event.FocusEvent evt) {
        ((JSpinner.DefaultEditor) spinnerNumber.getEditor()).getTextField().selectAll();
    }

    private void spinnerAgeStateChanged() {
        int age = (int) spinnerAge.getValue();
        for (int i = 0; i < comboAgeGroup.getItemCount(); i++) {
            AgeGroup ageGroup = (comboAgeGroup.getItemAt(i));
            if (ageGroup.includes(age)) {
                comboAgeGroup.setSelectedIndex(i);
            }
        }
    }

}
