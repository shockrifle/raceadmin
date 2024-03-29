package hu.danielb.raceadmin.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.j256.ormlite.dao.Dao;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.entity.Supervisor;

public class AddSupervisorDialog extends BaseDialog {

    protected JPanel contentPane;
    protected JButton buttonSave;
    protected JButton buttonCancel;
    private JTextField mTextFieldName;
    private JComboBox<School> mComboBoxSchools;
    private JRadioButton typeTeacherRadioButton;
    private JRadioButton typeCoachRadioButton;

    private List<SaveListener> listeners = new ArrayList<>();
    private Supervisor mData;

    AddSupervisorDialog(Dialog owner) {
        super(owner);
        initComponents();
        setLocationRelativeTo(owner);
    }

    AddSupervisorDialog(Dialog owner, Supervisor data) {
        this(owner);
        mData = data;
        initValues(mData);
    }

    AddSupervisorDialog(Dialog owner, School school) {
        this(owner);
        mComboBoxSchools.setModel(new DefaultComboBoxModel<>(new School[]{school}));
    }

    private void initComponents() {
        setContentPane(getBaseContentPane());
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        getSaveButton().addActionListener(e -> buttonSaveActionPerformed());
        getCancelButton().addActionListener(e -> buttonCancelActionPerformed());

        AutoCompleteDecorator.decorate(mComboBoxSchools, new ObjectToStringConverter() {
            @Override
            public String getPreferredStringForItem(Object item) {
                if (item instanceof School) {
                    return ((School) item).getNameWithSettlement();
                }
                return null;
            }
        });
        mComboBoxSchools.setRenderer(new DefaultListCellRenderer() {
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

        pack();
    }

    private void initValues(Supervisor data) {
        mTextFieldName.setText(data.getName());
        mComboBoxSchools.setSelectedItem(data.getSchool());
        switch (data.getType()) {
            case COACH:
                typeCoachRadioButton.setSelected(true);
                break;
            case TEACHER:
                typeTeacherRadioButton.setSelected(true);
                break;
        }
    }

    private void buttonCancelActionPerformed() {
        this.dispose();
    }

    private void buttonSaveActionPerformed() {
        if (validateData()) {
            saveData();
        } else {
            if (JOptionPane.showOptionDialog(this, getInvalidDataMessage() + "\nBiztos menti?", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Mentés", "Mégse"}, null) == 0) {
                saveData();
            }
        }
    }

    private Container getBaseContentPane() {
        return contentPane;
    }

    private AbstractButton getSaveButton() {
        return buttonSave;
    }

    private AbstractButton getCancelButton() {
        return buttonCancel;
    }

    private void refreshSchools() {
        mComboBoxSchools.setModel(new DefaultComboBoxModel<>(new School[]{new School()}));
        try {
            Database.get().getSchoolDao().queryForAll().stream().sorted(Comparator.comparing(o -> o.getNameWithSettlement().toLowerCase()))
                    .forEach(mComboBoxSchools::addItem);
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean validateData() {
        return !mTextFieldName.getText().isEmpty();
    }

    private String getInvalidDataMessage() {
        return "Nem adott meg nevet!";
    }

    private Supervisor newData() {
        return new Supervisor();
    }

    private void setFields(Supervisor data) {
        data.setName(mTextFieldName.getText());
        data.setSchool((School) mComboBoxSchools.getSelectedItem());
        data.setType(calculateType());
    }

    private Supervisor.Type calculateType() {
        if (typeCoachRadioButton.isSelected()) {
            return Supervisor.Type.COACH;
        }
        return Supervisor.Type.TEACHER;
    }

    protected Dao<Supervisor, Integer> getDatabase() throws SQLException {
        return Database.get().getSupervisorDao();
    }

    private void saveData() {
        if (mData == null) {
            mData = newData();
        }
        setFields(mData);
        try {
            getDatabase().createOrUpdate(mData);
            message("Mentve.");
            listeners.forEach(l -> l.onSave(mData));
            dispose();
        } catch (SQLException ex) {
            Logger.getLogger(AddSupervisorDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BaseDialog addSaveListener(SaveListener listener) {
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
        contentPane.setMaximumSize(new Dimension(436, 150));
        contentPane.setMinimumSize(new Dimension(436, 150));
        contentPane.setPreferredSize(new Dimension(436, 150));
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
        panel3.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Teljes név:");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Iskola:");
        panel3.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mComboBoxSchools = new JComboBox();
        panel3.add(mComboBoxSchools, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(350, -1), new Dimension(350, -1), new Dimension(400, -1), 0, false));
        mTextFieldName = new JTextField();
        panel3.add(mTextFieldName, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Típus: ");
        panel3.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typeTeacherRadioButton = new JRadioButton();
        typeTeacherRadioButton.setSelected(true);
        typeTeacherRadioButton.setText("Tanár");
        panel3.add(typeTeacherRadioButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typeCoachRadioButton = new JRadioButton();
        typeCoachRadioButton.setText("Edző");
        panel3.add(typeCoachRadioButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(typeTeacherRadioButton);
        buttonGroup.add(typeCoachRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    public interface SaveListener {
        void onSave(Supervisor newSupervisor);
    }

}
