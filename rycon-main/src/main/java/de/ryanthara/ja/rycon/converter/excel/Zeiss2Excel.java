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

import de.ryanthara.ja.rycon.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.elements.ZeissBlock;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;

/**
 * This class provides functions to convert measurement files from Zeiss REC format
 * and it's dialects (R4, R5, REC500 and M5) into Microsoft Excel (XLS and XLSX) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2Excel {

    private ArrayList<String> readStringLines;
    private Workbook workbook = null;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public Zeiss2Excel(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a Zeiss REC file element by element into an Excel file.
     *
     * @param isXLS           selector to distinguish between XLS and XLSX file extension
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion success
     */
    public boolean convertZeiss2Excel(boolean isXLS, String sheetName, boolean writeCommentRow) {
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
//        CellStyle cellStyle;

//        DataFormat format = workbook.createDataFormat();

        short rowNumber = 0;
        short cellNumber = 0;
        short countColumns = 0;

        // TODO implement comment row and multi line stored values

        /*
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
        */

        for (String line : readStringLines) {

            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                row = sheet.createRow(rowNumber);
                rowNumber++;

                cellNumber = 0;

                ZeissDecoder decoder = new ZeissDecoder();

                for (ZeissBlock zeissBlock : decoder.getZeissBlocks()) {
                    cell = row.createCell(cellNumber);
                    cell.setCellValue(zeissBlock.getValue());
                    cellNumber++;
                    countColumns++;
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

} // end of Zeiss2Excel
