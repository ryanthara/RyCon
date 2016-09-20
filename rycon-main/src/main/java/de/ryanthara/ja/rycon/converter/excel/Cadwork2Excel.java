/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.converter.excel
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
package de.ryanthara.ja.rycon.converter.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;

/**
 * This class provides functions to convert coordinate files from Cadwork CAD program into Microsoft Excel files
 * in XLS or XLSX format.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2Excel {

    private ArrayList<String> readStringLines;
    private Workbook workbook;

    /**
     * Class constructor for read line based text files from Cadwork CAD program in node.dat file format.
     *
     * @param readStringLines {@code ArrayList<String>} with read lines from node.dat file
     */
    public Cadwork2Excel(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a Microsoft Excel file.
     *
     * @param isXLS     selector to distinguish between XLS and XLSX file extension
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success of the conversion
     */
    public boolean convertCadwork2Excel(boolean isXLS, String sheetName, boolean writeCommentRow) {
        // general preparation of the workbook
        if (isXLS) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }

        String safeName = WorkbookUtil.createSafeSheetName(sheetName);
        String[] lineSplit;
        Sheet sheet = workbook.createSheet(safeName);
        Row row;
        Cell cell;

        short rowNumber = 0;
        short cellNumber = 0;

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        if (writeCommentRow) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            lineSplit = readStringLines.get(0).trim().split("\\t", -1);

            for (String description : lineSplit) {
                cell = row.createCell(cellNumber);
                cellNumber++;
                cell.setCellValue(description);
            }
        }

        // remove furthermore the still not needed comment line
        readStringLines.remove(0);

        for (String line : readStringLines) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            cellNumber = 0;

            lineSplit = line.trim().split("\\t", -1);

            cell = row.createCell(cellNumber);      // No
            cell.setCellValue(lineSplit[0]);
            cellNumber++;

            cell = row.createCell(cellNumber);      // X
            cell.setCellValue(lineSplit[1]);
            cellNumber++;

            cell = row.createCell(cellNumber);      // Y
            cell.setCellValue(lineSplit[2]);
            cellNumber++;

            cell = row.createCell(cellNumber);      // Z
            cell.setCellValue(lineSplit[3]);
            cellNumber++;

            cell = row.createCell(cellNumber);      // Code
            cell.setCellValue(lineSplit[4]);
            cellNumber++;

            cell = row.createCell(cellNumber);      // Name
            cell.setCellValue(lineSplit[5]);
        }

        // adjust column width to fit the content
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn((short) i);
        }

        return rowNumber > 1;
    }

    /**
     * Returns the Workbook for writing it to a file.
     *
     * @return Workbook
     */
    public Workbook getWorkbook() {
        return this.workbook;
    }

} // end of Cadwork2Excel
