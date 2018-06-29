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
 * The {@code SetupStructure} implements functions based on the COGO part of
 * the <tt>Leica Geosystems</tt> logfile.txt for {@code RyCON}.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class SetupStructure extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;
    private String setupMethod;

    /**
     * Constructs a new {@code SetupStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public SetupStructure(ArrayList<String> lines) {
        this.lines = lines;
    }

    /**
     * Analyze the SETUP structure of the <tt>Leica Geosystems</tt> logfile.txt and
     * fills the results into the return arrays.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        boolean success = false;

        System.out.println("SETUP structure");

        super.analyzeHeader(lines);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.startsWith(Elements.SETUP_METHOD.identifier)) {
                setupMethod = line.split("\"")[1];
            }



            else if (line.startsWith(Elements.ORIENTATION_CORRECTION.identifier)) {

            } else if (line.startsWith(Elements.SCALE.identifier)) {

            } else if (line.startsWith(Elements.STANDARD_DEVIATION_EAST.identifier)) {

            } else if (line.startsWith(Elements.STANDARD_DEVIATION_NORTH.identifier)) {

            } else if (line.startsWith(Elements.STANDARD_DEVIATION_HEIGHT.identifier)) {

            } else if (line.startsWith(Elements.STANDARD_DEVIATION_ORI.identifier)) {

            }


                /*
                Ori Corr.	:   138.7754
                Scale		:

                S. Dev. East	:      0.000
                S. Dev. North	:      0.000
                S. Dev. Height	:      0.001
                S. Dev. Ori	:     0.0002
                 */

            //System.out.println(line);
        }

        return success;
    }

    // use original order for enum
    private enum Elements {
        // Setup Method
        SETUP_METHOD("Setup Method"),

        // Observations
        POINT_ID("Point ID"),
        RESIDUALS_OF_POINT("Residuals of Point"),

        // Results for 'Resection'
        STATION_ID("Station ID"),
        ORIENTATION_CORRECTION("Ori Corr."),
        SCALE("Scale"),
        STANDARD_DEVIATION_EAST("S. Dev. East"),
        STANDARD_DEVIATION_NORTH("S. Dev. North"),
        STANDARD_DEVIATION_HEIGHT("S. Dev. Height"),
        STANDARD_DEVIATION_ORI("S. Dev. Ori");

        // Results for 'Ori & Ht Transfer'
        /*
        STATION_ID("Station ID"),
        ORIENTATION_CORRECTION("Ori Corr."),
        STANDARD_DEVIATION_HEIGHT("S. Dev. Height"),
        STANDARD_DEVIATION_ORI("S. Dev. Ori");
        */

        private final String identifier;

        Elements(String identifier) {
            this.identifier = identifier;
        }
    }

} // end of SetupStructure
