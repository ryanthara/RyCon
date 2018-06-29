/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.logfile.leica
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
 * The {@code ReferencePlane} implements functions based on the REFERENCE PLANE part of
 * the <tt>Leica Geosystems</tt> logfile.txt for {@code RyCON}.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class ReferencePlane extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;

    /**
     * Constructs a new {@code ReferencePlane} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public ReferencePlane(ArrayList<String> lines) {
        this.lines = lines;
    }

    /**
     * Analyze the REFERENCE PLANE structure of the <tt>Leica Geosystems</tt> logfile.txt and
     * fills the results into the return arrays.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        boolean success = false;

        System.out.println("REFERENCE PLANE structure");

        super.analyzeHeader(lines);

        for (String line : lines) {
            //System.out.println(line);
        }

        return success;
    }

    // use original order for enum
    private enum Elements {
        // Reference Line Information
        TPS_STATION("TPS Station"),

        // Reference Plane Information
        REFERENCE_PLANE_ID("Reference Plane ID"),
        NUMBER_OF_POINTS("Number of Points"),
        OFFSET_LIMIT("Offset Limit"),
        PLANE_STD_DEV("Plane Std Dev"),
        PLANE_OFFSET("Plane Offset"),
        ORIGIN_POINT_ID("Origin Point ID"),
        DEFINITION_POINT_ID_1("Definition Point ID 1"),
        DEFINITION_POINT_ID_2("Definition Point ID 1"),
        DEFINITION_POINT_ID_3("Definition Point ID 1"),

        // Scanning Perimeter
        FIRST_PERIMETER_POINT("First Perimeter Point"),
        SECOND_PERIMETER_POINT("Second Perimeter Point"),
        HORIZONTAL_X_SPACING("Horizontal/X Spacing"),
        VERTICAL_Z_SPACING("Vertical/Z Spacing"),
        Points_Scanned("Points Scanned"),
        Points_Skipped("Points Skipped"),

        // Scanned Points
        SCANNED_POINT("Scanned Point"),
        DIST_HEIGHT_FROM_PLANE("Dist/Height from Plane");

        private final String identifier;

        Elements(String identifier) {
            this.identifier = identifier;
        }
    }

} // end of ReferencePlane
