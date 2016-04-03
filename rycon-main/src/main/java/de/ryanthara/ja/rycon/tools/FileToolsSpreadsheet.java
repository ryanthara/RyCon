/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
 *
 * This package is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This package is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this package. If not, see <http://www.gnu.org/licenses/>.
 */

package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements basic operations on spreadsheets output operations like xls, xlsx, ... files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given files. Later on, there will be support for open document format too.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 * <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 9
 */
public class FileToolsSpreadsheet {

    private ArrayList<String> readStringLines;
    private List<String[]> readCSVLines;
    private Workbook workbook;

    /**
     * Member which helps distinguish between XLS and XLSX file format.
     */
    public static boolean isXLS = true;

    /**
     * Member which helps distinguish between XLS and XLSX file format.
     */
    public static boolean isXLSX = false;

    /**
     * Class Constructor with parameter.
     * <p>
     * As parameter the {@code ArrayList<String>} object with the lines in text format is used.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public FileToolsSpreadsheet(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Class constructor with parameter for the read lines as {@code List<String[]>} object.
     * <p>
     * This constructor is used for reading csv file lines.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public FileToolsSpreadsheet(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    public boolean convertCSV2Excel() {
        return false;
    }

    public boolean convertCSVBaselStadt2Excel() {
        return false;
    }

    public boolean convertCadwork2Excel() {
        return false;
    }

    public boolean convertGSI2Excel(boolean isExcel, String sheetName, boolean writeCommentRow) {

        // general preparation of the workbook
        if (isExcel) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }

        String safeName = WorkbookUtil.createSafeSheetName(sheetName);
        Sheet sheet = workbook.createSheet(safeName);
        Row row;
        Cell cell;
        CellStyle cellStyle;

        DataFormat format = workbook.createDataFormat();

        short rowNumber = 0;
        short cellNumber = 0;

        // preparation of the read gsi file
        FileToolsLeicaGSI gsiTools = new FileToolsLeicaGSI(readStringLines);
        ArrayList<ArrayList<GSIBlock>> blocksInLines = gsiTools.getEncodedGSIBlocks();

        // write comment row
        if (writeCommentRow) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            for (int wordIndex: gsiTools.getFoundWordIndices()) {
                cell = row.createCell(cellNumber);
                cellNumber++;

                cell.setCellValue(I18N.getWordIndexDescription(wordIndex));
            }
        }

        // fill gsi content into rows and cells
        for (ArrayList<GSIBlock> blocksAsLines : blocksInLines) {

            row = sheet.createRow(rowNumber);
            rowNumber++;

            cellNumber = 0;

            for (GSIBlock block : blocksAsLines) {

                cell = row.createCell(cellNumber);
                cellNumber++;

                switch (block.getWordIndex()) {
                    // GENERAL
                    case 11:    // Point number (includes block number)
                    case 12:    // Instrument serial no
                    case 13:    // Instrument type
                    case 18:    // Time format 1: pos. 8-9 year, 10-11 sec, 12-14 msec
                    case 19:    // Time format 2 : pos, 8-9 month 10-11 day, 12-13 hour, 14-15 min
                        cell.setCellValue(block.toPrintFormatCSV());
                        break;

                    // ANGLES
                    case 21:    // Horizontal Circle (Hz)
                    case 22:    // Vertical Angle (V)
                    case 25:    // Horizontal circle difference (Hz0-Hz)
                        cell.setCellValue(Double.parseDouble(block.toPrintFormatCSV()));
                        break;

                    // DISTANCE
                    case 31:    // Slope Distance
                    case 32:    // Horizontal Distance
                    case 33:    // Height Difference
                        cell.setCellValue(Double.parseDouble(block.toPrintFormatCSV()));
                        break;

                    // CODE BLOCK
                    case 41:    // Code number ( include block number)
                    case 42:    // Information 1
                    case 43:    // Information 2
                    case 44:    // Information 3
                    case 45:    // Information 4
                    case 46:    // Information 5
                    case 47:    // Information 6
                    case 48:    // Information 7
                    case 49:    // Information 8
                        cell.setCellValue(block.toPrintFormatCSV());
                        break;

                    // DISTANCE (additional information)
                    case 51:    // Constants(ppm, mm)
                    case 52:    // Number of measurements, standard deviation
                    case 53:    // Deviation
                    case 58:    // Signal strength
                    case 59:    // Reflector constant (1/10 mm)ppm
                        cell.setCellValue(block.toPrintFormatCSV());
                        break;

                    // POINT CODING
                    case 71:    // Point Code
                    case 72:    // Attribute 1
                    case 73:    // Attribute 2
                    case 74:    // Attribute 3
                    case 75:    // Attribute 4
                    case 76:    // Attribute 5
                    case 77:    // Attribute 6
                    case 78:    // Attribute 7
                    case 79:    // Attribute 8
                        cell.setCellValue(block.toPrintFormatCSV());
                        break;

                    // COORDINATES
                    case 81:    // Easting (Target)
                    case 82:    // Northing (Target)
                    case 83:    // Elevation (Target)
                    case 84:    // Station Easting (E0)
                    case 85:    // Station Northing (N0)
                    case 86:    // Station Elevation (H0)
                        cell.setCellValue(Double.parseDouble(block.toPrintFormatCSV()));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
                        break;
                    case 87:    // Reflector height (above ground)
                    case 88:    // Instrument height (above ground)
                        cell.setCellValue(Double.parseDouble(block.toPrintFormatCSV()));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
                        break;
                }

             }
        }

        //adjust column width to fit the content
        for (int i = 0; i < blocksInLines.size(); i++) {
            sheet.autoSizeColumn((short)i);
        }

        return false;
    }

    public boolean convertK2Excel() {
        return false;
    }

    public boolean convertTXT2Excel() {
        return false;
    }

    public boolean convertTXTBaseStadt2Excel() {
        return false;
    }

    public Workbook getWorkbook() {
        return this.workbook;
    }

    public boolean writeXLS(File writeFile) {
        try {
            FileOutputStream fileOut = new FileOutputStream(writeFile);
            workbook.write(fileOut);

            fileOut.close();
            return true;
        } catch (IOException e) {
            System.err.println("Error while writing XLS file to disk.");
            e.printStackTrace();
        }

        return false;
    }

    public boolean writeXLSX(File writeFile) {
        try {
            FileOutputStream fileOut = new FileOutputStream(writeFile);
            workbook.write(fileOut);

            fileOut.close();
            return true;
        } catch (IOException e) {
            System.err.println("Error while writing XLS file to disk.");
            e.printStackTrace();
        }
        return false;
    }

} // end of FileToolsSpreadsheet
