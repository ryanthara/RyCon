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
 * Instances of this class provides functions to convert coordinate files from Cadwork CAD program into
 * Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2Zeiss {

    private final ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files from Cadwork CAD program in node.dat file format.
     *
     * @param readStringLines {@code ArrayList<String>} with read lines from node.dat file
     */
    public Cadwork2Zeiss(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     *
     * @return string lines of the target file
     */
    public ArrayList<String> convertCadwork2REC(ZeissDialect dialect) {
        ArrayList<String> result = new ArrayList<>();

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        int lineNumber = 0;

        String number, code, easting, northing, height;

        for (String line : readStringLines) {
            lineNumber = lineNumber + 1;
            String[] lineSplit = line.trim().split("\\s+", -1);

            number = lineSplit[5];
            code = lineSplit[4];
            easting = lineSplit[1].substring(0, lineSplit[1].lastIndexOf('.') + 4);
            northing = lineSplit[2].substring(0, lineSplit[2].lastIndexOf('.') + 4);
            height = lineSplit[3].substring(0, lineSplit[3].lastIndexOf('.') + 5);

            result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
        }

        return result;
    }

} // end of Cadwork2Zeiss
