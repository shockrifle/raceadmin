package hu.danielb.raceadmin.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.j256.ormlite.stmt.Where;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.database.dao.CoachDao;
import hu.danielb.raceadmin.entity.Coach;
import hu.danielb.raceadmin.entity.School;

public class AddSchoolDialog extends BaseDialog {
    private JPanel contentPane;
    private JTextArea mTextAreaFullName;
    private JTextField mTextFieldShortName;
    private JTextField mTextFieldSettlement;
    private JButton buttonSave;
    private JButton buttonCancel;
    private JComboBox<Coach> mCoachComboBox;
    private JButton mNewCoachButton;
    private JLabel mCoachLabel;
    private List<SaveListener> listeners = new ArrayList<>();
    private School mSchool;

    AddSchoolDialog(Dialog owner) {
        super(owner);
        initComponents();
        setLocationRelativeTo(owner);
    }

    AddSchoolDialog(Dialog owner, School school) {
        this(owner);
        mSchool = school;
        initValues();
    }

    private void initValues() {
        mTextAreaFullName.setText(mSchool.getName());
        mTextFieldShortName.setText(mSchool.getShortName());
        mTextFieldSettlement.setText(mSchool.getSettlement());
        refreshCoaches();
        Coach coach = null;
        try {
            coach = Database.get().getCoachDao().queryForId(mSchool.getCoachId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCoachComboBox.setSelectedItem(coach);
        mCoachComboBox.setVisible(true);
        mCoachLabel.setVisible(true);
        mNewCoachButton.setVisible(true);
    }

    private void initCoaches() {
        mCoachComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Coach) {
                    value = ((Coach) value).getName();
                }
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                return this;
            }
        });
    }

    private void refreshCoaches() {
        mCoachComboBox.setModel(new DefaultComboBoxModel<>(new Coach[]{new Coach()}));
        try {
            CoachDao coachDao = Database.get().getCoachDao();
            List<Coach> coaches;
            if (mSchool != null && mSchool.getId() > 0) {
                coaches = coachDao.getBySchool(mSchool.getId());
            } else {
                coaches = coachDao.queryForAll();
            }
            coaches.stream().sorted(Comparator.comparing(o -> o.getName().toLowerCase()))
                    .forEach(mCoachComboBox::addItem);
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buttonCancelActionPerformed() {
        this.dispose();
    }

    private void buttonSaveActionPerformed() {
        String fullName = mTextAreaFullName.getText();
        if (fullName.length() < 3) {
            warn("Nem adott meg nevet!");
            return;
        }
        String[] names = fullName.split(" ");
        ArrayList<String> checked = new ArrayList<>();
        try {
            Where<School, Integer> where = Database.get().getSchoolDao().queryBuilder().where();

            for (int i = 0; i < names.length; i++) {
                String tempName = names[i];
                tempName = tempName.toLowerCase()
                        .replaceAll("\\.", "")
                        .replaceAll("isk", "")
                        .replaceAll("ált", "")
                        .replaceAll("iskola", "")
                        .replaceAll("általános", "");
                if (tempName.length() > 2) {
                    if (i > 0) {
                        where.or();
                    }
                    where.like(School.COLUMN_NAME, tempName);
                }
            }
            where.query().forEach(school1 -> checked.add(school1.getName()));

            if (checked.isEmpty()) {
                saveSchool();
            } else {
                StringBuilder buf = new StringBuilder("Hasonló névvel már léteznek a következő iskolák: \n");
                for (String aChecked : checked) {
                    buf.append(aChecked).append("\n");
                }
                buf.append("Ezek valamelyikére gondolt?");
                switch (JOptionPane.showOptionDialog(this, buf.toString(), "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Igen", "Mentés", "Javít"}, null)) {
                    case 0:
                        warn("Kerese meg a listában!");
                        dispose();
                        break;
                    case 1:
                        saveSchool();
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buttonNewCoachActionPerformed() {
        new AddCoachDialog(this, mSchool).addSaveListener(newCoach -> {
            refreshCoaches();
            if (mSchool.getCoachId() > 0) {
                mCoachComboBox.setSelectedItem(new Coach(mSchool.getCoachId()));
            } else {
                mCoachComboBox.setSelectedItem(newCoach);
            }
        }).setVisible(true);
    }

    private void initComponents() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        buttonSave.addActionListener(e -> buttonSaveActionPerformed());
        buttonCancel.addActionListener(e -> buttonCancelActionPerformed());
        mNewCoachButton.addActionListener(e -> buttonNewCoachActionPerformed());

        mCoachComboBox.setVisible(false);
        mCoachLabel.setVisible(false);
        mNewCoachButton.setVisible(false);

        mTextAreaFullName.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        initCoaches();

        pack();
    }

    private void saveSchool() {
        if (mSchool == null) {
            mSchool = new School();
        }
        mSchool.setName(mTextAreaFullName.getText());
        mSchool.setShortName(mTextFieldShortName.getText());
        mSchool.setSettlement(mTextFieldSettlement.getText());
        Coach coach = (Coach) mCoachComboBox.getSelectedItem();
        if (coach != null) {
            mSchool.setCoachId(coach.getId());
        }
        try {
            Database.get().getSchoolDao().createOrUpdate(mSchool);
            message("Iskola mentve.");
            listeners.forEach(listener -> listener.onSave(mSchool));
            dispose();
        } catch (SQLException ex) {
            Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    AddSchoolDialog addSaveListener(SaveListener listener) {
        listeners.add(listener);
        return this;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMaximumSize(new Dimension(436, 200));
        contentPane.setMinimumSize(new Dimension(436, 200));
        contentPane.setPreferredSize(new Dimension(436, 200));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonSave = new JButton();
        buttonSave.setText("Mentés");
        panel2.add(buttonSave, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Mégse");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Teljes név:");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Megjelenítendő név:");
        label2.setToolTipText("Rövidített név hogy kiférjen");
        panel3.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Helység:");
        panel3.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mTextAreaFullName = new JTextArea();
        Font mTextAreaFullNameFont = this.$$$getFont$$$(null, -1, 12, mTextAreaFullName.getFont());
        if (mTextAreaFullNameFont != null) mTextAreaFullName.setFont(mTextAreaFullNameFont);
        mTextAreaFullName.setLineWrap(true);
        mTextAreaFullName.setText("");
        mTextAreaFullName.setWrapStyleWord(true);
        panel3.add(mTextAreaFullName, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 66), new Dimension(-1, 66), new Dimension(-1, 66), 0, false));
        mTextFieldShortName = new JTextField();
        mTextFieldShortName.setToolTipText("Rövidített név hogy kiférjen");
        panel3.add(mTextFieldShortName, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mTextFieldSettlement = new JTextField();
        panel3.add(mTextFieldSettlement, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mCoachLabel = new JLabel();
        mCoachLabel.setText("Edzők");
        panel3.add(mCoachLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mCoachComboBox = new JComboBox();
        panel3.add(mCoachComboBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mNewCoachButton = new JButton();
        mNewCoachButton.setText("Új Edző");
        panel3.add(mNewCoachButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    public interface SaveListener {
        void onSave(School newSchool);
    }
}
