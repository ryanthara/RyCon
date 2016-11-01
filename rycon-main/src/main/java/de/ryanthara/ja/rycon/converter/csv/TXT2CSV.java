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
package de.ryanthara.ja.rycon.converter.csv;

import java.util.ArrayList;

/**
 * This class provides functions to convert a text formatted measurement or coordinate file
 * into a comma separated values file (csv format).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TXT2CSV {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TXT2CSV(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text formatted file into a CSV file with the given separator sign.
     * <p>
     * Due to some reasons the text file could not use white space characters in point numbers or code blocks.
     *
     * @param separator separator sign to use for conversion
     *
     * @return converted CSV file
     */
    public ArrayList<String> convertTXT2CSV(String separator) {
        ArrayList<String> result = new ArrayList<>();

        for (String line : readStringLines) {
            line = line.trim();
            result.add(line.replaceAll("\\s+", separator));
        }
        return result;
    }

} // end of TXT2CSV
