package hu.danielb.raceadmin.util;

import hu.danielb.raceadmin.ui.TableHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Arrays;

public class Printer implements Printable {

    private static final String FONT = "Times New Roman";
    private String mTitle;
    private JTable mTable;
    private String[] mHeader;
    private int numOfRowsPrinted;
    private int firstPageRows;
    private boolean mIsTeam;
    private JLabel mLabel;

    public Printer(TableHolder tableToPrint) {
        this("", tableToPrint, null);
    }

    public Printer(String title, TableHolder tableToPrint, String[] header) {
        this(title, tableToPrint, header, false);
    }

    public Printer(String title, TableHolder tableToPrint, String[] header, boolean isTeam) {
        mTitle = title;
        mTable = tableToPrint.getTable();
        mLabel = tableToPrint.getCoach();
        if (header != null) {
            mHeader = Arrays.copyOf(header, header.length);
        }
        mIsTeam = isTeam;
        mTable.clearSelection();
    }

    public void print() throws PrinterException {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(this);
        if (pj.printDialog()) {
            pj.print();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D graphicToPrint = (Graphics2D) graphics;

        graphicToPrint.setColor(Color.black);
        int fontHeight = graphicToPrint.getFontMetrics().getHeight();
        int fontDescent = graphicToPrint.getFontMetrics().getDescent();
        double currentDrawHeight = 0;

        double pageHeight = pageFormat.getImageableHeight();
        double pageWidth = pageFormat.getImageableWidth();
        double tableWidth = (double) mTable.getColumnModel().getTotalColumnWidth();
        double scale = 1;
        if (tableWidth >= pageWidth) {
            scale = (pageWidth / tableWidth);
        }

        double headerHeightOnPage = (mTable.getTableHeader().getHeight()) * scale;
        double tableWidthOnPage = tableWidth * scale;

        graphicToPrint.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        String index = "- " + (pageIndex + 1) + " -";
        graphicToPrint.drawString(index, (int) pageWidth / 2 - graphicToPrint.getFontMetrics().stringWidth(index) / 2, (int) (pageHeight - fontDescent));


        Font tmpFont = graphicToPrint.getFont();
        if (pageIndex < 1 && mHeader != null) {
            graphicToPrint.setFont(new Font(FONT, Font.BOLD, 14));

            currentDrawHeight += getFontDrawHeight(graphicToPrint);
            graphicToPrint.drawString(mHeader[0], 0, (float) currentDrawHeight);

            graphicToPrint.setFont(new Font(FONT, Font.PLAIN, 14));

            for (int i = 1; i < mHeader.length; i++) {
                currentDrawHeight += getFontDrawHeight(graphicToPrint);
                graphicToPrint.drawString(mHeader[i], 0, (float) currentDrawHeight);
            }
        }
        if (!mTitle.isEmpty()) {
            graphicToPrint.setFont(new Font(FONT, Font.ITALIC, 12));
            currentDrawHeight += getFontDrawHeight(graphicToPrint);
            graphicToPrint.drawString(mTitle, 0, (float) currentDrawHeight);
        }
        if (pageIndex < 1 && mHeader != null || !mTitle.isEmpty()) {
            currentDrawHeight += 3;
        }
        graphicToPrint.setFont(tmpFont);


        double oneRowHeight = (double) (mTable.getRowHeight() + 1) * scale;
        if (mIsTeam) {
            oneRowHeight *= 4;
        }
        int numOfRowsOnAPage;

        double tablesHeightBefore;
        if (pageIndex < 1) {
            numOfRowsOnAPage = (int) Math.floor((pageHeight - currentDrawHeight - headerHeightOnPage - fontHeight) / oneRowHeight);
            firstPageRows = numOfRowsOnAPage;

            double tableHeightForPage = oneRowHeight * (double) numOfRowsOnAPage;

            graphicToPrint.translate(0f, headerHeightOnPage + currentDrawHeight);

            graphicToPrint.setClip(0, (int) (oneRowHeight * numOfRowsPrinted),
                    (int) Math.ceil(tableWidthOnPage),
                    (int) Math.ceil(tableHeightForPage));

            graphicToPrint.scale(scale, scale);
            mTable.paint(graphicToPrint);
            graphicToPrint.scale(1 / scale, 1 / scale);
            if (pageIndex < 1) {
                graphicToPrint.translate(0f, -headerHeightOnPage);
            } else {
                graphicToPrint.translate(0f, -((-tableHeightForPage * (pageIndex - 1)) - tableHeightForPage + headerHeightOnPage));
            }
            graphicToPrint.setClip(0, 0, (int) Math.ceil(tableWidthOnPage), (int) Math.ceil(headerHeightOnPage));
            graphicToPrint.scale(scale, scale);
            mTable.getTableHeader().paint(graphicToPrint);
            String coachString = mLabel != null ? mLabel.getText() : "";
            graphicToPrint.drawString(coachString, (float) (tableWidth - graphicToPrint.getFontMetrics().stringWidth(coachString) - 2), (float) ((headerHeightOnPage / 2 + getFontDrawHeight(graphicToPrint) / 2)));

        } else {

            numOfRowsOnAPage = (int) Math.floor((pageHeight - currentDrawHeight - headerHeightOnPage - fontHeight) / oneRowHeight);

            numOfRowsPrinted = firstPageRows + numOfRowsOnAPage * (pageIndex - 1);
            int rowsLeft = mTable.getRowCount() - (mIsTeam ? numOfRowsPrinted * 4 : numOfRowsPrinted);
            if (rowsLeft < 1) {
                return NO_SUCH_PAGE;
            }
            int numOfRowsOnThisPage;
            if (rowsLeft > numOfRowsOnAPage) {
                numOfRowsOnThisPage = numOfRowsOnAPage;
            } else {
                numOfRowsOnThisPage = rowsLeft;
            }
            tablesHeightBefore = oneRowHeight * (double) (numOfRowsPrinted);

            double tableHeightForPage = oneRowHeight * (double) (numOfRowsOnThisPage);

            int numOfPagesToPrint = (int) Math.ceil((double) (mTable.getRowCount() - firstPageRows) / (double) numOfRowsOnAPage) + 1;

            if (pageIndex >= numOfPagesToPrint) {
                return NO_SUCH_PAGE;
            }

            graphicToPrint.translate(0f, currentDrawHeight + headerHeightOnPage - tablesHeightBefore);

            graphicToPrint.setClip(0, (int) tablesHeightBefore,
                    (int) Math.ceil(tableWidthOnPage),
                    (int) Math.ceil(tableHeightForPage));

            graphicToPrint.scale(scale, scale);
            mTable.paint(graphicToPrint);
            graphicToPrint.scale(1 / scale, 1 / scale);
            if (pageIndex < 1) {
                graphicToPrint.translate(0f, -headerHeightOnPage);
            } else {
                graphicToPrint.translate(0f, -(headerHeightOnPage - tablesHeightBefore));
            }
            graphicToPrint.setClip(0, 0, (int) Math.ceil(tableWidthOnPage), (int) Math.ceil(headerHeightOnPage));
            graphicToPrint.scale(scale, scale);
            mTable.getTableHeader().paint(graphicToPrint);


        }

        return Printable.PAGE_EXISTS;
    }

    private int getFontDrawHeight(Graphics2D graphicToPrint) {
        return graphicToPrint.getFontMetrics().getLeading() + graphicToPrint.getFontMetrics().getAscent();
    }
}
