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
 * Instances of this class provides functions to convert different Leica Geosystems
 * level protocol files into an ascii file.
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the Leica Geosystems level protocol file if present.
 * Otherwise they will be set to local values starting at 0,0 and raise in both axis by a constant value.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class LeicaProtocol2Asc extends Converter {

    private final boolean ignoreChangePoints;
    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for the read {@code ArrayList<String>}
     * from the Leica Geosystems protocol.
     *
     * @param readStringLines    read lines
     * @param ignoreChangePoints change points with number '0' has to be ignored
     */
    public LeicaProtocol2Asc(ArrayList<String> readStringLines, boolean ignoreChangePoints) {
        this.readStringLines = new ArrayList<>(readStringLines);
        this.ignoreChangePoints = ignoreChangePoints;
    }

    /**
     * Converts a read Leica Geosystems protocol file (*.ASC) into an ascii file
     * with pseudo coordinates for x and y.
     *
     * @return converted Nigra altitude register format file
     */
    @Override
    public ArrayList<String> convert() {
        ArrayList<String> reduced = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();

        // collect relevant lines into a new ArrayList
        boolean isResult = false;
        boolean isStarted = false;
        for (String line : readStringLines) {
            if (isResult) {
                if (isStarted) {
                    // the pagination stops here
                    if (line.startsWith("----------")) {
                        isResult = false;
                        isStarted = false;
                    } else {
                        if (!line.trim().equals("")) {
                            if (ignoreChangePoints) {
                                if (!line.startsWith("0         \t")) {
                                    reduced.add(line);
                                }
                            } else {
                                reduced.add(line);
                            }
                        }
                    }
                }
            }

            // adjusted altitudes are printed in a paginated structure
            if (line.contains("LINIEN-AUSGLEICHUNG")) {
                isResult = true;
            }

            // do not use the 'ö' for identification here
            if (line.contains("Punkt Nr.\tH")) {
                isStarted = true;
            }
        }

        ArrayList<Point> dummyCoordinates = DummyCoordinates.getList(reduced.size());

        for (int i = 0; i < reduced.size(); i++) {
            String line = reduced.get(i);
            String[] split = line.trim().split("\t");

            String number = split[0];
            String height = split[1];

            Point p = dummyCoordinates.get(i);
            String x = Integer.toString(p.x) + ".000";
            String y = Integer.toString(p.y) + ".000";

            result.add(number.trim() + Converter.SEPARATOR + x + Converter.SEPARATOR + y + Converter.SEPARATOR + height.trim());
        }

        return new ArrayList<>(result);
    }

} // end of LeicaProtocol2Asc
