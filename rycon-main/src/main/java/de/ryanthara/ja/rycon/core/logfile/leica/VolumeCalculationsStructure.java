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
 * The {@code VolumeCalculationsStructure} implements functions based on
 * the VOLUME CALCULATIONS part of the <tt>Leica Geosystems</tt> logfile.txt
 * for {@code RyCON}.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class VolumeCalculationsStructure extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;

    /**
     * Constructs a new {@code VolumeCalculationsStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public VolumeCalculationsStructure(ArrayList<String> lines) {
        this.lines = lines;
    }

    /**
     * Analyze the VOLUME CALCULATIONS structure of the <tt>Leica Geosystems</tt> logfile.txt and
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
        // Surface Triangulation Results
        SURFACE_NAME("Surface Name"),
        DATE("Date"),
        TOTAL_NUMBER_OF_POINTS("Total Number of Points"),
        POINTS_USED_FOR_BOUNDARY("Points used for Boundary"),
        POINTS_INSIDE_OF_BOUNDARY("Points inside of Boundary"),
        TOTAL_NUMBER_OF_TRIANGLES("Total Number of Triangles"),
        LONGEST_SIDE_OF_TRIANGLE_2D("Longest Side of Triangle 2D"),
        LONGEST_SIDE_OF_TRIANGLE_3D("Longest Side of Triangle 3D"),
        MAXIMUM_ELEVATION("Maximum Elevation"),
        MINIMUM_ELEVATION("Minimum Elevation"),
        SURFACE_AREA_2D("Surface Area 2D"),
        SURFACE_AREA_3D("Surface Area 3D"),
        SURFACE_PERIMETER_2D("Surface Perimeter 2D"),
        SURFACE_PERIMETER_3D("Surface Perimeter 3D"),

        // Volume Calculation Results
        // DATE("Date"),
        VOLUME_CALCULATION_METHOD("Volume Calculation Method"),
        VOLUME_FROM_SURFACE("Volume from Surface"),
        COMPUTED_FROM_POINT("Computed from Point"),
        COMPUTED_FROM_ELEVATION("Computed from Elevation"),
        TOTAL_VOLUME("Total Volume"),
        TOTAL_VOLUME_CUT("CUT"),
        TOTAL_VOLUME_FILL("FILL"),
        AVERAGE_THICKNESS("Average Thickness (Volume/Area)");

        private final String identifier;

        Elements(String identifier) {
            this.identifier = identifier;
        }
    }

} // end of VolumeCalculationsStructure
