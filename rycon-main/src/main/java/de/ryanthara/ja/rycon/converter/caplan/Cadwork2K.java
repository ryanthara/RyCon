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
 * This class provides functions to convert a coordinate file from Cadwork CAD program (node.dat)
 * into a Caplan K file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2K {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files from Cadwork CAD program in node.dat file format.
     *
     * @param readStringLines {@code ArrayList<String>} with read lines from node.dat file
     */
    public Cadwork2K(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a coordinate file from Cadwork CAD program (node.dat) into a Caplan K file.
     *
     * @param useSimpleFormat  option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     * @param writeCodeColumn  option to write the code column into the K file
     *
     * @return converted Caplan K file as ArrayList<String>
     */
    public ArrayList<String> convertCadwork2K(boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        for (String line : readStringLines) {
            int valencyIndicator;

            String[] lineSplit = line.trim().split("\\s+", -1);

            String valency = BaseToolsCaplanK.valency;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;

            // point number (no '*', ',' and ';'), column 1 - 16
            String number = BaseToolsCaplanK.cleanPointNumberString(lineSplit[5]);

            // easting E, column 19-32
            String easting = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[1], 4));

            // northing N, column 33-46
            String northing = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));
            valencyIndicator = 3;

            // height H, column 47-59
            String height = String.format("%13s", NumberFormatter.fillDecimalPlace(lineSplit[3], 5));
            if (Double.parseDouble(height) != 0d) {
                valencyIndicator += 4;
            }

            // code is the same as object type, column 62...
            if (writeCodeColumn) {
                objectTyp = "|".concat(lineSplit[4]);
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

} // end of Cadwork2K
