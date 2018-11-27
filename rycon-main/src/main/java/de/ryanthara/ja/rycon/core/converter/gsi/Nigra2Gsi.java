/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.converter.gsi
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

import de.ryanthara.ja.rycon.core.elements.GSIBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A converter with functions to convert NigraWin/NivNET altitude
 * register files into Leica Geosystems GSI8 or GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Nigra2Gsi {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based
     * height register from Nigra or NigraWin
     *
     * @param lines list with height register lines from Nigra or NigraWin
     */
    public Nigra2Gsi(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a NIGRA height register (*.ASC) file into a Leica Geosystems GSI file.
     *
     * @param isGSI16 true if GSI16 format is used
     * @return converted GSI format file
     * @since 5
     */
    // TODO Use method
    public List<String> convert(boolean isGSI16) {
        List<GSIBlock> blocks;
        List<List<GSIBlock>> blocksInLines = new ArrayList<>();
        StringTokenizer stringTokenizer;

        int lineCounter = 1;

        // skip the first 7 lines without any needed information
        for (int i = 5; i < lines.size(); i++) {
            blocks = new ArrayList<>();
            String line = lines.get(i);
            stringTokenizer = new StringTokenizer(line);

            if (stringTokenizer.countTokens() > 2) {
                String number = stringTokenizer.nextToken();
                String easting = Integer.toString(i);
                String northing = Integer.toString(i);
                String height = stringTokenizer.nextToken();

                blocks.add(new GSIBlock(isGSI16, 11, lineCounter, number));
                blocks.add(new GSIBlock(isGSI16, 81, easting));
                blocks.add(new GSIBlock(isGSI16, 82, northing));
                blocks.add(new GSIBlock(isGSI16, 83, height));
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter = lineCounter + 1;
                blocksInLines.add(blocks);
            }
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

}
