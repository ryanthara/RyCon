/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.core.converter.zeiss;

import de.ryanthara.ja.rycon.util.NumberFormatter;

/**
 * This class implements several basic operations for conversion to or from Zeiss REC files.
 * <p>
 * The Zeiss REC file format and it's dialects (R4, R5, REC500 and M5) is a line based and column orientated
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
 * <p>
 * The line positions are grabbed from technical definitions and are reduced with the value of '-1' because of the
 * array position numbering in java, which starts with zero for the first position.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class BaseToolsZeiss {

    // positions are 'java' corrected with the value of '-1' because of array position start at '0'
    private static final int[] M5_LINE_POSITIONS = {
            11, 15,     // line number
            17, 19,     // point identification
            21, 47,     // point name
            49, 50,     // 1. word block: type mark
            52, 65,     // 1. word block: user information
            67, 70,     // 1. word block: units
            72, 73,     // 2. word block: type mark
            75, 88,     // 2. word block: user information
            90, 93,     // 2. word block: units
            95, 96,     // 3. word block: type mark
            98, 111,    // 3. word block: user information
            113, 116,   // 3. word block: units
            118         // error
    };
    private static final int[] R4_LINE_POSITIONS = {
            7, 8,       // point identification
            10, 16,     // point name
            18, 19,     // 1. word block: type mark
            21, 31,     // 1. word block: user information
            33, 36,     // 1. word block: units
            38, 39,     // 2. word block: type mark
            41, 51,     // 2. word block: user information
            53, 56,     // 2. word block: units
            58, 59,     // 3. word block: type mark
            61, 71,     // 3. word block: user information
            73, 76      // 3. word block: units
    };
    private static final int[] R5_LINE_POSITIONS = {
            11, 14,     // line number
            16, 17,     // point identification
            19, 25,     // point name
            27, 28,     // 1. word block: type mark
            30, 40,     // 1. word block: user information
            42, 45,     // 1. word block: units
            47, 48,     // 2. word block: type mark
            50, 60,     // 2. word block: user information
            62, 65,     // 2. word block: units
            67, 68,     // 3. word block: type mark
            70, 80,     // 3. word block: user information
            82, 85      // 3. word block: units
    };
    private static final int[] REC500_LINE_POSITIONS = {
            3, 6,       // line counter
            8, 21,      // point number
            22, 34,     // point identification
            36, 37,     // 1. word block: type mark
            38, 49,     // 1. word block: value
            51, 52,     // 2. word block: type mark
            53, 65,     // 2. word block: value
            67, 68,     // 3. word block: type mark
            69, 77      // 3. word block: value
    };

    /**
     * Member which indicates a target height.
     */
    static boolean TARGET_HEIGHT = false;

    /**
     * Member which indicates a instrument height.
     */
    static boolean INSTRUMENT_HEIGHT = true;

    /**
     * Returns the integer array with the line positions of the elements in the chosen Zeiss RED dialect.
     *
     * @param dialect chosen dialect
     *
     * @return line positions
     */
    static int[] getLinePositions(ZeissDialect dialect) {
        switch (dialect) {
            case REC500:
                return REC500_LINE_POSITIONS;
            case R4:
                return R4_LINE_POSITIONS;
            case R5:
                return R5_LINE_POSITIONS;
            case M5:
                return M5_LINE_POSITIONS;
            default:
                System.err.println("BaseToolsZeiss.getLinePositions() : Unknown / wrong dialect used.");
                return new int[]{};
        }
    }

    /**
     * Prepares a Zeiss REC format line of coordinates for the given dialect.
     *
     * @param dialect    dialect (R4, R5, REC500 or M5)
     * @param number     point number
     * @param code       code column (REC500 and M5)
     * @param easting    easting coordinate
     * @param northing   northing coordinate
     * @param height     height coordinate
     * @param lineNumber line number
     *
     * @return prepared line as string in Zeiss REC format
     */
    static String prepareLineOfCoordinates(ZeissDialect dialect, String number, String code, String easting, String northing, String height, int lineNumber) {
        StringBuilder builder = new StringBuilder();
        String result = "";

        switch (dialect) {
            case R4:
                builder.append("For R4|");
                builder.append("KR ");
                builder.append(String.format("%-7s", number));
                builder.append("|Y  ");
                builder.append(String.format("%11.11s", NumberFormatter.fillDecimalPlace(easting, 3)));
                builder.append(" m   ");
                builder.append("|X  ");
                builder.append(String.format("%11.11s", NumberFormatter.fillDecimalPlace(northing, 3)));
                builder.append(" m   ");

                if (!height.equalsIgnoreCase("")) {
                    builder.append("|Z  ");
                    builder.append(String.format("%11.11s", NumberFormatter.fillDecimalPlace(height, 3)));
                    builder.append(" m   ");
                    builder.append("|");
                }

                result = builder.toString();
                break;
            case R5:
                builder.append("For R5|Adr");
                builder.append(String.format("%4d", lineNumber));
                builder.append("|PI1 ");
                builder.append(String.format("%-14s", number));
                builder.append(String.format("%-13s", code));
                builder.append("|Y  ");
                builder.append(String.format("%14.14s", NumberFormatter.fillDecimalPlace(easting, 3)));
                builder.append(" m   ");
                builder.append("|X  ");
                builder.append(String.format("%14.14s", NumberFormatter.fillDecimalPlace(northing, 3)));
                builder.append(" m   ");

                if (!height.equalsIgnoreCase("")) {
                    builder.append("|Z  ");
                    builder.append(String.format("%14.14s", NumberFormatter.fillDecimalPlace(height, 3)));
                    builder.append(" m   ");
                    builder.append("|");
                }

                result = builder.toString();
                break;
            case REC500:
                builder.append("   ");
                builder.append(String.format("%4d", lineNumber));
                builder.append(" ");
                builder.append(String.format("%-14s", number));
                builder.append(String.format("%-13s", code));
                builder.append(" ");
                builder.append("Y ");
                builder.append(String.format("%12s", NumberFormatter.fillDecimalPlace(easting, 3)));
                builder.append(" ");
                builder.append("X ");
                builder.append(String.format("%13s", NumberFormatter.fillDecimalPlace(northing, 3)));

                if (!height.equalsIgnoreCase("")) {
                    builder.append(" ");
                    builder.append("Z ");
                    builder.append(String.format("%9s", NumberFormatter.fillDecimalPlace(height, 3)));
                }

                result = builder.toString();
                break;
            case M5:
                builder.append("For M5|Adr ");
                builder.append(String.format("%5d", lineNumber));
                builder.append("|PI1 ");
                builder.append(String.format("%-27s", number));
                builder.append("|Y  ");
                builder.append(String.format("%-14s", NumberFormatter.fillDecimalPlace(easting, 3)));
                builder.append(" m   ");
                builder.append("|X  ");
                builder.append(String.format("%-14s", NumberFormatter.fillDecimalPlace(northing, 3)));
                builder.append(" m   ");

                if (!height.equalsIgnoreCase("")) {
                    builder.append("|Z  ");
                    builder.append(String.format("%-14s", NumberFormatter.fillDecimalPlace(height, 3)));
                    builder.append(" m   ");
                    builder.append("| ");
                }

                result = builder.toString();
                break;
        }

        return result;
    }

    /**
     * Prepares a line of instrument or target height for the given dialect.
     *
     * @param dialect            dialect (R4, R5, REC500 or M5)
     * @param isInstrumentHeight true if instrument height is use, false for target height
     * @param number             point number
     * @param code               code column (REC500 and M5)
     * @param height             target or instrument height
     * @param lineNumber         current line number
     *
     * @return prepared line as string in Zeiss REC format
     */
    static String prepareLineOfInstrumentOrTargetHeight(ZeissDialect dialect, boolean isInstrumentHeight, String number,
                                                        String code, String height, int lineNumber) {
        StringBuilder builder = new StringBuilder();
        String heightType, result = "";

        if (isInstrumentHeight) {
            heightType = "ih";
        } else {
            heightType = "th";
        }

        switch (dialect) {
            case R4:
                builder.append("For R4|");
                builder.append("KR ");
                builder.append(String.format("%-7s", number));
                builder.append("|");
                builder.append(heightType);
                builder.append(" ");
                builder.append(String.format("%11.11s", NumberFormatter.fillDecimalPlace(height, 3)));
                builder.append(" m   ");
                builder.append("|");

                result = builder.toString();
                break;
            case R5:
                builder.append("For R5|Adr");
                builder.append(String.format("%4d", lineNumber));
                builder.append("|PI1 ");
                builder.append(String.format("%-27s", number));
                builder.append("|");
                builder.append(heightType);
                builder.append(" ");
                builder.append(String.format("%14.14s", NumberFormatter.fillDecimalPlace(height, 3)));
                builder.append(" m   ");
                builder.append("|");

                result = builder.toString();
                break;
            case REC500:
                builder.append("   ");
                builder.append(String.format("%4d", lineNumber));
                builder.append(" ");
                builder.append(String.format("%-14s", number));
                builder.append(String.format("%-13s", code));
                builder.append(" ");
                builder.append(heightType);
                builder.append(String.format("%12s", NumberFormatter.fillDecimalPlace(height, 3)));
                builder.append(" ");

                result = builder.toString();
                break;
            case M5:
                builder.append("For M5|Adr ");
                builder.append(String.format("%5d", lineNumber));
                builder.append("|PI1 ");
                builder.append(String.format("%-14s", number));
                builder.append(String.format("%-13s", code));
                builder.append("|");
                builder.append(heightType);
                builder.append(" ");
                builder.append(String.format("%-14s", NumberFormatter.fillDecimalPlace(height, 3)));
                builder.append(" m   |                      |                      | ");

                result = builder.toString();
                break;
        }

        return result;
    }

    /**
     * Prepares a line of measurement for the given dialect.
     *
     * @param dialect         dialect (R4, R5, REC500 or M5)
     * @param number          point number
     * @param code            code column (REC500 and M5)
     * @param horizontalAngle horizontal angle
     * @param verticalAngle   vertical angle
     * @param slopeDistance   slope distance
     * @param lineNumber      current line number
     *
     * @return prepared line as string in Zeiss REC format
     */
    static String prepareLineOfMeasurement(ZeissDialect dialect, String number, String code, String horizontalAngle,
                                           String verticalAngle, String slopeDistance, int lineNumber) {
        StringBuilder builder = new StringBuilder();
        String result = "";

        switch (dialect) {
            case R4:
                builder.append("For R4|");
                builder.append("KR ");
                builder.append(String.format("%-7s", number));
                builder.append("|Hz ");
                builder.append(String.format("%11.11s", NumberFormatter.fillDecimalPlace(horizontalAngle, 4)));
                builder.append(" gon ");
                builder.append("|V1  ");
                builder.append(String.format("%11.11s", NumberFormatter.fillDecimalPlace(verticalAngle, 4)));
                builder.append(" gon ");
                builder.append("|D  ");
                builder.append(String.format("%11.11s", NumberFormatter.fillDecimalPlace(slopeDistance, 3)));
                builder.append(" m   ");
                builder.append("|");

                result = builder.toString();
                break;
            case R5:
                builder.append("For R5|Adr");
                builder.append(String.format("%4d", lineNumber));
                builder.append("|PI1 ");
                builder.append(String.format("%-27s", number));
                builder.append("|Hz ");
                builder.append(String.format("%14.14s", NumberFormatter.fillDecimalPlace(horizontalAngle, 4)));
                builder.append(" gon ");
                builder.append("|V1 ");
                builder.append(String.format("%14.14s", NumberFormatter.fillDecimalPlace(verticalAngle, 4)));
                builder.append(" gon ");
                builder.append("|D  ");
                builder.append(String.format("%14.14s", NumberFormatter.fillDecimalPlace(slopeDistance, 3)));
                builder.append(" m   ");
                builder.append("|");

                result = builder.toString();
                break;
            case REC500:
                builder.append("   ");
                builder.append(String.format("%4d", lineNumber));
                builder.append(" ");
                builder.append(String.format("%-14s", number));
                builder.append(String.format("%-13s", code));
                builder.append(" ");
                builder.append("Hz");
                builder.append(String.format("%12s", NumberFormatter.fillDecimalPlace(horizontalAngle, 4)));
                builder.append(" ");
                builder.append("V1");
                builder.append(String.format("%13s", NumberFormatter.fillDecimalPlace(verticalAngle, 4)));
                builder.append(" ");
                builder.append("D ");
                builder.append(String.format("%9s", NumberFormatter.fillDecimalPlace(slopeDistance, 3)));

                result = builder.toString();
                break;
            case M5:
                builder.append("For M5|Adr ");
                builder.append(String.format("%5d", lineNumber));
                builder.append("|PI1 ");
                builder.append(String.format("%-14s", number));
                builder.append(String.format("%-13s", code));
                builder.append("|Hz ");
                builder.append(String.format("%-14s", NumberFormatter.fillDecimalPlace(horizontalAngle, 4)));
                builder.append(" m   ");
                builder.append("|V1 ");
                builder.append(String.format("%-14s", NumberFormatter.fillDecimalPlace(verticalAngle, 4)));
                builder.append(" m   ");
                builder.append("|D  ");
                builder.append(String.format("%-14s", NumberFormatter.fillDecimalPlace(slopeDistance, 3)));
                builder.append(" m   ");
                builder.append("| ");

                result = builder.toString();
                break;
        }

        return result;
    }

} // end of BaseToolsZeiss