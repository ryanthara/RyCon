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

import java.util.List;

/**
 * This class provides functions to convert a csv formatted coordinate file from the geodata server
 * Basel Stadt (Switzerland) into an Open Document Format spreadsheet file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */public class CSVBaselStadt2ODF {

    private List<String[]> readCSVLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Class constructor for read line based CSV files from the geodata server Basel Stadt (Switzerland).
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSVBaselStadt2ODF(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Convert a CSV file from the geodata server Basel Stadt (Switzerland) into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion success
     */
    public boolean convertCSVBaselStadt2ODS(String sheetName, boolean writeCommentRow) {
        int colIndex = 0;
        int rowIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            if (writeCommentRow) {
                String[] commentLine = readCSVLines.get(0);

                for (String description : commentLine) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(description);
                    colIndex++;
                }
                rowIndex++;
            }

            // remove furthermore the still not needed comment line
            readCSVLines.remove(0);

            for (String[] csvLine : readCSVLines) {
                colIndex = 0;

                for (int i = 0; i < csvLine.length; i++) {
                    cell = table.getCellByPosition(colIndex, rowIndex);

                    switch (i) {
                        case 0:
                        case 1:
                            cell.setStringValue(csvLine[i]);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            if (csvLine[i].equalsIgnoreCase("")) {
                                cell.setStringValue(csvLine[i]);
                            } else {
                                cell.setDoubleValue(Double.parseDouble(csvLine[i]));
                                cell.setFormatString("#,##0.000");
                            }
                            break;
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                            cell.setStringValue(csvLine[i]);
                            break;
                    }
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
     * Returns the SpreadsheetDocument object for writing it to a file.
     *
     * @return SpreadsheetDocument
     */
    public SpreadsheetDocument getSpreadsheetDocument() {
        return this.spreadsheetDocument;
    }

} // end of CSVBaselStadt2ODF
