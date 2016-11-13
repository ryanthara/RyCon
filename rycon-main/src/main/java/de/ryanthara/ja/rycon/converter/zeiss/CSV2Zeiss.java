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
import java.util.Arrays;
import java.util.List;

/**
 * Instances of this class provides functions to convert coordinate files in CSV format (comma separated values)
 * into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 */
public class CSV2Zeiss {

    private List<String[]> readCSVLines = null;

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSV2Zeiss(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a CSV file (nr;x;y;z or nr;code;x;y;z) into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     *
     * @return string lines of the target file
     */
    public ArrayList<String> convertCSV2REC(ZeissDialect dialect) {
        ArrayList<String> result = new ArrayList<>();

        int lineNumber = 0;

        for (String[] stringField : readCSVLines) {
            String code = "";
            String easting = "";
            String northing = "";
            String height = "";

            String number = stringField[0];

            lineNumber = lineNumber + 1;

            switch (stringField.length) {
                case 3:     // contains nr x y
                    easting = stringField[1];
                    northing = stringField[2];
                    break;

                case 4:     // contains nr x y z
                    easting = stringField[1];
                    northing = stringField[2];
                    height = stringField[3];
                    break;

                case 5:     // contains nr code x y z
                    code = stringField[1];
                    easting = stringField[2];
                    northing = stringField[3];
                    height = stringField[4];
                    break;

                default:
                    System.err.println("CSV2Zeiss.convertCSV2REC() : line contains less or more tokens " + Arrays.toString(stringField));
            }

            result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
        }

        return result;
    }

} // end of CSV2Zeiss
