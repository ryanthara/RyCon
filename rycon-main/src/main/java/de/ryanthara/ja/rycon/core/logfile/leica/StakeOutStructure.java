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
 * The {@code StakeOutStructure} implements functions based on the COGO part of
 * the <tt>Leica Geosystems</tt> logfile.txt for {@code RyCON}.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class StakeOutStructure extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;

    /**
     * Constructs a new {@code StakeOutStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public StakeOutStructure(ArrayList<String> lines) {
        this.lines = lines;
    }

    /**
     * Analyze the STAKE OUT structure of the <tt>Leica Geosystems</tt> logfile.txt and
     * fills the results into the return arrays.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        boolean success = false;

        System.out.println("STAKE OUT structure");

        super.analyzeHeader(lines);

        for (String line : lines) {
            //System.out.println(line);
        }

        return success;
    }

    // use original order for enum
    private enum Elements {
        TPS_STATION("TPS Station"),
        POINT_ID("Point ID"),
        DESIGN_POINT("Design Point"),
        STAKED_POINT("Staked Point"),
        STAKEOUT_DIFF("Stakeout Diff");

        private final String identifier;

        Elements(String identifier) {
            this.identifier = identifier;
        }
    }
} // end of StakeOutStructure
