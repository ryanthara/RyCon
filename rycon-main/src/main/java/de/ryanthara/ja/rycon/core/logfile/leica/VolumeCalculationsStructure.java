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
import java.util.Arrays;
import java.util.Iterator;

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
        this.lines.removeAll(Arrays.asList(null, ""));
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

        System.out.println("VOLUME CALCULATIONS");

        super.analyzeHeader(lines);

        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {

            /*

            ------------------------------------------------------------
Leica System 1200, Volume Calculations, Version 8.72 Logfile
------------------------------------------------------------
Instrument Type        : TCRP1202+
Instrument Serial No.    : 264316
Store to Job        : 1026.320-F
Volume Calculation Start: 09.11.17, 8:22:32
-----------------------------------------------------------------------------------
Volume Calculation Results            Date computed: 08:22:32 -  9.11.17
Volume Calculation Method    : Deponie
Volume from Surface        : 1
  Computed from Point        : ------
  Computed from Elevation    : -----
Total Volume : 0.000
Total Volume : 0.000 CUT
Total Volume : 0.000 FILL
Average Thickness (Volume/Area): 0.000
-----------------------------------------------------------------------------------

------------------------------------------------------------
Leica System 1200, Volume Calculations, Version 8.72 Logfile
------------------------------------------------------------
Instrument Type		: TCRP1202+
Instrument Serial No.	: 264316

Store to Job		: 1026.320-F
Volume Calculation Start: 09.11.17, 8:20:58

-----------------------------------------------------------------------------------
Surface Triangulation Results

Surface Name			: 1	Date: 09.11.17
--------------------------------------------------------

Total Number of Points		:       6

Points used for Boundary	:       0
Points inside of Boundary	:       6


Total Number of Triangles	:       4

Longest Side of Triangle 2D	:   1.740
Longest Side of Triangle 3D	:   1.803

Maximum Elevation			: 263.565
Minimum Elevation			: 262.872

Surface Area 2D			: 2.871		Surface Area 3D 		: 3.259
Surface Perimeter 2D		: 6.643		Surface Perimeter 3D	: 7.203

-----------------------------------------------------------------------------------



++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                              System 1200 Logfile - End
                              System 1200 Logfile - Begin
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


------------------------------------------------------------
Leica System 1200, Volume Calculations, Version 8.72 Logfile
------------------------------------------------------------
Instrument Type		: TCRP1202+
Instrument Serial No.	: 264316

Store to Job		: 1026.320-F
Volume Calculation Start: 09.11.17, 8:22:32

-----------------------------------------------------------------------------------
Volume Calculation Results			Date computed: 08:22:32 -  9.11.17

Volume Calculation Method	: Deponie

Volume from Surface		: 1
  Computed from Point		: ------
  Computed from Elevation	: -----


Total Volume : 0.000

Total Volume : 0.000 CUT
Total Volume : 0.000 FILL

Average Thickness (Volume/Area): 0.000
-----------------------------------------------------------------------------------


             */

            //System.out.println(line);
        }

        success = true;

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
