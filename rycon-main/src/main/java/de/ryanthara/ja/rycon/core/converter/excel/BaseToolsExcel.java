/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Provides basic and helper functions that are used for converting
 * different file formats into Microsoft Excel XLS and XLSX files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public final class BaseToolsExcel {

    /**
     * BaseToolsExcel is non-instantiable.
     */
    private BaseToolsExcel() {
        throw new AssertionError();
    }

    /**
     * Prepares the workbook with the defined file format.
     *
     * @param fileFormat use XLS or XLSX file format for the workbook
     * @return prepared workbook
     */
    static Workbook prepareWorkbook(FileFormat fileFormat) {
        if (fileFormat == FileFormat.XLS) {
            return new HSSFWorkbook();
        } else if (fileFormat == FileFormat.XLSX) {
            return new XSSFWorkbook();
        } else {
            throw new IllegalArgumentException("Used wrong file format: " + fileFormat.toString());
        }
    }

    /**
     * Sets the cell style.
     *
     * @param workbook     the used workbook
     * @param cell         the current cell
     * @param format       the current data format
     * @param formatString the used format string (3 or 4 digits)
     */
    static void setCellStyle(Workbook workbook, Cell cell, DataFormat format, String formatString) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(format.getFormat(formatString));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cell.setCellStyle(cellStyle);
    }

}
