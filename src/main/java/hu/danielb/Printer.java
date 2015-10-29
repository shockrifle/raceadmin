/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * @author balu
 */
public class Printer implements Printable {

    private String title;
    private JTable table;
    private String[] header;
    private double firstPageOffset;
    private double firstPageHeight;
    private int numOfRowsPrinted;
    private int numOfPagesToPrint;
    private Graphics2D graphicToPrint;
    private int firstPageRows;
    private double tablesHeightBefore;
    private int lastRequestedPage = -1;
    private boolean isTeam;

    public Printer(JTable c) throws PrinterException {
        this("", c, null);
    }

    public Printer(String t, JTable c, String[] hd) throws PrinterException {
        this(t, c, hd, false);
    }

    public Printer(String t, JTable c, String[] hd, boolean team) throws PrinterException {
        title = t;
        table = c;
        header = hd;
        isTeam = team;
        table.clearSelection();
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(this);
        if (pj.printDialog()) {
            pj.print();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        graphicToPrint = (Graphics2D) graphics;

        graphicToPrint.setColor(Color.black);
        int fontHeight = graphicToPrint.getFontMetrics().getHeight();
        int fontDescent = graphicToPrint.getFontMetrics().getDescent();
        int fontAscent = graphicToPrint.getFontMetrics().getAscent();
        double currentDrawHeight = 0;

        //leave room for page number
        double pageHeight = pageFormat.getImageableHeight();
        double pageWidth = pageFormat.getImageableWidth();
        double tableWidth = (double) table.getColumnModel().getTotalColumnWidth();
        double scale = 1;
        if (tableWidth >= pageWidth) {
            scale = (pageWidth / tableWidth);
        }

        double headerHeightOnPage = (table.getTableHeader().getHeight()) * scale;
        double tableWidthOnPage = tableWidth * scale;

        graphicToPrint.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        String index = "- " + (pageIndex + 1) + " -";
        graphicToPrint.drawString(index, (int) pageWidth / 2 - graphicToPrint.getFontMetrics().stringWidth(index) / 2, (int) (pageHeight - fontDescent));//bottom center


        Font tmpFont = graphicToPrint.getFont();
        if (pageIndex < 1 && header != null) {
            graphicToPrint.setFont(new Font("New Times Roman", Font.BOLD, 14));

            currentDrawHeight += graphicToPrint.getFontMetrics().getLeading() + graphicToPrint.getFontMetrics().getAscent();
            graphicToPrint.drawString(header[0], 0, (float) currentDrawHeight);

            graphicToPrint.setFont(new Font("New Times Roman", Font.PLAIN, 14));

            for (int i = 1; i < header.length; i++) {
                currentDrawHeight += graphicToPrint.getFontMetrics().getLeading() + graphicToPrint.getFontMetrics().getAscent();
                graphicToPrint.drawString(header[i], 0, (float) currentDrawHeight);
            }
        }
        if (!title.isEmpty()) {
            graphicToPrint.setFont(new Font("New Times Roman", Font.ITALIC, 12));
            currentDrawHeight += graphicToPrint.getFontMetrics().getLeading() + graphicToPrint.getFontMetrics().getAscent();
            graphicToPrint.drawString(title, 0, (float) currentDrawHeight);
        }
        if (pageIndex < 1 && header != null || !title.isEmpty()) {
            currentDrawHeight += 3;
            firstPageOffset = currentDrawHeight;
        }
        graphicToPrint.setFont(tmpFont);


        double oneRowHeight = (double) (table.getRowHeight() + 1) * scale;
        if (isTeam) {
            oneRowHeight *= 4;
        }
        int numOfRowsOnAPage;

        if (pageIndex < 1) {
            numOfRowsOnAPage = (int) Math.floor((pageHeight - currentDrawHeight - headerHeightOnPage - fontHeight) / oneRowHeight);
            firstPageRows = numOfRowsOnAPage;

            double tableHeightForPage = oneRowHeight * (double) numOfRowsOnAPage;
            firstPageHeight = tableHeightForPage;

            graphicToPrint.translate(0f, headerHeightOnPage + currentDrawHeight);

            //If this piece of the table is smaller than the size available,
            //clip to the appropriate bounds. 

            graphicToPrint.setClip(0, (int) (oneRowHeight * numOfRowsPrinted),
                    (int) Math.ceil(tableWidthOnPage),
                    (int) Math.ceil(tableHeightForPage));

            graphicToPrint.scale(scale, scale);
            table.paint(graphicToPrint);
            graphicToPrint.scale(1 / scale, 1 / scale);
            if (pageIndex < 1) {
                graphicToPrint.translate(0f, -headerHeightOnPage);
            } else {
                graphicToPrint.translate(0f, -((-tableHeightForPage * (pageIndex - 1)) - firstPageHeight + headerHeightOnPage));
            }
            graphicToPrint.setClip(0, 0, (int) Math.ceil(tableWidthOnPage), (int) Math.ceil(headerHeightOnPage));
            graphicToPrint.scale(scale, scale);
            table.getTableHeader().paint(graphicToPrint);//paint header at top

            tablesHeightBefore = firstPageHeight;

        } else {

            numOfRowsOnAPage = (int) Math.floor((pageHeight - currentDrawHeight - headerHeightOnPage - fontHeight) / oneRowHeight);

            numOfRowsPrinted = firstPageRows + numOfRowsOnAPage * (pageIndex - 1);
            int rowsLeft = table.getRowCount() - (isTeam ? numOfRowsPrinted * 4 : numOfRowsPrinted);
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

            numOfPagesToPrint = (int) Math.ceil((double) (table.getRowCount() - firstPageRows) / (double) numOfRowsOnAPage) + 1;

            if (pageIndex >= numOfPagesToPrint) {
                return NO_SUCH_PAGE;
            }

            graphicToPrint.translate(0f, currentDrawHeight + headerHeightOnPage - tablesHeightBefore);

            graphicToPrint.setClip(0, (int) tablesHeightBefore,
                    (int) Math.ceil(tableWidthOnPage),
                    (int) Math.ceil(tableHeightForPage));

            graphicToPrint.scale(scale, scale);
            table.paint(graphicToPrint);
            graphicToPrint.scale(1 / scale, 1 / scale);
            if (pageIndex < 1) {
                graphicToPrint.translate(0f, -headerHeightOnPage);
            } else {
                graphicToPrint.translate(0f, -(headerHeightOnPage - tablesHeightBefore));
            }
            graphicToPrint.setClip(0, 0, (int) Math.ceil(tableWidthOnPage), (int) Math.ceil(headerHeightOnPage));
            graphicToPrint.scale(scale, scale);
            table.getTableHeader().paint(graphicToPrint);//paint header at top


        }

        return Printable.PAGE_EXISTS;
    }
}
