package hu.danielb.raceadmin.ui;


import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.database.DatabaseOld;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.PrintHeader;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.ui.components.GenericTabbedPane;
import hu.danielb.raceadmin.ui.components.PrintHeaderMenuItem;
import hu.danielb.raceadmin.ui.components.table.AttributiveCellTableModel;
import hu.danielb.raceadmin.ui.components.table.CellSpan;
import hu.danielb.raceadmin.ui.components.table.MultiSpanCellTable;
import hu.danielb.raceadmin.ui.components.table.models.ResultsTableModel;
import hu.danielb.raceadmin.util.Constants;
import hu.danielb.raceadmin.util.Printer;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//TODO: fejléc szerkesztés
//TODO: beállítások ablak összes beállítást egybefogva
//TODO: korosztály gyors beállítás (kezdődátum +hány év egy korosztály)
//TODO: régi adatok újrafelhasználása rajtszám elvételével
//TODO: iskolalista
//TODO: iskola legördülő ne csak az elején keressen
//TODO: nevek nagybetűvel kezdése
//TODO: iskola rövidített név

public class MainFrame extends javax.swing.JFrame {

    public static final String COLUMN_AGE_GROUP_ID = "age_group_id";
    public static final String COLUMN_SCHOOL_ID = "school_id";
    public static final String COLUMN_SCHOOL_NAME = "school_name";
    private Map<String, JTable> tables;
    private String[] headerString;
    private JDialog startupScreen;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton buttonFinisher;
    private javax.swing.JMenu menuPrintHeader;
    private javax.swing.JTabbedPane ageGroupPane;
    private String exportsPath;

    public MainFrame() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        init();

        positionToCenter();
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    private void initComponents() {

        buttonGroup2 = new javax.swing.ButtonGroup();
        ageGroupPane = new javax.swing.JTabbedPane();
        buttonFinisher = new javax.swing.JButton();
        JMenu menuFile = new JMenu();
        JMenuItem menuItemPrint = new JMenuItem();
        JMenuItem menuItemExport = new JMenuItem();
        JMenuItem menuItemExit = new JMenuItem();
        JMenu menuEdit = new JMenu();
        JMenuItem menuItemNewContestant = new JMenuItem();
        JMenuItem menuItemFinisher = new JMenuItem();
        JMenuItem menuItemAgeGroups = new JMenuItem();
        JMenuItem menuItemContestants = new JMenuItem();
        JMenu menuSettings = new JMenu();
        menuPrintHeader = new javax.swing.JMenu();
        JMenuBar menuBar = new JMenuBar();
        JPanel jPanel4 = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);

        initPane();

        buttonFinisher.setText("Beérkező");
        buttonFinisher.addActionListener(MainFrame.this::buttonFinisherActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(buttonFinisher, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(buttonFinisher, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuFile.setText("File");

        menuItemPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        menuItemPrint.setText("Nyomtatás");
        menuItemPrint.addActionListener(MainFrame.this::menuItemPrintActionPerformed);
        menuFile.add(menuItemPrint);

        menuItemExport.setText("Export");
        menuItemExport.addActionListener(MainFrame.this::menuItemExportActionPerformed);
        menuFile.add(menuItemExport);

        menuItemExit.setText("Kilépés");
        menuItemExit.addActionListener(MainFrame.this::menuItemExitActionPerformed);
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuEdit.setText("Szerkesztés");

        menuItemNewContestant.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuItemNewContestant.setText("Versenyző hozzáadása");
        menuItemNewContestant.addActionListener(MainFrame.this::menuItemNewContestantActionPerformed);
        menuEdit.add(menuItemNewContestant);

        menuItemFinisher.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFinisher.setText("Beérkezők megadása");
        menuItemFinisher.addActionListener(MainFrame.this::menuItemFinisherActionPerformed);
        menuEdit.add(menuItemFinisher);

        menuItemAgeGroups.setText("Korosztályok");
        menuItemAgeGroups.addActionListener(MainFrame.this::menuItemAgeGroupsActionPerformed);
        menuEdit.add(menuItemAgeGroups);

        menuItemContestants.setText("Versenyzők");
        menuItemContestants.addActionListener(MainFrame.this::menuItemContestantsActionPerformed);
        menuEdit.add(menuItemContestants);

        menuBar.add(menuEdit);

        menuSettings.setText("Beállítások");

        menuPrintHeader.setText("Nyomtatási Fejléc");
        loadPrintHeaders();
        menuSettings.add(menuPrintHeader);

        menuBar.add(menuSettings);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(ageGroupPane, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ageGroupPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void menuItemAgeGroupsActionPerformed(java.awt.event.ActionEvent evt) {
        AgeGroupsDialog dialog = new AgeGroupsDialog(this);
        dialog.setVisible(true);
        loadData();
    }

    private void menuItemNewContestantActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog addFrame = new AddContestantDialog(this);
        addFrame.setVisible(true);
        loadData();
    }

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void menuItemFinisherActionPerformed(java.awt.event.ActionEvent evt) {
        buttonFinisher.doClick();
    }

    private void buttonFinisherActionPerformed(java.awt.event.ActionEvent evt) {
        FinishingDialog dialog = new FinishingDialog(this);
        dialog.addFinishingListener(v -> loadData());
        dialog.setVisible(true);
    }

    private void menuItemPrintActionPerformed(java.awt.event.ActionEvent evt) {
        if (headerString != null && headerString.length > 0) {
            GenericTabbedPane ageGroupTab = (GenericTabbedPane) ageGroupPane.getComponentAt(ageGroupPane.getSelectedIndex());
            AgeGroup ageGroup = (AgeGroup) ageGroupTab.getData();
            GenericTabbedPane.Tab tab = ageGroupTab.getTab(ageGroupTab.getSelectedIndex());

            try {
                if (tab.getId() == Constants.BOYTEAM || tab.getId() == Constants.GIRLTEAM) {
                    new Printer(ageGroup.getName() + ", " + tab.getTitle(), tables.get(ageGroup.getId() + (String) tab.getId()), headerString, true);
                } else {
                    new Printer(ageGroup.getName() + ", " + tab.getTitle(), tables.get(ageGroup.getId() + (String) tab.getId()), headerString);
                }
            } catch (PrinterException ex) {
                warn("Nyomtatási hiba!");
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            warn("Nincs fejléc kiválasztva!");
        }
    }

    private void menuItemContestantsActionPerformed(java.awt.event.ActionEvent evt) {
        ContestantsDialog dial = new ContestantsDialog(this);
        dial.setVisible(true);
        loadData();
    }

    private void menuItemExportActionPerformed(java.awt.event.ActionEvent evt) {

        List<Category> result = new ArrayList<>();

        try {
            Database.get().getAgeGroupDao().queryForAll().stream().sorted((o1, o2) -> Integer.compare(o1.getMinimum(), o2.getMinimum())).forEach(ageGroup -> {
                try {
                    File exportDir = new File(exportsPath);
                    if (!exportDir.exists()) {
                        if (!exportDir.mkdirs()) {
                            throw new IOException("Cannot create directory for exports at: " + exportDir.getAbsolutePath());
                        }
                    }
                    Category boyAgeGroup = new Category();
                    result.add(boyAgeGroup);
                    boyAgeGroup.individual.name = ageGroup.getName() + " " + "Fiú, egyéni";
                    boyAgeGroup.team.name = ageGroup.getName() + " " + "Fiú, csapat";

                    List<Contestant> contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.BOY);
                    contestants.forEach(boyAgeGroup.individual.contestants::add);

                    contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.BOY);
                    contestants.forEach(contestant -> {
                        if (contestant.getPosition() > 0) {
                            Vector<Vector<String>> data = new Vector<>();
                            data.add(new Vector<>(Arrays.asList(new String[]{
                                    String.valueOf(contestant.getId()),
                                    String.valueOf(contestant.getPosition()),
                                    String.valueOf(contestant.getNumber()),
                                    contestant.getName(),
                                    contestant.getSchool().getName()})));
                            boyAgeGroup.team.teams.addAll(makeTeams(data));
                        }
                    });

                    Category girlAgeGroup = new Category();
                    result.add(girlAgeGroup);
                    girlAgeGroup.individual.name = ageGroup.getName() + " " + "Lány, egyéni";
                    girlAgeGroup.team.name = ageGroup.getName() + " " + "Lány, csapat";

                    contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.GIRL);
                    contestants.forEach(girlAgeGroup.individual.contestants::add);

                    contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.GIRL);
                    contestants.forEach(contestant -> {
                        if (contestant.getPosition() > 0) {
                            Vector<Vector<String>> data = new Vector<>();
                            data.add(new Vector<>(Arrays.asList(new String[]{
                                    String.valueOf(contestant.getId()),
                                    String.valueOf(contestant.getPosition()),
                                    String.valueOf(contestant.getNumber()),
                                    contestant.getName(),
                                    contestant.getSchool().getName()})));
                            girlAgeGroup.team.teams.addAll(makeTeams(data));
                        }
                    });
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            Map<String, List> beans = new HashMap<>();
            beans.put("eredmenyek", result);
            XLSTransformer transformer = new XLSTransformer();
            File file = new File(exportsPath + File.separator + "export.xls");
            OutputStream os = new FileOutputStream(file);
            InputStream template = getClass().getResourceAsStream("/templates/egyeni_template.xls");
            transformer.transformXLS(template, beans).write(os);
        } catch (IOException | ParsePropertyException | InvalidFormatException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void positionToCenter() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        this.setLocation(x, y);
    }

    private void init() {
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startupScreen = new StartupScreen(this, true, properties.getProperty("version"));
        exportsPath = properties.getProperty("exports-path");
        try {
            Class.forName("org.sqlite.JDBC");
            new Thread() {
                @Override
                public void run() {
                    try {
                        DatabaseOld.connect();

                        tables = new HashMap<>();
                        initComponents();
                        loadData();

                        startupScreen.dispose();
                    } catch (SQLException ex) {
                        warn("Hiba az adatok betöltése közben!");
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        System.exit(1);
                    }
                }
            }.start();
            startupScreen.setVisible(true);
        } catch (ClassNotFoundException ex) {
            warn("Hiba az adatok betöltése közben!");
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    private void loadPrintHeaders() {
        MenuElement[] elements = menuPrintHeader.getSubElements();
        for (MenuElement element : elements) {
            MenuElement[] menuElement = element.getSubElements();
            for (MenuElement menuElement1 : menuElement) {
                buttonGroup2.remove((JMenuItem) menuElement1);
            }
        }
        menuPrintHeader.removeAll();

        try {
            Database.get().getPrintHeaderDao().queryForAll().forEach(printHeader -> {
                PrintHeaderMenuItem<PrintHeader> item = new PrintHeaderMenuItem<>(printHeader);
                item.addActionListener(e -> headerString = ((PrintHeader) ((PrintHeaderMenuItem) e.getSource()).getData()).getText().split("\n"));
                buttonGroup2.add(item);
                menuPrintHeader.add(item);
                buttonGroup2.setSelected(item.getModel(), true);
                headerString = item.getData().getText().split("\n");

            });

            JMenuItem edit = new JMenuItem("Új");
            edit.addActionListener(e -> {
                AddPrintHeaderDialog addPrintHeaderDialog = new AddPrintHeaderDialog(MainFrame.this);
                addPrintHeaderDialog.setVisible(true);
                loadPrintHeaders();
            });
            menuPrintHeader.add(edit);
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initPane() {
        ageGroupPane.removeAll();
        try {
            Database.get().getAgeGroupDao().queryForAll().stream().sorted((o1, o2) -> Integer.compare(o1.getMinimum(), o2.getMinimum())).forEach(ageGroup -> {
                GenericTabbedPane<AgeGroup> ageGroupTab = new GenericTabbedPane<>(ageGroup);
                String ageGroupId = String.valueOf(ageGroup.getId());

                ageGroupTab.setPreferredSize(new Dimension(ageGroupPane.getHeight(), ageGroupPane.getWidth()));

                ageGroupTab.addTab(Constants.BOY, "Fiú", createTable(ageGroupId + Constants.BOY, new Dimension(ageGroupTab.getHeight(), ageGroupTab.getWidth())));
                ageGroupTab.addTab(Constants.GIRL, "Lány", createTable(ageGroupId + Constants.GIRL, new Dimension(ageGroupTab.getHeight(), ageGroupTab.getWidth())));
                ageGroupTab.addTab(Constants.BOYTEAM, "Fiú Csapat", createTable(ageGroupId + Constants.BOYTEAM, new Dimension(ageGroupTab.getHeight(), ageGroupTab.getWidth())));
                ageGroupTab.addTab(Constants.GIRLTEAM, "Lány Csapat", createTable(ageGroupId + Constants.GIRLTEAM, new Dimension(ageGroupTab.getHeight(), ageGroupTab.getWidth())));

                ageGroupPane.addTab(ageGroup.getName(), ageGroupTab);
            });
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JScrollPane createTable(String name, Dimension size) {

        MultiSpanCellTable currentTable = new MultiSpanCellTable(new ResultsTableModel(new ArrayList<>()));
        currentTable.getTableHeader().setReorderingAllowed(false);
        currentTable.getTableHeader().setResizingAllowed(false);

        for (int i = 0; i < currentTable.getColumnCount(); i++) {
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
            cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            currentTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane tableScrollPane = new JScrollPane(currentTable);
        tableScrollPane.setPreferredSize(size);
        tables.put(name, currentTable);
        return tableScrollPane;
    }

    private void loadData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Database.get().getAgeGroupDao().queryForAll().stream().sorted((o1, o2) -> Integer.compare(o1.getMinimum(), o2.getMinimum())).forEach(MainFrame.this::updateData);
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    private void updateData(AgeGroup ageGroup) {
        loadTable(Constants.BOY, ageGroup);
        loadTable(Constants.GIRL, ageGroup);
    }

    private void loadTable(String sex, AgeGroup ageGroup) {
        try {
            List<Contestant> contestants = getByAgeGroupAndSex(ageGroup.getId(), sex);
            Vector<Vector<String>> data = new Vector<>();
            Vector<Vector<String>> disq = new Vector<>();
            contestants.forEach(contestant -> {
                if (contestant.getPosition() > 0) {
                    data.add(new Vector<>(Arrays.asList(new String[]{
                            String.valueOf(contestant.getId()),
                            String.valueOf(contestant.getPosition()),
                            String.valueOf(contestant.getNumber()),
                            contestant.getName(),
                            contestant.getSchool().getName()})));
                } else {
                    data.add(new Vector<>(Arrays.asList(new String[]{
                            String.valueOf(contestant.getId()),
                            "",
                            String.valueOf(contestant.getNumber()),
                            contestant.getName(),
                            contestant.getSchool().getName()})));
                }

            });

            if (!data.isEmpty() || !disq.isEmpty()) {
                loadTeams(sex, ageGroup, data);

                data.addAll(disq);

                addDataToTable(String.valueOf(ageGroup.getId()) + sex, data);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTeams(String sex, AgeGroup ageGroup, Vector<Vector<String>> data) {

        List<hu.danielb.raceadmin.entity.Team> teams = makeTeams(data);
        Vector<Vector<String>> teamData = new Vector<>();
        try {
            for (int i = 0; i < teams.size(); i++) {
                for (int j = 0; j < teams.get(i).getMembers().size(); j++) {
                    ResultSet rs;
                    rs = DatabaseOld.runSql("select Contestant.*,School.name as " + COLUMN_SCHOOL_NAME + " from " + Contestant.TABLE + " inner join " + School.TABLE + " on Contestant." + COLUMN_SCHOOL_ID + "=School.id where Contestant.id = ?", DatabaseOld.QUERRY, String.valueOf(teams.get(i).getMembers().get(j).getId()));
                    while (rs.next()) {
                        teamData.add(new Vector<>(Arrays.asList(
                                new String[]{String.valueOf(rs.getInt("id")),
                                        String.valueOf(i + 1),
                                        String.valueOf(teams.get(i).getPoints(4)),
                                        String.valueOf(rs.getInt("position")),
                                        String.valueOf(rs.getInt("number")),
                                        rs.getString("name"),
                                        rs.getString(COLUMN_SCHOOL_NAME)})));
                    }

                }
            }
            String tableName = String.valueOf(ageGroup.getId()) + getTeamConst(sex);
            addDataToTable(tableName, teamData);
            JTable jt = tables.get(tableName);
            CellSpan cellAtt = (CellSpan) ((AttributiveCellTableModel) jt.getModel()).getCellAttribute();
            for (int i = 0; i < (teams.size() * 4); i = i + 4) {
                cellAtt.combine(new int[]{i, i + 1, i + 2, i + 3}, new int[]{1});
                cellAtt.combine(new int[]{i, i + 1, i + 2, i + 3}, new int[]{2});
                cellAtt.combine(new int[]{i, i + 1, i + 2, i + 3}, new int[]{6});
            }

            jt.clearSelection();
            jt.revalidate();
            jt.repaint();

        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<hu.danielb.raceadmin.entity.Team> makeTeams(Vector<Vector<String>> data) {
        HashMap<String, hu.danielb.raceadmin.entity.Team> tmp = new HashMap<>();
        for (Vector<String> data1 : data) {
            boolean added = false;
            int n = 0;
            do {
                String sch = data1.get(4) + n;
                if (tmp.containsKey(sch)) {
                    if (!tmp.get(sch).isFull()) {
                        tmp.get(sch).addMember(new Contestant(
                                Integer.parseInt(data1.get(0)),
                                Integer.parseInt(data1.get(1).isEmpty() ? "0" : data1.get(1)),
                                data1.get(3),
                                "",
                                Integer.parseInt(data1.get(2)),
                                new AgeGroup(0, "", 0, 0),
                                new School(0, data1.get(4)), 0));
                        added = true;
                    } else {
                        n++;
                    }
                } else {
                    tmp.put(sch, new hu.danielb.raceadmin.entity.Team(sch));
                    tmp.get(sch).addMember(new Contestant(
                            Integer.parseInt(data1.get(0)),
                            Integer.parseInt(data1.get(1).isEmpty() ? "0" : data1.get(1)),
                            data1.get(3),
                            "",
                            Integer.parseInt(data1.get(2)),
                            new AgeGroup(0, "", 0, 0),
                            new School(0, data1.get(4)), 0));
                    added = true;
                }
            } while (!added);
        }

        List<hu.danielb.raceadmin.entity.Team> teams = tmp.entrySet().stream().filter(entry -> entry.getValue().isFull()).map(Map.Entry::getValue).collect(Collectors.toList());

        boolean swapped;

        do {
            swapped = false;
            for (int i = 1; i < teams.size(); i++) {
                if (teams.get(i).lowerThan(teams.get(i - 1))) {
                    hu.danielb.raceadmin.entity.Team t = teams.get(i - 1);
                    teams.set(i - 1, teams.get(i));
                    teams.set(i, t);
                    swapped = true;
                }
            }
        } while (swapped);

        return teams;
    }

    private void addDataToTable(String tableName, Vector<Vector<String>> data) {
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);

        JTable currentTable = tables.get(tableName);
        if (tableName.contains(Constants.BOYTEAM) || tableName.contains(Constants.GIRLTEAM)) {
            currentTable.setModel(new AttributiveCellTableModel(data, new Vector<>(Arrays.asList(new String[]{"", "Helyezés", "Pontszám", "Egyéni", "Rajtszám", "Név", "School"}))) {
                @Override
                public boolean isCellEditable(int rowIndex, int colIndex) {
                    return false;
                }
            });
            setColumnWidth(currentTable, 0, 0);
            setColumnWidth(currentTable, 1, 60);
            setColumnWidth(currentTable, 2, 60);
            setColumnWidth(currentTable, 3, 60);
            setColumnWidth(currentTable, 4, 60);
            setColumnWidth(currentTable, 5, 180);
        } else {
            currentTable.setModel(new AttributiveCellTableModel(data, new Vector<>(Arrays.asList(new String[]{"", "Helyezés", "Rajtszám", "Név", "School"}))) {
                @Override
                public boolean isCellEditable(int rowIndex, int colIndex) {
                    return false;
                }
            });
            setColumnWidth(currentTable, ResultsTableModel.COLUMN_CONTESTANT_ID, 0);
            setColumnWidth(currentTable, ResultsTableModel.COLUMN_POSITION, 80);
            setColumnWidth(currentTable, ResultsTableModel.COLUMN_NUMBER, 80);
            setColumnWidth(currentTable, ResultsTableModel.COLUMN_NAME, 180);
        }
        currentTable.getTableHeader().setReorderingAllowed(false);
        currentTable.getTableHeader().setResizingAllowed(false);
        for (int i = 0; i < currentTable.getColumnCount(); i++) {
            currentTable.getColumnModel().getColumn(i).setCellRenderer(dtcr);
        }
        currentTable.setEnabled(false);
    }

    private String getTeamConst(String sex) {
        switch (sex) {
            case Constants.BOY:
                return Constants.BOYTEAM;
            case Constants.GIRL:
                return Constants.GIRLTEAM;
            default:
                throw new AssertionError();
        }
    }

    protected void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.WARNING_MESSAGE);
    }

    private void setColumnWidth(JTable table, int columnNumber, int size) {
        TableColumn tc = table.getColumnModel().getColumn(columnNumber);
        tc.setMaxWidth(size);
        tc.setMinWidth(size);
        tc.setPreferredWidth(size);
    }


    private List<Contestant> getByAgeGroupAndSex(int ageGroupId, String sex) throws SQLException {
        List<Contestant> contestants = Database.get().getContestantDao().queryBuilder()
                .where()
                .eq(Contestant.COLUMN_AGE_GROUP_ID, ageGroupId).and()
                .eq(Contestant.COLUMN_SEX, sex).and()
                .gt(Contestant.COLUMN_POSITION, 0)
                .query();
        contestants.addAll(Database.get().getContestantDao().queryBuilder()
                .where()
                .eq(Contestant.COLUMN_AGE_GROUP_ID, ageGroupId).and()
                .eq(Contestant.COLUMN_SEX, sex).and()
                .eq(Contestant.COLUMN_POSITION, 0)
                .query());

        return contestants;
    }

    public class Category {

        Individual individual = new Individual();
        Team team = new Team();

    }

    public class Individual {

        String name = "";
        List<Contestant> contestants = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class Team {

        String name = "";
        List<hu.danielb.raceadmin.entity.Team> teams = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
