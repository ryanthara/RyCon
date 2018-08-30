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

import de.ryanthara.ja.rycon.core.elements.CaplanBlock;
import de.ryanthara.ja.rycon.i18n.Columns;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.COLUMN_NAMES;

/**
 * Instances of this class provides functions to convert a Caplan K formatted coordinate file
 * into an OpenDocument spreadsheet file.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class Caplan2Odf {

    private static final Logger logger = LoggerFactory.getLogger(Caplan2Odf.class.getName());

    private final ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Constructs a new instance of this class with the reader Caplan K file {@link ArrayList} string as parameter.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in Caplan K format
     */
    public Caplan2Odf(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a K file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
     *
     * @return success conversion success
     */
    public boolean convertCaplan2Ods(Path sheetName, boolean writeCommentRow) {
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
                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAMES, Columns.pointNumber));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAMES, Columns.easting));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAMES, Columns.northing));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAMES, Columns.height));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAMES, Columns.object));
                colIndex = colIndex + 1;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAMES, Columns.attribute));

                rowIndex = rowIndex + 1;
            }

            for (String line : readStringLines) {
                // skip empty lines directly after reading
                if (!line.trim().isEmpty()) {
                    colIndex = 0;

                    CaplanBlock caplanBlock = new CaplanBlock(line);

                    if (caplanBlock.getNumber() != null) {
                        cell = table.getCellByPosition(colIndex, rowIndex);
                        cell.setStringValue(caplanBlock.getNumber());
                        colIndex = colIndex + 1;
                    }

                    if (caplanBlock.getEasting() != null) {
                        if (!caplanBlock.getEasting().equals("")) {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(caplanBlock.getEasting()));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (caplanBlock.getNorthing() != null) {
                        if (!caplanBlock.getNorthing().equals("")) {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(caplanBlock.getNorthing()));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (caplanBlock.getHeight() != null) {
                        if (!caplanBlock.getHeight().equals("")) {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(caplanBlock.getHeight()));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (caplanBlock.getCode() != null) {
                        cell = table.getCellByPosition(colIndex, rowIndex);
                        cell.setStringValue(caplanBlock.getCode());
                        colIndex = colIndex + 1;

                        if (caplanBlock.getAttributes().size() > 0) {
                            for (String attribute : caplanBlock.getAttributes()) {
                                cell = table.getCellByPosition(colIndex, rowIndex);
                                cell.setStringValue(attribute);
                                colIndex = colIndex + 1;
                            }
                        }
                    }

                    rowIndex = rowIndex + 1;
                }
            }

        } catch (RuntimeException e) {
            logger.error("Thrown runtime exception.", e.getCause());
            throw e;
        } catch (Exception e) {
            logger.warn("Can not convert Caplan K file to open document spreadsheet file.", e.getCause());
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

} // end of Caplan2Odf
