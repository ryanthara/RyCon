/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.tools;

import org.odftoolkit.simple.SpreadsheetDocument;


/**
 * FileToolsODF implements basic operations on spreadsheet output operations for Open Document Format files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given files. At the moment there is no internal RyCON format used.
 *
 * @author sebastian
 * @version 2
 * @since 9
 */
public class FileToolsODF {

    private SpreadsheetDocument spreadsheetDocument = null;

    /**
     * Class constructor for writing the filled table to the file system.
     *
     * @param spreadsheetDocument {@code SpreadsheetDocument} object
     */
    public FileToolsODF(SpreadsheetDocument spreadsheetDocument) {
        this.spreadsheetDocument = spreadsheetDocument;
    }

    /**
     * Writes the Open Document Format Spreadsheet file to the filesystem.
     *
     * @param fileName output filename
     *
     * @return file writing success
     */
    public boolean writeODS(String fileName) {
        try {
            spreadsheetDocument.save(fileName);
            return true;
        } catch (Exception e) {
            System.err.println("unable to save Open Document Spreadsheet file to disk.");
            System.err.println(e.getMessage());
        }

        return false;
    }

} // end of FileToolsODF
