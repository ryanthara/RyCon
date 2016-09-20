/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.converter.zeiss;

/**
 * This class implements several basic operations for conversion to or from Zeiss REC files.
 * <p>
 * The Zeiss REC file format and it's dialects (R4, R5, REC500 and M5) are a line based and column orientated
 * file format developed by Zeiss to store coordinates and measurement information in text based files.
 * <p>
 * Example file in M5 format:
 * <p>
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9----+---10----+---11----+----12
 * !-------------------------------------------------------------------------------
 * ! Folgende Daten wurden von RyCON Build xxx am 03.02.2016 erzeugt.
 * !-------------------------------------------------------------------------------
 * For M5|Adr     1|TI  Berechn. Punkt             |                      |                      |                      |
 * For M5|Adr     2|PI1 2154H                      |th 1.600          m   |                      |                      |
 * For M5|Adr     3|PI1 2154H                      |Hz 34.9078        gon |V1 106.3481       gon |D  89.893         m   |
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class BaseToolsZeiss {

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String R4 = "R4";

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String R5 = "R5";

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String REC500 = "REC500";

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String M5 = "M5";

    public static final int[] M5_LINE_POSITIONS = {
            11, 17,     // line number
            17, 20,     // point identification
            21, 48,     // point name
            49, 51,     // 1. word block: type mark
            52, 66,     // 1. word block: user information
            67, 71,     // 1. word block: units
            72, 74,     // 2. word block: type mark
            75, 89,     // 2. word block: user information
            90, 94,     // 2. word block: units
            95, 97,     // 3. word block: type mark
            98, 112,    // 3. word block: user information
            113, 116,   // 3. word block: units
            118         // error
    };

    public static final int[] R4_LINE_POSITIONS = {
            7, 9,       // point identification
            10, 17,     // point name
            18, 20,     // 1. word block: type mark
            21, 32,     // 1. word block: user information
            33, 37,     // 1. word block: units
            38, 40,     // 2. word block: type mark
            41, 51,     // 2. word block: user information
            54, 56,     // 2. word block: units
            58, 60,     // 3. word block: type mark
            61, 72,     // 3. word block: user information
            73, 77      // 3. word block: units
    };

    public static final int[] R5_LINE_POSITIONS = {
            11, 16,     // line number
            16, 18,     // point identification
            19, 26,     // point name
            27, 29,     // 1. word block: type mark
            30, 41,     // 1. word block: user information
            42, 46,     // 1. word block: units
            47, 49,     // 2. word block: type mark
            50, 61,     // 2. word block: user information
            62, 66,     // 2. word block: units
            67, 69,     // 3. word block: type mark
            70, 81,     // 3. word block: user information
            82, 86      // 3. word block: units
    };
    public static final int[] REC500_LINE_POSITIONS = {
            3, 7,       // line counter
            8, 22,      // point number
            22, 35,     // point identification
            36, 38,     // 1. word block: type mark
            38, 50,     // 1. word block: value
            51, 53,     // 2. word block: type mark
            53, 66,     // 2. word block: value
            67, 69,     // 3. word block: type mark
            69, 78      // 3. word block: value
    };

} // end of BaseToolsZeiss
