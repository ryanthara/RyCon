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
 * Instances of this class provides functions to convert a Nigra/NigraWin calculation file into an ascii file.
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the Nigra calculation file if present. Otherwise they will be set
 * to local values starting at 0,0 and raise in both axis by a constant value.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class NigraCalculation2Asc extends Converter {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for the read {@code ArrayList<String>}
     * calculation file from Nigra/NigraWin.
     *
     * @param readStringLines read lines
     */
    public NigraCalculation2Asc(ArrayList<String> readStringLines) {
        this.readStringLines = new ArrayList<>(readStringLines);
    }

    /**
     * Converts a read Nigra calculation file (*.ASC) into an ascii file with pseudo coordinates for x and y.
     *
     * @return converted Nigra/NigraWin calculation format file
     */
    @Override
    public ArrayList<String> convert() {
        ArrayList<String> reduced = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();

        // collect relevant lines into a new ArrayList
        boolean isStarted = false;
        for (String line : readStringLines) {
            // measurement content stops the line before
            if (line.startsWith("\f")) {
                isStarted = false;
            }

            if (isStarted) {
                if (!line.trim().equals("")) {
                    // nothing relevant after this line
                    if (line.trim().startsWith("Gesamtsumme")) {
                        break;
                    }

                    if (!line.trim().startsWith("Summe")) {
                        if (line.trim().split("\\s+").length >= 4) {
                            reduced.add(line.trim());
                        }
                    }
                }
            }

            // measurement content starts in the following line
            if (line.trim().startsWith("Strecke")) {
                isStarted = true;
            }
        }

        ArrayList<Point> dummyCoordinates = DummyCoordinates.getList(reduced.size());

        int counter = 0;
        for (String line : reduced) {
            String[] split = line.trim().split("\\s+");

            if (split.length > 3) {
                String height = split[2];
                String number = split[3];

                // if point number contains white spaces
                if (split.length > 4) {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 3; i < split.length; i++) {
                        stringBuilder.append(split[i]);
                        stringBuilder.append(" ");
                    }

                    number = stringBuilder.toString().trim();
                }

                Point p = dummyCoordinates.get(counter);
                String x = Integer.toString(p.x) + ".000";
                String y = Integer.toString(p.y) + ".000";

                result.add(number.trim() + Converter.SEPARATOR + x + Converter.SEPARATOR + y + Converter.SEPARATOR + height.trim());
                counter = counter + 1;
            }
        }

        return new ArrayList<>(result);
    }

} // end of NigraCalculation2Asc
