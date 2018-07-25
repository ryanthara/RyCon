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
package de.ryanthara.ja.rycon.core.converter.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;

/**
 * This class provides functions to convert coordinate files from the geodata server 'Basel Landschaft' (Switzerland)
 * into Microsoft Excel (XLS and XLSX) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Excel {

    private ArrayList<String> readStringLines;
    private Workbook workbook;

    /**
     * Class constructor for reader line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TxtBaselLandschaft2Excel(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a txt file from the geodata server Basel Landschaft (Switzerland) element by element into an Excel file.
     *
     * @param isXLS           selector to distinguish between XLS and XLSX file extension
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
     *
     * @return success conversion success
     */
    public boolean convertTXTBaselLand2Excel(boolean isXLS, String sheetName, boolean writeCommentRow) {
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

            String[] lineSplit = readStringLines.get(0).trim().split("\\t", -1);

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

            String[] lineSplit = line.trim().split("\\t", -1);

            cellNumber = 0;

            switch (lineSplit.length) {
                case 5:     // HFP file
                    cell = row.createCell(cellNumber);      // Art
                    cell.setCellValue(lineSplit[0]);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Number
                    cell.setCellValue(lineSplit[1]);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // X
                    cell.setCellValue(Double.parseDouble(lineSplit[2]));
                    cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                    cell.setCellStyle(cellStyle);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Y
                    cell.setCellValue(Double.parseDouble(lineSplit[3]));
                    cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                    cell.setCellStyle(cellStyle);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Z
                    if (lineSplit[4].equalsIgnoreCase("NULL")) {
                        cell.setCellValue("NULL");
                    } else {
                        cell.setCellValue(Double.parseDouble(lineSplit[4]));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                        cell.setCellStyle(cellStyle);
                    }

                    countColumns = 5;
                    break;

                case 6:     // LFP file
                    cell = row.createCell(cellNumber);      // Art
                    cell.setCellValue(lineSplit[0]);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Number
                    cell.setCellValue(lineSplit[1]);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // VArt
                    cell.setCellValue(lineSplit[2]);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // X
                    cell.setCellValue(Double.parseDouble(lineSplit[3]));
                    cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                    cell.setCellStyle(cellStyle);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Y
                    cell.setCellValue(Double.parseDouble(lineSplit[4]));
                    cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                    cell.setCellStyle(cellStyle);
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Z
                    if (lineSplit[5].equalsIgnoreCase("NULL")) {
                        cell.setCellValue("NULL");
                    } else {
                        cell.setCellValue(Double.parseDouble(lineSplit[5]));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                        cell.setCellStyle(cellStyle);
                    }

                    countColumns = 6;
                    break;

                default:
                    System.err.println("TxtBaselLandschaft2Excel.convertTXTBaselLand2Excel() : line contains less or more tokens " + line);
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

} // end of TxtBaselLandschaft2Excel
