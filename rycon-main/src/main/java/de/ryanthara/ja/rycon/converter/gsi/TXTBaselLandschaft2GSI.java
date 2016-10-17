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
 * This class provides functions to convert text formatted coordinate files from the geodata server
 * Basel Landschaft (Switzerland) into Leica GSI8 and GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TXTBaselLandschaft2GSI {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based coordinate files.
     * <p>
     * The differentiation of the content is done by the called method.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public TXTBaselLandschaft2GSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text file from the geodata server Basel Landschaft (Switzerland) into a GSI formatted file.
     * <p>
     * This method can differ between LFP and HFP files, which has a different structure.
     * With a parameter it is possible to distinguish between GSI8 and GSI16.
     *
     * @param isGSI16             distinguish between GSI8 or GSI16 output
     * @param useAnnotationColumn write additional information as annotation column (WI 71)
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertTXTBaselLandschaft2GSI(boolean isGSI16, boolean useAnnotationColumn) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        // remove comment line
        readStringLines.remove(0);

        for (String line : readStringLines) {
            blocks = new ArrayList<>();

            String[] lineSplit = line.trim().split("\\t", -1);

            switch (lineSplit.length) {
                case 5:     // HFP file
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[1]));

                    if (useAnnotationColumn) {
                        blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[0]));
                    }

                    blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[2]));
                    blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[3]));
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[4]));
                    break;

                case 6:     // LFP file
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[1]));

                    if (useAnnotationColumn) {
                        if (lineSplit[2].equals("NULL")) {
                            blocks.add(new GSIBlock(isGSI16, 41, lineCounter, "-1"));
                        } else {
                            blocks.add(new GSIBlock(isGSI16, 41, lineCounter, lineSplit[2]));
                        }
                        blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[0]));
                    }

                    blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[3]));
                    blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[4]));

                    // prevent 'NULL' element in height
                    if (!lineSplit[5].equals("NULL")) {
                        blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[5]));
                    }

                    break;
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter = lineCounter + 1;
                blocksInLines.add(blocks);
            }
        }

        return BaseToolsGSI.lineTransformation(isGSI16, blocksInLines);
    }

} // end of TXTBaselLandschaft2GSI
