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
package de.ryanthara.ja.rycon.core.converter.ltop;

import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.util.NumberFormatter;

import java.util.ArrayList;

/**
 * This class provides functions to convert text formatted coordinate files into LTOP KOO files.
 */
public class Txt2Ltop {

    private final ArrayList<String> readStringLines;

    /**
     * Class constructor for reader line based coordinate files in text format.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Txt2Ltop(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a TXT coordinate file into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertTXT2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        BaseToolsLtop.writeCommendLine(result, BaseToolsLtop.cartesianCoordsIdentifier);

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                // prevent wrong output with empty strings of defined length from class
                number = BaseToolsLtop.number;
                pointType = BaseToolsLtop.pointType;
                toleranceCategory = BaseToolsLtop.toleranceCategory;
                easting = BaseToolsLtop.easting;
                northing = BaseToolsLtop.northing;
                height = BaseToolsLtop.height;
                geoid = BaseToolsLtop.geoid;
                eta = BaseToolsLtop.eta;
                xi = BaseToolsLtop.xi;

                String[] lineSplit = line.trim().split("\\s+");

                switch (lineSplit.length) {
                    case 4:     // nr x y z
                        number = String.format("%10s", lineSplit[0].trim());
                        easting = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[1], 4));
                        northing = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));
                        height = String.format("%10s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
                        break;

                    case 5:     // nr code x y z
                        number = String.format("%10s", lineSplit[0]);
                        easting = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));
                        northing = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
                        height = String.format("%10s", NumberFormatter.fillDecimalPlace(lineSplit[4], 4));
                        break;

                    default:
                        System.err.println("Txt2Ltop.convertTXT2KOO() : line contains less or more tokens " + line);
                }

                // pick up the relevant elements from the blocks from every line
                resultLine = BaseToolsLtop.prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                        height, geoid, eta, xi);

                // fill elements in a special object structure for duplicate elimination
                if (eliminateDuplicates) {
                    BaseToolsLtop.fillRyPoints(ryPoints, easting, northing, height, resultLine);
                }

                if (!resultLine.isEmpty()) {
                    result.add(resultLine);
                }
            }
        }

        result = eliminateDuplicates ? BaseToolsLtop.eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? BaseToolsLtop.sortResult(result) : result;
    }

} // end of Txt2Ltop
