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
 * A converter with functions to convert Cadwork CAD program
 * coordinate files into comma separated values (CSV) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2Csv {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text file from Cadwork CAD program.
     *
     * @param lines list with read node.dat lines
     */
    public Cadwork2Csv(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a CSV file with a given separator sign.
     *
     * @param separator        separator sign to use for conversion
     * @param writeCommentLine writes a comment line with information about the column content
     * @param useCodeColumn    use the code column from node.dat
     * @param useZeroHeights   use heights with zero (0.000) values
     * @return converted CSV file
     */
    public List<String> convert(String separator, boolean writeCommentLine,
                                     boolean useCodeColumn, boolean useZeroHeights) {
        List<String> result = new ArrayList<>();

        if (writeCommentLine) {
            removeHeadlines(2);

            String[] lineSplit = lines.get(0).trim().split("\\s+", -1);

            // point number
            String commentLine = lineSplit[5];
            commentLine = commentLine.concat(separator);

            // use code if necessary
            if (useCodeColumn) {
                commentLine = commentLine.concat(lineSplit[4]);
                commentLine = commentLine.concat(separator);
            }

            // easting, northing and height
            commentLine = commentLine.concat(lineSplit[1]);
            commentLine = commentLine.concat(separator);
            commentLine = commentLine.concat(lineSplit[2]);
            commentLine = commentLine.concat(separator);
            commentLine = commentLine.concat(lineSplit[3]);

            lines.remove(0);

            result.add(commentLine);
        } else {
            removeHeadlines(3);
        }

        for (String line : lines) {
            String s;
            String[] values = line.trim().split("\\t, -1");

            // point number
            s = values[5];
            s = s.concat(separator);

            // use code if necessary
            if (useCodeColumn) {
                s = s.concat(values[4]);
                s = s.concat(separator);
            }

            // easting and northing
            s = s.concat(values[1]);
            s = s.concat(separator);
            s = s.concat(values[2]);
            s = s.concat(separator);

            // use height if necessary
            if (useZeroHeights) {
                s = s.concat(values[3]);
            } else {
                if (!values[3].equals("0.000000")) {
                    s = s.concat(values[3]);
                }
            }

            result.add(s.trim());
        }

        return List.copyOf(result);
    }

    private void removeHeadlines(int toIndex) {
        lines.subList(0, toIndex).clear();
    }

}
