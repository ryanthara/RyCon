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

import de.ryanthara.ja.rycon.elements.RyPoint;
import de.ryanthara.ja.rycon.tools.NumberFormatter;

import java.util.ArrayList;

/**
 * Created by sebastian on 13.09.16.
 */
public class TXTBaselLandschaft2LTOP {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public TXTBaselLandschaft2LTOP(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a TXT coordinate file from the geodata server Basel Landschaft into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertTXTBaselLandschaft2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();

        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        BaseToolsLTOP.writeCommendLine(result, BaseToolsLTOP.cartesianCoordsIdentifier);

        // remove comment line
        readStringLines.remove(0);

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

                String[] lineSplit = line.trim().split("\\t", -1);

                switch (lineSplit.length) {
                    case 5:     // HFP file
                        number = String.format("%10s", lineSplit[1].trim());
                        easting = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));
                        northing = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
                        height = String.format("%10s", NumberFormatter.fillDecimalPlace(lineSplit[4], 4));
                        break;

                    case 6:     // LFP file
                        number = String.format("%10s", lineSplit[1]);
                        easting = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
                        northing = String.format("%12s", NumberFormatter.fillDecimalPlace(lineSplit[4], 4));

                        // prevent 'NULL' element in height
                        if (!lineSplit[5].equals("NULL")) {
                            height = String.format("%10s", NumberFormatter.fillDecimalPlace(lineSplit[5], 4));
                        }
                        break;

                    default:
                        System.err.println("TXTBaselLandschaft2LTOP.convertTXTBaselLandschaft2KOO() : line contains less or more tokens " + line);
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

        result = eliminateDuplicates ? BaseToolsLTOP.eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? BaseToolsLTOP.sortResult(result) : result;
    }

} // end of TXTBaselLandschaft2LTOP
