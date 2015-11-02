package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.config.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.util.Constants;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public AddContestantDialog(Frame owner) {
        super(owner);
        init();
        setLocationRelativeTo(owner);
    }

    public AddContestantDialog(Dialog owner, Contestant contestant) {
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
            spinnerNumber.setValue(contestant.getNumber());
            if (Constants.BOY.equals(contestant.getSex())) {
                radioBoy.setSelected(true);
            } else {
                radioGirl.setSelected(true);
            }
        }
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

        AutoCompleteDecorator.decorate(comboSchool);
        comboSchool.setModel(new javax.swing.DefaultComboBoxModel<>(new School[]{new School(0, "")}));
        try {
            ResultSet rs = Database.runSql("select * from " + School.TABLE + " order by " + School.COLUMN_NAME);
            while (rs.next()) {
                comboSchool.addItem(new School(rs.getInt(School.COLUMN_ID), rs.getString(School.COLUMN_NAME)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        int min = 9999;
        int max = 0;
        try {
            ResultSet rs = Database.runSql("select * from " + AgeGroup.TABLE);
            while (rs.next()) {
                if (rs.getInt(AgeGroup.COLUMN_MINIMUM) < min) min = rs.getInt(AgeGroup.COLUMN_MINIMUM);
                if (rs.getInt(AgeGroup.COLUMN_MAXIMUM) > max) max = rs.getInt(AgeGroup.COLUMN_MAXIMUM);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        SpinnerNumberModel spinnerModelNumber = new SpinnerNumberModel(min, min, max, 1);
        spinnerAge.setModel(spinnerModelNumber);
        spinnerAge.addChangeListener(AddContestantDialog.this::jSpinner2StateChanged);

        spinnerModelNumber = new SpinnerNumberModel(1, 1, 9999, 1);
        spinnerNumber.setModel(spinnerModelNumber);
        spinnerNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSpinner1FocusGained(evt);
            }
        });

        radioBoy.addKeyListener(new KeyListener() {
                                    @Override
                                    public void keyPressed(KeyEvent e) {
                                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                                            radioGirl.requestFocus();
                                        }
                                    }

                                    @Override
                                    public void keyReleased(KeyEvent e) {
                                    }

                                    @Override
                                    public void keyTyped(KeyEvent e) {
                                    }

                                }
        );
        buttonGroupSex.add(radioBoy);
        radioBoy.setText("Fiu");
        radioBoy.setActionCommand(Constants.BOY);
        buttonGroupSex.add(radioBoy);

        radioGirl.addKeyListener(new KeyListener() {
                                     @Override
                                     public void keyPressed(KeyEvent e) {
                                         if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                                             buttonSave.requestFocus();
                                         }
                                     }

                                     @Override
                                     public void keyReleased(KeyEvent e) {
                                     }

                                     @Override
                                     public void keyTyped(KeyEvent e) {
                                     }

                                 }
        );
        buttonGroupSex.add(radioGirl);
        radioGirl.setText("Lány");
        radioGirl.setActionCommand(Constants.GIRL);
        buttonGroupSex.add(radioGirl);

        buttonSave.setText("Mentés");
        buttonSave.addActionListener(AddContestantDialog.this::buttonSaveActionPerformed);

        buttonEnd.setText("Vége");
        buttonEnd.addActionListener(AddContestantDialog.this::buttonEndActionPerformed);

        buttonNew.setText("Új");
        buttonNew.setFocusable(false);
        buttonNew.addActionListener(AddContestantDialog.this::buttonNewActionPerformed);

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

        comboAgeGroup.setModel(new javax.swing.DefaultComboBoxModel<>(new AgeGroup[]{new AgeGroup(0, "", 0, 0)}));
        try {
            ResultSet rs = Database.runSql("select * from " + AgeGroup.TABLE + " order by " + AgeGroup.COLUMN_NAME);
            while (rs.next()) {
                comboAgeGroup.addItem(new AgeGroup(rs.getInt(AgeGroup.COLUMN_ID), rs.getString(AgeGroup.COLUMN_NAME), rs.getInt(AgeGroup.COLUMN_MINIMUM), rs.getInt(AgeGroup.COLUMN_MAXIMUM)));
            }
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
                                        .addComponent(buttonSave))
                                .addContainerGap())
        );

        pack();
    }

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        String name = (textName.getText()).trim();
        int school = ((School) comboSchool.getSelectedItem()).getId();
        int age = ((Integer) spinnerAge.getValue());
        int ageGroupId = ((AgeGroup) comboAgeGroup.getSelectedItem()).getId();
        int number = ((Integer) spinnerNumber.getValue());
        String sex = "";
        int position = (Integer) spinnerPosition.getValue();
        if (buttonGroupSex.getSelection() != null) {
            sex = buttonGroupSex.getSelection().getActionCommand();
        }
        try {
            if (name.length() < 3) {
                throw new Exception("Adjon meg nevet!");
            }
            if (school < 1) {
                throw new Exception("Válasszon iskolát!");
            }
            if (number < 1) {
                throw new Exception("Adjon meg rajtszámot!");
            }
            ResultSet rs = Database.runSql("select * from " + Contestant.TABLE + " where " +
                    Contestant.COLUMN_NUMBER + " = ? and " +
                    Contestant.COLUMN_ID + " != ?", Database.QUERRY, String.valueOf(number), contestant != null ? String.valueOf(contestant.getId()) : "-1");
            if (rs.next()) {
                throw new Exception("Már létezik versenyző ezzel a rajtszámmal!\nNév: " + rs.getString(Contestant.COLUMN_NAME));
            }

            if (sex.isEmpty()) {
                throw new Exception("Nem választott nemet!");
            }
            if (contestant == null) {
                Database.runSql("insert into " + Contestant.TABLE + " (" +
                                Contestant.COLUMN_POSITION + "," +
                                Contestant.COLUMN_NAME + "," +
                                Contestant.COLUMN_SEX + "," +
                                Contestant.COLUMN_NUMBER + "," +
                                Contestant.COLUMN_AGE_GROUP_ID + "," +
                                Contestant.COLUMN_SCHOOL_ID + "," +
                                Contestant.COLUMN_AGE + ") "
                                + "values(?,?,?,?,?,?,?)",
                        Database.UPDATE, String.valueOf(position), name, sex, String.valueOf(number), String.valueOf(ageGroupId), String.valueOf(school), String.valueOf(age));

                textName.setText("");
                spinnerNumber.setValue(((int) spinnerNumber.getValue()) + 1);
            } else {

                if (age != contestant.getAge() || !sex.equals(contestant.getSex()) || ageGroupId != contestant.getAgeGroup().getId()) {
                    if (0 == JOptionPane.showOptionDialog(this, "Ha megváltoztatja a versenyző korát, vagy nemét, az eredménye törlődik!", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Rendben", "Mégsem"}, null)) {
                        position = 0;
                    } else {
                        throw new Exception("Nem lettek mentve az adatok.");
                    }
                }

                if (position != contestant.getPosition()) {
                    if ((0 < position && position < contestant.getPosition()) || contestant.getPosition() == 0) {
                        Database.runSql("update " + Contestant.TABLE + " set " +
                                        Contestant.COLUMN_POSITION + " = " + Contestant.COLUMN_POSITION + "+1 where " +
                                        Contestant.COLUMN_POSITION + " >= ? and " +
                                        Contestant.COLUMN_POSITION + " < ? and " +
                                        Contestant.COLUMN_POSITION + " != 0 and " +
                                        Contestant.COLUMN_SEX + " = ? and " +
                                        Contestant.COLUMN_AGE_GROUP_ID + " = ?",
                                Database.UPDATE, String.valueOf(position), String.valueOf(contestant.getPosition() != 0 ? contestant.getPosition() : 9999), contestant.getSex(), String.valueOf(contestant.getAgeGroup().getId()));
                    } else if ((0 < contestant.getPosition() && contestant.getPosition() < position) || position == 0) {
                        Database.runSql("update " + Contestant.TABLE + " set " +
                                        Contestant.COLUMN_POSITION + " = " + Contestant.COLUMN_POSITION + "-1 where " +
                                        Contestant.COLUMN_POSITION + " > ? and " +
                                        Contestant.COLUMN_POSITION + " <= ? and " +
                                        Contestant.COLUMN_POSITION + " != 0 and " +
                                        Contestant.COLUMN_SEX + " = ? and " +
                                        Contestant.COLUMN_AGE_GROUP_ID + " = ?",
                                Database.UPDATE, String.valueOf(contestant.getPosition()), String.valueOf(position != 0 ? position : 9999), contestant.getSex(), String.valueOf(contestant.getAgeGroup().getId()));
                    }
                }

                Database.runSql("update " + Contestant.TABLE + " set "
                                + Contestant.COLUMN_POSITION + " = ?, "
                                + Contestant.COLUMN_NAME + " = ?, "
                                + Contestant.COLUMN_SEX + " = ?, "
                                + Contestant.COLUMN_NUMBER + " = ?, "
                                + Contestant.COLUMN_AGE_GROUP_ID + " = ?,"
                                + Contestant.COLUMN_SCHOOL_ID + " = ?,"
                                + Contestant.COLUMN_AGE + " = ? "
                                + "where " + Contestant.COLUMN_ID + " = ?",
                        Database.UPDATE,
                        String.valueOf(position),
                        name,
                        sex,
                        String.valueOf(number),
                        String.valueOf(ageGroupId),
                        String.valueOf(school),
                        String.valueOf(age),
                        String.valueOf(contestant.getId()));

                this.dispose();
            }
            textName.requestFocus();

        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            warn(ex.getMessage());
        }
    }

    private void buttonEndActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void buttonNewActionPerformed(java.awt.event.ActionEvent evt) {
        new AddSchoolDialog(this).setVisible(true);
        comboSchool.setModel(new DefaultComboBoxModel<>(new School[]{new School(0, "")}));
        try {
            ResultSet rs = Database.runSql("select * from " + School.TABLE + " order by " + School.COLUMN_NAME);
            while (rs.next()) {
                comboSchool.addItem(new School(rs.getInt(School.COLUMN_ID), rs.getString(School.COLUMN_NAME)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void jSpinner1FocusGained(java.awt.event.FocusEvent evt) {
        ((JSpinner.DefaultEditor) spinnerNumber.getEditor()).getTextField().selectAll();
    }

    private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {
        int age = (int) spinnerAge.getValue();
        for (int i = 0; i < comboAgeGroup.getItemCount(); i++) {
            AgeGroup k = (comboAgeGroup.getItemAt(i));
            if (k.getMinimum() <= age && age <= k.getMaximum()) {
                comboAgeGroup.setSelectedIndex(i);
            }
        }
    }

}
