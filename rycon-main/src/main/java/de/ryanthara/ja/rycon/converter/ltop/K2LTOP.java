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
package de.ryanthara.ja.rycon.converter.ltop;

import de.ryanthara.ja.rycon.tools.NumberHelper;
import de.ryanthara.ja.rycon.tools.elements.RyPoint;

import java.util.ArrayList;

/**
 * Created by sebastian on 13.09.16.
 */
public class K2LTOP {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public K2LTOP(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a CAPLAN K coordinate file into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertK2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        BaseToolsLTOP.writeCommendLine(result, BaseToolsLTOP.cartesianCoordsIdentifier);

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                // prevent wrong output with empty strings of defined length from class
                number = BaseToolsLTOP.number;
                pointType = BaseToolsLTOP.pointType;
                toleranceCategory = BaseToolsLTOP.toleranceCategory;
                easting = BaseToolsLTOP.easting;
                northing = BaseToolsLTOP.northing;
                height = BaseToolsLTOP.height;
                geoid = BaseToolsLTOP.geoid;
                eta = BaseToolsLTOP.eta;
                xi = BaseToolsLTOP.xi;

                if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                    if (line.length() >= 16) {  // point number (no '*', ',' and ';'), column 1 - 16
                        number = String.format("%10s", line.substring(0, 16).trim());
                    }

                    if (line.length() >= 32) {  // easting E, column 19-32
                        easting = String.format("%12s", NumberHelper.fillDecimalPlace(line.substring(20, 32).trim(), 4));
                    }

                    if (line.length() >= 46) {  // northing N, column 33-46
                        northing = String.format("%12s", NumberHelper.fillDecimalPlace(line.substring(34, 46).trim(), 4));
                    }

                    if (line.length() >= 59) {  // height H, column 61-70
                        height = String.format("%10s", NumberHelper.fillDecimalPlace(line.substring(48, 59).trim(), 4));
                    }

                    // pick up the relevant elements from the blocks from every line
                    resultLine = BaseToolsLTOP.prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                            height, geoid, eta, xi);

                    // fill elements in a special object structure for duplicate elimination
                    if (eliminateDuplicates) {
                        BaseToolsLTOP.fillRyPoints(ryPoints, easting, northing, height, resultLine);
                    }

                    if (!resultLine.isEmpty()) {
                        result.add(resultLine);
                    }
                }
            }
        }

        result = eliminateDuplicates ? BaseToolsLTOP.eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? BaseToolsLTOP.sortResult(result) : result;
    }

} // end of K2LTOP
