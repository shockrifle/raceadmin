package hu.danielb.raceadmin.ui;

import com.j256.ormlite.stmt.Where;
import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.School;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddSchoolDialog extends BaseDialog {
    private JPanel contentPane;
    private JTextArea mTextAreaFullName;
    private JTextField mTextFieldShortName;
    private JTextField mTextFieldSettlement;
    private JButton buttonSave;
    private JButton buttonCancel;
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
        init();
    }

    private void init() {
        mTextAreaFullName.setText(mSchool.getName());
        mTextFieldShortName.setText(mSchool.getShortName());
        mTextFieldSettlement.setText(mSchool.getSettlement());
    }

    private void initComponents() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        buttonSave.addActionListener(e -> buttonSaveActionPerformed());
        buttonCancel.addActionListener(e -> buttonCancelActionPerformed());

        mTextAreaFullName.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        pack();
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
        String settlement = mTextFieldSettlement.getText();
        if (settlement.length() < 2) {
            warn("Nem adott meg helységet!");
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

        listeners.forEach(listener -> listener.onSave(new School()));
    }

    private void saveSchool() {
        if (mSchool == null) {
            mSchool = new School();
        }
        mSchool.setName(mTextAreaFullName.getText());
        mSchool.setShortName(mTextFieldShortName.getText());
        mSchool.setSettlement(mTextFieldSettlement.getText());
        try {
            Database.get().getSchoolDao().createOrUpdate(mSchool);
            message("Iskola mentve.");
            dispose();
        } catch (SQLException ex) {
            Logger.getLogger(AddSchoolDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    AddSchoolDialog addSaveListener(SaveListener listener) {
        listeners.add(listener);
        return this;
    }

    public interface SaveListener {
        void onSave(School newSchool);
    }
}
