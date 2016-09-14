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
 * Created by sebastian on 13.09.16.
 */
public class Cadwork2ODF {

    private ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public Cadwork2ODF(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Cadwork node.dat file into an Open Document Format spreadsheet file.
     * <p>
     * Cadwork node.dat files are tab separated.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion
     */
    public boolean convertCadwork2ODS(String sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;
        String[] lineSplit;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            // remove not needed headlines
            for (int i = 0; i < 3; i++) {
                readStringLines.remove(0);
            }

            if (writeCommentRow) {
                lineSplit = readStringLines.get(0).trim().split("\\t", -1);

                for (String description : lineSplit) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(description);
                    colIndex++;
                }
                rowIndex++;
            }

            // remove furthermore the still not needed comment line
            readStringLines.remove(0);

            for (String line : readStringLines) {
                colIndex = 0;

                lineSplit = line.trim().split("\\t", -1);

                cell = table.getCellByPosition(colIndex, rowIndex);      // No
                cell.setStringValue(lineSplit[0]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // X
                cell.setStringValue(lineSplit[1]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                cell.setStringValue(lineSplit[2]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                cell.setStringValue(lineSplit[3]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Code
                cell.setStringValue(lineSplit[4]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Name
                cell.setStringValue(lineSplit[5]);
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

} // end of Cadwork2ODF
