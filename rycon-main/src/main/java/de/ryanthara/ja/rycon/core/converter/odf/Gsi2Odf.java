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

import de.ryanthara.ja.rycon.core.converter.gsi.GsiDecoder;
import de.ryanthara.ja.rycon.core.elements.GSIBlock;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.WordIndices;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WORDINDEX;

/**
 * A converter with functions to convert Leica Geosystems GSI format (GSI8 and GSI16)
 * coordinate and measurement files into an Open Document Format spreadsheet file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi2Odf {

    private static final Logger logger = LoggerFactory.getLogger(Gsi2Odf.class.getName());
    private final GsiDecoder gsiDecoder;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Creates a converter with a list for the read line based
     * Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public Gsi2Odf(List<String> lines) {
        gsiDecoder = new GsiDecoder(lines);
    }

    /**
     * Converts a Leica Geosystems GSI file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row to the output file
     * @return success conversion success
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

            if (writeCommentRow) {
                rowIndex = prepareCommentRow(rowIndex, colIndex, table);
            }

            // fill gsi content into rows and cells
            for (List<GSIBlock> blocksInLine : gsiDecoder.getDecodedLinesOfGsiBlocks()) {
                colIndex = 0;
                for (GSIBlock block : blocksInLine) {
                    cell = table.getCellByPosition(colIndex, rowIndex);

                    switch (block.getWordIndex()) {
                        // GENERAL
                        case 11:    // Point number (includes block number)
                        case 12:    // Instrument serial no
                        case 13:    // Instrument type
                        case 18:    // Time format 1: pos. 8-9 year, 10-11 sec, 12-14 msec
                        case 19:    // Time format 2 : pos, 8-9 month 10-11 day, 12-13 hour, 14-15 min
                            cell.setStringValue(block.toPrintFormatCsv());
                            break;

                        // ANGLES
                        case 21:    // Horizontal Circle (Hz)
                        case 22:    // Vertical Angle (V)
                        case 25:    // Horizontal circle difference (Hz0-Hz)
                            setCellValue(cell, block);
                            break;

                        // DISTANCE
                        case 31:    // Slope Distance
                        case 32:    // Horizontal Distance
                        case 33:    // Height Difference
                            setCellValue(cell, block);
                            break;

                        // CODE BLOCK
                        case 41:    // Code number ( include block number)
                        case 42:    // Information 1
                        case 43:    // Information 2
                        case 44:    // Information 3
                        case 45:    // Information 4
                        case 46:    // Information 5
                        case 47:    // Information 6
                        case 48:    // Information 7
                        case 49:    // Information 8
                            cell.setStringValue(block.toPrintFormatCsv());
                            break;

                        // DISTANCE (additional information)
                        case 51:    // Constants(ppm, mm)
                        case 52:    // Number of measurements, standard deviation
                        case 53:    // Deviation
                        case 58:    // Signal strength
                        case 59:    // Reflector constant (1/10 mm)ppm
                            cell.setStringValue(block.toPrintFormatCsv());
                            break;

                        // POINT CODING
                        case 71:    // Point Code
                        case 72:    // Attribute 1
                        case 73:    // Attribute 2
                        case 74:    // Attribute 3
                        case 75:    // Attribute 4
                        case 76:    // Attribute 5
                        case 77:    // Attribute 6
                        case 78:    // Attribute 7
                        case 79:    // Attribute 8
                            cell.setStringValue(block.toPrintFormatCsv());
                            break;

                        // COORDINATES
                        case 81:    // Easting (Target)
                        case 82:    // Northing (Target)
                        case 83:    // Elevation (Target)
                        case 84:    // Station Easting (E0)
                        case 85:    // Station Northing (N0)
                        case 86:    // Station Elevation (H0)
                            setCellValue(cell, block);
                            cell.setFormatString("#,##0.0000");
                            break;

                        case 87:    // Reflector height (above ground)
                        case 88:    // Instrument height (above ground)
                            setCellValue(cell, block);
                            cell.setFormatString("#,##0.000");
                            break;

                        default:
                            logger.trace("Line contains unknown word index ({}).", block.toPrintFormatCsv());
                            break;
                    }
                    colIndex = colIndex + 1;
                }
                rowIndex = rowIndex + 1;
            }
        } catch (RuntimeException e) {
            logger.error("Thrown runtime exception.", e.getCause());
            throw e;
        } catch (Exception e) {
            logger.warn("Can not convert Leica Geosystems gsi file to open document spreadsheet file.", e.getCause());
        }

        // check number of written lines
        return rowIndex > 1;
    }

    private int prepareCommentRow(int rowIndex, int colIndex, Table table) {
        Cell cell;
        for (int wordIndex : gsiDecoder.getFoundWordIndices()) {
            cell = table.getCellByPosition(colIndex, 0);
            colIndex = colIndex + 1;

            cell.setStringValue(ResourceBundleUtils.getLangString(WORDINDEX, WordIndices.valueOf("WI" + wordIndex)));
        }
        rowIndex = rowIndex + 1;
        return rowIndex;
    }

    private void setCellValue(Cell cell, GSIBlock block) {
        cell.setDoubleValue(StringUtils.parseDoubleValue(block.toPrintFormatCsv()));
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
