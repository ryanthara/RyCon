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
package de.ryanthara.ja.rycon.converter.caplan;

import de.ryanthara.ja.rycon.tools.NumberFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides functions to convert comma separated coordinate files (CSV format) into Caplan K files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CSV2K {

    private List<String[]> readCSVLines = null;

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSV2K(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a CSV file (nr;x;y(;z) or nr;code;x;y;z) into a Caplan K file.
     *
     * @param useSimpleFormat  option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertCSV2K(boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        for (String[] stringField : readCSVLines) {
            int valencyIndicator = 0;

            String valency = BaseToolsCaplanK.valency;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;
            String easting = BaseToolsCaplanK.easting;
            String northing = BaseToolsCaplanK.northing;
            String height = BaseToolsCaplanK.height;

            // point number (no '*', ',' and ';'), column 1 - 16
            String number = BaseToolsCaplanK.cleanPointNumberString(stringField[0].replaceAll("\\s+", "").trim());

            switch (stringField.length) {
                case 3:     // contains nr x y
                    // easting E, column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[1], 4));

                    // northing N, column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[2], 4));
                    valencyIndicator = 3;
                    break;

                case 4:     // contains nr x y z
                    // easting E, column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[1], 4));

                    // northing N, column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[2], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 5, but not always valued
                    height = "";
                    if (!stringField[4].equals("")) {
                        // height H, column 47-59
                        height = String.format("%13s", NumberFormatter.fillDecimalPlace(stringField[3], 5));
                        Double d = Double.parseDouble(height);
                        if (d != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;

                case 5:     // contains nr code x y z
                    // code is in column 2 and the same as object type, column 62...
                    if (writeCodeColumn) {
                        objectTyp = "|".concat(stringField[1]);
                    }

                    // easting E, column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[2], 4));

                    // northing N, column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[3], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 5, but not always valued
                    height = "";
                    if (!stringField[4].equals("")) {
                        // height H, column 47-59
                        height = String.format("%13s", NumberFormatter.fillDecimalPlace(stringField[4], 5));
                        Double d = Double.parseDouble(height);
                        if (d != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;

                default:
                    System.err.println("CSV2K.convertCSV2K() : line contains less or more tokens " + stringField.length);
                    break;

            }

            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            /*
            pick up the relevant elements from the blocks from every line, check ZF option
            if ZF option is checked, then use only no 7 x y z for K file
             */
            result.add(BaseToolsCaplanK.prepareStringBuilder(useSimpleFormat, number, valency, easting, northing, height,
                    freeSpace, objectTyp).toString());
        }
        return result;
    }

} // end of CSV2K
