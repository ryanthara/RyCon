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
package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.data.PreferenceHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class provides functions to clean up a Leica GSI format with some smart functions.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSITidyUp {

    private boolean isGSI16 = false;
    private ArrayList<String> readStringLines = null;
    private TreeSet<Integer> foundCodes = new TreeSet<>();

    /**
     * Class constructor for read line based Leica GSI formatted files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GSITidyUp(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Cleans up a LTOP GSI8 polar measurement file and eliminates reference points and control points.
     * <p>
     * The measurement file must have the following structure:
     * <ul>
     * <li>station line</li>
     * <li>reference point</li>
     * <li>...</li>
     * <li>reference point</li>
     * <li>control point ('STKE')</li>
     * <li>measurement points</li>
     * <li>...</li>
     * <li>measurement points</li>
     * <li>control point ('STKE')</li>
     * </ul>
     *
     * @return clean up LTOP MES file
     */
    public ArrayList<String> processLTOPClean() {
        ArrayList<String> result = new ArrayList<>();

        /*
        Strategy
            - identify a station line (WI 84, 85, 86 and 88 (instrument height)
            - identify the first control point after the reference points by the char sequence 'STKE'
            - identify the last control point before the next station line or at the file ending by the char sequence 'STKE'
         */

        for (String line : readStringLines) {

            int size;

            if (line.startsWith("*")) {
                size = 24;
                line = line.substring(1, line.length());
            } else {
                size = 16;
            }

            String currentLine, beforeLine, nextLine;
            int tokens = (line.length() + size - 1) / size;

            System.out.println("Number of 'tokens': " + tokens);

        }

        return result;
    }

    /**
     * Tidy up resurrection (stations) and control point measurements from files.
     * <p>
     * RyCON has the intelligence to tidy up resurrection and control points by a given
     * structure in the measurement file. Stations are identified by word index (WI) and
     * the control / stake out points by order in the file and the pattern 'STKE'.
     *
     * @param holdStations      decide to hold station lines
     * @param holdControlPoints decide to hold control points
     *
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<String> processTidyUp(boolean holdStations, boolean holdControlPoints) {

        /*
          Inner class in method processTidyUp to simplify getting the number (substring operations)
         */
        class StringHelper {

            /**
             * Check a line for being a target line (three times coordinate is zero)
             * @param line line to check
             * @return true if it is a target line
             */
            private boolean isTargetLine(String line) {
                if (isGSI16) {
                    return (line.split("0000000000000000").length - 1) == 3;
                } else {
                    return (line.split("00000000").length - 1) == 3;
                }
            }

            /**
             * Return the number of a given line (substring operations)
             * @param string string to get a defined substring from
             * @return substring
             */
            private String numberConvert(String string) {
                if (isGSI16) {
                    return string.substring(8, 24);
                } else {
                    return string.substring(8, 16);
                }
            }

        } // end of class StringHelper

        String controlPointIdentifier = Main.pref.getUserPref(PreferenceHandler.PARAM_CONTROL_POINT_STRING);
        String freeStationIdentifier = "000" + Main.pref.getUserPref(PreferenceHandler.PARAM_FREE_STATION_STRING);
        String stationIdentifier = "000" + Main.pref.getUserPref(PreferenceHandler.PARAM_KNOWN_STATION_STRING);

        ArrayList<String> result = new ArrayList<>();

        // handle special case / exception when the file starts with one or more free station or (station) lines
        String firstRow = readStringLines.get(0).toUpperCase();

        if (firstRow.startsWith("*")) {
            isGSI16 = true;
            freeStationIdentifier = "00000000" + freeStationIdentifier;
            stationIdentifier = "00000000" + stationIdentifier;
        } else {
            isGSI16 = false;
        }

        breakOut:
        // breaking out of nested loops with a label called 'breakOut'
        if (firstRow.contains(freeStationIdentifier) || firstRow.contains(stationIdentifier)) {
            for (Iterator<String> iter = readStringLines.iterator(); iter.hasNext(); ) {
                firstRow = iter.next();
                if (firstRow.toUpperCase().contains(freeStationIdentifier) || firstRow.toUpperCase().contains(stationIdentifier)) {
                    if (!holdStations) {
                        iter.remove();
                    }
                } else if (firstRow.toUpperCase().contains(controlPointIdentifier)) {
                    if (!holdControlPoints) {
                        iter.remove();
                    }
                } else {
                    break breakOut;
                }
            }
        }

        /*
         * Use a helper array to identify the different lines by 'type'.
         *
         * type:
         * =================================
         * 1: target measurement
         * 2: free station
         * 3: stake out value / control points
         * 9: measurement value
         */
        int[] helperArray = new int[readStringLines.size()];

        /*
         * Try to detect single and two face measurements of control points.
         *
         * A one face measured control point contains only zero values as coordinates. A two face
         * measured control point contains the coordinates of the control point in the first, and
         * only zeros in the second line. Therefore the comparison has to be made from current
         * to previous line!
         *
         * The first comparison is made with the biggest integer value.
         */
        String currentLine;
        String previousLine = "12345678901234567890" + Integer.toString(Integer.MAX_VALUE);

        // The operations starts with the last line outside the for-loop!
        for (int i = 0; i < readStringLines.size(); i++) {
            currentLine = readStringLines.get(i);

            // detect two face measurement for target measurement
            String currentLineNumber = new StringHelper().numberConvert(currentLine);
            String previousLineNumber = new StringHelper().numberConvert(previousLine);

            // detect line type
            if (new StringHelper().isTargetLine(currentLine)) {
                helperArray[i] = 1;

                if (currentLineNumber.equals(previousLineNumber)) {
                    helperArray[i - 1] = 1;
                } else if (previousLineNumber.contains(controlPointIdentifier)) {
                    helperArray[i - 1] = 3;
                }
            } else if (currentLine.contains(freeStationIdentifier) || currentLine.contains(stationIdentifier)) {
                helperArray[i] = 2;
            } else if (currentLine.contains(controlPointIdentifier)) {

                // line above is free station
                if (previousLineNumber.contains(freeStationIdentifier) || currentLineNumber.contains(stationIdentifier)) {
                    helperArray[i] = 3;
                }
                // line above is the same control point -> stake out point is marked as target point
                else if (currentLineNumber.equals(previousLineNumber)) {
                    if (holdControlPoints) {
                        helperArray[i] = 3;
                    } else {
                        helperArray[i] = 1;
                    }
                }
                // line above is control point and not last line -> stake out point is measurement value
                else {
                    if (i < readStringLines.size() - 1) {
                        helperArray[i] = 9;
                    } else {
                        helperArray[i] = 3;
                    }
                }
            } else {
                helperArray[i] = 9;
            }
            previousLine = currentLine;
        }

        // preparing the result lines
        for (int i = 0; i < helperArray.length; i++) {
            int value = helperArray[i];

            String resultLine = BaseToolsGSI.prepareLineEnding(readStringLines.get(i));

            if (value == 9) {
                result.add(resultLine);
            } else {
                if (holdStations) {
                    if (value == 2) {
                        result.add(resultLine);
                    }
                }

                if (holdControlPoints) {
                    if (value == 3) {
                        result.add(resultLine);
                    }
                }
            }
        }

        return result;
    }

} // end of GSITidyUp
