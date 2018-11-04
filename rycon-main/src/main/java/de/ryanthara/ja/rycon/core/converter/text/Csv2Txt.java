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
package de.ryanthara.ja.rycon.core.converter.text;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert comma separated
 * values (CSV) coordinate files into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Csv2Txt {

    private final List<String[]> lines;

    /**
     * Creates a converter with a list for the read line based comma separated values (CSV) files.
     *
     * @param lines list with lines of comma separated values (CSV)
     */
    public Csv2Txt(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a CSV file into a TXT file with a given separator sign.
     * <p>
     * The number of columns are quite equal.
     *
     * @param separator separator sign to use for conversion
     * @return converted TXT file
     */
    public List<String> convert(String separator) {
        List<String> result = new ArrayList<>();

        // convert the List<String[]> into an ArrayList<String> and use known stuff (-:
        for (String[] values : lines) {
            String line = "";

            for (String value : values) {
                line = line.concat(value);
                line = line.concat(separator);
            }

            line = line.trim();
            line = line.replace(',', '.');

            // skip empty lines
            if (!line.equals("")) {
                result.add(line);
            }
        }

        return List.copyOf(result);
    }

}
