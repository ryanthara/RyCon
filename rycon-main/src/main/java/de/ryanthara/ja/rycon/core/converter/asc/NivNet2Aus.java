/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.asc
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
import de.ryanthara.ja.rycon.util.DummyCoordinates;
import org.eclipse.swt.graphics.Point;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert a NivNET output file into an ascii file.
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the NivNET output file file if present. Otherwise they will be set
 * to local values starting at 0,0 and raise in both axis by a constant value.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class NivNet2Aus extends Converter {

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for the read {@code ArrayList<String>}
     * altitude register from NivNET.
     *
     * @param readStringLines read lines
     */
    public NivNet2Aus(ArrayList<String> readStringLines) {
        this.readStringLines = new ArrayList<>(readStringLines);
    }

    /**
     * Converts a read NivNET altitude register file (*.ASC) into an ascii file with the read coordinates
     * if present, else uses pseudo coordinates for x and y.
     *
     * @return converted NivNET altitude register format file
     */
    @Override
    public ArrayList<String> convert() {
        ArrayList<String> reduced = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();

        // collect relevant lines into a new ArrayList
        boolean isResult = false;
        boolean isStarted = false;
        for (String line : readStringLines) {
            // the pagination stops here
            if (line.startsWith("\f      NIVNET")) {
                isResult = false;
                isStarted = false;
            }

            if (isResult) {
                if (isStarted) {
                    if (!line.trim().equals("")) {
                        reduced.add(line);
                    }
                }
            }

            // adjusted altitudes are printed in a paginated structure
            if (line.contains("Ergebnis:  Ausgeglichene Hoehen")) {
                isResult = true;
            }

            if (line.contains("--------")) {
                isStarted = true;
            }
        }

        ArrayList<Point> dummyCoordinates = DummyCoordinates.getList(reduced.size());

        int counter = 0;
        for (String line : reduced) {
            String number = line.substring(10, 26);
            String knownHeight = line.substring(26, 38);

            String adjustedHeight;
            if (line.length() > 38) {
                adjustedHeight = line.substring(38, 50);
            } else {
                adjustedHeight = knownHeight;
            }

            Point p = dummyCoordinates.get(counter);
            String x = Integer.toString(p.x) + ".000";
            String y = Integer.toString(p.y) + ".000";

            result.add(number.trim() + Converter.SEPARATOR + x + Converter.SEPARATOR + y + Converter.SEPARATOR + adjustedHeight.trim());
            counter = counter + 1;
        }

        return new ArrayList<>(result);
    }

} // end of NivNet2Aus
