package hu.danielb.raceadmin.ui;

import com.j256.ormlite.dao.GenericRawResults;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.*;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.ui.listeners.FinishingListener;
import hu.danielb.raceadmin.util.Constants;

//import java.util.Timer;

class FinishingDialog extends BaseDialog {

    private HashMap<AgeGroup, Positions> positionsForAgeGroup;
    private HashMap<Integer, Contestant> contestants;
    private List<FinishingListener> listeners;

    //    TEST
//    private Contestant contestantTest;
//    private Timer timer;

    private javax.swing.JLabel labelContestantName;
    private javax.swing.JLabel labelSchoolName;
    private javax.swing.JLabel labelMessage;
    private javax.swing.JLabel labelPositionValue;
    private javax.swing.JLabel labelAgeGroupName;
    private javax.swing.JTextField textNumber;
    private AgeGroup mLastAgeGroup;
    private String mLastSex;
    private boolean mFirstFinisher = true;
    private boolean mAgeGroupCheck = true;
    private boolean mSexCheck = true;
    private JCheckBox mCheckBoxAgeGroupCheck;
    private JCheckBox mCheckBoxSexCheck;

    FinishingDialog(Frame owner) {
        super(owner);
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {
        initComponents();

        positionsForAgeGroup = new HashMap<>();
        contestants = new HashMap<>();
        listeners = new ArrayList<>();
        try {
            Database.get().getAgeGroupDao().queryForAll().stream().sorted().collect(Collectors.toList())
                    .forEach(ageGroup -> positionsForAgeGroup.put(ageGroup, new Positions()));

            positionsForAgeGroup.entrySet().forEach(entry -> {
                try {
                    GenericRawResults<String[]> maximums = Database.get().getContestantDao().queryBuilder().selectRaw(Contestant.COLUMN_SEX + ", MAX(" + Contestant.COLUMN_POSITION + ")").groupBy(Contestant.COLUMN_SEX)
                            .where().eq(Contestant.COLUMN_AGE_GROUP_ID, entry.getKey().getId()).queryRaw();
                    Positions positions = new Positions();
                    maximums.forEach(max ->
                            positions.positions.put(max[0], Integer.parseInt(max[1]) + 1)
                    );
                    entry.setValue(positions);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            List<Contestant> query = Database.get().getContestantDao().queryBuilder().where().eq(Contestant.COLUMN_POSITION, 0)
                    .and().isNotNull(Contestant.COLUMN_AGE_GROUP_ID).query();
            query.forEach(contestant -> contestants.put(contestant.getNumber(), contestant));

        } catch (SQLException ex) {
            Logger.getLogger(FinishingDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {

        textNumber = new javax.swing.JTextField();
        labelContestantName = new javax.swing.JLabel();
        labelSchoolName = new javax.swing.JLabel();
        labelMessage = new javax.swing.JLabel();
        labelAgeGroupName = new javax.swing.JLabel();
        labelPositionValue = new javax.swing.JLabel();
        mCheckBoxAgeGroupCheck = new JCheckBox("Korosztály váltás figyelmeztetés");
        mCheckBoxSexCheck = new JCheckBox("Fiú/Lány váltás figyelmeztetés");
        JButton buttonEnd = new JButton();
        JLabel labelName = new JLabel();
        JLabel labelSchool = new JLabel();
        JLabel labelPosition = new JLabel();
        JLabel labelAgeGroup = new JLabel();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        buttonEnd.setText("Vége");
        buttonEnd.addActionListener(e -> buttonEndActionPerformed());

        textNumber.setFont(new java.awt.Font("SansSerif", Font.BOLD, 40));
        textNumber.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        textNumber.setMaximumSize(new java.awt.Dimension(360, 58));
        textNumber.setMinimumSize(new java.awt.Dimension(360, 58));
        textNumber.setPreferredSize(new java.awt.Dimension(360, 58));
        textNumber.setHorizontalAlignment(JTextField.CENTER);
        textNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textNumberKeyReleased(evt);
            }
        });

        labelName.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelName.setText("Név:");

        labelSchool.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelSchool.setText("Iskola:");

        labelContestantName.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelContestantName.setText("");
        labelContestantName.setMaximumSize(new java.awt.Dimension(263, 24));
        labelContestantName.setMinimumSize(new java.awt.Dimension(263, 24));
        labelContestantName.setPreferredSize(new java.awt.Dimension(263, 24));

        labelSchoolName.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelSchoolName.setText("");
        labelSchoolName.setMaximumSize(new java.awt.Dimension(263, 24));
        labelSchoolName.setMinimumSize(new java.awt.Dimension(263, 24));
        labelSchoolName.setPreferredSize(new java.awt.Dimension(263, 24));

        labelMessage.setText("");

        labelPosition.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelPosition.setText("Helyezés:");

        labelPositionValue.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelPositionValue.setMaximumSize(new java.awt.Dimension(263, 24));
        labelPositionValue.setMinimumSize(new java.awt.Dimension(263, 24));
        labelPositionValue.setPreferredSize(new java.awt.Dimension(263, 24));

        labelAgeGroup.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelAgeGroup.setText("Kcs:");

        labelAgeGroupName.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        labelAgeGroupName.setMaximumSize(new java.awt.Dimension(263, 24));
        labelAgeGroupName.setMinimumSize(new java.awt.Dimension(263, 24));
        labelAgeGroupName.setPreferredSize(new java.awt.Dimension(263, 24));

        mCheckBoxAgeGroupCheck.setSelected(mAgeGroupCheck);
        mCheckBoxAgeGroupCheck.addChangeListener(e -> mAgeGroupCheck = mCheckBoxAgeGroupCheck.isSelected());

        mCheckBoxSexCheck.setSelected(mSexCheck);
        mCheckBoxSexCheck.addChangeListener(e -> mSexCheck = mCheckBoxSexCheck.isSelected());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(labelAgeGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(labelAgeGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(labelPosition)
                                                        .addComponent(labelSchool, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(labelName, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(labelSchoolName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(labelContestantName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(labelPositionValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addComponent(textNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(labelMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(mCheckBoxSexCheck)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mCheckBoxAgeGroupCheck)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(labelContestantName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelSchool, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelSchoolName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelPosition)
                                        .addComponent(labelPositionValue, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelAgeGroup)
                                        .addComponent(labelAgeGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(buttonEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelMessage))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(mCheckBoxSexCheck)
                                        .addComponent(mCheckBoxAgeGroupCheck))
                                .addContainerGap())
        );

        pack();
    }

    private void textNumberKeyReleased(java.awt.event.KeyEvent evt) {

        String numberToCheck = textNumber.getText().trim();
        Contestant contestant = null;
        boolean contains = false;

        for (Map.Entry<Integer, Contestant> entry : contestants.entrySet()) {
            String number = String.valueOf(entry.getKey());
            if (number.contains(numberToCheck)) {
                contains = true;
            }
            if (number.equals(numberToCheck)) {
                contestant = entry.getValue();
                break;
            }
        }

        if (contestant != null) {
            labelContestantName.setText(contestant.getName());
            labelSchoolName.setText(contestant.getSchool().getNameWithSettlement());
            labelPositionValue.setText(String.valueOf(positionsForAgeGroup.get(contestant.getAgeGroup()).positions.get(contestant.getSex())));

            if (contestant.getSex().equals(Constants.BOY)) {
                labelAgeGroupName.setText(contestant.getAgeGroup().getName() + " Fiú");
            } else {
                labelAgeGroupName.setText(contestant.getAgeGroup().getName() + " Lány");
            }

        } else {
            labelContestantName.setText("");
            labelSchoolName.setText("");
            labelPositionValue.setText("");
            labelAgeGroupName.setText("");
        }
        if (contains) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if (contestant != null) {
                    if (contestant.getAgeGroup().equals(mLastAgeGroup) || mFirstFinisher || !mAgeGroupCheck) {
                        if (contestant.getSex().equals(mLastSex) || mFirstFinisher || !mSexCheck) {
                            saveContestantPosition(contestant);
                        } else {
                            if (0 == JOptionPane.showOptionDialog(this, "Ez a versenyző másik nembe tartozik mint az előző!\n Biztos menti az eredményét?", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Igen", "Mégsem"}, null)) {
                                saveContestantPosition(contestant);
                            }
                        }
                    } else {
                        if (0 == JOptionPane.showOptionDialog(this, "Ez a versenyző másik korosztályba tartozik mint az előző!\n Biztos menti az eredményét?", "Figyelem!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Igen", "Mégsem"}, null)) {
                            saveContestantPosition(contestant);
                        }
                    }
                } else {
                    warning();
                }
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        } else if (evt.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
            warning();
        }

    }

    private void saveContestantPosition(Contestant contestant) {
        try {
            int nextPos = positionsForAgeGroup.get(contestant.getAgeGroup()).positions.get(contestant.getSex());
            contestant.setPosition(nextPos);
            Database.get().getContestantDao().update(contestant);
            positionsForAgeGroup.get(contestant.getAgeGroup()).positions.put(contestant.getSex(), nextPos + 1);
        } catch (SQLException ex) {
            Logger.getLogger(FinishingDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (FinishingListener finishingListener : listeners) {
            finishingListener.racerFinished(contestant);
        }
        contestants.remove(contestant.getNumber());

        textNumber.setText("");

        mLastAgeGroup = contestant.getAgeGroup();
        mLastSex = contestant.getSex();
        mFirstFinisher = false;
    }

    private void buttonEndActionPerformed() {
        this.dispose();
//        TEST
//        mSexCheck = false;
//        mAgeGroupCheck = false;
//        mCheckBoxSexCheck.setSelected(mSexCheck);
//        mCheckBoxAgeGroupCheck.setSelected(mAgeGroupCheck);
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//            return;
//        }
//        timer = new Timer(true);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (contestants.isEmpty() || !labelMessage.getText().isEmpty()) {
//                    buttonEndActionPerformed();
//                    return;
//                }
//                int i = 0;
//                int r = new Random(System.nanoTime() * 2).nextInt(contestants.size());
//                for (Map.Entry<Integer, Contestant> entry : contestants.entrySet()) {
//                    if (i == r) {
//                        break;
//                    }
//                    i++;
//                    contestantTest = entry.getValue();
//                }
//                final Timer t = new Timer(true);
//                t.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        SwingUtilities.invokeLater(() -> {
//                            int h = textNumber.getText().length();
//                            if (contestantTest == null) {
//                                t.cancel();
//                                return;
//                            }
//                            String number = String.valueOf(contestantTest.getNumber());
//                            if (h == number.length()) {
//                                try {
//                                    textNumberKeyReleased(new KeyEvent(textNumber, 10, 9999, 0, KeyEvent.VK_ENTER, 'e'));
//                                } catch (StringIndexOutOfBoundsException e) {
//                                    Logger.getLogger(FinishingDialog.class.getName()).log(Level.SEVERE, null, e);
//                                    textNumber.setText("");
//                                }
//                                t.cancel();
//
//                                return;
//                            }
//                            textNumber.setText(number.substring(0, h + 1));
//                            textNumberKeyReleased(new KeyEvent(textNumber, 10, 9999, 0, KeyEvent.VK_9, 'e'));
//                        });
//                    }
//                }, 0, 75);
//            }
//        }, 0, 1000);
    }

    private void warning() {
        new Thread(() -> {
            labelMessage.setText("Nincs ilyen rajtszám!");
            Toolkit.getDefaultToolkit().beep();
            int i = 0;
            long mTime = System.currentTimeMillis();
            while (i < 300) {
                long curentTime = System.currentTimeMillis();
                if (curentTime > mTime + 25) {
                    textNumber.setBackground(new Color(255, Math.min(i, 255), Math.min(i, 255)));
                    i += 25;
                    mTime = curentTime;
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(FinishingDialog.class.getName()).log(Level.SEVERE, null, ex);
                labelMessage.setText("");
            }
            labelMessage.setText("");
        }).start();
    }

    void addFinishingListener(FinishingListener l) {
        listeners.add(l);
    }


    private static class Positions {
        Map<String, Integer> positions = new HashMap<>();

        Positions() {
            positions.put(Constants.BOY, 1);
            positions.put(Constants.GIRL, 1);
        }
    }
}
