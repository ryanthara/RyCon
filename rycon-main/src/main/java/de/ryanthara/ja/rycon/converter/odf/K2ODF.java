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

import de.ryanthara.ja.rycon.i18n.I18N;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

import java.util.ArrayList;

/**
 * Created by sebastian on 14.09.16.
 */
public class K2ODF {

    private ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public K2ODF(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a K file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     * @return success conversion success
     */
    public boolean convertCaplan2ODS(String sheetName, boolean writeCommentRow) {
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
                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("pointNumber"));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("easting"));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("northing"));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("height"));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("object"));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("attribute"));

                rowIndex = rowIndex + 1;
            }

            for (String line : readStringLines) {
                colIndex = 0;

                if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                    String s;

                    if (line.length() >= 16) {
                        cell = table.getCellByPosition(colIndex, rowIndex);
                        cell.setStringValue(line.substring(0, 16).trim());          // point number (no '*', ',' and ';'), column 1 - 16
                        colIndex = colIndex + 1;
                    }

                    if (line.length() >= 32) {
                        if (!(s = line.substring(20, 32).trim()).equals("")) {      // easting E, column 19-32
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(s));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (line.length() >= 46) {
                        if (!(s = line.substring(34, 46).trim()).equals("")) {      // northing N, column 33-46
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(s));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (line.length() >= 59) {
                        if (!(s = line.substring(48, 59).trim()).equals("")) {      // height H, column 47-59
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(s));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (line.length() >= 62) {
                        String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");

                        cell = table.getCellByPosition(colIndex, rowIndex);
                        cell.setStringValue(lineSplit[0].trim());                   // code is the same as object type, column 62...
                        colIndex = colIndex + 1;

                        for (int i = 1; i < lineSplit.length; i++) {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue(lineSplit[i].trim());
                            colIndex = colIndex + 1;
                        }
                    }
                    rowIndex = rowIndex + 1;
                }
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

} // end of K2ODF
