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
package de.ryanthara.ja.rycon.converter.zeiss;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides functions to convert coordinate files from the geodata server Basel Stadt (Switzerland)
 * into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 */
public class CSVBaselStadt2Zeiss {

    private List<String[]> readCSVLines = null;

    /**
     * Class constructor for read line based CSV files from the geodata server Basel Stadt (Switzerland).
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSVBaselStadt2Zeiss(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     *
     * @return converted Zeiss REC file as {@code ArrayList<String>}
     */
    public ArrayList<String> convertCSVBaselStadt2REC(ZeissDialect dialect) {
        ArrayList<String> result = new ArrayList<>();

        int lineNumber = 0;
        String number, code, easting, northing, height;

        // remove comment line
        readCSVLines.remove(0);

        for (String[] stringField : readCSVLines) {
            lineNumber = lineNumber + 1;
            // point number is in column 1
            number = stringField[0].replaceAll("\\s+", "").trim();

            // code is in column 2
            code = stringField[1];

            // easting (Y) is in column 3
            easting = stringField[2];

            // northing (X) is in column 4
            northing = stringField[3];

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                height = stringField[4];
            } else {
                height = "-9999";
            }

            result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
        }

        return result;
    }

} // end of CSVBaselStadt2Zeiss
