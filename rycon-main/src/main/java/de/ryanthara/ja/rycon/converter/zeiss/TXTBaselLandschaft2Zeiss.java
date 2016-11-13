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
 * Instances of this class provides functions to convert coordinate files from the geodata server Basel Landschaft (Switzerland)
 * into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TXTBaselLandschaft2Zeiss {

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for the read line based text files from
     * the geodata server 'Basel Landschaft' (Switzerland) as string array.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TXTBaselLandschaft2Zeiss(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text formatted coordinate file from the geodata server 'Basel Landschaft' (Switzerland)
     * into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     *
     * @return string lines of the target file
     */
    public ArrayList<String> convertTXTBaselLandschaft2REC(ZeissDialect dialect) {
        ArrayList<String> result = new ArrayList<>();

        // remove comment line
        readStringLines.remove(0);

        int lineNumber = 0;

        for (String line : readStringLines) {
            String[] lineSplit = line.trim().split("\\t", -1);

            String number, code, easting, northing, height;

            lineNumber = lineNumber + 1;

            switch (lineSplit.length) {
                case 5:     // HFP file
                    /*
                    Art	Nummer	X	Y	Z
                    HFP2	NC17014	2624601.9	1262056.014	348.298
                     */
                    code = lineSplit[0];
                    number = lineSplit[1];
                    easting = lineSplit[2];
                    northing = lineSplit[3];
                    height = lineSplit[4];

                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                    break;

                case 6:     // LFP file
                    /*
                    Art	Nummer	VArt	X	Y	Z
                    LFP2	10681160	0	2623800.998	1263204.336	328.05
                    */
                    code = lineSplit[0];
                    number = lineSplit[1];
                    easting = lineSplit[3];
                    northing = lineSplit[4];
                    height = "";

                    // prevent 'NULL' element in height
                    if (!lineSplit[5].equals("NULL")) {
                        height = lineSplit[5];
                    }

                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                    break;

                default:
                    System.err.println("TXTBaselLandschaft2Zeiss.convertTXTBaselLandschaft2REC() : line contains less or more tokens " + line);
            }
        }

        return result;
    }

} // end of TXTBaselLandschaft2Zeiss
