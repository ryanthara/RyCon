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
package de.ryanthara.ja.rycon.converter.gsi;

import de.ryanthara.ja.rycon.elements.GSIBlock;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by sebastian on 15.09.16.
 */
public class Nigra2GSI {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given an {@code ArrayList<String>} file from Nigra or NigraWin.
     *
     * @param readStringLines read lines
     */
    public Nigra2GSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a NIGRA height listing (*.ASC) file into a Leica GSI file.
     *
     * @param isGSI16 true if GSI16 format is used
     *
     * @return converted GSI format file
     *
     * @since 5
     */
    public ArrayList<String> convertNIGRA2GSI(boolean isGSI16) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();
        StringTokenizer stringTokenizer;

        int lineCounter = 1;

        // skip the first 7 lines without any needed information
        for (int i = 5; i < readStringLines.size(); i++) {
            blocks = new ArrayList<>();
            String line = readStringLines.get(i);
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

        return BaseToolsGSI.lineTransformation(isGSI16, blocksInLines);
    }

} // end of Nigra2GSI
