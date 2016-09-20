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
package de.ryanthara.ja.rycon.converter.text;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides functions to convert a CSV formatted measurement or coordinate file into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CSV2TXT {

    private List<String[]> readCSVLines;

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSV2TXT(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a CSV file into a TXT file with a given separator sign.
     * <p>
     * The number of columns are quite equal.
     *
     * @param separator separator sign to use for conversion
     *
     * @return converted TXT file
     */
    public ArrayList<String> convertCSV2TXT(String separator) {
        ArrayList<String> result = new ArrayList<>();

        // convert the List<String[]> into an ArrayList<String> and use known stuff (-:
        for (String[] stringField : readCSVLines) {
            String line = "";

            for (String s : stringField) {
                line = line.concat(s);
                line = line.concat(separator);
            }

            line = line.trim();
            line = line.replace(',', '.');

            // skip empty lines
            if (!line.equals("")) {
                result.add(line);
            }
        }
        return result;
    }

} // end of CSV2TXT
