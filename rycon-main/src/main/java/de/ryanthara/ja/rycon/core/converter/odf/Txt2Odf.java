/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.converter.odf
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
package de.ryanthara.ja.rycon.core.converter.odf;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert ASCII text coordinate
 * files into an Open Document Format spreadsheet file.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class Txt2Odf {

    private static final Logger logger = LoggerFactory.getLogger(Txt2Odf.class.getName());

    private final List<String> lines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Creates a converter with a list for the read line based ASCII text file.
     *
     * @param lines list with ASCII text lines
     */
    public Txt2Odf(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a TXT file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName name of the sheet (file name from input file)
     * @return success conversion success
     */
    public boolean convertTXT2Ods(Path sheetName) {
        int rowIndex = 0;
        int colIndex;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName.toString());

            Cell cell;

            for (String line : lines) {
                String[] values = line.trim().split("\\s+");
                colIndex = 0;

                for (String value : values) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(value);
                    colIndex = colIndex + 1;
                }

                rowIndex = rowIndex + 1;
            }
        } catch (RuntimeException e) {
            logger.error("Thrown runtime exception.", e.getCause());
            throw e;
        } catch (Exception e) {
            logger.warn("Can not convert text file to open document spreadsheet file.", e.getCause());
        }

        return rowIndex > 1;
    }

    /**
     * Returns the SpreadsheetDocument for writing it to a file.
     *
     * @return SpreadsheetDocument
     */
    public SpreadsheetDocument getSpreadsheetDocument() {
        return this.spreadsheetDocument;
    }

}
