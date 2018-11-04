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

import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.util.NumberFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from
 * the geodata server Basel Stadt (Switzerland) into Caplan K files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2K {

    private final List<String[]> lines;

    /**
     * Creates a converter with a list for the read line based comma separated
     * values (CSV) file from the geodata server Basel Stadt (Switzerland).
     *
     * @param lines list with lines as string array
     */
    public CsvBaselStadt2K(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a Zeiss REC file.
     *
     * @param useSimpleFormat  option to writer a reduced K file which is compatible to Z+F LaserControl
     * @param writeCommentLine option to writer a comment line into the K file with basic information
     * @return converted K file as {@code List<String>}
     */
    public List<String> convert(boolean useSimpleFormat, boolean writeCommentLine) {
        List<String> result = new ArrayList<>();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        removeHeadLine();

        for (String[] values : lines) {
            int valencyIndicator;

            String valency = BaseToolsCaplanK.valency;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;

            // point number (no '*', ',' and ';'), column 1 - 16
            String number = BaseToolsCaplanK.cleanPointNumberString(values[0].replaceAll("\\s+", "").trim());

            // easting E, column 19-32
            String easting = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[2], 4));

            // northing N, column 33-46
            String northing = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[3], 4));
            valencyIndicator = 3;

            // height (Z) is in column 5, but not always valued
            String height = "";
            if (!values[4].equals("")) {
                // height H, column 47-59
                height = String.format("%13s", NumberFormatter.fillDecimalPlaces(values[4], 5));
                valencyIndicator = BaseToolsCaplanK.getValencyIndicator(valencyIndicator, height);
            }

            if (valencyIndicator > 0) {
                valency = Separator.WHITESPACE.getSign().concat(Integer.toString(valencyIndicator));
            }

            /*
            pick up the relevant elements from the blocks from every line, check Z+F option
            if Z+F option is checked, then use only no 7 x y z for K file
             */
            result.add(BaseToolsCaplanK.prepareCaplanLine(useSimpleFormat, number, valency, easting, northing, height,
                    freeSpace, objectTyp).toString());
        }
        return List.copyOf(result);
    }

    private void removeHeadLine() {
        lines.remove(0);
    }

}
