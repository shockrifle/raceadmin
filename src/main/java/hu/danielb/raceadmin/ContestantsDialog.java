package hu.danielb.raceadmin;

import hu.danielb.raceadmin.components.ButtonEditor;
import hu.danielb.raceadmin.components.ButtonRenderer;
import hu.danielb.raceadmin.data.AgeGroup;
import hu.danielb.raceadmin.data.Contestant;
import hu.danielb.raceadmin.data.Database;
import hu.danielb.raceadmin.data.School;
import hu.danielb.raceadmin.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContestantsDialog extends BaseDialog {

    public static final String COLUMN_AGE_GROUP_NAME = "age_group_name";
    public static final String COLUMN_SCHOOL_NAME = "school_name";
    protected int sortBy = 4;
    protected boolean sortBackwards = false;
    protected Map<Integer, String> sortStrings;

    private JComboBox<School> comboSchools;
    private javax.swing.JTable tableContestants;
    private javax.swing.JTextField textSearch;

    public ContestantsDialog(Frame owner) {
        super(owner);
        init();
        this.setLocationRelativeTo(owner);
    }

    private void init() {
        sortStrings = new HashMap<>();
        sortStrings.put(3, Contestant.TABLE + "." + Contestant.COLUMN_POSITION);
        sortStrings.put(4, Contestant.TABLE + "." + Contestant.COLUMN_NAME);
        sortStrings.put(5, Contestant.TABLE + "." + Contestant.COLUMN_NUMBER);
        sortStrings.put(6, Contestant.TABLE + "." + Contestant.COLUMN_AGE);
        sortStrings.put(7, COLUMN_AGE_GROUP_NAME);
        sortStrings.put(8, COLUMN_SCHOOL_NAME);
        sortStrings.put(9, Contestant.TABLE + "." + Contestant.COLUMN_SEX);

        initComponents();
        tableContestants.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tableContestants.columnAtPoint(e.getPoint());
                sortBackwards = col == sortBy && !sortBackwards;
                sortBy = col;
                jTextField1KeyReleased(null);
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

        loadData();
        scrollPaneTableContestants.setViewportView(tableContestants);

        labelSearch.setText("Keresés:");

        textSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        comboSchools.setModel(new javax.swing.DefaultComboBoxModel<>(new School[]{new School(0, "")}));
        try {
            ResultSet rs = Database.runSql("select * from " + School.TABLE + " order by " + School.COLUMN_NAME);
            while (rs.next()) {
                comboSchools.addItem(new School(rs.getInt(School.COLUMN_ID), rs.getString(School.COLUMN_NAME)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddContestantDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        comboSchools.addActionListener(ContestantsDialog.this::jComboBox1ActionPerformed);

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
        menuItemPrint.addActionListener(ContestantsDialog.this::jMenuItem1ActionPerformed);
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

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {
        String find = textSearch.getText().trim();
        if (evt != null && (evt.getModifiers() == KeyEvent.CTRL_DOWN_MASK)) {
            return;
        }
        if (!find.isEmpty()) {
            filterData(find);
        } else {
            loadData();
        }
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            new Printer(tableContestants);
        } catch (PrinterException | HeadlessException ex) {
            Logger.getLogger(ContestantsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {

        String name = ((School) comboSchools.getSelectedItem()).getName();
        if (name.isEmpty()) {
            loadData();
            return;
        }
        filterData(name);
    }

    private void filterData(String filter) {
        Vector<Vector<String>> data = new Vector<>();
        filter = "%" + filter + "%";
        try {
            ResultSet rs = Database.runSql("select " + Contestant.TABLE + ".*," +
                    School.TABLE + "." + School.COLUMN_NAME + " as " + COLUMN_SCHOOL_NAME + "," +
                    AgeGroup.TABLE + "." + AgeGroup.COLUMN_NAME + " as " + COLUMN_AGE_GROUP_NAME + " from " + Contestant.TABLE + " "
                    + "inner join " + School.TABLE + " on " + Contestant.TABLE + "." + Contestant.COLUMN_SCHOOL_ID + "=" + School.TABLE + "." + School.COLUMN_ID + " "
                    + "inner join " + AgeGroup.TABLE + " on " + Contestant.TABLE + "." + Contestant.COLUMN_AGE_GROUP_ID + "=" + AgeGroup.TABLE + "." + AgeGroup.COLUMN_ID + " "
                    + "where " + Contestant.TABLE + "." + Contestant.COLUMN_NAME + " like ? "
                    + "or " + Contestant.TABLE + "." + Contestant.COLUMN_NUMBER + " like ? "
                    + "or " + Contestant.TABLE + "." + Contestant.COLUMN_AGE + " like ? "
                    + "or " + COLUMN_AGE_GROUP_NAME + " like ? "
                    + "or " + COLUMN_SCHOOL_NAME + " like ? "
                    + "order by " + sortStrings.get(sortBy) + (sortBackwards ? " desc" : " asc"), Database.QUERRY, filter, filter, filter, filter, filter, filter);
            while (rs.next()) {
                data.add(new Vector<>(Arrays.asList(
                        new String[]{String.valueOf(rs.getInt(Contestant.COLUMN_ID)),
                                String.valueOf(rs.getInt(Contestant.COLUMN_SCHOOL_ID)),
                                String.valueOf(rs.getInt(Contestant.COLUMN_AGE_GROUP_ID)),
                                rs.getInt(Contestant.COLUMN_POSITION) < 1 ? "" : String.valueOf(rs.getInt(Contestant.COLUMN_POSITION)),
                                rs.getString(Contestant.COLUMN_AGE),
                                String.valueOf(rs.getInt(Contestant.COLUMN_NUMBER)),
                                String.valueOf(rs.getInt(Contestant.COLUMN_AGE)),
                                rs.getString(COLUMN_AGE_GROUP_NAME),
                                rs.getString(COLUMN_SCHOOL_NAME),
                                Constants.BOY.equals(rs.getString(Contestant.COLUMN_SEX))?"F":"L",
                                "Szerkesztés"})));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContestantsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(data);
    }

    private void loadData() {

        Vector<Vector<String>> data = new Vector<>();
        try {
            ResultSet rs = Database.runSql("select " + Contestant.TABLE + ".*," +
                    School.TABLE + "."+School.COLUMN_NAME+" as "+COLUMN_SCHOOL_NAME+"," +
                    AgeGroup.TABLE + "."+AgeGroup.COLUMN_NAME+" as "+COLUMN_AGE_GROUP_NAME+" from " + Contestant.TABLE + " "
                    + "inner join " + School.TABLE + " on " + Contestant.TABLE + "." + Contestant.COLUMN_SCHOOL_ID + "=" + School.TABLE + "." + School.COLUMN_ID + " "
                    + "inner join " + AgeGroup.TABLE + " on " + Contestant.TABLE + "." + Contestant.COLUMN_AGE_GROUP_ID + "=" + AgeGroup.TABLE + "." + AgeGroup.COLUMN_ID + " "
                    + "order by " + sortStrings.get(sortBy) + (sortBackwards ? " desc" : " asc"));
            while (rs.next()) {
                data.add(new Vector<>(Arrays.asList(
                        new String[]{String.valueOf(rs.getInt(Contestant.COLUMN_ID)),
                                String.valueOf(rs.getInt(Contestant.COLUMN_SCHOOL_ID)),
                                String.valueOf(rs.getInt(Contestant.COLUMN_AGE_GROUP_ID)),
                                rs.getInt(Contestant.COLUMN_POSITION) < 1 ? "" : String.valueOf(rs.getInt(Contestant.COLUMN_POSITION)),
                                rs.getString(Contestant.COLUMN_AGE),
                                String.valueOf(rs.getInt(Contestant.COLUMN_NUMBER)),
                                String.valueOf(rs.getInt(Contestant.COLUMN_AGE)),
                                rs.getString(COLUMN_AGE_GROUP_NAME),
                                rs.getString(COLUMN_SCHOOL_NAME),
                                Constants.BOY.equals(rs.getString(Contestant.COLUMN_SEX))?"F":"L",
                                "Szerkesztés"})));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContestantsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(data);

    }

    private void loadData(Vector<Vector<String>> data) {

        tableContestants.setModel(new DefaultTableModel(data, new Vector<>(Arrays.asList(
                new String[]{
                        "",
                        "",
                        "",
                        "Helyezés" + putSortMark(3),
                        "Név" + putSortMark(4),
                        "Rajtszám" + putSortMark(5),
                        "Születési év" + putSortMark(6),
                        "Korosztály" + putSortMark(7),
                        "Iskola" + putSortMark(8),
                        "Nem" + putSortMark(9),
                        ""}))) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10;
            }
        });
        setColumnWidth(0, 0);
        setColumnWidth(1, 0);
        setColumnWidth(2, 0);
        setColumnWidth(3, 65);
        setColumnWidth(5, 65);
        setColumnWidth(6, 75);
        setColumnWidth(7, 90);
        setColumnWidth(9, 40);
        setColumnWidth(10, 90);
        tableContestants.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
        tableContestants.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor(
                ContestantsDialog.this::editButtonActionPerformed).addEditingStoppedListener(ContestantsDialog.this::loadData));
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
                new Contestant(Integer.parseInt((String) tableContestants.getValueAt(row, 0)),
                        "".equals(tableContestants.getValueAt(row, 3)) ? 0 : Integer.parseInt((String) tableContestants.getValueAt(row, 3)),
                        (String) tableContestants.getValueAt(row, 4),
                        (String) tableContestants.getValueAt(row, 9),
                        Integer.parseInt((String) tableContestants.getValueAt(row, 5)),
                        new AgeGroup(Integer.parseInt((String) tableContestants.getValueAt(row, 2)), "", 0, 0),
                        new School(Integer.parseInt((String) tableContestants.getValueAt(row, 1)), (String) tableContestants.getValueAt(row, 8)),
                        Integer.parseInt((String) tableContestants.getValueAt(row, 6))));

        dialog.setVisible(true);
        textSearch.setText("");
    }

    private String putSortMark(int i) {
        if (i == sortBy) {
            if (sortBackwards) {
                return "^";
            }
            return "ˇ";
        }
        return "";
    }
}
