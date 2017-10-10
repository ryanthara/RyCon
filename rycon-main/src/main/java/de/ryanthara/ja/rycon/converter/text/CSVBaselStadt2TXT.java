/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.converter.text;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides functions to convert a csv formatted coordinate file from the geodata server
 * Basel Stadt (Switzerland) into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CSVBaselStadt2TXT {

    private List<String[]> readCSVLines = null;

    /**
     * Class constructor for read line based CSV files from the geodata server Basel Stadt (Switzerland).
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSVBaselStadt2TXT(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (Switzerland) into a text formatted file.
     * <p>
     * With a parameter it is possible to distinguish between space or tabulator as separator.
     *
     * @param separator separator sign as {@code String}
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertCSVBaselStadt2TXT(String separator) {
        ArrayList<String> result = new ArrayList<>();

        // remove comment line
        readCSVLines.remove(0);

        for (String[] stringField : readCSVLines) {
            String line;

            // point number is in column 1
            line = stringField[0].replaceAll("\\s+", "").trim();
            line = line.concat(separator);

            // easting (Y) is in column 3
            line = line.concat(stringField[2]);
            line = line.concat(separator);

            // northing (X) is in column 4
            line = line.concat(stringField[3]);

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                line = line.concat(separator);
                line = line.concat(stringField[4]);
            }

            result.add(line.trim());
        }
        return result;
    }

} // end of CSVBaselStadt2TXT
