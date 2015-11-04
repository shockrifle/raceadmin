package hu.danielb.raceadmin.ui;

import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.ui.components.ButtonEditor;
import hu.danielb.raceadmin.ui.components.ButtonRenderer;
import hu.danielb.raceadmin.ui.components.table.models.ContestantTableModel;
import hu.danielb.raceadmin.util.Constants;
import hu.danielb.raceadmin.util.Printer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ContestantsDialog extends BaseDialog {

    private JComboBox<School> comboSchools;
    private javax.swing.JTable tableContestants;
    private javax.swing.JTextField textSearch;

    private int sortBy = ContestantTableModel.COLUMN_NAME;
    private boolean sortBackwards = false;
    private String filter = "";

    public ContestantsDialog(Frame owner) {
        super(owner);
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {
        initComponents();
        tableContestants.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnAtPoint = tableContestants.columnAtPoint(e.getPoint());
                sortBackwards = sortBy == columnAtPoint && !sortBackwards;
                sortBy = columnAtPoint;
                loadData();
            }
        });
    }

    private void initComponents() {

        tableContestants = new javax.swing.JTable();
        textSearch = new javax.swing.JTextField();
        comboSchools = new javax.swing.JComboBox<>();
        JLabel labelSearch = new JLabel();
        JScrollPane scrollPaneTableContestants = new JScrollPane();
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu();
        JMenuItem menuItemPrint = new JMenuItem();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setModal(true);
        setResizable(false);

        tableContestants.setModel(new ContestantTableModel(new ArrayList<>()));
        loadData();
        scrollPaneTableContestants.setViewportView(tableContestants);

        labelSearch.setText("Keresés:");

        textSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textSearchKeyReleased(evt);
            }
        });

        comboSchools.setModel(new javax.swing.DefaultComboBoxModel<>(new School[]{new School(0, "")}));
        try {
            Database.get().getSchoolDao().queryBuilder().orderBy(School.COLUMN_NAME, true).query().forEach(comboSchools::addItem);
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboSchools.addActionListener(ContestantsDialog.this::comboSchoolsActionPerformed);

        menuBar.setMaximumSize(new java.awt.Dimension(0, 0));
        menuBar.setMinimumSize(new java.awt.Dimension(0, 0));
        menuBar.setPreferredSize(new java.awt.Dimension(0, 0));

        menuFile.setText("File");
        menuFile.setMaximumSize(new java.awt.Dimension(0, 0));
        menuFile.setPreferredSize(new java.awt.Dimension(0, 0));

        menuItemPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        menuItemPrint.setText("Print");
        menuItemPrint.setMaximumSize(new java.awt.Dimension(0, 0));
        menuItemPrint.setName("");
        menuItemPrint.setPreferredSize(new java.awt.Dimension(0, 0));
        menuItemPrint.addActionListener(ContestantsDialog.this::menuItemPrintActionPerformed);
        menuFile.add(menuItemPrint);

        menuBar.add(menuFile);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(scrollPaneTableContestants, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(labelSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboSchools, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelSearch)
                                        .addComponent(textSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboSchools, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPaneTableContestants, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE))
        );

        pack();
    }

    private void textSearchKeyReleased(java.awt.event.KeyEvent evt) {
        String find = textSearch.getText().toLowerCase().trim();
        if (evt != null && (evt.getModifiers() == KeyEvent.CTRL_DOWN_MASK)) {
            return;
        }
        filter = find;
        loadData();
    }

    private void menuItemPrintActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            new Printer(tableContestants);
        } catch (PrinterException | HeadlessException ex) {
            Logger.getLogger(ContestantsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void comboSchoolsActionPerformed(java.awt.event.ActionEvent evt) {
        filter = ((School) comboSchools.getSelectedItem()).getName();
        loadData();
    }

    private void loadData() {

        List<Contestant> data = new ArrayList<>();
        try {

            data = Database.get().getContestantDao().queryForAll();

            if (!filter.isEmpty()) {
                data = data.stream().filter(contestant -> contestant.getName().toLowerCase().contains(filter) ||
                        contestant.getAgeGroup().getName().toLowerCase().contains(filter) ||
                        contestant.getSchool().getName().toLowerCase().contains(filter) ||
                        (contestant.getSex().equals(Constants.BOY) ? "Fiú".toLowerCase().contains(filter) : "Lány".toLowerCase().contains(filter)) ||
                        String.valueOf(contestant.getPosition()).toLowerCase().contains(filter) ||
                        String.valueOf(contestant.getNumber()).toLowerCase().contains(filter) ||
                        String.valueOf(contestant.getAge()).toLowerCase().contains(filter))
                        .collect(Collectors.toList());
            }

            data = data.stream().sorted((o1, o2) -> {
                int bigger = 0;
                switch (sortBy) {
                    case ContestantTableModel.COLUMN_POSITION:
                        bigger = Integer.compare(o1.getPosition(), o2.getPosition());
                        break;
                    case ContestantTableModel.COLUMN_NAME:
                        bigger = o1.getName().compareTo(o2.getName());
                        break;
                    case ContestantTableModel.COLUMN_NUMBER:
                        bigger = Integer.compare(o1.getNumber(), o2.getNumber());
                        break;
                    case ContestantTableModel.COLUMN_AGE:
                        bigger = Integer.compare(o1.getAge(), o2.getAge());
                        break;
                    case ContestantTableModel.COLUMN_AGE_GROUP_NAME:
                        bigger = o1.getAgeGroup().getName().compareTo(o2.getAgeGroup().getName());
                        break;
                    case ContestantTableModel.COLUMN_SCHOOL_NAME:
                        bigger = o1.getSchool().getName().compareTo(o2.getSchool().getName());
                        break;
                    case ContestantTableModel.COLUMN_SEX:
                        bigger = o1.getSex().compareTo(o2.getSex());
                        break;
                    default:
                        bigger = 0;
                }
                return sortBackwards ? bigger * -1 : bigger;
            }).collect(Collectors.toList());

        } catch (SQLException ex) {
            Logger.getLogger(ContestantsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(data);

    }

    private void loadData(List<Contestant> data) {

        tableContestants.setModel(new ContestantTableModel(data).setSortBy(sortBy).setSortBackwards(sortBackwards));
        setColumnWidth(ContestantTableModel.COLUMN_CONTESTANT_ID, 0);
        setColumnWidth(ContestantTableModel.COLUMN_SCHOOL_ID, 0);
        setColumnWidth(ContestantTableModel.COLUMN_AGE_GROUP_ID, 0);
        setColumnWidth(ContestantTableModel.COLUMN_POSITION, 60);
        setColumnWidth(ContestantTableModel.COLUMN_NUMBER, 60);
        setColumnWidth(ContestantTableModel.COLUMN_AGE, 70);
        setColumnWidth(ContestantTableModel.COLUMN_AGE_GROUP_NAME, 85);
        setColumnWidth(ContestantTableModel.COLUMN_SEX, 40);
        setColumnWidth(ContestantTableModel.COLUMN_EDIT, 90);
        tableContestants.getColumnModel().getColumn(ContestantTableModel.COLUMN_EDIT).setCellRenderer(new ButtonRenderer());
        tableContestants.getColumnModel().getColumn(ContestantTableModel.COLUMN_EDIT).setCellEditor(
                new ButtonEditor(ContestantsDialog.this::editButtonActionPerformed)
                        .addEditingStoppedListener(ContestantsDialog.this::loadData));
        tableContestants.getTableHeader().setReorderingAllowed(false);
        tableContestants.getTableHeader().setResizingAllowed(false);
    }

    private void setColumnWidth(int columnNumber, int size) {
        TableColumn tc = tableContestants.getColumnModel().getColumn(columnNumber);
        tc.setMaxWidth(size);
        tc.setMinWidth(size);
        tc.setPreferredWidth(size);
    }

    private void editButtonActionPerformed(ActionEvent evt) {
        int row = evt.getID();
        AddContestantDialog dialog = new AddContestantDialog(this,
                ((ContestantTableModel) tableContestants.getModel()).getDataAt(row));

        dialog.setVisible(true);
        textSearch.setText("");
    }
}
