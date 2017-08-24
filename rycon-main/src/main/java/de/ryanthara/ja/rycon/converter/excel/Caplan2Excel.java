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

import de.ryanthara.ja.rycon.elements.CaplanBlock;
import de.ryanthara.ja.rycon.i18n.Columns;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.COLUMNS;

/**
 * Instances of this class provides functions to convert a Caplan K formatted coordinate file
 * into a Microsoft Excel file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2Excel {

    private ArrayList<String> readStringLines;
    private Workbook workbook;

    /**
     * Constructs a new instance of this class with the read Caplan K file {@link ArrayList} string as parameter.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in Caplan K format
     */
    public Caplan2Excel(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Caplan K file element by element into a Microsoft Excel file.
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
            cell.setCellValue(ResourceBundleUtils.getLangString(COLUMNS, Columns.pointNumber));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(ResourceBundleUtils.getLangString(COLUMNS, Columns.easting));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(ResourceBundleUtils.getLangString(COLUMNS, Columns.northing));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(ResourceBundleUtils.getLangString(COLUMNS, Columns.height));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(ResourceBundleUtils.getLangString(COLUMNS, Columns.object));
            cellNumber++;

            cell = row.createCell(cellNumber);
            cell.setCellValue(ResourceBundleUtils.getLangString(COLUMNS, Columns.attribute));
        }

        for (String line : readStringLines) {
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
                        cell.setCellValue(Double.parseDouble(caplanBlock.getEasting()));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (caplanBlock.getNorthing() != null) {
                    cell = row.createCell(cellNumber);

                    if (!caplanBlock.getNorthing().equals("")) {
                        cell.setCellValue(Double.parseDouble(caplanBlock.getNorthing()));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell.setCellValue("");
                    }

                    cellNumber++;
                }

                if (caplanBlock.getHeight() != null) {
                    cell = row.createCell(cellNumber);

                    if (!caplanBlock.getHeight().equals("")) {
                        cell.setCellValue(Double.parseDouble(caplanBlock.getHeight()));
                        cellStyle = workbook.createCellStyle();
                        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));
                        cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                        cell.setCellStyle(cellStyle);
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

    /**
     * Returns the Workbook for writing it to a file.
     *
     * @return Workbook
     */
    public Workbook getWorkbook() {
        return this.workbook;
    }

} // end of Caplan2Excel
