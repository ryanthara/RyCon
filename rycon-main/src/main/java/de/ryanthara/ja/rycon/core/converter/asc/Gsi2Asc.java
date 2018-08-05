/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.gsi
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
package de.ryanthara.ja.rycon.core.converter.asc;

import de.ryanthara.ja.rycon.core.converter.Converter;
import de.ryanthara.ja.rycon.core.converter.gsi.BaseToolsGsi;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.util.DummyCoordinates;
import org.eclipse.swt.graphics.Point;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert different Leica Geosystems level files into an ascii file.
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the Leica Geosystems level file if present. Otherwise they will be set
 * to local values starting at 0,0 and raise in both axis by a constant value.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class Gsi2Asc extends Converter {

    private final boolean ignoreChangePoints;
    private final BaseToolsGsi baseToolsGsi;

    /**
     * Constructs a new instance of this class with a parameter for the read {@code ArrayList<String>}
     * from Nigra/NigraWin.
     *
     * @param readStringLines    read lines
     * @param ignoreChangePoints change points with number '0' has to be ignored
     */
    public Gsi2Asc(ArrayList<String> readStringLines, boolean ignoreChangePoints) {
        baseToolsGsi = new BaseToolsGsi(readStringLines);
        this.ignoreChangePoints = ignoreChangePoints;
    }

    /**
     * Converts a read Leica Geosystems AG leveling file (*.GSI) into
     * an ascii file with pseudo coordinates for x and y.
     *
     * @return converted gsi leveling format file
     */
    @Override
    public ArrayList<String> convert() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<ArrayList<GsiBlock>> encodedLinesOfGSIBlocks = baseToolsGsi.getEncodedLinesOfGSIBlocks();

        int countLevelLines = 0;
        for (ArrayList<GsiBlock> blocksInLine : encodedLinesOfGSIBlocks) {
            if (blocksInLine.size() == 1) {
                countLevelLines = countLevelLines + 1;
            }
        }

        // works only with the reduced ArrayList without change points for better dummy coordinate result
        if (ignoreChangePoints) {
            ArrayList<ArrayList<GsiBlock>> reducedEncodedLinesOfGSIBlocks = new ArrayList<>();

            for (ArrayList<GsiBlock> blocksInLine : encodedLinesOfGSIBlocks) {
                if (!blocksInLine.get(0).toPrintFormatAsc().equals("0")) {
                    reducedEncodedLinesOfGSIBlocks.add(blocksInLine);
                }
            }

            encodedLinesOfGSIBlocks = new ArrayList<>(reducedEncodedLinesOfGSIBlocks);
        }

        int size = (int) StrictMath.ceil((encodedLinesOfGSIBlocks.size() - countLevelLines) / 3d + 1);
        ArrayList<Point> dummyCoordinates = DummyCoordinates.getList(size + 1);

        /*
        Strategy:
            - identify a station line (one token)
            - identify point line with height (four tokens)
            - identify change points and maybe ignore them (point number is '0')
            - grab the relevant information and prepare the writable output
         */

        int counter = 0;
        for (ArrayList<GsiBlock> blocksInLine : encodedLinesOfGSIBlocks) {
            String number = blocksInLine.get(0).toPrintFormatAsc();
            String height = null;

            switch (blocksInLine.size()) {
                // line number
                case 1:
                    break;
                // start point of the line with altitude
                case 2:
                    height = blocksInLine.get(1).toPrintFormatAsc();
                    break;
                // observations points with altitude
                case 4:
                    height = blocksInLine.get(3).toPrintFormatAsc();
                    break;
                default:
            }

            if (height != null) {
                Point p = dummyCoordinates.get(counter);
                String x = Integer.toString(p.x) + ".000";
                String y = Integer.toString(p.y) + ".000";

                result.add(number.trim() + Converter.SEPARATOR + x + Converter.SEPARATOR + y + Converter.SEPARATOR + height.trim());
                counter = counter + 1;
            }
        }
        return new ArrayList<>(result);
    }

} // end of Gsi2Asc
