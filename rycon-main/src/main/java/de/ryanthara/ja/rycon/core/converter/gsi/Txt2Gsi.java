/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.core.converter.gsi;

import de.ryanthara.ja.rycon.core.elements.GsiBlock;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert text formatted measurement files into
 * Leica GSI8 and GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Txt2Gsi {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given an {@code ArrayList<String>} that contains
     * the read txt formatted coordinate file.
     *
     * @param readStringLines read lines
     */
    public Txt2Gsi(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text file (space or tabulator separated) into a GSI formatted file.
     * <p>
     * RyCON uses space or tab as separation sign and not a fixed column position.
     * <p>
     * The GSI format decision is done by a parameter in the constructor.
     *
     * @param isGSI16                  decision which GSI format is used
     * @param sourceContainsCodeColumn if source file contains a code column
     *
     * @return converted {@code ArrayList<String>>} with lines
     */
    public ArrayList<String> convertTXT2GSI(boolean isGSI16, boolean sourceContainsCodeColumn) {
        ArrayList<GsiBlock> blocks;
        ArrayList<ArrayList<GsiBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        for (String line : readStringLines) {
            blocks = new ArrayList<>();

            String[] lineSplit = line.trim().split("\\s+");
            switch (lineSplit.length) {
                case 1:     // prevent fall through
                    break;

                case 2:     // no, height
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GsiBlock(isGSI16, 83, lineSplit[1]));
                    break;

                case 3:     // no, code, height or no, easting, northing
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    if (sourceContainsCodeColumn) {
                        blocks.add(new GsiBlock(isGSI16, 71, lineSplit[1]));
                        blocks.add(new GsiBlock(isGSI16, 83, lineSplit[2]));
                    } else {
                        blocks.add(new GsiBlock(isGSI16, 81, lineSplit[1]));
                        blocks.add(new GsiBlock(isGSI16, 82, lineSplit[2]));
                    }
                    break;

                case 4:     // no, easting, northing, height
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GsiBlock(isGSI16, 81, lineSplit[1]));
                    blocks.add(new GsiBlock(isGSI16, 82, lineSplit[2]));

                    // necessary because of Basel Stadt CSV distinguish between points without height
                    if (!lineSplit[3].equals("-9999")) {
                        blocks.add(new GsiBlock(isGSI16, 83, lineSplit[3]));
                    }
                    break;

                case 5:     // no, code, easting, northing, height
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GsiBlock(isGSI16, 71, lineSplit[1]));
                    blocks.add(new GsiBlock(isGSI16, 81, lineSplit[2]));
                    blocks.add(new GsiBlock(isGSI16, 82, lineSplit[3]));
                    blocks.add(new GsiBlock(isGSI16, 83, lineSplit[4]));
                    break;

                default:
                    System.err.println("Txt2Gsi.convertTXT2GSI() : line contains less or more tokens " + line);
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter = lineCounter + 1;
                blocksInLines.add(blocks);
            }
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

} // end of Txt2Gsi
