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

import java.util.List;

/**
 * This class provides functions to convert coordinate files from CSV format into Microsoft Excel files
 * in XLS or XLSX format.
 */
public class CSV2Excel {

    private List<String[]> readCSVLines;
    private Workbook workbook;

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSV2Excel(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Convert a CSV file element by element into an Excel file.
     *
     * @param isXLS     selector to distinguish between XLS and XLSX file extension
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success conversion success
     */
    public boolean convertCSV2Excel(boolean isXLS, String sheetName) {
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

        short rowNumber = 0;
        short cellNumber;
        short countColumns = 0;

        for (String[] csvLine : readCSVLines) {
            row = sheet.createRow(rowNumber);
            rowNumber++;

            cellNumber = 0;

            for (String element : csvLine) {
                cell = row.createCell(cellNumber);
                cellNumber++;
                cell.setCellValue(element);
            }

            if (cellNumber > countColumns) {
                countColumns = cellNumber;
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

} // end of CSV2Excel
