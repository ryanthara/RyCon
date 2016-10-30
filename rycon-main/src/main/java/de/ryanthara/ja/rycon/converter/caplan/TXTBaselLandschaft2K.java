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

/**
 * This class provides functions to convert text formatted coordinate files from the geodata server
 * Basel Landschaft (Switzerland) into Caplan K formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TXTBaselLandschaft2K {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public TXTBaselLandschaft2K(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (Switzerland) into a K formatted file.
     *
     * @param useSimpleFormat  option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCodeColumn  option to write a found code into the K file
     * @param writeCommentLine option to write a comment line into the K file with basic information
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertTXTBaselLandschaft2K(boolean useSimpleFormat, boolean writeCodeColumn,
                                                         boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();

        // remove not needed headlines
        readStringLines.remove(0);

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        for (String line : readStringLines) {
            int valencyIndicator = -1;

            String[] lineSplit = line.trim().split("\\t", -1);

            String valency = BaseToolsCaplanK.valency;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;
            String northing = BaseToolsCaplanK.northing;
            String easting = BaseToolsCaplanK.easting;
            String height = BaseToolsCaplanK.height;

            // point number is always in column 1 (no '*', ',' and ';'), column 1 - 16
            String number = BaseToolsCaplanK.cleanPointNumberString(lineSplit[1]);

            switch (lineSplit.length) {
                case 5:     // HFP file
                    // easting (Y) is in column 3 -> column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));

                    // northing (X) is in column 4 -> column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 5, and not always valued (LFP file) -> column 47-59
                    height = String.format("%13s", NumberFormatter.fillDecimalPlace(lineSplit[4], 5));
                    Double d = Double.parseDouble(height);
                    if (d != 0d) {
                        valencyIndicator += 4;
                    }
                    break;

                case 6:     // LFP file
                    // use 'Versicherungsart' as code. It is in column 3 -> column 62...
                    if (writeCodeColumn) {
                        objectTyp = "|".concat(lineSplit[2]);
                    }

                    // easting (Y) is in column 4 -> column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));

                    // northing (X) is in column 5 -> column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[4], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 6, and not always valued (LFP file) -> column 47-59
                    if (lineSplit[5].equals("NULL")) {
                        height = String.format("%13s", NumberFormatter.fillDecimalPlace("-9999", 5));
                    } else {
                        height = String.format("%13s", NumberFormatter.fillDecimalPlace(lineSplit[5], 5));
                        if (Double.parseDouble(height) != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;
            }
            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            /*
            2. pick up the relevant elements from the blocks from every line, check ZF option
            if ZF option is checked, then use only no 7 x y z for K file
             */
            result.add(BaseToolsCaplanK.prepareStringBuilder(useSimpleFormat, number, valency, easting, northing, height,
                    freeSpace, objectTyp).toString());
        }
        return result;
    }

} // end of TXTBaselLandschaft2K
