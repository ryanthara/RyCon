/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements basic operations on csv based measurement and coordinate files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files. During the development of RyCON the text and csv
 * file operations are split into separate classes.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 * <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
public class FileToolsCSV {

    private ArrayList<String> readStringLines;
    private List<String[]> list;

    /**
     * Class Constructor with parameter.
     * <p>
     * As parameter the {@code ArrayList<String>} object with the lines in text format is used.
     *
     * @param arrayList {@code ArrayList<String>} with lines in text format
     */
    public FileToolsCSV(ArrayList<String> arrayList) {
        this.readStringLines = arrayList;
    }

    /**
     * Class Constructor with parameter.
     * <p>
     * As parameter the {@code List<String[]>} object with the lines in csv format is used.
     *
     * @param list {@code List<String[]>} with lines in csv format
     */
    public FileToolsCSV(List<String[]> list) {
        this.list = list;
    }

    /**
     * Converts a text file from cadwork (node.dat) into a CSV file with the given separator sign.
     *
     * @param separator        separator sign to use for conversion
     * @param writeCommentLine writes an comment line with information about the column content
     * @param useCodeColumn    Use the code column from node.dat
     * @param useZeroHeights   Use heights with zero (0.000) values
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

            String[] lineSplit = readStringLines.get(0).trim().split("\\s+");

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
            String[] lineSplit = line.trim().split("\\s+");

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
                if (!lineSplit[4].equals("0.000000")) {
                    s = s.concat(lineSplit[3]);
                }
            }
            result.add(s.trim());
        }

        return result;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (switzerland) into a CSV format file.
     * <p>
     * With a parameter it is possible to distinguish between comma or semicolon as separator.
     *
     * @param separator separator sign as {@code String}
     *
     * @return converted {@code ArrayList<String>} with lines of CSV format
     */
    public ArrayList<String> convertCSVBaselStadt2CSV(String separator) {
        ArrayList<String> result = new ArrayList<>();

        for (String[] stringField : list) {
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

    /**
     * Converts a TXT file into a CSV file with the given separator sign.
     *
     * @param separator separator sign to use for conversion
     *
     * @return converted CSV file
     */
    public ArrayList<String> convertTXT2CSV(String separator) {
        ArrayList<String> result = new ArrayList<>();

        for (String line : readStringLines) {
            // get rid off one or more empty signs at the beginning and end of the given string
            line = line.trim();
            result.add(line.replaceAll("\\s+", separator));
        }
        return result;
    }


}
