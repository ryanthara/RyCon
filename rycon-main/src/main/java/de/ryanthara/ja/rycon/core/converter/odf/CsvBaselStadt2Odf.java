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

import de.ryanthara.ja.rycon.util.StringUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files from the geodata
 * server Basel Stadt (Switzerland) into an Open Document Format spreadsheet file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2Odf {

    private static final Logger logger = LoggerFactory.getLogger(CsvBaselStadt2Odf.class.getName());

    private final List<String[]> lines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Creates a converter with a list for the read line based comma separated
     * values (CSV) file from the geodata server Basel Stadt (Switzerland).
     *
     * @param lines list with lines as string array
     */
    public CsvBaselStadt2Odf(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Convert a CSV file from the geodata server Basel Stadt (Switzerland) into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
     * @return success conversion success
     */
    public boolean convert(Path sheetName, boolean writeCommentRow) {
        int colIndex = 0;
        int rowIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName.toString());

            Cell cell;

            if (writeCommentRow) {
                rowIndex = prepareCommentRow(colIndex, rowIndex, table);
            }

            removeCommentLine();

            for (String[] values : lines) {
                colIndex = 0;

                for (int i = 0; i < values.length; i++) {
                    cell = table.getCellByPosition(colIndex, rowIndex);

                    switch (i) {
                        case 0:
                        case 1:
                            cell.setStringValue(values[i]);
                            break;

                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            if (values[i].equalsIgnoreCase("")) {
                                cell.setStringValue(values[i]);
                            } else {
                                cell.setDoubleValue(StringUtils.parseDoubleValue(values[i]));
                                cell.setFormatString("#,##0.000");
                            }
                            break;

                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                            cell.setStringValue(values[i]);
                            break;

                        default:
                            logger.trace("Line contains less or more tokens ({}) than needed or allowed.\n{}", values.length, Arrays.toString(values));
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
            logger.warn("Can not convert coordinate file from geodata server Basel Stadt to open document spreadsheet file.", e.getCause());
        }

        return rowIndex > 1;
    }

    private int prepareCommentRow(int colIndex, int rowIndex, Table table) {
        Cell cell;
        String[] values = lines.get(0);

        for (String value : values) {
            cell = table.getCellByPosition(colIndex, rowIndex);
            cell.setStringValue(value);
            colIndex = colIndex + 1;
        }
        rowIndex = rowIndex + 1;
        return rowIndex;
    }

    private void removeCommentLine() {
        lines.remove(0);
    }

    /**
     * Returns the SpreadsheetDocument object for writing it to a file.
     *
     * @return SpreadsheetDocument
     */
    public SpreadsheetDocument getSpreadsheetDocument() {
        return this.spreadsheetDocument;
    }

}
