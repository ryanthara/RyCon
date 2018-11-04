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
import java.util.Arrays;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files from the geodata
 * server Basel Stadt (Switzerland) into Microsoft Excel files in XLS or XLSX format.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2Excel {

    private static final Logger logger = LoggerFactory.getLogger(CsvBaselStadt2Excel.class.getName());

    private final List<String[]> lines;
    private Workbook workbook = null;

    /**
     * Creates a converter with a list for the read line based comma separated
     * values (CSV) file from the geodata server Basel Stadt (Switzerland).
     *
     * @param lines list with lines as string array
     */
    public CsvBaselStadt2Excel(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a Zeiss REC formatted file.
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

        if (writeCommentRow) {
            rowNumber = prepareCommentRow(sheet, rowNumber, cellNumber);
        }

        removeHeadLine();

        for (String[] csvLine : lines) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            cellNumber = 0;

            prepareCell(row, format, cellNumber, csvLine);
        }

        // adjust column width to fit the content
        for (int i = 0; i < lines.get(0).length; i++) {
            sheet.autoSizeColumn((short) i);
        }

        return rowNumber > 1;
    }

    private void prepareCell(Row row, DataFormat format, short cellNumber, String[] csvLine) {
        Cell cell;

        for (int i = 0; i < csvLine.length; i++) {
            cell = row.createCell(cellNumber);
            cellNumber++;

            switch (i) {
                case 0:
                case 1:
                    cell.setCellValue(csvLine[i]);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    if (csvLine[i].equalsIgnoreCase("")) {
                        cell.setCellValue(csvLine[i]);
                    } else {
                        cell.setCellValue(StringUtils.parseDoubleValue(csvLine[i]));
                        BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_3.getString());
                    }
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    cell.setCellValue(csvLine[i]);
                    break;

                default:
                    logger.trace("Line contains less or more tokens ({}) than needed or allowed.\n{}", csvLine.length, Arrays.toString(csvLine));
                    break;
            }
        }
    }

    private short prepareCommentRow(Sheet sheet, short rowNumber, short cellNumber) {
        Row row;
        Cell cell;
        row = sheet.createRow(rowNumber);
        rowNumber++;

        String[] commentLine = lines.get(0);

        for (String description : commentLine) {
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
