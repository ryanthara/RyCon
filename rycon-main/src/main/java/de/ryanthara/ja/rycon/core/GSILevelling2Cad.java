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
package de.ryanthara.ja.rycon.core;

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.elements.GSIBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class provides functions to convert and prepare levelling files in the Leica GSI format
 * for CAD import.
 * <p>
 * Therefore a raising northing and easting coordinate value is added to every read height line.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSILevelling2Cad {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given a read line based text file in the Leica GSI format.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in Leica GSI format
     */
    public GSILevelling2Cad(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a levelling file to a coordinate one (no, x, y, z) in Leica GSI format for cad import.
     * <p>
     * Within this conversation a x, y coordinate will be generated from the line number. The units are
     * rounded down to 1/10mm.
     *
     * @param ignoreChangePoints if change points with number '0' has to be ignored
     *
     * @return Converted {@code ArrayList<String>} for cad import
     */
    public ArrayList<String> processLevelling2Cad(boolean ignoreChangePoints) {
        boolean isGSI16 = false;
        int lineCounter = 1;
        int lineNumber = -1;
        String newLine;

        ArrayList<String> result = new ArrayList<>();

        /*
        Strategy:
            - identify a station line (one token)
            - identify point line with height (four tokens)
            - identify change points and maybe ignore them (point number is '0')
            - grab the relevant information and prepare the write output
         */

        for (String line : readStringLines) {
            int size = BaseToolsGSI.getBlockSize(line);

            if (size == 24) {
                isGSI16 = true;
                line = line.substring(1, line.length());
            }

            // split read line into separate Strings
            List<String> lineSplit = new ArrayList<>((line.length() + size - 1) / size);
            for (int i = 0; i < line.length(); i += size) {
                lineSplit.add(line.substring(i, Math.min(line.length(), i + size)));
            }

            switch (lineSplit.size()) {
                // new levelling line has only one token
                case 1:
                    lineNumber = lineNumber + 1;
                    break;

                // line with height information from levelling has four tokens in GSI format
                case 4:
                    // number - the GSI16 format identifier has to be add to the first block
                    newLine = size == 24 ? "*" + lineSplit.get(0) : lineSplit.get(0);

                    // detect change points (number = 0) with regex
                    if (!(newLine.substring(8, newLine.length()).matches("[0]+") & ignoreChangePoints)) {
                        /*
                        x and y in 1/10 mm with the same value -> diagonal line later on...
                        for every new levelling line the y coordinate is raised with 10
                         */
                        int coordinate = lineCounter * 10000;
                        String valueX = Integer.toString(coordinate);
                        String valueY = Integer.toString(coordinate + 100000 * lineNumber);

                        GSIBlock x = new GSIBlock(isGSI16, 81, "..46", "+", valueX);
                        GSIBlock y = new GSIBlock(isGSI16, 82, "..46", "+", valueY);

                        newLine = newLine.concat(" " + x.toString());
                        newLine = newLine.concat(" " + y.toString());

                        // leveled height rounded to 1/10mm (RAPP AG hack)
                        String leveled = lineSplit.get(3);
                        String leveledRounded = leveled.substring(0, 4) + "26" + leveled.substring(6, 7) + "0" + leveled.substring(7, leveled.length() - 1);

                        newLine = newLine.concat(" " + leveledRounded);
                        newLine = BaseToolsGSI.prepareLineEnding(newLine);

                        result.add(newLine);
                        lineCounter = lineCounter + 1;
                    }
                    break;

                default:
                    System.err.println("GSILevelling2Cad.processLevelling2Cad() : line contains less or more tokens " + lineSplit);
            }
        }
        return result;
    }

} // end of GSILevelling2Cad
