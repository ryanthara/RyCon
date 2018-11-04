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

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files from the geodata server
 * Basel Stadt (Switzerland) into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 */
public class CsvBaselStadt2Zeiss {

    private final List<String[]> lines;

    /**
     * Creates a converter with a list for the read line based comma separated
     * values (CSV) file from the geodata server Basel Stadt (Switzerland).
     *
     * @param lines list with lines as string array
     */
    public CsvBaselStadt2Zeiss(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return converted Zeiss REC file as {@code List<String>}
     */
    public List<String> convert(ZeissDialect dialect) {
        List<String> result = new ArrayList<>();

        int lineNumber = 0;
        String number, code, easting, northing, height;

        removeHeadLine();

        for (String[] values : lines) {
            lineNumber = lineNumber + 1;
            // point number is in column 1
            number = values[0].replaceAll("\\s+", "").trim();

            // code is in column 2
            code = values[1];

            // easting (Y) is in column 3
            easting = values[2];

            // northing (X) is in column 4
            northing = values[3];

            // height (Z) is in column 5, but not always valued
            if (!values[4].equals("")) {
                height = values[4];
            } else {
                height = "";
            }

            result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
        }

        return List.copyOf(result);
    }

    private void removeHeadLine() {
        lines.remove(0);
    }

}
