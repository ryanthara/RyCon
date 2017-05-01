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

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.elements.GSIBlock;
import de.ryanthara.ja.rycon.i18n.GSIWordIndices;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert measurement or coordinate files from Leica GSI format
 * (GSI8 and GSI16) into an Open Document Format spreadsheet file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSI2ODF {

    private BaseToolsGSI baseToolsGSI;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Constructs a new instance of this class for read Leica GSI files as parameter.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public GSI2ODF(ArrayList<String> readStringLines) {
        baseToolsGSI = new BaseToolsGSI(readStringLines);
    }

    /**
     * Converts a Leica GSI file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success conversion success
     */
    public boolean convertGSI2ODS(Path sheetName, boolean writeCommentRow) {
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
                for (int wordIndex : baseToolsGSI.getFoundAllWordIndices()) {
                    cell = table.getCellByPosition(colIndex, 0);
                    colIndex = colIndex + 1;

                    cell.setStringValue(GSIWordIndices.getWordIndexDescription(wordIndex));
                }
                rowIndex = rowIndex + 1;
            }

            // fill gsi content into rows and cells
            for (ArrayList<GSIBlock> blocksInLine : baseToolsGSI.getEncodedLinesOfGSIBlocks()) {
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
                            cell.setStringValue(block.toPrintFormatCSV());
                            break;

                        // ANGLES
                        case 21:    // Horizontal Circle (Hz)
                        case 22:    // Vertical Angle (V)
                        case 25:    // Horizontal circle difference (Hz0-Hz)
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
                            break;

                        // DISTANCE
                        case 31:    // Slope Distance
                        case 32:    // Horizontal Distance
                        case 33:    // Height Difference
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
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
                            cell.setStringValue(block.toPrintFormatCSV());
                            break;

                        // DISTANCE (additional information)
                        case 51:    // Constants(ppm, mm)
                        case 52:    // Number of measurements, standard deviation
                        case 53:    // Deviation
                        case 58:    // Signal strength
                        case 59:    // Reflector constant (1/10 mm)ppm
                            cell.setStringValue(block.toPrintFormatCSV());
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
                            cell.setStringValue(block.toPrintFormatCSV());
                            break;

                        // COORDINATES
                        case 81:    // Easting (Target)
                        case 82:    // Northing (Target)
                        case 83:    // Elevation (Target)
                        case 84:    // Station Easting (E0)
                        case 85:    // Station Northing (N0)
                        case 86:    // Station Elevation (H0)
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
                            cell.setFormatString("#,##0.0000");
                            break;

                        case 87:    // Reflector height (above ground)
                        case 88:    // Instrument height (above ground)
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
                            cell.setFormatString("#,##0.000");
                            break;

                        default:
                            System.err.println("GSI2ODF.convertGSI2ODS() : found unknown word index " + block.toPrintFormatCSV());
                    }
                    colIndex = colIndex + 1;
                }
                rowIndex = rowIndex + 1;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        // check number of written lines
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

} // end of GSI2ODF
