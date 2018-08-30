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

/**
 * Instances of this class provides functions to convert coordinate files from Cadwork CAD program into
 * an OpenDocument spreadsheet file.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class Cadwork2Odf {

    private static final Logger logger = LoggerFactory.getLogger(Cadwork2Odf.class.getName());

    private final ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Constructs a new instance of this class for reader line based text files from Cadwork CAD program
     * in node.dat file format.
     *
     * @param readStringLines {@code ArrayList<String>} with reader lines from node.dat file
     */
    public Cadwork2Odf(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into an Open Document Format spreadsheet file.
     * <p>
     * Cadwork node.dat files are tab separated.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
     *
     * @return success conversion
     */
    public boolean convertCadwork2Ods(Path sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;
        String[] lineSplit;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName.toString());

            Cell cell;

            // remove not needed headlines
            readStringLines.subList(0, 3).clear();

            if (writeCommentRow) {
                lineSplit = readStringLines.get(0).trim().split("\\s+", -1);

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
                colIndex = 0;

                lineSplit = line.trim().split("\\t", -1);

                cell = table.getCellByPosition(colIndex, rowIndex);      // No
                cell.setStringValue(lineSplit[0]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // X
                cell.setStringValue(lineSplit[1]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                cell.setStringValue(lineSplit[2]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                cell.setStringValue(lineSplit[3]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Code
                cell.setStringValue(lineSplit[4]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Name
                cell.setStringValue(lineSplit[5]);
                rowIndex = rowIndex + 1;
            }
        } catch (RuntimeException e) {
            logger.error("Thrown runtime exception.", e.getCause());
            throw e;
        } catch (Exception e) {
            logger.warn("Can not convert cadwork file to open document spreadsheet file.", e.getCause());
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

} // end of Cadwork2Odf
