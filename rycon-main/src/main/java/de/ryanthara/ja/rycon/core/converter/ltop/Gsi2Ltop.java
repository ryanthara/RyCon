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

import de.ryanthara.ja.rycon.core.converter.gsi.GsiDecoder;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.util.NumberFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert Leica Geosystems GSI format
 * (GSI8 and GSI16) coordinate and measurement files into LTOP KOO files.
 *
 * <p>
 * With a little 'intelligence' it is possible to create the needed coordinate file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi2Ltop {
    private static final Logger logger = LoggerFactory.getLogger(Gsi2Ltop.class.getName());

    private final GsiDecoder gsiDecoder;

    /**
     * Creates a converter with a list for the read line based
     * Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public Gsi2Ltop(List<String> lines) {
        gsiDecoder = new GsiDecoder(lines);
    }

    /**
     * Converts a Leica Geosystems GSI coordinate file into a KOO file for LTOP.
     * <p>
     * In this RyCON version only the WIs 81 till 86 are supported.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     * @return converted KOO file
     */
    public List<String> convert(boolean eliminateDuplicates, boolean sortOutputFile) {
        List<String> result = new ArrayList<>();
        List<RyPoint> ryPoints = new ArrayList<>();

        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        BaseToolsLtop.writeCommendLine(result, BaseToolsLtop.cartesianCoordsIdentifier);

        for (List<GsiBlock> blocksInLine : gsiDecoder.getDecodedLinesOfGsiBlocks()) {
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

            for (int i = 0; i < gsiDecoder.getDecodedLinesOfGsiBlocks().size(); i++) {
                for (GsiBlock block : blocksInLine) {
                    String s = block.toPrintFormatCsv();

                    switch (block.getWordIndex()) {
                        case 11:        // point number, column 1-10, aligned left
                            number = String.format("%-10s", s);
                            break;

                        case 81:        // easting E, column 33-44
                            easting = String.format("%12s", NumberFormatter.fillDecimalPlaces(s, 4));
                            break;

                        case 82:        // northing N, column 45-56
                            northing = String.format("%12s", NumberFormatter.fillDecimalPlaces(s, 4));
                            break;

                        case 83:        // height H, column 61-70
                            height = String.format("%10s", NumberFormatter.fillDecimalPlaces(s, 4));
                            break;

                        case 84:        // easting E0, column 33-44
                            easting = String.format("%12s", NumberFormatter.fillDecimalPlaces(s, 4));
                            break;

                        case 85:        // northing N0, column 45-56
                            northing = String.format("%12s", NumberFormatter.fillDecimalPlaces(s, 4));
                            break;

                        case 86:        // height H0, column 61-70
                            height = String.format("%10s", NumberFormatter.fillDecimalPlaces(s, 4));
                            break;

                        default:
                            logger.trace("Line contains unknown word index ({}).", block.toPrintFormatCsv());
                            break;
                    }
                }
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

        result = eliminateDuplicates ? BaseToolsLtop.eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? BaseToolsLtop.sortResult(result) : new ArrayList<>(result);
    }

}
