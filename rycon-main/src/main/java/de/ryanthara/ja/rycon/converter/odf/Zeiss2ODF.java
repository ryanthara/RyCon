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

import de.ryanthara.ja.rycon.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.elements.ZeissBlock;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * This class provides functions to convert measurement files from Zeiss REC format
 * and it's dialects (R4, R5, REC500 and M5) into OpenDocument spreadsheet files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2ODF {

    private ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Constructs a new instance of this class with a parameter for the read line based Zeiss REC files in
     * different dialects (R4, R5, REC500 and M5).
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public Zeiss2ODF(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Zeiss REC file (R4, R5, M5 or REC500) into a text formatted file.
     * <p>
     * This method can differ between different Zeiss REC dialects because of the
     * different structure and line length.
     *
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success conversion success
     */
    public boolean convertZeiss2ODS(Path sheetName) {
        int rowIndex = 0;
        int colIndex;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName.toString());

            Cell cell;

            for (String line : readStringLines) {

                // skip empty lines
                if (line.trim().length() > 0) {
                    colIndex = 0;
                    ZeissDecoder decoder = new ZeissDecoder();

                    for (ZeissBlock zeissBlock : decoder.getZeissBlocks()) {
                        cell = table.getCellByPosition(colIndex, rowIndex);
                        cell.setStringValue(zeissBlock.getValue());
                        colIndex = colIndex + 1;
                    }
                }

                rowIndex = rowIndex + 1;
            }
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file " + sheetName.toString() + ".");
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

} // end of Zeiss2ODF
