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

import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.core.elements.ZeissBlock;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert measurement and coordinate files from Zeiss REC
 * format and it's dialects (R4, R5, REC500 and M5) into OpenDocument spreadsheet files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2Odf {

    private static final Logger logger = LoggerFactory.getLogger(Zeiss2Odf.class.getName());

    private final List<String> lines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Creates a converter with a list for the read line based
     * text files in the Zeiss REC format and it's dialects.
     *
     * <p>
     * The differentiation of the content is done by the called
     * method and it's content analyze functionality.
     *
     * @param lines list with Zeiss REC format lines
     */
    public Zeiss2Odf(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a Zeiss REC file (R4, R5, M5 or REC500) into a text formatted file.
     * <p>
     * This method can differ between different Zeiss REC dialects because of the
     * different structure and line length.
     *
     * @param sheetName name of the sheet (file name from input file)
     * @return success conversion success
     */
    public boolean convertZeiss2Ods(Path sheetName) {
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
        } catch (RuntimeException e) {
            logger.error("Thrown runtime exception.", e.getCause());
            throw e;
        } catch (Exception e) {
            logger.warn("Can not convert Zeiss REC file to open document spreadsheet file.", e.getCause());
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
