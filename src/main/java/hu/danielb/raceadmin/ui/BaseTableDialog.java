package hu.danielb.raceadmin.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import hu.danielb.raceadmin.ui.components.ButtonEditor;
import hu.danielb.raceadmin.ui.components.ButtonRenderer;
import hu.danielb.raceadmin.ui.components.table.tablemodels.BaseTableModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.ColumnModel;
import hu.danielb.raceadmin.ui.components.table.tablemodels.SortableAttributiveCellTableModel;

public abstract class BaseTableDialog<T> extends BaseDialog {
    private JPanel contentPane;
    private JTextField mSearchTextField;
    private JTable mTable;
    private JButton mNewButton;

    private boolean mSortBackwards = false;
    private int mSortBy = 0;
    private String mFilter = "";

    BaseTableDialog(Frame owner) {
        super(owner);
        init();
        setLocationRelativeTo(owner);
    }

    private void init() {
        initComponents();

        mTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnAtPoint = mTable.columnAtPoint(e.getPoint());
                if (getColumnModels().get(columnAtPoint).isSortable()) {
                    mSortBackwards = mSortBy == columnAtPoint && !mSortBackwards;
                    mSortBy = columnAtPoint;
                    loadData();
                }
            }
        });

        mNewButton.addActionListener(e -> newButtonActionPerformed());

        loadData();
    }

    private ColumnModel<T> getColumnModel(int i) {
        return getColumnModels().get(i);
    }

    private void initComponents() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);

        mSearchTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                textSearchKeyReleased(evt);
            }
        });

        pack();
    }

    private void textSearchKeyReleased(KeyEvent evt) {
        String find = mSearchTextField.getText().toLowerCase().trim();
        if (evt != null && (evt.getModifiers() == KeyEvent.CTRL_DOWN_MASK)) {
            return;
        }
        mFilter = find;
        loadData();
    }

    private void loadData() {

        List<T> data = getData();
        if (data != null) {

            data = filter(data)
                    .stream()
                    .sorted(getColumnModel(mSortBy).getComparator(mSortBackwards))
                    .collect(Collectors.toList());

            SortableAttributiveCellTableModel<T> tableModel = getTableModel(data);
            mTable.setModel(tableModel.setSortBy(mSortBy).setSortBackwards(mSortBackwards));
            getColumnModels().forEach(c -> setColumnWidth(c.getOrdinal(), c.getWidth()));

            List<ColumnModel<T>> buttons = getColumnModels().stream().filter(ColumnModel::isButton).collect(Collectors.toList());

            buttons.forEach(c -> setupButton(c.getOrdinal()));

            mTable.getTableHeader().setReorderingAllowed(false);
            mTable.getTableHeader().setResizingAllowed(false);

        }
    }

    protected abstract List<T> getData();

    protected abstract List<ColumnModel<T>> getColumnModels();

    protected abstract SortableAttributiveCellTableModel<T> getTableModel(List<T> data);

    private void setColumnWidth(int columnNumber, int size) {
        if (size > 0) {
            TableColumn tc = mTable.getColumnModel().getColumn(columnNumber);
            tc.setMaxWidth(size);
            tc.setMinWidth(size);
            tc.setPreferredWidth(size);
        }
    }

    private void setupButton(int column) {
        mTable.getColumnModel().getColumn(column).setCellRenderer(new ButtonRenderer());
        mTable.getColumnModel().getColumn(column).setCellEditor(new ButtonEditor(this::editButtonActionPerformed)
                .addEditingStoppedListener(this::loadData));
    }

    private List<T> filter(List<T> data) {
        if (!mFilter.isEmpty()) {
            data = data.stream().filter(getFilter(mFilter)).collect(Collectors.toList());
        }
        return data;
    }

    protected abstract Predicate<T> getFilter(String filter);

    @SuppressWarnings("unchecked")
    private void editButtonActionPerformed(ActionEvent evt) {
        int row = evt.getID();
        TableModel model = mTable.getModel();

        BaseDialog dialog = getEditDialog(((BaseTableModel<T>) model).getDataAt(row));

        dialog.setVisible(true);
        mSearchTextField.setText("");
    }

    private void newButtonActionPerformed() {
        BaseDialog dialog = getEditDialog();

        dialog.setVisible(true);
        mSearchTextField.setText("");
        loadData();
    }

    protected abstract BaseDialog getEditDialog();

    protected abstract BaseDialog getEditDialog(T data);

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
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMaximumSize(new Dimension(800, 600));
        contentPane.setMinimumSize(new Dimension(800, 600));
        contentPane.setPreferredSize(new Dimension(800, 600));
        contentPane.setRequestFocusEnabled(true);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mSearchTextField = new JTextField();
        panel1.add(mSearchTextField, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Keresés: ");
        panel1.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mNewButton = new JButton();
        mNewButton.setText("Új");
        panel1.add(mNewButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mTable = new JTable();
        scrollPane1.setViewportView(mTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
