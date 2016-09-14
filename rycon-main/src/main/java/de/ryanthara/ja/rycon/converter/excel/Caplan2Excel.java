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

import de.ryanthara.ja.rycon.i18n.I18N;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;

/**
 * Created by sebastian on 13.09.16.
 */
public class Caplan2Excel {

    private ArrayList<String> readStringLines;
    private Workbook workbook;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public Caplan2Excel(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a K file element by element into an Excel file.
     *
     * @param isXLS           selector to distinguish between XLS and XLSX file extension
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion success
     */
    public boolean convertCaplan2Excel(boolean isXLS, String sheetName, boolean writeCommentRow) {
        // general preparation of the workbook
        if (isXLS) {
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
        short countColumns = 0;

        if (writeCommentRow) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(I18N.getCaplanColumnTyp("pointNumber"));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(I18N.getCaplanColumnTyp("easting"));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(I18N.getCaplanColumnTyp("northing"));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(I18N.getCaplanColumnTyp("height"));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(I18N.getCaplanColumnTyp("object"));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(I18N.getCaplanColumnTyp("attribute"));
        }

        for (String line : readStringLines) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            cellNumber = 0;

            if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                String s;

                if (line.length() >= 16) {
                    cell = row.createCell(cellNumber);
                    cell.setCellValue(line.substring(0, 16).trim());       // point number (no '*', ',' and ';'), column 1 - 16
                    cellNumber++;
                }

                if (line.length() >= 32) {
                    cell = row.createCell(cellNumber);

                    if (!(s = line.substring(20, 32).trim()).equals("")) {      // easting E, column 19-32
                        cell.setCellValue(Double.parseDouble(s));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (line.length() >= 46) {
                    cell = row.createCell(cellNumber);

                    if (!(s = line.substring(34, 46).trim()).equals("")) {      // northing N, column 33-46
                        cell.setCellValue(Double.parseDouble(s));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (line.length() >= 59) {
                    cell = row.createCell(cellNumber);

                    if (!(s = line.substring(48, 59).trim()).equals("")) {      // height H, column 47-59
                        cell.setCellValue(Double.parseDouble(s));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (line.length() >= 62) {
                    String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");

                    cell = row.createCell(cellNumber);
                    cell.setCellValue(lineSplit[0].trim());              // code is the same as object type, column 62...
                    cellNumber++;

                    for (int i = 1; i < lineSplit.length; i++) {
                        cell = row.createCell(cellNumber);
                        cell.setCellValue(lineSplit[i].trim());
                        cellNumber++;
                    }
                }

                if (cellNumber > countColumns) {
                    countColumns = cellNumber;
                }

            }
        }

        // adjust column width to fit the content
        for (int i = 0; i < countColumns; i++) {
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

} // end of Caplan2Excel
