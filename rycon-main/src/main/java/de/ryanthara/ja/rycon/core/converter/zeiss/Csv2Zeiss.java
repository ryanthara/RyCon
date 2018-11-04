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
import java.util.Arrays;
import java.util.List;

/**
 * A converter with functions to convert comma separated values (CSV) coordinate
 * files into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 */
public class Csv2Zeiss {

    private static final Logger logger = LoggerFactory.getLogger(Csv2Zeiss.class.getName());

    private final List<String[]> lines;

    /**
     * Creates a converter with a list for the read line based comma separated values (CSV) files.
     *
     * @param lines list with lines of comma separated values (CSV)
     */
    public Csv2Zeiss(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a CSV file (nr;x;y;z or nr;code;x;y;z) into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public List<String> convert(ZeissDialect dialect) {
        List<String> result = new ArrayList<>();

        int lineNumber = 0;

        for (String[] values : lines) {
            String code = "";
            String easting = "";
            String northing = "";
            String height = "";

            String number = values[0];

            lineNumber = lineNumber + 1;

            switch (values.length) {
                case 3:     // contains nr x y
                    easting = values[1];
                    northing = values[2];
                    break;

                case 4:     // contains nr x y z
                    easting = values[1];
                    northing = values[2];
                    height = values[3];
                    break;

                case 5:     // contains nr code x y z
                    code = values[1];
                    easting = values[2];
                    northing = values[3];
                    height = values[4];
                    break;

                default:
                    logger.trace("Line contains less or more tokens ({}) than needed or allowed.\n{}", values.length, Arrays.toString(values));
            }

            result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
        }

        return List.copyOf(result);
    }

}
