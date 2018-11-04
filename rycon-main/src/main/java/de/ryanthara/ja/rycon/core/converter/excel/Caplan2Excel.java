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

import de.ryanthara.ja.rycon.core.elements.CaplanBlock;
import de.ryanthara.ja.rycon.i18n.Column;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.nio.FileFormat;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;

import java.util.ArrayList;
import java.util.List;

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.COLUMN_NAME;

/**
 * A converter with functions to convert coordinate coordinate files from
 * Caplan K program into Microsoft Excel files in XLS or XLSX format.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2Excel {

    private final List<String> lines;
    private Workbook workbook;

    /**
     * Creates a converter with a list for the read line based Caplan K file.
     *
     * @param lines list with Caplan K formatted lines
     */
    public Caplan2Excel(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a Caplan K file element by element into a Microsoft Excel file.
     *
     * @param fileFormat      distinguish between XLS and XLSX file format
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

        for (String line : lines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                row = sheet.createRow(rowNumber);
                rowNumber++;

                cellNumber = 0;

                CaplanBlock caplanBlock = new CaplanBlock(line);

                if (caplanBlock.getNumber() != null) {
                    cell = row.createCell(cellNumber);
                    cell.setCellValue(caplanBlock.getNumber());
                    cellNumber++;
                }

                if (caplanBlock.getEasting() != null) {
                    cell = row.createCell(cellNumber);

                    if (!caplanBlock.getEasting().equals("")) {
                        cell.setCellValue(StringUtils.parseDoubleValue(caplanBlock.getEasting()));
                        BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_4.getString());
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (caplanBlock.getNorthing() != null) {
                    cell = row.createCell(cellNumber);

                    if (!caplanBlock.getNorthing().equals("")) {
                        cell.setCellValue(StringUtils.parseDoubleValue(caplanBlock.getNorthing()));
                        BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_4.getString());
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (caplanBlock.getHeight() != null) {
                    cell = row.createCell(cellNumber);

                    if (!caplanBlock.getHeight().equals("")) {
                        cell.setCellValue(StringUtils.parseDoubleValue(caplanBlock.getHeight()));
                        BaseToolsExcel.setCellStyle(workbook, cell, format, Format.DIGITS_4.getString());
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (caplanBlock.getCode() != null) {
                    cell = row.createCell(cellNumber);
                    cell.setCellValue(caplanBlock.getCode());
                    cellNumber++;

                    if (caplanBlock.getAttributes().size() > 0) {
                        for (String attribute : caplanBlock.getAttributes()) {
                            cell = row.createCell(cellNumber);
                            cell.setCellValue(attribute);
                            cellNumber++;
                        }
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

    private short prepareCommentRow(Sheet sheet, short rowNumber, short cellNumber) {
        Row row;
        Cell cell;
        row = sheet.createRow(rowNumber);
        rowNumber++;

        cell = row.createCell(cellNumber);
        cell.setCellValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Column.pointNumber));
        cellNumber++;

        cell = row.createCell(cellNumber);
        cell.setCellValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Column.easting));
        cellNumber++;

        cell = row.createCell(cellNumber);
        cell.setCellValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Column.northing));
        cellNumber++;

        cell = row.createCell(cellNumber);
        cell.setCellValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Column.height));
        cellNumber++;

        cell = row.createCell(cellNumber);
        cell.setCellValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Column.object));
        cellNumber++;

        cell = row.createCell(cellNumber);
        cell.setCellValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Column.attribute));
        return rowNumber;
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
