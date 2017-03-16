package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.ui.components.ButtonEditor;
import hu.danielb.raceadmin.ui.components.ButtonRenderer;
import hu.danielb.raceadmin.ui.components.table.models.SchoolTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class SchoolsDialog extends BaseDialog {
    private JPanel contentPane;
    private JTextField mSearchTextField;
    private JTable mTableSchools;
    private JButton mNewSchoolButton;

    private boolean mSortBackwards = false;
    private SchoolTableModel.Column mSortBy = SchoolTableModel.Column.NAME;
    private String filter = "";

    SchoolsDialog(Frame owner) {
        super(owner);
        init();
        setLocationRelativeTo(owner);
    }

    private void init() {
        initComponents();

        mTableSchools.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnAtPoint = mTableSchools.columnAtPoint(e.getPoint());
                mSortBackwards = mSortBy.ordinal() == columnAtPoint && !mSortBackwards;
                mSortBy = SchoolTableModel.Column.values()[columnAtPoint];
                loadData();
            }
        });

        mNewSchoolButton.addActionListener(e -> newButtonActionPerformed());

        loadData();
    }

    private void initComponents() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        mSearchTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                textSearchKeyReleased(evt);
            }
        });

        pack();
    }

    private void textSearchKeyReleased(java.awt.event.KeyEvent evt) {
        String find = mSearchTextField.getText().toLowerCase().trim();
        if (evt != null && (evt.getModifiers() == KeyEvent.CTRL_DOWN_MASK)) {
            return;
        }
        filter = find;
        loadData();
    }

    private void loadData() {

        try {
            java.util.List<School> schools = Database.get().getSchoolDao().getAll();

            if (!filter.isEmpty()) {
                schools = schools.stream().filter(school -> school.getName().toLowerCase().contains(filter) ||
                        school.getShortName() != null && school.getShortName().toLowerCase().contains(filter) ||
                        school.getSettlement() != null && school.getSettlement().toLowerCase().contains(filter))
                        .collect(Collectors.toList());
            }

            schools = schools.stream().sorted((o1, o2) -> {
                int bigger;
                switch (mSortBy) {
                    case NAME:
                        bigger = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                        break;
                    case SHORT_NAME:
                        if (o1.getShortName() == null) {
                            bigger = -1;
                            break;
                        }
                        if (o2.getShortName() == null) {
                            bigger = 1;
                            break;
                        }
                        bigger = o1.getShortName().toLowerCase().compareTo(o2.getShortName().toLowerCase());
                        break;
                    case SETTLEMENT:
                        if (o1.getSettlement() == null) {
                            bigger = -1;
                            break;
                        }
                        if (o2.getSettlement() == null) {
                            bigger = 1;
                            break;
                        }
                        bigger = o1.getSettlement().toLowerCase().compareTo(o2.getSettlement().toLowerCase());
                        break;
                    default:
                        bigger = 0;
                }
                return mSortBackwards ? bigger * -1 : bigger;
            }).collect(Collectors.toList());

            mTableSchools.setModel(new SchoolTableModel(schools).setSortBy(mSortBy).setSortBackwards(mSortBackwards));

            setColumnWidth(SchoolTableModel.Column.NAME.ordinal(), 280);
            setColumnWidth(SchoolTableModel.Column.SHORT_NAME.ordinal(), 210);
            setColumnWidth(SchoolTableModel.Column.SETTLEMENT.ordinal(), 180);
            setColumnWidth(SchoolTableModel.Column.EDIT.ordinal(), 90);

            mTableSchools.getColumnModel().getColumn(SchoolTableModel.Column.EDIT.ordinal()).setCellRenderer(new ButtonRenderer());
            mTableSchools.getColumnModel().getColumn(SchoolTableModel.Column.EDIT.ordinal()).setCellEditor(
                    new ButtonEditor(this::editButtonActionPerformed)
                            .addEditingStoppedListener(this::loadData));
            mTableSchools.getTableHeader().setReorderingAllowed(false);
            mTableSchools.getTableHeader().setResizingAllowed(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setColumnWidth(int columnNumber, int size) {
        TableColumn tc = mTableSchools.getColumnModel().getColumn(columnNumber);
        tc.setMaxWidth(size);
        tc.setMinWidth(size);
        tc.setPreferredWidth(size);
    }

    private void editButtonActionPerformed(ActionEvent evt) {
        int row = evt.getID();
        AddSchoolDialog dialog = new AddSchoolDialog(this,
                ((SchoolTableModel) mTableSchools.getModel()).getDataAt(row));

        dialog.setVisible(true);
        mSearchTextField.setText("");
        loadData();
    }

    private void newButtonActionPerformed() {
        AddSchoolDialog dialog = new AddSchoolDialog(this);

        dialog.setVisible(true);
        mSearchTextField.setText("");
        loadData();
    }
}
