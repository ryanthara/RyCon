/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.converter.zeiss
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
package de.ryanthara.ja.rycon.core.converter.zeiss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from the geodata server Basel
 * Landschaft (Switzerland) into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Zeiss {

    private static final Logger logger = LoggerFactory.getLogger(TxtBaselLandschaft2Zeiss.class.getName());

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text files
     * from the geodata server Basel Landschaft (Switzerland).
     *
     * @param lines list with coordinate lines
     */
    public TxtBaselLandschaft2Zeiss(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a text formatted coordinate file from the geodata server 'Basel Landschaft' (Switzerland)
     * into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public List<String> convert(ZeissDialect dialect) {
        List<String> result = new ArrayList<>();

        removeHeadLine();

        int lineNumber = 0;

        for (String line : lines) {
            String[] values = line.trim().split("\\t", -1);

            String number, code, easting, northing, height;

            lineNumber = lineNumber + 1;

            switch (values.length) {
                case 5:     // HFP file
                    /*
                    Art	Nummer	X	Y	Z
                    HFP2	NC17014	2624601.9	1262056.014	348.298
                     */
                    code = values[0];
                    number = values[1];
                    easting = values[2];
                    northing = values[3];
                    height = values[4];

                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                    break;

                case 6:     // LFP file
                    /*
                    Art	Nummer	VArt	X	Y	Z
                    LFP2	10681160	0	2623800.998	1263204.336	328.05
                    */
                    code = values[0];
                    number = values[1];
                    easting = values[3];
                    northing = values[4];
                    height = "";

                    // prevent 'NULL' element in height
                    if (!values[5].equals("NULL")) {
                        height = values[5];
                    }

                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                    break;

                default:
                    logger.trace("Line contains less or more tokens ({}) than needed or allowed.", values.length);
                    break;
            }
        }

        return List.copyOf(result);
    }

    private void removeHeadLine() {
        lines.remove(0);
    }

}
