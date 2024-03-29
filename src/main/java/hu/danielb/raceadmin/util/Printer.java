package hu.danielb.raceadmin.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;

import hu.danielb.raceadmin.ui.TableHolder;

public class Printer implements Printable {

    private static final String FONT = "Times New Roman";
    private final String mTitle;
    private final JTable mTable;
    private final boolean mIsTeam;
    private final JLabel mLabel;
    private String[] mHeader;
    private int numOfRowsPrinted;
    private int firstPageRows;

    public Printer(TableHolder tableToPrint) {
        this("", tableToPrint, null);
    }

    public Printer(String title, TableHolder tableToPrint, String[] header) {
        this(title, tableToPrint, header, false);
    }

    public Printer(String title, TableHolder tableToPrint, String[] header, boolean isTeam) {
        mTitle = title;
        mTable = tableToPrint.getTable();
        mLabel = tableToPrint.getSupervisor();
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
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        Graphics2D graphicToPrint = (Graphics2D) graphics;

        graphicToPrint.setColor(Color.black);
        int fontHeight = graphicToPrint.getFontMetrics().getHeight();
        int fontDescent = graphicToPrint.getFontMetrics().getDescent();
        double currentDrawHeight = 0;

        double pageHeight = pageFormat.getImageableHeight();
        double pageWidth = pageFormat.getImageableWidth();
        double tableWidth = mTable.getColumnModel().getTotalColumnWidth();
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
        if (pageIndex < 1 && mHeader != null && mHeader.length > 0) {
            graphicToPrint.setFont(new Font(FONT, Font.BOLD, 22));

            List<String> title = calculateSplits(pageWidth, graphicToPrint.getFontMetrics(), mHeader[0]);

            currentDrawHeight += getFontDrawHeight(graphicToPrint);
            for (String string : title) {
                currentDrawHeight += getFontDrawHeight(graphicToPrint);
                graphicToPrint.drawString(string, (float) (pageWidth / 2 - graphicToPrint.getFontMetrics().stringWidth(string) / 2), (float) currentDrawHeight);
            }
            double logoStartHeight = currentDrawHeight;

            graphicToPrint.setFont(new Font(FONT, Font.PLAIN, 18));

            currentDrawHeight += getFontDrawHeight(graphicToPrint);

            int subtitleHeight = 0;
            for (int i = 1; i < mHeader.length; i++) {
                List<String> subtitle = calculateSplits(pageWidth / 2, graphicToPrint.getFontMetrics(), mHeader[i]);
                for (String string : subtitle) {
                    subtitleHeight++;
                    currentDrawHeight += getFontDrawHeight(graphicToPrint);
                    graphicToPrint.drawString(string, (float) (pageWidth / 2 - graphicToPrint.getFontMetrics().stringWidth(string) / 2), (float) currentDrawHeight);
                }
            }
            currentDrawHeight += getFontDrawHeight(graphicToPrint);

            try {
                double maxLogoHeight = (2 + subtitleHeight) * getFontDrawHeight(graphicToPrint);
                double maxLogoWidth = pageWidth / 4;
                double maxLogoRatio = maxLogoHeight / maxLogoWidth;

                InputStream resource = this.getClass().getClassLoader().getResourceAsStream("images/hunyadi.png");
                if (resource != null) {
                    BufferedImage image = ImageIO.read(resource);

                    int w = image.getWidth(null);
                    int h = image.getHeight(null);

                    double ratio = (float) h / (float) w;
                    double rescaleRatio;
                    if (maxLogoRatio > ratio) {
                        rescaleRatio = maxLogoWidth / w;
                    } else {
                        rescaleRatio = maxLogoHeight / h;
                    }
                    int newW = (int) (w * rescaleRatio);
                    int newH = (int) (h * rescaleRatio);

                    graphicToPrint.drawImage(image, (int) pageWidth - newW - (int) (maxLogoWidth / 2 - newW / 2), (int) logoStartHeight, newW, newH, null);
                }
            } catch (IOException e) {
                Logger.getLogger(Printer.class.getName()).log(Level.WARNING, "cannot read logo", e);
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

        double tablesHeightBefore;
        int numOfRowsOnAPage = (int) Math.floor((pageHeight - currentDrawHeight - headerHeightOnPage - fontHeight) / oneRowHeight);
        if (pageIndex < 1) {
            firstPageRows = numOfRowsOnAPage;

            double tableHeightForPage = oneRowHeight * (double) numOfRowsOnAPage;

            graphicToPrint.translate(0f, headerHeightOnPage + currentDrawHeight);

            graphicToPrint.setClip(0, (int) (oneRowHeight * numOfRowsPrinted),
                    (int) Math.ceil(tableWidthOnPage),
                    (int) Math.ceil(tableHeightForPage));

            graphicToPrint.scale(scale, scale);
            mTable.paint(graphicToPrint);
            graphicToPrint.scale(1 / scale, 1 / scale);
            graphicToPrint.translate(0f, -headerHeightOnPage);
            graphicToPrint.setClip(0, 0, (int) Math.ceil(tableWidthOnPage), (int) Math.ceil(headerHeightOnPage));
            graphicToPrint.scale(scale, scale);
            mTable.getTableHeader().paint(graphicToPrint);
            String coachString = mLabel != null ? mLabel.getText() : "";
            graphicToPrint.drawString(coachString, (float) (tableWidth - graphicToPrint.getFontMetrics().stringWidth(coachString) - 2), (float) ((headerHeightOnPage / 2 + getFontDrawHeight(graphicToPrint) / 2)));

        } else {

            numOfRowsPrinted = firstPageRows + numOfRowsOnAPage * (pageIndex - 1);
            int rowsLeft = mTable.getRowCount() - (mIsTeam ? numOfRowsPrinted * 4 : numOfRowsPrinted);
            if (rowsLeft < 1) {
                return NO_SUCH_PAGE;
            }
            int numOfRowsOnThisPage = Math.min(rowsLeft, numOfRowsOnAPage);
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
            graphicToPrint.translate(0f, -(headerHeightOnPage - tablesHeightBefore));
            graphicToPrint.setClip(0, 0, (int) Math.ceil(tableWidthOnPage), (int) Math.ceil(headerHeightOnPage));
            graphicToPrint.scale(scale, scale);
            mTable.getTableHeader().paint(graphicToPrint);


        }

        return Printable.PAGE_EXISTS;
    }

    private java.util.List<String> calculateSplits(double pageWidth, FontMetrics metrics, String text) {
        java.util.List<String> splits = new ArrayList<>();
        String[] words = text.split(" ");
        for (String word : words) {
            if (splits.isEmpty()) {
                splits.add(word);
            } else {
                String split = splits.get(splits.size() - 1);
                String newSplit = split + " " + word;
                if (metrics.stringWidth(newSplit) < pageWidth) {
                    splits.set(splits.size() - 1, newSplit);
                } else {
                    splits.add(word);
                }
            }
        }
        return splits;
    }

    private int getFontDrawHeight(Graphics2D graphicToPrint) {
        return graphicToPrint.getFontMetrics().getLeading() + graphicToPrint.getFontMetrics().getAscent();
    }
}
