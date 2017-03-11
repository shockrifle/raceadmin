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

class ContestantsDialog extends BaseDialog {

    private JComboBox<School> comboSchools;
    private javax.swing.JTable tableContestants;
    private javax.swing.JTextField textSearch;

    private ContestantTableModel.Column sortBy = ContestantTableModel.Column.NAME;
    private boolean sortBackwards = false;
    private String filter = "";
    private String schoolFilter = "";

    ContestantsDialog(Frame owner) {
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
                sortBackwards = sortBy.ordinal() == columnAtPoint && !sortBackwards;
                sortBy = ContestantTableModel.Column.values()[columnAtPoint];
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
            Database.get().getSchoolDao().queryBuilder().orderBy(School.COLUMN_NAME, true).query().forEach(school -> {
                try {
                    if (Database.get().getContestantDao().queryForEq(Contestant.COLUMN_SCHOOL_ID, school.getId()).size() > 0)
                        comboSchools.addItem(school);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboSchools.addActionListener(e -> comboSchoolsActionPerformed());

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
        menuItemPrint.addActionListener(e -> menuItemPrintActionPerformed());
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

    private void menuItemPrintActionPerformed() {
        try {
            new Printer(tableContestants).print();
        } catch (PrinterException | HeadlessException ex) {
            Logger.getLogger(ContestantsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void comboSchoolsActionPerformed() {
        schoolFilter = ((School) comboSchools.getSelectedItem()).getName().toLowerCase();
        loadData();
    }

    private void loadData() {

        List<Contestant> data = new ArrayList<>();
        try {

            data = Database.get().getContestantDao().queryForAll();


            if (!schoolFilter.isEmpty()) {
                data = data.stream().filter(contestant -> contestant.getSchool().getName().toLowerCase().contains(schoolFilter))
                        .collect(Collectors.toList());
            }
            if (!filter.isEmpty()) {
                data = data.stream().filter(contestant -> contestant.getName().toLowerCase().contains(filter) ||
                        contestant.getAgeGroup() != null && contestant.getAgeGroup().getName().toLowerCase().contains(filter) ||
                        contestant.getSchool().getName().toLowerCase().contains(filter) ||
                        (contestant.getSex().equals(Constants.BOY) ? "Fiú".toLowerCase().contains(filter) : "Lány".toLowerCase().contains(filter)) ||
                        String.valueOf(contestant.getPosition()).toLowerCase().contains(filter) ||
                        String.valueOf(contestant.getNumber()).toLowerCase().contains(filter) ||
                        String.valueOf(contestant.getAge()).toLowerCase().contains(filter))
                        .collect(Collectors.toList());
            }

            data = data.stream().sorted((o1, o2) -> {
                int bigger;
                switch (sortBy) {
                    case POSITION:
                        bigger = Integer.compare(o1.getPosition(), o2.getPosition());
                        break;
                    case NAME:
                        bigger = o1.getName().compareTo(o2.getName());
                        break;
                    case NUMBER:
                        bigger = Integer.compare(o1.getNumber(), o2.getNumber());
                        break;
                    case AGE:
                        bigger = Integer.compare(o1.getAge(), o2.getAge());
                        break;
                    case AGE_GROUP_NAME:
                        if (o1.getAgeGroup() == null) {
                            bigger = -1;
                            break;
                        }
                        if (o2.getAgeGroup() == null) {
                            bigger = 1;
                            break;
                        }
                        bigger = o1.getAgeGroup().getName().compareTo(o2.getAgeGroup().getName());
                        break;
                    case SCHOOL_NAME:
                        bigger = o1.getSchool().getName().compareTo(o2.getSchool().getName());
                        break;
                    case SEX:
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
        setColumnWidth(ContestantTableModel.Column.POSITION.ordinal(), 60);
        setColumnWidth(ContestantTableModel.Column.NUMBER.ordinal(), 60);
        setColumnWidth(ContestantTableModel.Column.AGE.ordinal(), 70);
        setColumnWidth(ContestantTableModel.Column.AGE_GROUP_NAME.ordinal(), 85);
        setColumnWidth(ContestantTableModel.Column.SEX.ordinal(), 40);
        setColumnWidth(ContestantTableModel.Column.EDIT.ordinal(), 90);
        tableContestants.getColumnModel().getColumn(ContestantTableModel.Column.EDIT.ordinal()).setCellRenderer(new ButtonRenderer());
        tableContestants.getColumnModel().getColumn(ContestantTableModel.Column.EDIT.ordinal()).setCellEditor(
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
