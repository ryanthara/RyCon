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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert ASCII text coordinate
 * files into Microsoft Excel files in XLS or XLSX format.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Txt2Excel {

    private final List<String> lines;
    private Workbook workbook;

    /**
     * Creates a converter with a list for the read line based ASCII text file.
     *
     * @param lines list with ASCII text lines
     */
    public Txt2Excel(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a TXT file element by element into an Excel file.
     *
     * @param fileFormat distinguish between XLS and XLSX file format
     * @param sheetName  name of the sheet (file name from input file)
     * @return success conversion success
     */
    public boolean convert(FileFormat fileFormat, String sheetName) {
        workbook = BaseToolsExcel.prepareWorkbook(fileFormat);

        String safeName = WorkbookUtil.createSafeSheetName(sheetName);
        Sheet sheet = workbook.createSheet(safeName);
        Row row;
        Cell cell;

        short rowNumber = 0;
        short cellNumber;
        short countColumns = 0;

        for (String line : lines) {
            String[] lineSplit = line.trim().split("\\s+");

            row = sheet.createRow(rowNumber);
            rowNumber++;

            cellNumber = 0;

            for (String element : lineSplit) {
                cell = row.createCell(cellNumber);
                cellNumber++;
                cell.setCellValue(element);

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

}
