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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from the
 * geodata server Basel Landschaft (Switzerland) into Caplan K files.
 *
 * <p>
 * Due to some reasons the geodata server Basel Landschaft (Switzerland) delivers
 * different file formats for location and height reference points.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2K {

    private static final Logger logger = LoggerFactory.getLogger(TxtBaselLandschaft2K.class.getName());

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text files
     * from the geodata server Basel Landschaft (Switzerland).
     *
     * @param lines list with coordinate lines
     */
    public TxtBaselLandschaft2K(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a text formatted file from the geodata server Basel Landschaft (Switzerland) into a K formatted file.
     *
     * @param useSimpleFormat  option to writer a reduced K file which is compatible to Z+F LaserControl
     * @param writeCodeColumn  option to writer a found code into the K file
     * @param writeCommentLine option to writer a comment line into the K file with basic information
     * @return converted K file as {@code List<String>}
     */
    public List<String> convert(boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        List<String> result = new ArrayList<>();

        removeHeadLine();

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

        for (String line : lines) {
            int valencyIndicator = -1;

            String[] values = line.trim().split("\\t", -1);

            String valency = BaseToolsCaplanK.valency;
            String freeSpace = BaseToolsCaplanK.freeSpace;
            String objectTyp = BaseToolsCaplanK.objectTyp;
            String northing = BaseToolsCaplanK.northing;
            String easting = BaseToolsCaplanK.easting;
            String height = BaseToolsCaplanK.height;

            // point number is always in column 1 (no '*', ',' and ';'), column 1 - 16
            String number = BaseToolsCaplanK.cleanPointNumberString(values[1]);

            switch (values.length) {
                case 5:     // HFP file
                    // easting (Y) is in column 3 -> column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[2], 4));

                    // northing (X) is in column 4 -> column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[3], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 5, and not always valued (LFP file) -> column 47-59
                    height = String.format("%13s", NumberFormatter.fillDecimalPlaces(values[4], 5));
                    valencyIndicator = BaseToolsCaplanK.getValencyIndicator(valencyIndicator, height);
                    break;

                case 6:     // LFP file
                    // use 'Versicherungsart' as code. It is in column 3 -> column 62...
                    if (writeCodeColumn) {
                        objectTyp = "|".concat(values[2]);
                    }

                    // easting (Y) is in column 4 -> column 19-32
                    easting = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[3], 4));

                    // northing (X) is in column 5 -> column 33-46
                    northing = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[4], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 6, and not always valued (LFP file) -> column 47-59
                    if (values[5].equals("NULL")) {
                        height = String.format("%13s", NumberFormatter.fillDecimalPlaces("-9999", 5));
                    } else {
                        height = String.format("%13s", NumberFormatter.fillDecimalPlaces(values[5], 5));
                        valencyIndicator = BaseToolsCaplanK.getValencyIndicator(valencyIndicator, height);
                    }
                    break;

                default:
                    logger.trace("Line contains less or more tokens ({}) than needed or allowed.", values.length);
                    break;
            }
            if (valencyIndicator > 0) {
                valency = Separator.WHITESPACE.getSign().concat(Integer.toString(valencyIndicator));
            }

            /*
            2. pick up the relevant values from the blocks from every line, check Z+F option
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
