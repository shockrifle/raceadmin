package hu.danielb.raceadmin.ui;


import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.PrintHeader;
import hu.danielb.raceadmin.entity.Team;
import hu.danielb.raceadmin.ui.components.GenericTabbedPane;
import hu.danielb.raceadmin.ui.components.PrintHeaderMenuItem;
import hu.danielb.raceadmin.ui.components.table.CellSpan;
import hu.danielb.raceadmin.ui.components.table.MultiSpanCellTable;
import hu.danielb.raceadmin.ui.components.table.models.AttributiveCellTableModel;
import hu.danielb.raceadmin.ui.components.table.models.ResultsTableModel;
import hu.danielb.raceadmin.ui.components.table.models.TeamResultsTableModel;
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
                if (tab.getId() == Constants.BOY_TEAM || tab.getId() == Constants.GIRL_TEAM) {
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
                    boyAgeGroup.individualCategory.name = ageGroup.getName() + " " + "Fiú, egyéni";
                    boyAgeGroup.teamCategory.name = ageGroup.getName() + " " + "Fiú, csapat";

                    List<Contestant> contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.BOY);
                    contestants.forEach(boyAgeGroup.individualCategory.contestants::add);

                    contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.BOY);
                    boyAgeGroup.teamCategory.teams.addAll(makeTeams(contestants.stream().filter(contestant1 -> contestant1.getPosition() > 0).collect(Collectors.toList())));

                    Category girlAgeGroup = new Category();
                    result.add(girlAgeGroup);
                    girlAgeGroup.individualCategory.name = ageGroup.getName() + " " + "Lány, egyéni";
                    girlAgeGroup.teamCategory.name = ageGroup.getName() + " " + "Lány, csapat";

                    contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.GIRL);
                    contestants.forEach(girlAgeGroup.individualCategory.contestants::add);

                    contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.GIRL);
                    girlAgeGroup.teamCategory.teams.addAll(makeTeams(contestants.stream().filter(contestant1 -> contestant1.getPosition() > 0).collect(Collectors.toList())));

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
            Database.get();
            new Thread() {
                @Override
                public void run() {
                    tables = new HashMap<>();
                    initComponents();
                    loadData();

                    startupScreen.dispose();
                }
            }.start();
            startupScreen.setVisible(true);
        } catch (SQLException e) {
            warn("Hiba az adatok betöltése közben!:\n" + e.getLocalizedMessage());
            e.printStackTrace();
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
                ageGroupTab.addTab(Constants.BOY_TEAM, "Fiú Csapat", createTable(ageGroupId + Constants.BOY_TEAM, new Dimension(ageGroupTab.getHeight(), ageGroupTab.getWidth())));
                ageGroupTab.addTab(Constants.GIRL_TEAM, "Lány Csapat", createTable(ageGroupId + Constants.GIRL_TEAM, new Dimension(ageGroupTab.getHeight(), ageGroupTab.getWidth())));

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
            List<Contestant> data = new ArrayList<>();
            List<Contestant> disqualified = new ArrayList<>();
            contestants.forEach(contestant -> {
                if (contestant.getPosition() > 0) {
                    data.add(contestant);
                } else {
                    disqualified.add(contestant);
                }
            });

            if (!data.isEmpty() || !disqualified.isEmpty()) {
                loadTeams(sex, ageGroup, data);

                data.addAll(disqualified);

                addContestantDataToTable(String.valueOf(ageGroup.getId()) + sex, data);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTeams(String sex, AgeGroup ageGroup, List<Contestant> data) {

        List<Team> teams = makeTeams(data);
        String tableName = String.valueOf(ageGroup.getId()) + getTeamConst(sex);
        addTeamDataToTable(tableName, teams);
        JTable jt = tables.get(tableName);
        CellSpan cellAtt = (CellSpan) ((AttributiveCellTableModel) jt.getModel()).getCellAttribute();
        for (int i = 0; i < (teams.size() * Team.MAX_MEMBERS); i = i + Team.MAX_MEMBERS) {
            int[] rowsArray = new int[Team.MAX_MEMBERS];
            for (int j = 0; j < Team.MAX_MEMBERS; j++) {
                rowsArray[j] = i + j;
            }
            cellAtt.combine(rowsArray, new int[]{TeamResultsTableModel.Column.POSITION.ordinal()});
            cellAtt.combine(rowsArray, new int[]{TeamResultsTableModel.Column.POINTS.ordinal()});
            cellAtt.combine(rowsArray, new int[]{TeamResultsTableModel.Column.SCHOOL_NAME.ordinal()});
        }

        jt.clearSelection();
        jt.revalidate();
        jt.repaint();


    }

    private List<Team> makeTeams(List<Contestant> data) {
        HashMap<String, Team> teams = new HashMap<>();
        for (Contestant contestant : data) {
            boolean added = false;
            int n = 0;
            do {
                String teamName = contestant.getSchool().getName() + n;
                if (teams.containsKey(teamName)) {
                    if (!teams.get(teamName).isFull()) {
                        teams.get(teamName).addMember(contestant);
                        added = true;
                    } else {
                        n++;
                    }
                } else {
                    teams.put(teamName, new Team(teamName));
                    teams.get(teamName).addMember(contestant);
                    added = true;
                }
            } while (!added);
        }


        return teams.entrySet().stream()
                .filter(entry -> entry.getValue().isFull())
                .map(Map.Entry::getValue).collect(Collectors.toList())
                .stream()
                .sorted().collect(Collectors.toList());
    }

    private void addTeamDataToTable(String tableName, List<Team> data) {

        JTable currentTable = tables.get(tableName);

        currentTable.setModel(new TeamResultsTableModel(data));
        setColumnWidth(currentTable, TeamResultsTableModel.Column.POSITION.ordinal(), 60);
        setColumnWidth(currentTable, TeamResultsTableModel.Column.POINTS.ordinal(), 60);
        setColumnWidth(currentTable, TeamResultsTableModel.Column.INDIVIDUAL_POSITION.ordinal(), 60);
        setColumnWidth(currentTable, TeamResultsTableModel.Column.NUMBER.ordinal(), 60);
        setColumnWidth(currentTable, TeamResultsTableModel.Column.NAME.ordinal(), 180);

        setupTable(currentTable);
    }

    private void addContestantDataToTable(String tableName, List<Contestant> data) {

        JTable currentTable = tables.get(tableName);

        currentTable.setModel(new ResultsTableModel(data));
        setColumnWidth(currentTable, ResultsTableModel.Column.POSITION.ordinal(), 80);
        setColumnWidth(currentTable, ResultsTableModel.Column.NUMBER.ordinal(), 80);
        setColumnWidth(currentTable, ResultsTableModel.Column.NAME.ordinal(), 180);

        setupTable(currentTable);
    }

    private void setupTable(JTable table) {
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        for (int i = 0; i < table.getColumnCount(); i++) {
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
            cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        table.setEnabled(false);
    }

    private String getTeamConst(String sex) {
        switch (sex) {
            case Constants.BOY:
                return Constants.BOY_TEAM;
            case Constants.GIRL:
                return Constants.GIRL_TEAM;
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
        return Database.get().getContestantDao().queryBuilder()
                .where()
                .eq(Contestant.COLUMN_AGE_GROUP_ID, ageGroupId).and()
                .eq(Contestant.COLUMN_SEX, sex)
                .query()
                .stream().sorted((o1, o2) -> Integer.compare(o1.getPosition(), o2.getPosition())).collect(Collectors.toList());
    }

    public class Category {

        IndividualCategory individualCategory = new IndividualCategory();
        TeamCategory teamCategory = new TeamCategory();

    }

    public class IndividualCategory {

        String name = "";
        List<Contestant> contestants = new ArrayList<>();

        public String getName() {
            return name;
        }

    }

    public class TeamCategory {

        String name = "";
        List<Team> teams = new ArrayList<>();

        public String getName() {
            return name;
        }

    }
}
