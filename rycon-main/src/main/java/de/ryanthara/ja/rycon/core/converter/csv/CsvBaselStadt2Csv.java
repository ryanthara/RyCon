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
package de.ryanthara.ja.rycon.core.converter.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from the geodata
 * server Basel Stadt (Switzerland) into comma separated values (CSV) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2Csv {

    private final List<String[]> lines;

    /**
     * Creates a converter with a list for the read line based comma separated
     * values (CSV) file from the geodata server Basel Stadt (Switzerland).
     *
     * @param lines list with lines as string array
     */
    public CsvBaselStadt2Csv(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a CSV file.
     * <p>
     * With a parameter it is possible to distinguish between comma or semicolon as separator.
     *
     * @param separator separator sign as {@code String}
     * @return converted {@code List<String>} with lines of CSV format
     */
    public List<String> convert(String separator) {
        List<String> result = new ArrayList<>();

        removeHeadLine();

        for (String[] values : lines) {
            // point number is in column 1
            String line = values[0].replaceAll("\\s+", "").trim();
            line = line.concat(separator);

            // easting (Y) is in column 3
            line = line.concat(values[2]);
            line = line.concat(separator);

            // northing (X) is in column 4
            line = line.concat(values[3]);

            // height (Z) is in column 5, but not always valued
            if (!values[4].equals("")) {
                line = line.concat(separator);
                line = line.concat(values[4]);
            }

            result.add(line.trim());
        }

        return List.copyOf(result);
    }

    private void removeHeadLine() {
        lines.remove(0);
    }

}
