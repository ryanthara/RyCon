/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.logfile
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
package de.ryanthara.ja.rycon.core.logfile.leica;

import java.util.ArrayList;

/**
 * The {@code CogoStructure} implements functions based on the COGO part of
 * the <tt>Leica Geosystems</tt> logfile.txt for {@code RyCON}.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class CogoStructure extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;

    /**
     * Constructs a new {@code CogoStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public CogoStructure(ArrayList<String> lines) {
        this.lines = lines;
    }

    /**
     * Analyze the COGO structure of the <tt>Leica Geosystems</tt> logfile.txt and
     * fills the results into the return arrays.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        boolean success = false;

        System.out.println("COGO structure");

        super.analyzeHeader(lines);

        for (String line : lines) {
            //System.out.println(line);
        }

        return success;
    }

    // use original order for enum
    private enum Elements {
        // Line Calculations - Line Info
        START_POINT_ID("Start Point ID"),
        END_POINT_ID("End Point ID"),
        LENGTH("Length"),
        AZIMUTH("Azimuth"),
        BASE_POINT("Base Point"),

        OFFSET_POINT_ID("Offset Point ID"),
        COMPUTED("Computed"),
        DIST_ALONG_LINE("Dist along line"),
        OFFSET_FROM_LINE("Offset from line");

        private final String identifier;

        Elements(String identifier) {
            this.identifier = identifier;
        }
    }

} // end of CogoStructure
