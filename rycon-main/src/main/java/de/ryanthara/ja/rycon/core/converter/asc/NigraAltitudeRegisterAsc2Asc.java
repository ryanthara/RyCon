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
import java.util.Iterator;
import java.util.List;

/**
 * A converter with functions to convert NigraWin/NivNET altitude register files into ASCII text files.
 *
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the NigraWin/NivNET altitude register file if present. Otherwise they
 * will be set to local values starting at 0,0 and raise in both axis by a constant value.
 * <p>
 * Until version 3.x the NigraWin/NivNET altitude register uses the file ending (*.HVZ). Since version 4.0 it
 * uses the file ending (*.ASC).
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class NigraAltitudeRegisterAsc2Asc extends Converter {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based altitude register from Nigra/NigraWin.
     *
     * @param lines list with the altitude register
     */
    public NigraAltitudeRegisterAsc2Asc(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a read Nigra/NigraWin altitude register file (*.ASC) into an ascii file with
     * pseudo coordinates for x and y.
     * <p>
     * This function based on fixed string positions and substring operations from the standard
     * output of Nigra/NigraWin. This is necessary to find possible x and y coordinates.
     * <p>
     * If they are not present, the algorithm fall back to pseudo coordinates for x and y.
     *
     * @return converted Nigra/NigraWin altitude register format file
     */
    @Override
    public List<String> convert() {
        List<String> result = new ArrayList<>();

        // Detect the number of pages of the Nigra/NigraWin altitude register
        int numPages = getNumPages();

        List<Point> dummyCoordinates = DummyCoordinates.getList(lines.size() - numPages * 8);

        removeHeadLines();

        int counter = 0;
        for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            String line = iterator.next();

            // The Nigra/NigraWin altitude register is paginated for printing -> skip this 8 lines on each page
            if (line.startsWith("\f")) {
                iterator.remove();
                iterator.next();
                iterator.remove();
                iterator.next();
            } else {
                if (!line.trim().equals("")) {
                    String number = line.substring(0, 19);
                    String height = line.substring(19, 31);

                    String x, y;

                    // Line length and positions are taken from the Nigra/NigraWin standard format
                    if (line.length() == 116) {
                        y = line.substring(91, 105);
                        x = line.substring(106, 116);
                    } else {
                        Point p = dummyCoordinates.get(counter);
                        x = Integer.toString(p.x) + ".000";
                        y = Integer.toString(p.y) + ".000";
                    }

                    result.add(number.trim() + Converter.SEPARATOR + x + Converter.SEPARATOR + y + Converter.SEPARATOR + height.trim());
                    counter = counter + 1;
                }
            }
        }

        return List.copyOf(result);
    }

    private int getNumPages() {
        int numPages = 1;
        for (String line : lines) {
            if (line.startsWith("\f")) {
                numPages = numPages + 1;
            }
        }
        return numPages;
    }

    private void removeHeadLines() {
        List<String> headlines = new ArrayList<>(lines.subList(0, 8));
        lines.removeAll(headlines);
    }

}
