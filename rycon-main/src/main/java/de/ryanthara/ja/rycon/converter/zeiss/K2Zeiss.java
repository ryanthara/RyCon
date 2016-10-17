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

/**
 * This class provides functions to convert coordinate files from Caplan K format into Zeiss REC files with
 * it's dialects (R4, R5, REC500 and M5).
 *
 * Example K file:
 * <p>
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8
 * !-------------------------------------------------------------------------------
 * ! The following data was created by RyCON Build xxx on 2016-09-06.
 * !-------------------------------------------------------------------------------
 *      GB1 7  2612259.5681  1256789.1990    256.90815 |10
 *      GB2 7  2612259.5681  1256789.1990    256.90815 |10
 *     1003 7  2612259.5681  1256789.1990    256.90815 |10|Att1|Att2
 *     1062 7  2612259.5681  1256789.1990    256.90815 |10
 * TF 1067G 4  2612259.5681  1256789.1990    256.90815 |10
 * NG 2156U 3  2612259.5681  1256789.1990      0.00000 |10
 */
public class K2Zeiss {

    private final ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files in Caplan K format.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public K2Zeiss(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Caplan K formatted measurement or coordinate based file into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public ArrayList<String> convertK2REC(String dialect) {
        ArrayList<String> result = new ArrayList<>();

        int lineNumber = 0;

        for (String line : readStringLines) {

            lineNumber = lineNumber + 1;

            String number = "", easting = "", northing = "", height = "", code = "";

            if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                if (line.length() >= 16) {
                    number = line.substring(0, 16).trim();          // point number (no '*', ',' and ';'), column 1 - 16
                }

                if (line.length() >= 32) {
                    easting = line.substring(20, 32).trim();        // easting E, column 19-32
                }

                if (line.length() >= 46) {
                    northing = line.substring(34, 46).trim();       // northing N, column 33-46
                }

                if (line.length() >= 59) {
                    height = line.substring(48, 59).trim();         // height H, column 47-59
                }

                if (line.length() >= 61) {
                    // TODO: 16.10.16 Check correct implementation of the first occurrence of the '|'
                    code = line.substring(61, line.indexOf("|"));   // possible code, column 61 till first '|' 
                }

                result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
            }
        }

        return result;
    }

} // end of K2Zeiss
