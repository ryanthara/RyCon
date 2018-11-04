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

import de.ryanthara.ja.rycon.nio.FileFormat;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from the geodata server
 * Basel Landschaft (Switzerland) into Microsoft Excel files in XLS or XLSX format.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Excel {

    private static final Logger logger = LoggerFactory.getLogger(TxtBaselLandschaft2Excel.class.getName());

    private final List<String> lines;
    private Workbook workbook;

    /**
     * Creates a converter with a list for the read line based text files
     * from the geodata server Basel Landschaft (Switzerland).
     *
     * @param lines list with coordinate lines
     */
    public TxtBaselLandschaft2Excel(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a txt file from the geodata server Basel Landschaft (Switzerland) element by element into an Excel file.
     *
     * @param fileFormat      selector to distinguish between XLS and XLSX file extension
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
     * @return success conversion success
     */
    public boolean convert(FileFormat fileFormat, String sheetName, boolean writeCommentRow) {
        workbook = BaseToolsExcel.prepareWorkbook(fileFormat);

        String safeName = WorkbookUtil.createSafeSheetName(sheetName);
        Sheet sheet = workbook.createSheet(safeName);
        Row row;
        Cell cell;

        DataFormat format = workbook.createDataFormat();

        short rowNumber = 0;
        short cellNumber = 0;
        short countColumns = 0;

        if (writeCommentRow) {
            rowNumber = prepareCommentRow(sheet, rowNumber, cellNumber);
        }

        removeHeadLine();

        for (String line : lines) {
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
                    cell.setCellValue(StringUtils.parseDoubleValue(lineSplit[2]));
                    BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_3.getString());
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Y
                    cell.setCellValue(StringUtils.parseDoubleValue(lineSplit[3]));
                    BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_3.getString());
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Z
                    if (lineSplit[4].equalsIgnoreCase("NULL")) {
                        cell.setCellValue("NULL");
                    } else {
                        cell.setCellValue(StringUtils.parseDoubleValue(lineSplit[4]));
                        BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_3.getString());
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

                    cell.setCellValue(StringUtils.parseDoubleValue(lineSplit[3]));
                    BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_4.getString());
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Y
                    cell.setCellValue(StringUtils.parseDoubleValue(lineSplit[4]));
                    BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_4.getString());
                    cellNumber++;

                    cell = row.createCell(cellNumber);      // Z
                    if (lineSplit[5].equalsIgnoreCase("NULL")) {
                        cell.setCellValue("NULL");
                    } else {
                        cell.setCellValue(StringUtils.parseDoubleValue(lineSplit[5]));
                        BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_4.getString());
                    }

                    countColumns = 6;
                    break;

                default:
                    logger.trace("Line contains less or more tokens ({}) than needed or allowed.", lineSplit.length);
                    break;
            }
        }

        // adjust column width to fit the content
        for (int i = 0; i < countColumns; i++) {
            sheet.autoSizeColumn((short) i);
        }

        return rowNumber > 1;
    }

    private short prepareCommentRow(Sheet sheet, short rowNumber, short cellNumber) {
        Row row;
        Cell cell;
        row = sheet.createRow(rowNumber);
        rowNumber++;

        String[] lineSplit = lines.get(0).trim().split("\\t", -1);

        for (String description : lineSplit) {
            cell = row.createCell(cellNumber);
            cellNumber++;
            cell.setCellValue(description);
        }
        return rowNumber;
    }

    private void removeHeadLine() {
        lines.remove(0);
    }

    /**
     * Returns the Workbook for writing it to a file.
     *
     * @return Workbook
     */
    public Workbook getWorkbook() {
        return this.workbook;
    }

}
