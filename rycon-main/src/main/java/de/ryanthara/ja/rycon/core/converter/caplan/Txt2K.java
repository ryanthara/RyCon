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
package de.ryanthara.ja.rycon.core.converter.caplan;

import de.ryanthara.ja.rycon.util.NumberFormatter;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert text formatted coordinate files into Caplan K files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Txt2K {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for read line based coordinate files in text format.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Txt2K(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text file (nr x y (z) or nr code x y z) into a Caplan K file.
     *
     * @param useSimpleFormat  option to write a reduced K file which is compatible to Z+F LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     * @param writeCodeColumn  option to write a found code into the K file
     *
     * @return converted K file as {@code ArrayList<String>}
     */
    public ArrayList<String> convertTXT2K(boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        for (String line : readStringLines) {
            int valencyIndicator = -1;

            String[] lineSplit = line.trim().split("\\s+");

            String valency = BaseToolsCaplanK.valency;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;
            String northing = BaseToolsCaplanK.northing;
            String easting = BaseToolsCaplanK.easting;
            String height = BaseToolsCaplanK.height;

            // point number is always in column 1 (no '*', ',' and ';'), column 1 - 16
            String number = BaseToolsCaplanK.cleanPointNumberString(lineSplit[0]);

            switch (lineSplit.length) {
                case 3:     // line contains no height
                    // easting (Y) is in column 2 -> column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[1], 4));

                    // northing (X) is in column 3 -> column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));
                    valencyIndicator = 3;
                    break;

                case 4:     // line contains no code
                    // easting (Y) is in column 2 -> column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[1], 4));

                    // northing (X) is in column 3 -> column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 4 -> column 47-59
                    height = String.format("%13s", NumberFormatter.fillDecimalPlace(lineSplit[3], 5));
                    Double d = Double.parseDouble(height);
                    if (d != 0d) {
                        valencyIndicator += 4;
                    }
                    break;

                case 6:     // line contains code at second position and height
                    // code is in column 2 -> column 62...
                    if (writeCodeColumn) {
                        objectTyp = "|".concat(lineSplit[1]);
                    }

                    // easting (Y) is in column 4 -> column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));

                    // northing (X) is in column 5 -> column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 6, and not always valued (LFP file) -> column 47-59
                    if (lineSplit[5].equals("NULL")) {
                        height = String.format("%13s", NumberFormatter.fillDecimalPlace("-9999", 4));
                    } else {
                        height = String.format("%13s", NumberFormatter.fillDecimalPlace(lineSplit[4], 5));
                        if (Double.parseDouble(height) != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;

                default:
                    System.err.println("Txt2K.convertTXT2K() : line contains less or more tokens " + line);
            }
            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            /*
            pick up the relevant elements from the blocks from every line, check Z+F option
            if Z+F option is checked, then use only no 7 x y z for K file
             */
            result.add(BaseToolsCaplanK.prepareCaplanLine(useSimpleFormat, number, valency, easting, northing, height,
                    freeSpace, objectTyp).toString());
        }

        return result;
    }

} // end of Txt2K
