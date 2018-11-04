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
import java.util.List;

/**
 * A converter with functions to convert Cadwork CAD program
 * coordinate files into Leica Geosystems GSI files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class Cadwork2Gsi {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text file from Cadwork CAD program.
     *
     * @param lines list with read node.dat lines
     */
    public Cadwork2Gsi(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a Leica Geosystems GSI8 or GS16 formatted file.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16        Output file is GSI16 format
     * @param useCodeColumn  Use the code column from node.dat
     * @param useZeroHeights Use heights with zero (0.000) values
     * @return converted {@code List<String>} with lines of GSI8 or GSI16 format
     */
    public List<String> convert(boolean isGSI16, boolean useCodeColumn, boolean useZeroHeights) {
        List<GsiBlock> blocks;
        List<List<GsiBlock>> blocksInLines = new ArrayList<>();

        removeHeadLines();

        int lineCounter = 1;

        for (String line : lines) {
            blocks = new ArrayList<>();

            String[] values = line.trim().split("\\s+", -1);

            // point number
            blocks.add(new GsiBlock(isGSI16, 11, lineCounter, values[5]));

            // use code if necessary
            if (useCodeColumn) {
                blocks.add(new GsiBlock(isGSI16, 71, values[4]));
            }

            // easting and northing
            blocks.add(new GsiBlock(isGSI16, 81, values[1]));
            blocks.add(new GsiBlock(isGSI16, 82, values[2]));

            // use height if necessary
            if (useZeroHeights) {
                blocks.add(new GsiBlock(isGSI16, 83, values[3]));
            } else {
                if (!values[3].equals("0.000000")) {
                    blocks.add(new GsiBlock(isGSI16, 83, values[3]));
                }
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter = lineCounter + 1;
                blocksInLines.add(blocks);
            }
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

    private void removeHeadLines() {
        lines.subList(0, 3).clear();
    }

}
