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

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.tools.NumberHelper;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.ArrayList;

/**
 * This class provides functions to convert coordinate and measurement files from Leica GSI format (GSI8 and GSI16)
 * into Caplan K files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSI2K {

    private BaseToolsGSI baseToolsGSI;

    /**
     * Class constructor for read line based text files in the Leica GSI format.
     * <p>
     * The differentiation of the content is done by the called method and it's content analyze functionality.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GSI2K(ArrayList<String> readStringLines) {
        baseToolsGSI = new BaseToolsGSI(readStringLines);
    }

    /**
     * Converts a Leica GSI file into a CAPLAN K file.
     *
     * @param useSimpleFormat  option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertGSI2K(boolean useSimpleFormat, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        for (ArrayList<GSIBlock> blocksAsLines : baseToolsGSI.getEncodedLinesOfGSIBlocks()) {
            StringBuilder stringBuilder = new StringBuilder();

            // prevent wrong output with empty strings of defined length from class
            String number = "";
            String valency = BaseToolsCaplanK.valency;
            String easting = BaseToolsCaplanK.easting;
            String northing = BaseToolsCaplanK.northing;
            String height = BaseToolsCaplanK.height;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;
            String attr = "";

            for (int i = 0; i < baseToolsGSI.getFoundAllWordIndices().size(); i++) {
                int valencyIndicator = 0;

                for (GSIBlock block : blocksAsLines) {
                    String s = block.toPrintFormatCSV();

                    switch (block.getWordIndex()) {
                        case 11:        // point number (no '*', ',' and ';'), column 1 - 16
                            number = BaseToolsCaplanK.cleanPointNumberString(s);
                            break;
                        case 41:        // code is the same as object type, column 62...
                            objectTyp = "|".concat(s);
                            break;
                        case 71:        // comment 1, used as Attr1
                        case 72:        // comment 2, used as Attr2
                        case 73:        // comment 3, used as Attr3
                        case 74:        // comment 4, used as Attr4
                        case 75:        // comment 5, used as Attr5
                        case 76:        // comment 6, used as Attr6
                        case 77:        // comment 7, used as Attr7
                        case 78:        // comment 8, used as Attr8
                        case 79:        // comment 9, used as Attr9
                            attr = attr.concat("|".concat(s));
                            break;
                        case 81:        // easting E, column 19-32
                            easting = String.format("%14s", NumberHelper.fillDecimalPlace(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 82:        // northing N, column 33-46
                            northing = String.format("%14s", NumberHelper.fillDecimalPlace(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 83:        // height H, column 47-59
                            height = String.format("%13s", NumberHelper.fillDecimalPlace(s, 5));
                            valencyIndicator += 4;
                            break;
                        case 84:        // easting E0, column 19-32
                            easting = String.format("%14s", NumberHelper.fillDecimalPlace(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 85:        // northing N0, column 33-46
                            northing = String.format("%14s", NumberHelper.fillDecimalPlace(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 86:        // height H0, column 47-59
                            height = String.format("%13s", NumberHelper.fillDecimalPlace(s, 5));
                            valencyIndicator += 4;
                            break;
                    }

                    if (valencyIndicator > 0) {
                        valency = " ".concat(Integer.toString(valencyIndicator));
                    }
                }

                /*
                pick up the relevant elements from the blocks from every line, check ZF option
                if ZF option is checked, then use only no 7 x y z for K file
                 */
                stringBuilder = BaseToolsCaplanK.prepareStringBuilder(useSimpleFormat, number, valency, easting, northing, height,
                        freeSpace, objectTyp);

                if ((!useSimpleFormat) && (!attr.equals(""))) {
                    stringBuilder.append(attr);
                }

                // clean up some variables after line reading is finished
                attr = "";
            }
            result.add(stringBuilder.toString());
        }

        return result;
    }

} // end of GSI2K
