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

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.tools.NumberHelper;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;
import de.ryanthara.ja.rycon.tools.elements.RyPoint;

import java.util.ArrayList;

/**
 * Created by sebastian on 13.09.16.
 */
public class GSI2LTOP {

    private BaseToolsGSI baseToolsGSI;

    /**
     * Class constructor for read line based text files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GSI2LTOP(ArrayList<String> readStringLines) {
        baseToolsGSI = new BaseToolsGSI(readStringLines);
    }

    /**
     * Converts a Leica GSI coordinate file into a KOO file for LTOP.
     * <p>
     * In this RyCON version only the WIs 81 till 86 are supported.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertGSI2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();

        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        BaseToolsLTOP.writeCommendLine(result, BaseToolsLTOP.cartesianCoordsIdentifier);

        for (ArrayList<GSIBlock> blocksAsLine : baseToolsGSI.getEncodedLinesOfGSIBlocks()) {
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

            for (int i = 0; i < baseToolsGSI.getFoundWordIndices().size(); i++) {
                for (GSIBlock block : blocksAsLine) {
                    String s = block.toPrintFormatCSV();

                    switch (block.getWordIndex()) {
                        case 11:        // point number, column 1-10, aligned left
                            number = String.format("%-10s", s);
                            break;
                        case 81:        // easting E, column 33-44
                            easting = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 82:        // northing N, column 45-56
                            northing = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 83:        // height H, column 61-70
                            height = String.format("%10s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 84:        // easting E0, column 33-44
                            easting = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 85:        // northing N0, column 45-56
                            northing = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 86:        // height H0, column 61-70
                            height = String.format("%10s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                    }
                }
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

} // end of GSI2LTOP
