/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.converter.gsi;

import de.ryanthara.ja.rycon.elements.GSIBlock;

import java.util.ArrayList;

/**
 * This class provides functions to convert measurement files from Caplan K format into Leica GSI8 or GSI16 formatted files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class K2GSI {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given an {@code ArrayList<String} that contains the read Caplan K file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public K2GSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a CAPLAN K file to GSI8 or GSI16 formatted file.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16         true if GSI16 format is used
     * @param writeCodeColumn true if the valency indicator is used as code (WI71)
     *
     * @return converted GSI format file
     */
    public ArrayList<String> convertK2GSI(boolean isGSI16, boolean writeCodeColumn) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;
        String number, valency, easting, northing, height;

        for (String line : readStringLines) {
            blocks = new ArrayList<>();

            if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                if (line.length() >= 16) {
                    number = line.substring(0, 16).trim();       // point number (no '*', ',' and ';'), column 1 - 16
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, number));
                }

                if ((line.length() >= 18) & writeCodeColumn) {
                    valency = line.substring(18, 18);            // valency, column 18
                    blocks.add(new GSIBlock(isGSI16, 41, valency));
                }

                if (line.length() >= 32) {
                    easting = line.substring(20, 32).trim();     // easting E, column 19-32
                    blocks.add(new GSIBlock(isGSI16, 81, easting));
                }

                if (line.length() >= 46) {
                    northing = line.substring(34, 46).trim();    // northing N, column 33-46
                    blocks.add(new GSIBlock(isGSI16, 82, northing));
                }

                if (line.length() >= 59) {
                    height = line.substring(48, 59).trim();      // height H, column 47-59
                    blocks.add(new GSIBlock(isGSI16, 83, height));
                }

                if ((line.length() >= 62) & writeCodeColumn) {
                    String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");

                    String code = lineSplit[0].trim();              // code is the same as object type, column 62...
                    blocks.add(new GSIBlock(isGSI16, 71, code));

                    for (int i = 1; i < lineSplit.length; i++) {
                        String attr = lineSplit[i].trim();
                        blocks.add(new GSIBlock(isGSI16, (71 + i), attr));
                        lineCounter = lineCounter + 1;
                    }
                }

                // check for at least one or more added elements to prevent writing empty lines
                if (blocks.size() > 0) {
                    lineCounter = lineCounter + 1;
                    blocksInLines.add(blocks);
                }
            }
        }

        return BaseToolsGSI.lineTransformation(isGSI16, blocksInLines);
    }

} // end of K2GSI
