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
package de.ryanthara.ja.rycon.converter.odf;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

import java.util.ArrayList;

/**
 * Created by sebastian on 14.09.16.
 */
public class TXT2ODF {

    private ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TXT2ODF(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a TXT file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success conversion success
     */
    public boolean convertTXT2ODS(String sheetName) {
        int rowIndex = 0;
        int colIndex;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            for (String line : readStringLines) {
                String[] lineSplit = line.trim().split("\\s+");
                colIndex = 0;
                for (String element : lineSplit) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(element);
                    colIndex++;
                }
                rowIndex++;
            }
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
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

} // end of TXT2ODF
