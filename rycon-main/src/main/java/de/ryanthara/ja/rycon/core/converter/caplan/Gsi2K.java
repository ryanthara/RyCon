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
package de.ryanthara.ja.rycon.core.converter.caplan;

import de.ryanthara.ja.rycon.core.converter.gsi.BaseToolsGsi;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.util.NumberFormatter;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert coordinate and measurement files from
 * the Leica GSI format (GSI8 and GSI16) into Caplan K formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi2K {

    private final BaseToolsGsi baseToolsGsi;

    /**
     * Constructs a new instance of this class with a parameter for reader line based text files in the Leica GSI format.
     * <p>
     * The differentiation of the content is done by the called method and it's content analyze functionality.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Gsi2K(ArrayList<String> readStringLines) {
        baseToolsGsi = new BaseToolsGsi(readStringLines);
    }

    /**
     * Converts a Leica GSI file into a CAPLAN K file.
     *
     * @param useSimpleFormat  option to writer a reduced K file which is compatible to Z+F LaserControl
     * @param writeCommentLine option to writer a comment line into the K file with basic information
     *
     * @return converted K file as {@code ArrayList<String>}
     */
    public ArrayList<String> convertGSI2K(boolean useSimpleFormat, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        for (ArrayList<GsiBlock> blocksInLine : baseToolsGsi.getEncodedLinesOfGSIBlocks()) {
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

            for (int i = 0; i < baseToolsGsi.getFoundAllWordIndices().size(); i++) {
                int valencyIndicator = 0;

                for (GsiBlock block : blocksInLine) {
                    String printFormatCSV = block.toPrintFormatCsv();

                    switch (block.getWordIndex()) {
                        case 11:        // point number (no '*', ',' and ';'), column 1 - 16
                            number = BaseToolsCaplanK.cleanPointNumberString(printFormatCSV);
                            break;

                        case 41:        // code is the same as object type, column 62...
                            objectTyp = "|".concat(printFormatCSV);
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
                            attr = attr.concat("|".concat(printFormatCSV));
                            break;

                        case 81:        // easting E, column 19-32
                            easting = String.format("%14s", NumberFormatter.fillDecimalPlace(printFormatCSV, 4));
                            valencyIndicator = 3;
                            break;

                        case 82:        // northing N, column 33-46
                            northing = String.format("%14s", NumberFormatter.fillDecimalPlace(printFormatCSV, 4));
                            valencyIndicator = 3;
                            break;

                        case 83:        // height H, column 47-59
                            height = String.format("%13s", NumberFormatter.fillDecimalPlace(printFormatCSV, 5));
                            valencyIndicator += 4;
                            break;

                        case 84:        // easting E0, column 19-32
                            easting = String.format("%14s", NumberFormatter.fillDecimalPlace(printFormatCSV, 4));
                            valencyIndicator = 3;
                            break;

                        case 85:        // northing N0, column 33-46
                            northing = String.format("%14s", NumberFormatter.fillDecimalPlace(printFormatCSV, 4));
                            valencyIndicator = 3;
                            break;

                        case 86:        // height H0, column 47-59
                            height = String.format("%13s", NumberFormatter.fillDecimalPlace(printFormatCSV, 5));
                            valencyIndicator += 4;
                            break;

                        default:
                            System.err.println("Gsi2K.convertGSI2K() : line contains unknown word index " + printFormatCSV);
                    }

                    if (valencyIndicator > 0) {
                        valency = " ".concat(Integer.toString(valencyIndicator));
                    }
                }

                /*
                pick up the relevant elements from the blocks from every line, check Z+F option
                if Z+F option is checked, then use only no 7 y x z for K file
                 */
                stringBuilder = BaseToolsCaplanK.prepareCaplanLine(useSimpleFormat, number, valency, easting, northing, height,
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

} // end of Gsi2K
