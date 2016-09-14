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
import java.util.List;

/**
 * Created by sebastian on 13.09.16.
 */
public class CSV2LTOP {

    private List<String[]> readCSVLines = null;

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSV2LTOP(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Convert a CSV coordinate file (nr x y z) into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertCSV2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        BaseToolsLTOP.writeCommendLine(result, BaseToolsLTOP.cartesianCoordsIdentifier);

        for (String[] stringField : readCSVLines) {
            // prevent wrong output with empty strings of defined length from class
            pointType = BaseToolsLTOP.pointType;
            toleranceCategory = BaseToolsLTOP.toleranceCategory;
            height = BaseToolsLTOP.height;
            geoid = BaseToolsLTOP.geoid;
            eta = BaseToolsLTOP.eta;
            xi = BaseToolsLTOP.xi;

            // point number, column 1-10, aligned left
            number = String.format("%-10s", stringField[0].replaceAll("\\s+", "").trim());

            // easting (Y) is in column 3
            easting = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[1], 4));

            // northing (X) is in column 4
            northing = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[2], 4));

            // height (Z) is in column 5, but not always valued
            if (!stringField[3].equals("")) {
                height = String.format("%10s", NumberHelper.fillDecimalPlace(stringField[3], 4));
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

        result = eliminateDuplicates ? BaseToolsLTOP.eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? BaseToolsLTOP.sortResult(result) : result;
    }

} // end of CSV2LTOP
