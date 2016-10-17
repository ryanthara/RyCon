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

/**
 * This class provides functions to convert a Caplan K formatted measurement file into text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class K2TXT {

    private ArrayList<String> readStringLines;

    /**
     * @param readStringLines
     */
    public K2TXT(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Caplan K file to a text formatted file.
     *
     * @param separator         distinguish between tabulator or space as division sign
     * @param writeCommentLine  writes a comment line into the file
     * @param writeCodeColumn   writes a code column (nr code x y z attr)
     * @param writeSimpleFormat writes a simple format (nr x y z or nr code x y z)
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertK2TXT(String separator, boolean writeCommentLine, boolean writeCodeColumn,
                                          boolean writeSimpleFormat) {

        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            String commentLine = "";

            if (writeSimpleFormat) {
                commentLine = "nr" + separator + "x" + separator + "y" + separator + "z";
            } else if (writeCodeColumn) {
                commentLine = "nr" + separator + "code" + separator + "x" + separator + "y" + separator + "z" + separator + "attribute";
            }

            result.add(commentLine);
        }

        for (String line : readStringLines) {
            if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                String s = "";

                if (line.length() >= 16) {
                    s = line.substring(0, 16).trim();       // point number (no '*', ',' and ';'), column 1 - 16
                }

                if ((line.length() >= 62) && writeSimpleFormat && writeCodeColumn) {
                    String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");
                    String code = lineSplit[0].trim();      // code is the same as object type, column 62...

                    s = s.concat(separator);
                    s = s.concat(code);
                } else if (writeCodeColumn) {
                    s = s.concat(separator);
                    s = s.concat("NULL");
                }

                if (line.length() >= 32) {
                    String easting = line.substring(20, 32).trim();     // easting E, column 19-32
                    s = s.concat(separator);
                    s = s.concat(easting);
                }

                if (line.length() >= 46) {
                    String northing = line.substring(34, 46).trim();    // northing N, column 33-46
                    s = s.concat(separator);
                    s = s.concat(northing);
                }

                if (line.length() >= 59) {
                    String height = line.substring(48, 59).trim();      // height H, column 47-59
                    s = s.concat(separator);
                    s = s.concat(height);
                }

                if ((line.length() >= 62) && !writeSimpleFormat && writeCodeColumn) {
                    String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");

                    String code = lineSplit[0].trim();              // code is the same as object type, column 62...
                    s = s.concat(separator);
                    s = s.concat(code);

                    for (int i = 1; i < lineSplit.length; i++) {
                        String attr = lineSplit[i].trim();
                        s = s.concat(separator);
                        s = s.concat(attr);
                    }
                }

                result.add(s.trim());
            }
        }

        return result;
    }

} // end of K2TXT
