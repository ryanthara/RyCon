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
 * The {@code ReferencePlaneStructure} implements functions based on the REFERENCE PLANE part of
 * the <tt>Leica Geosystems</tt> logfile.txt for {@code RyCON}.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class ReferencePlaneStructure extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;

    /**
     * Constructs a new {@code ReferencePlaneStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public ReferencePlaneStructure(ArrayList<String> lines) {
        this.lines = lines;
        this.lines.removeAll(Arrays.asList(null, ""));
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

        for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            String line = iterator.next();

            System.out.println(": " + line);

            /*
            ------------------------------------------------------
Leica System 1200 Reference Plane, Version 8.72 Logfile
------------------------------------------------------
Instrument Type		: TCRP1202+
Instrument Serial No.	: 264316

Store To Job		: 1026.320-FIX
Reference Plane Start	: 09.11.17, 08:19:08

TPS Station		:             FS01	E=           20.328	N_           36.698	H=          263.145	hi=      0.000
						X=            0.732	Y=           -2.163	Z=            0.776

Reference Plane Information
---------------------------
Reference Plane ID	:               1
Number of Points	:               3

Offset Limit		:           0.300
Plane Std Dev		:           -----

Plane Offset		:           0.000
Origin Point ID		:               1	X=            0.000	Y=            0.000	Z=            0.000

Definition Point ID	:                3	E=           22.288	N=           35.397	H=          262.894	dD=           -0.000	Use Flag=         Ja
Definition Point ID	:                2	E=           22.680	N=           36.778	H=          262.911	dD=            0.000	Use Flag=         Ja
Definition Point ID	:                1	E=           22.708	N=           36.779	H=          262.765	dD=            0.000	Use Flag=         Ja


Scanning Perimeter
------------------
First Perimeter Point	:			E=           22.677	N=           36.748	H=          262.883
Second Perimeter Point	:			E=           22.317 	N=           35.433	H=          262.794

Horizontal/X Spacing	:           0.050
Vertical/Z Spacing	:           0.050

Points Scanned		:               7
Points Skipped		:               0


Scanned Points
--------------
Scanned Point		:        Scan0001	E=           22.679	N=           36.748	H=          262.883	dD=            0.002	dH=            0.008
Dist/Height from Plane	:                 	dDist=        0.002	dH=           0.008
Scanned Point		:        Scan0002	E=           22.665	N=           36.700	H=          262.883	dD=            0.001	dH=            0.008
Dist/Height from Plane	:                 	dDist=        0.001	dH=           0.008

             */

            //System.out.println(line);
        }

        success = true;

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

} // end of ReferencePlaneStructure
