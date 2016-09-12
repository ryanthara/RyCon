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
package de.ryanthara.ja.rycon.converter.gsi;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides functions to convert csv formatted measurement or coordinate files into
 * Leica GSI8 or GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CSV2GSI {

    private List<String[]> readCSVLines = null;

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSV2GSI(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a CSV file (comma or semicolon delimited) into a GSI file.
     * <p>
     * The format of the GSI file is controlled with a parameter. The separator
     * sign is automatically detected.
     *
     * @param isGSI16                  control if GSI8 or GSI16 format is written
     * @param sourceContainsCodeColumn if source file contains a code column
     *
     * @return converted {@code ArrayList<String>} with lines of GSI format
     */
    public ArrayList<String> convertCSV2GSI(boolean isGSI16, boolean sourceContainsCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        // convert the List<String[]> into an ArrayList<String> and use known stuff (-:
        for (String[] stringField : readCSVLines) {
            String line = "";

            for (String s : stringField) {
                line = line.concat(s);
                line = line.concat(" ");
            }

            line = line.trim();
            line = line.replace(',', '.');

            // skip empty lines
            if (!line.equals("")) {
                result.add(line);
            }
        }

        TXT2GSI txt2GSI = new TXT2GSI(result);

        return txt2GSI.convertTXT2GSI(isGSI16, sourceContainsCodeColumn);
    }

} // end of CSV2GSI
