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

import java.util.List;

/**
 * This class provides functions to convert coordinate files from the geodata server 'Basel Stadt' (Switzerland)
 * into Microsoft Excel (XLS and XLSX) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2Excel {

    private List<String[]> readCSVLines;
    private Workbook workbook = null;

    /**
     * Class constructor for reader line based CSV files from the geodata server Basel Stadt (Switzerland).
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CsvBaselStadt2Excel(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a Zeiss REC formatted file.
     *
     * @param isXLS           selector to distinguish between XLS and XLSX file extension
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
     *
     * @return success conversion success
     */
    public boolean convertCSVBaselStadt2Excel(boolean isXLS, String sheetName, boolean writeCommentRow) {
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

        if (writeCommentRow) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            String[] commentLine = readCSVLines.get(0);

            for (String description : commentLine) {
                cell = row.createCell(cellNumber);
                cellNumber++;
                cell.setCellValue(description);
            }
        }

        // remove furthermore the still not needed comment line
        readCSVLines.remove(0);

        for (String[] csvLine : readCSVLines) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            cellNumber = 0;

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
                            cell.setCellValue(Double.parseDouble(csvLine[i]));
                            cellStyle = workbook.createCellStyle();
                            cellStyle.setDataFormat(format.getFormat("#,##0.000"));
                            cellStyle.setVerticalAlignment(CellStyle.ALIGN_RIGHT);
                            cell.setCellStyle(cellStyle);
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
                        System.err.println("Error in convertCSVBaselStadt2Excel: unknown element found or to much columns");
                }
            }
        }

        // adjust column width to fit the content
        for (int i = 0; i < readCSVLines.get(0).length; i++) {
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

} // end of CsvBaselStadt2Excel
