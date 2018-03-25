package hu.danielb.raceadmin.ui;


import hu.danielb.raceadmin.database.Database;
import hu.danielb.raceadmin.database.dao.SettingDao;
import hu.danielb.raceadmin.entity.AgeGroup;
import hu.danielb.raceadmin.entity.Contestant;
import hu.danielb.raceadmin.entity.School;
import hu.danielb.raceadmin.entity.Team;
import hu.danielb.raceadmin.ui.components.GenericTabbedPane;
import hu.danielb.raceadmin.ui.components.table.CellSpan;
import hu.danielb.raceadmin.ui.components.table.MultiSpanCellTable;
import hu.danielb.raceadmin.ui.components.table.tablemodels.AttributiveCellTableModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.ResultsTableModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.TeamResultsTableModel;
import hu.danielb.raceadmin.util.*;
import hu.danielb.raceadmin.util.Properties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainFrame extends javax.swing.JFrame {

    private Map<String, JTable> tables;
    private JDialog startupScreen;
    private javax.swing.JButton buttonFinisher;
    private javax.swing.JTabbedPane ageGroupPane;

    private MainFrame() {
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

        ageGroupPane = new javax.swing.JTabbedPane();
        buttonFinisher = new javax.swing.JButton();
        JMenu menuFile = new JMenu();
        JMenuItem menuItemPrint = new JMenuItem();
        JMenuItem menuItemImport = new JMenuItem();
        JMenuItem menuItemExport = new JMenuItem();
        JMenuItem menuItemExit = new JMenuItem();
        JMenu menuEdit = new JMenu();
        JMenuItem menuItemNewContestant = new JMenuItem();
        JMenuItem menuItemFinisher = new JMenuItem();
        JMenuItem menuItemContestants = new JMenuItem();
        JMenuItem menuItemSchools = new JMenuItem();
        JMenuItem menuItemCoaches = new JMenuItem();
        JMenuItem menuItemSettings = new JMenuItem();
        JMenu menuHelp = new JMenu();
        JMenuItem menuItemAbout = new JMenuItem();
        JMenuItem menuItemUserManual = new JMenuItem();
        JMenuBar menuBar = new JMenuBar();
        JPanel jPanel4 = new JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);

        initPane();

        buttonFinisher.setText("Beérkező");
        buttonFinisher.addActionListener(e -> buttonFinisherActionPerformed());

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
        menuItemPrint.addActionListener(e -> menuItemPrintActionPerformed());
        menuFile.add(menuItemPrint);

        menuItemImport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        menuItemImport.setText("Import");
        menuItemImport.addActionListener(e -> menuItemImportActionPerformed());
        menuFile.add(menuItemImport);

        menuItemExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        menuItemExport.setText("Export");
        menuItemExport.addActionListener(e -> menuItemExportActionPerformed());
        menuFile.add(menuItemExport);

        menuItemExit.setText("Kilépés");
        menuItemExit.addActionListener(e -> menuItemExitActionPerformed());
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuEdit.setText("Szerkesztés");

        menuItemNewContestant.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuItemNewContestant.setText("Versenyző hozzáadása");
        menuItemNewContestant.addActionListener(e -> menuItemNewContestantActionPerformed());
        menuEdit.add(menuItemNewContestant);

        menuItemFinisher.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        menuItemFinisher.setText("Beérkezők megadása");
        menuItemFinisher.addActionListener(e -> menuItemFinisherActionPerformed());
        menuEdit.add(menuItemFinisher);

        menuItemContestants.setText("Versenyzők");
        menuItemContestants.addActionListener(e -> menuItemContestantsActionPerformed());
        menuEdit.add(menuItemContestants);

        menuItemSchools.setText("Iskolák");
        menuItemSchools.addActionListener(e -> menuItemSchoolsActionPerformed());
        menuEdit.add(menuItemSchools);

        menuItemCoaches.setText("Edzők/Tanárok");
        menuItemCoaches.addActionListener(e -> menuItemCoachesActionPerformed());
        menuEdit.add(menuItemCoaches);

        menuItemSettings.setText("Beállítások");
        menuItemSettings.addActionListener(e -> menuItemSettingsActionPerformed());
        menuEdit.add(menuItemSettings);

        menuBar.add(menuEdit);

        menuHelp.setText("Súgó");

        menuItemUserManual.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuItemUserManual.setText("Súgó");
        menuItemUserManual.addActionListener(e -> menuItemUserManualActionPerformed());
        menuHelp.add(menuItemUserManual);

        menuItemAbout.setText("Névjegy");
        menuItemAbout.addActionListener(e -> menuItemAboutActionPerformed());
        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

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

    private void menuItemNewContestantActionPerformed() {
        JDialog addFrame = new AddContestantDialog(this);
        addFrame.setVisible(true);
        loadData();
    }

    private void menuItemExitActionPerformed() {
        this.dispose();
    }

    private void menuItemFinisherActionPerformed() {
        buttonFinisher.doClick();
    }

    private void buttonFinisherActionPerformed() {
        FinishingDialog dialog = new FinishingDialog(this);
        dialog.addFinishingListener(v -> loadData());
        dialog.setVisible(true);
    }

    private void menuItemPrintActionPerformed() {


        String[] headerString = new String[]{"", ""};
        try {
            SettingDao settingDao = Database.get().getSettingDao();
            headerString = new String[]{
                    settingDao.getPrintHeaderTitle(),
                    settingDao.getPrintHeaderSubtitle() + " " + DateUtils.formatDate(settingDao.getRaceDate())
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (headerString[0].length() != 0 && headerString[1].length() != 0) {
            GenericTabbedPane ageGroupTab = (GenericTabbedPane) ageGroupPane.getComponentAt(ageGroupPane.getSelectedIndex());
            AgeGroup ageGroup = (AgeGroup) ageGroupTab.getData();
            GenericTabbedPane.Tab tab = ageGroupTab.getTab(ageGroupTab.getSelectedIndex());

            try {
                if (tab.getId() == Constants.BOY_TEAM || tab.getId() == Constants.GIRL_TEAM) {
                    new Printer(ageGroup.getName() + ", " + tab.getTitle(), tables.get(ageGroup.getId() + (String) tab.getId()), headerString, true).print();
                } else {
                    new Printer(ageGroup.getName() + ", " + tab.getTitle(), tables.get(ageGroup.getId() + (String) tab.getId()), headerString).print();
                }
            } catch (PrinterException ex) {
                warn("Nyomtatási hiba!");
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            warn("Nem adott meg nyomtatási fejlécet!\n(Beállítások -> Nyomtatási fejléc)");
        }
    }

    private void menuItemContestantsActionPerformed() {
        ContestantsDialog dial = new ContestantsDialog(this);
        dial.setVisible(true);
        loadData();
    }

    private void menuItemSchoolsActionPerformed() {
        SchoolsDialog dial = new SchoolsDialog(this);
        dial.setVisible(true);
        loadData();
    }

    private void menuItemCoachesActionPerformed() {
        CoachesDialog dial = new CoachesDialog(this);
        dial.setVisible(true);
        loadData();
    }

    private void menuItemSettingsActionPerformed() {
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.setVisible(true);
        initPane();
        loadData();
    }

    private void menuItemImportActionPerformed() {

        final JFileChooser fc = new JFileChooser(Properties.getUserDir());
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("csv", "csv"));
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            LoadingDialog dialog = new LoadingDialog(this, "Importálás...");
            new Thread(() -> {
                CSVRecord rowToLogError = null;
                List<Contestant> contestants = new ArrayList<>();
                try {
                    CSVParser parse = CSVParser.parse(file, Charset.defaultCharset(),
                            CSVFormat.newFormat(';').withHeader());
                    List<CSVRecord> records = parse.getRecords();
                    dialog.setMax(records.size() * 2);
                    Map<Integer, Integer> numbers = new HashMap<>(records.size());
                    for (CSVRecord row : records) {
                        rowToLogError = row;
                        AgeGroup ageGroup = Database.get().getAgeGroupDao().queryForId(Integer.parseInt(row.get(Contestant.COLUMN_AGE_GROUP_ID)));
                        School school = Database.get().getSchoolDao().queryForId(Integer.parseInt(row.get(Contestant.COLUMN_SCHOOL_ID)));
                        int number = Integer.parseInt(row.get(Contestant.COLUMN_NUMBER));
                        int count = numbers.get(number) != null ? numbers.get(number) : 0;
                        int existsWithNumber = Database.get().getContestantDao().queryForEq(Contestant.COLUMN_NUMBER, number).size();
                        if (existsWithNumber > 0 || ++count > 1) {
                            throw new IllegalArgumentException("Már létezik versenyző ezzel a számmal!");
                        }
                        numbers.put(number, count);
                        if (school == null) {
                            throw new IllegalArgumentException("Iskola nem található!");
                        }
                        if (ageGroup == null) {
                            throw new IllegalArgumentException("Korosztály nem található!");
                        }

                        contestants.add(new Contestant(
                                0,
                                0,
                                row.get(Contestant.COLUMN_NAME),
                                row.get(Contestant.COLUMN_SEX),
                                number,
                                ageGroup,
                                school,
                                Integer.parseInt(row.get(Contestant.COLUMN_AGE))));

                        dialog.progress();
                    }
                    for (Contestant contestant : contestants) {
                        Database.get().getContestantDao().create(contestant);
                        dialog.progress();
                    }
                } catch (SQLException | IOException | IllegalArgumentException e) {
                    e.printStackTrace();
                    warn("Hiba történ importálás közben:\n" +
                            e.getLocalizedMessage() + "\nHiba helye\n" +
                            (rowToLogError != null ? rowToLogError.getRecordNumber() : "") +
                            ": " + (rowToLogError != null ? rowToLogError.toMap() : ""));
                }

                dialog.dispose();
            }).start();
            dialog.setVisible(true);
        }
    }

    private void menuItemExportActionPerformed() {

        LoadingDialog dialog = new LoadingDialog(this, "Exportálás...");
        new Thread() {
            @Override
            public void run() {
                List<Category> results = new ArrayList<>();

                String exportsPath = Properties.getExportsPath();
                String printHeaderTitle = "";
                String printHeaderSubtitle = "";
                String date = "";
                try {
                    printHeaderTitle = Database.get().getSettingDao().getPrintHeaderTitle();
                    printHeaderSubtitle = Database.get().getSettingDao().getPrintHeaderSubtitle();
                    date = DateUtils.formatDate(Database.get().getSettingDao().getRaceDate());


                    Database.get().getAgeGroupDao().queryForAll().stream().sorted().forEach(ageGroup -> {
                        try {
                            File exportDir = new File(exportsPath);
                            if (!exportDir.exists()) {
                                if (!exportDir.mkdirs()) {
                                    throw new IOException("Cannot create directory for exports at: " + exportDir.getAbsolutePath());
                                }
                            }
                            Category boyAgeGroup = new Category();
                            results.add(boyAgeGroup);
                            boyAgeGroup.individualCategory.name = ageGroup.getName() + " " + "Fiú, egyéni";
                            boyAgeGroup.teamCategory.name = ageGroup.getName() + " " + "Fiú, csapat";

                            boyAgeGroup.individualCategory.contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.BOY);
                            boyAgeGroup.teamCategory.teams.addAll(
                                    makeTeams(boyAgeGroup.individualCategory.contestants.stream().filter(contestant1 -> contestant1.getPosition() > 0).collect(Collectors.toList()),
                                            ageGroup.getTeamMinMembers(), ageGroup.getTeamMaxMembers(), true));

                            Category girlAgeGroup = new Category();
                            results.add(girlAgeGroup);
                            girlAgeGroup.individualCategory.name = ageGroup.getName() + " " + "Lány, egyéni";
                            girlAgeGroup.teamCategory.name = ageGroup.getName() + " " + "Lány, csapat";

                            girlAgeGroup.individualCategory.contestants = getByAgeGroupAndSex(ageGroup.getId(), Constants.GIRL);
                            girlAgeGroup.teamCategory.teams.addAll(
                                    makeTeams(girlAgeGroup.individualCategory.contestants.stream().filter(contestant1 -> contestant1.getPosition() > 0).collect(Collectors.toList()),
                                            ageGroup.getTeamMinMembers(), ageGroup.getTeamMaxMembers(), true));

                        } catch (SQLException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try (InputStream is = getClass().getResourceAsStream("/templates/result_template.xlsx")) {
                    try (OutputStream os = new FileOutputStream(exportsPath + File.separator + "export" + DateUtils.formatForFile(new Date()) + ".xlsx")) {
                        Context context = new Context();
                        context.putVar("results", results);
                        context.putVar("header", printHeaderTitle);
                        context.putVar("subheader", printHeaderSubtitle);
                        context.putVar("date", date);
                        JxlsHelper.getInstance().processTemplate(is, os, context);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dispose();
            }
        }.start();
        dialog.setVisible(true);

    }

    private void menuItemAboutActionPerformed() {
        new AboutDialog(this).setVisible(true);
    }

    private void menuItemUserManualActionPerformed() {
        try {
            Desktop.getDesktop().open(new File("Súgó.pdf"));
        } catch (IOException e) {
            e.printStackTrace();
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

        XlsCommentAreaBuilder.addCommandMapping(EachMergeCommand.COMMAND_NAME, EachMergeCommand.class);
        startupScreen = new StartupScreen(this, true, Properties.getVersion());
        try {
            Database.get();
            new Thread(() -> {
                tables = new HashMap<>();
                initComponents();
                loadData();

                startupScreen.dispose();
            }).start();
            startupScreen.setVisible(true);
        } catch (SQLException e) {
            warn("Hiba az adatok betöltése közben!:\n" + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initPane() {
        ageGroupPane.removeAll();
        try {
            Database.get().getAgeGroupDao().queryForAll().stream().sorted().forEach(ageGroup -> {
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
        new Thread(() -> {
            try {
                Database.get().getAgeGroupDao().queryForAll().stream().sorted(Comparator.comparingInt(AgeGroup::getMinimum)).forEach(MainFrame.this::updateData);
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

    private void updateData(AgeGroup ageGroup) {
        loadTable(Constants.BOY, ageGroup);
        loadTable(Constants.GIRL, ageGroup);
    }

    private void loadTable(String sex, AgeGroup ageGroup) {
        try {
            List<Contestant> data = getByAgeGroupAndSex(ageGroup.getId(), sex);
            if (!data.isEmpty()) {
                loadTeams(sex, ageGroup, data.stream().filter(contestant -> contestant.getPosition() > 0).collect(Collectors.toList()));
                addContestantDataToTable(String.valueOf(ageGroup.getId()) + sex, data);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTeams(String sex, AgeGroup ageGroup, List<Contestant> data) {

        int teamMaxMembers = ageGroup.getTeamMaxMembers();
        int teamMinMembers = ageGroup.getTeamMinMembers();
        List<Team> teams = makeTeams(data, teamMinMembers, teamMaxMembers);
        String tableName = String.valueOf(ageGroup.getId()) + getTeamConst(sex);
        addTeamDataToTable(tableName, teams);
        JTable jt = tables.get(tableName);
        CellSpan cellAtt = (CellSpan) ((AttributiveCellTableModel) jt.getModel()).getCellAttribute();

        int row = 0;
        for (Team team : teams) {
            int teamSize = team.getSize();
            int[] rowsArray = new int[teamSize];
            for (int j = 0; j < teamSize; j++) {
                rowsArray[j] = row + j;
            }
            cellAtt.combine(rowsArray, new int[]{TeamResultsTableModel.Column.POSITION.ordinal()});
            cellAtt.combine(rowsArray, new int[]{TeamResultsTableModel.Column.POINTS.ordinal()});
            cellAtt.combine(rowsArray, new int[]{TeamResultsTableModel.Column.SCHOOL_NAME.ordinal()});
            row += teamSize;
        }

        jt.clearSelection();
        jt.revalidate();
        jt.repaint();
    }

    private List<Team> makeTeams(List<Contestant> data, int minMembers, int maxMembers) {
        return makeTeams(data, minMembers, maxMembers, false);
    }

    private List<Team> makeTeams(List<Contestant> data, int minMembers, int maxMembers, boolean addPlaceholder) {
        HashMap<String, Team> teams = new HashMap<>();
        for (Contestant contestant : data) {
            boolean added = false;
            int n = 0;
            do {
                String teamName = contestant.getSchool().getNameWithSettlement() + (n > 0 ? " " + (char) ('A' + n) : "");
                if (teams.containsKey(teamName)) {
                    if (!teams.get(teamName).isFull()) {
                        teams.get(teamName).addMember(contestant);
                        added = true;
                    } else {
                        n++;
                    }
                } else {
                    teams.put(teamName, new Team(teamName, minMembers, maxMembers));
                    teams.get(teamName).addMember(contestant);
                    added = true;
                }
            } while (!added);
        }


        List<Team> teamList = teams.entrySet().stream()
                .filter(entry -> entry.getValue().isValid())
                .map(Map.Entry::getValue).collect(Collectors.toList());

        if (addPlaceholder) {
            teamList.forEach(team -> team.addMember(new Contestant()));
        }

        return teamList.stream().sorted().collect(Collectors.toList());

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

    private void warn(String msg) {
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
                .ne(Contestant.COLUMN_POSITION, 0)
                .query()
                .stream().sorted(Comparator.comparingInt(Contestant::getPosition)).collect(Collectors.toList());
        if (!Database.get().getSettingDao().getHideDisqualified()) {
            contestants.addAll(Database.get().getContestantDao().queryBuilder()
                    .where()
                    .eq(Contestant.COLUMN_AGE_GROUP_ID, ageGroupId).and()
                    .eq(Contestant.COLUMN_SEX, sex).and()
                    .eq(Contestant.COLUMN_POSITION, 0)
                    .query());
        }
        return contestants;
    }

    @SuppressWarnings("unused")
    public static class IndividualCategory {

        String name = "";
        List<Contestant> contestants = new ArrayList<>();

        public String getName() {
            return name;
        }

        public List<Contestant> getContestants() {
            return contestants;
        }
    }

    @SuppressWarnings("unused")
    public static class TeamCategory {

        String name = "";
        List<Team> teams = new ArrayList<>();

        public String getName() {
            return name;
        }

        public List<Team> getTeams() {
            return teams;
        }
    }

    @SuppressWarnings("unused")
    public class Category {

        IndividualCategory individualCategory = new IndividualCategory();
        TeamCategory teamCategory = new TeamCategory();

        public IndividualCategory getIndividualCategory() {
            return individualCategory;
        }

        public TeamCategory getTeamCategory() {
            return teamCategory;
        }
    }
}
