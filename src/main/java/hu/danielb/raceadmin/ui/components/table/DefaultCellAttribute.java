package hu.danielb.raceadmin.ui.components.table;

import java.awt.*;
import java.util.Arrays;

public class DefaultCellAttribute implements CellAttribute, CellSpan {

    private int rowSize;
    private int columnSize;
    private int[][][] span;
    private Color[][] foreground;
    private Color[][] background;
    private Font[][] font;

    public DefaultCellAttribute(int numRows, int numColumns) {
        setSize(new Dimension(numColumns, numRows));
    }

    private void initValue() {
        for (int i = 0; i < span.length; i++) {
            for (int j = 0; j < span[i].length; j++) {
                span[i][j][COLUMN] = 1;
                span[i][j][ROW] = 1;
            }
        }
    }

    @Override
    public int[] getSpan(int row, int column) {
        if (isOutOfBounds(row, column)) {
            return new int[]{1, 1};
        }
        return span[row][column];
    }

    @Override
    public void setSpan(int[] span, int row, int column) {
        if (isOutOfBounds(row, column)) {
            return;
        }
        this.span[row][column] = Arrays.copyOf(span, span.length);
    }

    @Override
    public boolean isVisible(int row, int column) {
        return !isOutOfBounds(row, column) && !((span[row][column][COLUMN] < 1) || (span[row][column][ROW] < 1));
    }

    @Override
    public void combine(int[] rows, int[] columns) {
        if (isOutOfBounds(rows, columns)) {
            return;
        }
        int rowSpan = rows.length;
        int columnSpan = columns.length;
        int startRow = rows[0];
        int startColumn = columns[0];
        for (int i = 0; i < rowSpan; i++) {
            for (int j = 0; j < columnSpan; j++) {
                if ((span[startRow + i][startColumn + j][COLUMN] != 1)
                        || (span[startRow + i][startColumn + j][ROW] != 1)) {
                    return;
                }
            }
        }
        for (int i = 0, ii = 0; i < rowSpan; i++, ii--) {
            for (int j = 0, jj = 0; j < columnSpan; j++, jj--) {
                span[startRow + i][startColumn + j][COLUMN] = jj;
                span[startRow + i][startColumn + j][ROW] = ii;
            }
        }
        span[startRow][startColumn][COLUMN] = columnSpan;
        span[startRow][startColumn][ROW] = rowSpan;

    }

    @Override
    public void split(int row, int column) {
        if (isOutOfBounds(row, column)) {
            return;
        }
        int columnSpan = span[row][column][COLUMN];
        int rowSpan = span[row][column][ROW];
        for (int i = 0; i < rowSpan; i++) {
            for (int j = 0; j < columnSpan; j++) {
                span[row + i][column + j][COLUMN] = 1;
                span[row + i][column + j][ROW] = 1;
            }
        }
    }

    @Override
    public void addColumn() {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows][numColumns + 1][2];
        System.arraycopy(oldSpan, 0, span, 0, numRows);
        for (int i = 0; i < numRows; i++) {
            span[i][numColumns][COLUMN] = 1;
            span[i][numColumns][ROW] = 1;
        }
    }

    @Override
    public void addRow() {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows + 1][numColumns][2];
        System.arraycopy(oldSpan, 0, span, 0, numRows);
        for (int i = 0; i < numColumns; i++) {
            span[numRows][i][COLUMN] = 1;
            span[numRows][i][ROW] = 1;
        }
    }

    @Override
    public void insertRow(int row) {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows + 1][numColumns][2];
        if (0 < row) {
            System.arraycopy(oldSpan, 0, span, 0, row - 1);
        }
        System.arraycopy(oldSpan, 0, span, row, numRows - row);
        for (int i = 0; i < numColumns; i++) {
            span[row][i][COLUMN] = 1;
            span[row][i][ROW] = 1;
        }
    }

    @Override
    public Dimension getSize() {
        return new Dimension(rowSize, columnSize);
    }

    @Override
    public void setSize(Dimension size) {
        columnSize = size.width;
        rowSize = size.height;
        span = new int[rowSize][columnSize][2];
        foreground = new Color[rowSize][columnSize];
        background = new Color[rowSize][columnSize];
        font = new Font[rowSize][columnSize];
        initValue();
    }

    private boolean isOutOfBounds(int row, int column) {
        return (row < 0) || (rowSize <= row)
                || (column < 0) || (columnSize <= column);
    }

    private boolean isOutOfBounds(int[] rows, int[] columns) {
        for (int row : rows) {
            if ((row < 0) || (rowSize <= row)) {
                return true;
            }
        }
        for (int column : columns) {
            if ((column < 0) || (columnSize <= column)) {
                return true;
            }
        }
        return false;
    }

}
