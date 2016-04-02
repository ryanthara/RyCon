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

import de.ryanthara.ja.rycon.tools.elements.GSIBlock;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

    public boolean convertCSV2XLS() {
        return false;
    }

    public boolean convertCSV2XLSX() {
        return false;
    }

    public boolean convertCSVBaselStadt2XLS() {
        return false;
    }

    public boolean convertCSVBaselStadt2XLSX() {
        return false;
    }

    public boolean convertCadwork2XLS() {
        return false;
    }

    public boolean convertCadwork2XLSX() {
        return false;
    }

    public boolean convertGSI2XLS(String sheetName, boolean writeCommentRow) {

        // general preparation of the workbook
        workbook = new HSSFWorkbook();
        String safeName = WorkbookUtil.createSafeSheetName(sheetName);
        Sheet sheet1 = workbook.createSheet(safeName);
        Row row = null;
        Cell cell = null;

        // preparation of the read gsi file
        FileToolsLeicaGSI gsiTools = new FileToolsLeicaGSI(readStringLines);
        ArrayList<ArrayList<GSIBlock>> blocksInLines = gsiTools.getEncodedGSIBlocks();

        // write comment row
        if (writeCommentRow) {

        }

        short rowNumber = 0;

        // fill gsi content into rows and cells
        for (ArrayList<GSIBlock> blocksAsLines : blocksInLines) {

            // create a row
            row = sheet1.createRow(rowNumber);
            rowNumber++;

            short cellNumber = 0;
            for (GSIBlock block : blocksAsLines) {
                switch (block.getWordIndex()) {
                    /*
                    GENERAL
                    11	Point number (includes block number)
                    12	Instrument serial no
                    13	Instrument type
                    18	Time format 1: pos. 8-9 year, 10-11 sec, 12-14 msec
                    19	Time format 2 : pos, 8-9 month 10-11 day, 12-13 hour, 14-15 min
                     */

                    case 11:
                    case 19:
                        break;

                    /*
                    ANGLES
                    21	Horizontal Circle (Hz)
                    22	Vertical Angle (V)
                    25	Horizontal circle difference (Hz0-Hz)

                     */
                    case 21:
                    case 22:
                        break;
                    case 25:
                        break;

                    /*
                    DISTANCE
                    31	Slope Distance
                    32	Horizontal Distance
                    33	Height Difference
                     */
                    case 31:
                    case 33:
                        break;

                    /*
                    CODE BLOCK
                    41	Code number ( include block number)
                    42 – 49	Information 1-8
                     */
                    case 41:
                        break;
                    case 42:
                    case 49:
                        break;

                    /*
                    DISTANCE (additional information)
                    51	Constants(ppm, mm)
                    52	Number of measurements, standard deviation
                    53	Deviation
                    58	Signal strength
                    59	Reflector constant (1/10 mm)ppm
                     */
                    case 51:
                    case 53:
                        break;
                    case 58:
                    case 59:
                        break;

                    /*
                    POINT CODING
                    71	Point Code
                    72 – 79	Attribute 1-8
                     */
                    case 71:
                        break;
                    case 72:
                    case 79:
                        break;

                    /*
                    COORDINATES
                    81	Easting (Target)
                    82	Northing (Target)
                    83	Elevation (Target)
                    84	Station Easting (Eo)
                    85	Station Northing (No)
                    86	Station Elevation (Ho)
                    87	Reflector height (above ground)
                    88	Instrument height (above ground)
                     */
                    case 81:
                    case 88:
                        break;
                }

                cell = row.createCell(cellNumber);
                cellNumber++;
                cell.setCellValue(block.toPrintFormatCSV());

             }
        }

        return false;
    }

    public boolean convertGSI2XLSX(String sheetName) {
        workbook = new XSSFWorkbook();

        String safeName = WorkbookUtil.createSafeSheetName(sheetName);
        Sheet sheet1 = workbook.createSheet(safeName);

        // prepare table data


        return false;
    }

    public boolean convertK2XLS() {
        return false;
    }

    public boolean convertK2XLSX() {
        return false;
    }

    public boolean convertTXT2XLS() {
        return false;
    }

    public boolean convertTXT2XLSX() {
        return false;
    }

    public boolean convertTXTBaseStadt2XLS() {
        return false;
    }

    public boolean convertTXTBaseStadt2XLSX() {
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
