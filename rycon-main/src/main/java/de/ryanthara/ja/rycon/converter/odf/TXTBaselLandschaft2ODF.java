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
public class TXTBaselLandschaft2ODF {

    private ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TXTBaselLandschaft2ODF(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a txt file from the geodata server Basel Landschaft (Switzerland) element by element into an
     * Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     * @return success conversion success
     */
    public boolean convertTXTBaselLandschaft2ODS(String sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            if (writeCommentRow) {
                String[] lineSplit = readStringLines.get(0).trim().split("\\t", -1);

                for (String description : lineSplit) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(description);
                    colIndex = colIndex + 1;
                }
                rowIndex = rowIndex + 1;
            }

            // remove furthermore the still not needed comment line
            readStringLines.remove(0);

            for (String line : readStringLines) {
                String[] lineSplit = line.trim().split("\\t", -1);

                colIndex = 0;

                switch (lineSplit.length) {
                    case 5:     // HFP file
                        cell = table.getCellByPosition(colIndex, rowIndex);      // Art
                        cell.setStringValue(lineSplit[0]);
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Number
                        cell.setStringValue(lineSplit[1]);
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // X
                        cell.setDoubleValue(Double.parseDouble(lineSplit[2]));
                        cell.setFormatString("#,##0.000");
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                        cell.setDoubleValue(Double.parseDouble(lineSplit[3]));
                        cell.setFormatString("#,##0.000");
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                        if (lineSplit[4].equalsIgnoreCase("NULL")) {
                            cell.setStringValue("NULL");
                        } else {
                            cell.setDoubleValue(Double.parseDouble(lineSplit[4]));
                            cell.setFormatString("#,##0.000");
                        }
                        break;

                    case 6:     // LFP file
                        cell = table.getCellByPosition(colIndex, rowIndex);      // Art
                        cell.setStringValue(lineSplit[0]);
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Number
                        cell.setStringValue(lineSplit[1]);
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // VArt
                        cell.setStringValue(lineSplit[2]);
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // X
                        cell.setDoubleValue(Double.parseDouble(lineSplit[3]));
                        cell.setFormatString("#,##0.000");
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                        cell.setDoubleValue(Double.parseDouble(lineSplit[4]));
                        cell.setFormatString("#,##0.000");
                        colIndex = colIndex + 1;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                        if (lineSplit[5].equalsIgnoreCase("NULL")) {
                            cell.setStringValue("NULL");
                        } else {
                            cell.setDoubleValue(Double.parseDouble(lineSplit[5]));
                            cell.setFormatString("#,##0.000");
                        }
                        break;

                    default:
                        System.err.println("Error in convertTXTBaselLandschaft2ODS: line length doesn't match 5 or 6 elements");

                }
                rowIndex = rowIndex + 1;
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

} // end of TXTBaselLandschaft2ODF


