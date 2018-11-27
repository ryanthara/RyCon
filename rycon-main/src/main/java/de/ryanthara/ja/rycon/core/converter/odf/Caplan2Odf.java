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

import de.ryanthara.ja.rycon.core.converter.excel.Format;
import de.ryanthara.ja.rycon.core.elements.CaplanBlock;
import de.ryanthara.ja.rycon.i18n.Columns;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.COLUMN_NAME;

/**
 * A converter with functions to convert coordinate coordinate files
 * from Caplan K program into an OpenDocument spreadsheet file.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class Caplan2Odf {

    private static final Logger logger = LoggerFactory.getLogger(Caplan2Odf.class.getName());

    private final List<String> lines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Creates a converter with a list for the read line based Caplan K file.
     *
     * @param lines list with Caplan K formatted lines
     */
    public Caplan2Odf(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a K file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow writer comment row
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

            for (String line : lines) {
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
                            cell.setDoubleValue(StringUtils.parseDoubleValue(caplanBlock.getEasting()));
                            cell.setFormatString(Format.DIGITS_4.getString());
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (caplanBlock.getNorthing() != null) {
                        if (!caplanBlock.getNorthing().equals("")) {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(StringUtils.parseDoubleValue(caplanBlock.getNorthing()));
                            cell.setFormatString(Format.DIGITS_4.getString());
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex = colIndex + 1;
                    }

                    if (caplanBlock.getHeight() != null) {
                        if (!caplanBlock.getHeight().equals("")) {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(StringUtils.parseDoubleValue(caplanBlock.getHeight()));
                            cell.setFormatString(Format.DIGITS_4.getString());
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

    private int prepareCommentRow(int rowIndex, int colIndex, Table table) {
        Cell cell;
        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Columns.pointNumber));
        colIndex = colIndex + 1;

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Columns.easting));
        colIndex = colIndex + 1;

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Columns.northing));
        colIndex = colIndex + 1;

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Columns.height));
        colIndex = colIndex + 1;

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Columns.object));
        colIndex = colIndex + 1;

        cell = table.getCellByPosition(colIndex, rowIndex);
        cell.setStringValue(ResourceBundleUtils.getLangString(COLUMN_NAME, Columns.attribute));

        rowIndex = rowIndex + 1;
        return rowIndex;
    }

    /**
     * Returns the {@link SpreadsheetDocument} for writing it to a file.
     *
     * @return SpreadsheetDocument
     */
    public SpreadsheetDocument getSpreadsheetDocument() {
        return this.spreadsheetDocument;
    }

}
