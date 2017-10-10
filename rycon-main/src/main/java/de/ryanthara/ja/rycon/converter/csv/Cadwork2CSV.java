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
package de.ryanthara.ja.rycon.converter.csv;

import java.util.ArrayList;

/**
 * This class provides functions to convert coordinate files from Cadwork CAD program (node.dat) into
 * comma separated values files (CSV) with different separation signs.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2CSV {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files from Cadwork CAD program in node.dat file format.
     *
     * @param readStringLines {@code ArrayList<String>} with read lines from node.dat file
     */
    public Cadwork2CSV(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a CSV file with a given separator sign.
     *
     * @param separator        separator sign to use for conversion
     * @param writeCommentLine writes a comment line with information about the column content
     * @param useCodeColumn    use the code column from node.dat
     * @param useZeroHeights   use heights with zero (0.000) values
     *
     * @return converted CSV file
     */
    public ArrayList<String> convertCadwork2CSV(String separator, boolean writeCommentLine,
                                                boolean useCodeColumn, boolean useZeroHeights) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            // remove not needed headlines
            readStringLines.remove(0);
            readStringLines.remove(0);

            String[] lineSplit = readStringLines.get(0).trim().split("\\s+", -1);

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

            readStringLines.remove(0);

            result.add(commentLine);
        } else {
            // remove not needed headlines
            for (int i = 0; i < 3; i++) {
                readStringLines.remove(0);
            }
        }

        for (String line : readStringLines) {
            String s;
            String[] lineSplit = line.trim().split("\\t, -1");

            // point number
            s = lineSplit[5];
            s = s.concat(separator);

            // use code if necessary
            if (useCodeColumn) {
                s = s.concat(lineSplit[4]);
                s = s.concat(separator);
            }

            // easting and northing
            s = s.concat(lineSplit[1]);
            s = s.concat(separator);
            s = s.concat(lineSplit[2]);
            s = s.concat(separator);

            // use height if necessary
            if (useZeroHeights) {
                s = s.concat(lineSplit[3]);
            } else {
                if (!lineSplit[3].equals("0.000000")) {
                    s = s.concat(lineSplit[3]);
                }
            }
            result.add(s.trim());
        }

        return result;
    }

} // end of Cadwork2CSV
