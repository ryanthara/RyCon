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

import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.ArrayList;

/**
 * This class provides functions to convert coordinate files from Cadwork CAD program into Leica GSI files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2GSI {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based coordinate files from cadwork CAD program.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Cadwork2GSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a cadwork node.dat file into GSI8 or GS16 format.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16        Output file is GSI16 format
     * @param useCodeColumn  Use the code column from node.dat
     * @param useZeroHeights Use heights with zero (0.000) values
     *
     * @return converted {@code ArrayList<String>} with lines of GSI8 or GSI16 format
     */
    public ArrayList<String> convertCadwork2GSI(boolean isGSI16, boolean useCodeColumn, boolean useZeroHeights) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        int lineCounter = 1;

        for (String line : readStringLines) {
            blocks = new ArrayList<>();

            String[] lineSplit = line.trim().split("\\t", -1);

            // point number
            blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[5]));

            // use code if necessary
            if (useCodeColumn) {
                blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[4]));
            }

            // easting and northing
            blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[1]));
            blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[2]));

            // use height if necessary
            if (useZeroHeights) {
                blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[3]));
            } else {
                if (!lineSplit[3].equals("0.000000")) {
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[3]));
                }
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter++;
                blocksInLines.add(blocks);
            }
        }

        BaseToolsGSI baseToolsGSI = new BaseToolsGSI();

        return baseToolsGSI.lineTransformation(isGSI16, blocksInLines);
    }

} // end of Cadwork2GSI
