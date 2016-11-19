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
 * Instances of this class provides functions to convert text formatted coordinate files into Zeiss REC files and
 * it's dialects (R4, R5, REC500 and M5).
 */
public class TXT2Zeiss {

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for read line based text files in Zeiss REC format
     * and it's dialects (R4, R5, REC500 and M5).
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TXT2Zeiss(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text formatted coordinate file (nr x y (z) or nr code x y z) into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the destination file
     *
     * @return string lines of the destination file
     */
    public ArrayList<String> convertTXT2REC(ZeissDialect dialect) {
        ArrayList<String> result = new ArrayList<>();

        int lineNumber = 0;

        for (String line : readStringLines) {
            String[] lineSplit = line.trim().split("\\s+");

            String code = "";
            String northing = "";
            String easting = "";
            String height = "";

            String number = lineSplit[0];

            lineNumber = lineNumber + 1;

            switch (lineSplit.length) {
                case 3:     // line contains no height
                    easting = lineSplit[1];
                    northing = lineSplit[2];
                    break;

                case 4:     // line contains no code
                    easting = lineSplit[1];
                    northing = lineSplit[2];
                    height = lineSplit[3];
                    break;

                case 6:     // line contains code at second position and height
                    /*
                    Code is not used at the moment because the only chance to do this would be in M5 dialect.
                     */
                    // TODO: 16.10.16 use code from ASCII text file in M5 format
                    //code = lineSplit[1];
                    easting = lineSplit[2];
                    northing = lineSplit[3];
                    height = lineSplit[4];
                    break;

                default:
                    System.err.println("TXT2Zeiss.convertTXT2REC() : line contains less or more tokens " + line);
            }

            result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
        }

        return result;
    }

} // end of TXT2Zeiss
