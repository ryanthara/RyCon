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
 * A converter with functions to convert Cadwork CAD program
 * coordinate files into an OpenDocument spreadsheet file.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class Cadwork2Odf {

    private static final Logger logger = LoggerFactory.getLogger(Cadwork2Odf.class.getName());

    private final List<String> lines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Creates a converter with a list for the read line based text file from Cadwork CAD program.
     *
     * @param lines list with read node.dat lines
     */
    public Cadwork2Odf(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into an Open Document Format spreadsheet file.
     * <p>
     * Cadwork node.dat files are tab separated.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
     * @return success conversion
     */
    public boolean convert(Path sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName.toString());

            Cell cell;

            removeHeadLines();

            if (writeCommentRow) {
                rowIndex = prepareCommentRow(rowIndex, colIndex, table);
            }

            removeCommentLine();

            for (String line : lines) {
                colIndex = 0;

                String[] cellValues = line.trim().split("\\t", -1);

                cell = table.getCellByPosition(colIndex, rowIndex);      // No
                cell.setStringValue(cellValues[0]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // X
                cell.setStringValue(cellValues[1]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                cell.setStringValue(cellValues[2]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                cell.setStringValue(cellValues[3]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Code
                cell.setStringValue(cellValues[4]);
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Name
                cell.setStringValue(cellValues[5]);
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

    private int prepareCommentRow(int rowIndex, int colIndex, Table table) {
        Cell cell;
        String[] descriptions = lines.get(0).trim().split("\\s+", -1);

        for (String description : descriptions) {
            cell = table.getCellByPosition(colIndex, rowIndex);
            cell.setStringValue(description);
            colIndex = colIndex + 1;
        }
        rowIndex = rowIndex + 1;
        return rowIndex;
    }

    private void removeCommentLine() {
        lines.remove(0);
    }

    private void removeHeadLines() {
        lines.subList(0, 3).clear();
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
