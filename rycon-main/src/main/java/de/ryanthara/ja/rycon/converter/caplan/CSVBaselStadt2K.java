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
import java.util.List;

/**
 * Instances of this class provides functions to convert comma separated files (CSV) formatted coordinate files
 * from the geodata server Basel Stadt (Switzerland) into comma separated files (CSV).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CSVBaselStadt2K {

    private List<String[]> readCSVLines = null;

    /**
     * Constructs a new instance of this class with a parameter for read line based comma separated values (CSV) files
     * from the geodata server Basel Stadt (Switzerland).
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSVBaselStadt2K(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a Zeiss REC file.
     *
     * @param useSimpleFormat  option to write a reduced K file which is compatible to Z+F LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     *
     * @return converted K file as {@code ArrayList<String>}
     */
    public ArrayList<String> convertCSVBaselStadt2K(boolean useSimpleFormat, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        // remove comment line
        readCSVLines.remove(0);

        for (String[] stringField : readCSVLines) {
            int valencyIndicator;

            String valency = BaseToolsCaplanK.valency;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;

            // point number (no '*', ',' and ';'), column 1 - 16
            String number = BaseToolsCaplanK.cleanPointNumberString(stringField[0].replaceAll("\\s+", "").trim());

            // easting E, column 19-32
            String easting = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[2], 4));

            // northing N, column 33-46
            String northing = String.format("%14s", NumberFormatter.fillDecimalPlace(stringField[3], 4));
            valencyIndicator = 3;

            // height (Z) is in column 5, but not always valued
            String height = "";
            if (!stringField[4].equals("")) {
                // height H, column 47-59
                height = String.format("%13s", NumberFormatter.fillDecimalPlace(stringField[4], 5));
                Double d = Double.parseDouble(height);
                if (d != 0d) {
                    valencyIndicator += 4;
                }
            }

            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            /*
            pick up the relevant elements from the blocks from every line, check Z+F option
            if Z+F option is checked, then use only no 7 x y z for K file
             */
            result.add(BaseToolsCaplanK.prepareCaplanLine(useSimpleFormat, number, valency, easting, northing, height,
                    freeSpace, objectTyp).toString());
        }
        return result;
    }

} // end of CSVBaselStadt2K
