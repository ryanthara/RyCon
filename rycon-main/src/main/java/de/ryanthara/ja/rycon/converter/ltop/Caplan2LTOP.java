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

import de.ryanthara.ja.rycon.elements.CaplanBlock;
import de.ryanthara.ja.rycon.elements.RyPoint;
import de.ryanthara.ja.rycon.tools.NumberFormatter;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert a Caplan K formatted coordinate file
 * into a LTOP coordinate file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2LTOP {

    private ArrayList<String> readStringLines;

    /**
     * Constructs an instance of this class with the read Caplan K file {@link ArrayList} string as parameter.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in Caplan K format
     */
    public Caplan2LTOP(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Caplan K coordinate file into a KOO file for LTOP.
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

                CaplanBlock caplanBlock = new CaplanBlock(line);

                if (caplanBlock.getNumber() != null) {
                    number = String.format("%10s", caplanBlock.getNumber());
                }

                if (caplanBlock.getEasting() != null) {
                    easting = String.format("%12s", NumberFormatter.fillDecimalPlace(caplanBlock.getEasting(), 4));
                }

                if (caplanBlock.getNorthing() != null) {
                    northing = String.format("%12s", NumberFormatter.fillDecimalPlace(caplanBlock.getNorthing(), 4));
                }

                if (caplanBlock.getHeight() != null) {
                    height = String.format("%10s", NumberFormatter.fillDecimalPlace(caplanBlock.getHeight(), 4));
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

} // end of Caplan2LTOP
