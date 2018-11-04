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

import de.ryanthara.ja.rycon.data.PreferenceKey;
import de.ryanthara.ja.rycon.util.NumberFormatter;
import de.ryanthara.ja.rycon.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files from
 * the geodata server Basel Stadt (Switzerland) into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2Txt {

    private final boolean writeZeroHeights;
    private final List<String[]> lines;

    /**
     * Creates a converter with a list for the read line based comma separated
     * values (CSV) file from the geodata server Basel Stadt (Switzerland).
     *
     * @param lines            list with lines as string array
     * @param writeZeroHeights writes zero coordinates (0.000 metre) into the output file
     */
    public CsvBaselStadt2Txt(List<String[]> lines, boolean writeZeroHeights) {
        this.lines = new ArrayList<>(lines);
        this.writeZeroHeights = writeZeroHeights;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (Switzerland) into a text formatted file.
     * <p>
     * With a parameter it is possible to distinguish between space or tabulator as separator.
     * <p>
     * Trailing zeroes are add up til a number of three.
     *
     * @param separator separator sign as {@code String}
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(String separator) {
        List<String> result = new ArrayList<>();

        boolean addTrailingZeroes = StringUtils.parseBooleanValue(PreferenceKey.ADD_TRAILING_ZEROES);

        removeHeadLine();

        for (String[] values : lines) {
            String line;

            // point number is in column 1
            line = values[0].replaceAll("\\s+", "").trim();
            line = line.concat(separator);

            // easting (Y) is in column 3
            String easting = values[2];

            if (addTrailingZeroes) {
                easting = NumberFormatter.fillDecimalPlaces(easting, 3);
            }

            line = line.concat(easting);
            line = line.concat(separator);

            // northing (X) is in column 4
            String northing = values[3];

            if (addTrailingZeroes) {
                northing = NumberFormatter.fillDecimalPlaces(northing, 3);
            }

            line = line.concat(northing);

            // height (Z) is in column 5, but not always valued
            if (!values[4].equals("")) {
                line = line.concat(separator);

                String height = values[4];

                if (addTrailingZeroes) {
                    height = NumberFormatter.fillDecimalPlaces(height, 3);
                }

                line = line.concat(height);
            } else if (writeZeroHeights) {
                line = line.concat(separator);
                line = line.concat("0.000");
            }

            result.add(line.trim());
        }

        return List.copyOf(result);
    }

    private void removeHeadLine() {
        lines.remove(0);
    }

}
