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
package de.ryanthara.ja.rycon.core;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.core.converter.gsi.BaseToolsGsi;
import de.ryanthara.ja.rycon.data.PreferenceKeys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Instances of {@code GsiTidyUp} provides functions to clean up a
 * Leica Geosystems GSI formatted file with some smart functions.
 * <p>
 * With version 3 the tidy up function is completely rewritten and
 * fully operational.
 *
 * @author sebastian
 * @version 3
 * @since 12
 */
public class GsiTidyUp {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given a line based
     * Leica Geosystems GSI formatted file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GsiTidyUp(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Tidies up structured measurement files with a simple 'intelligence'.
     * <p>
     * The following aspects are become considerate.
     * <ul>
     * <li>Single or double station lines are tidied up</li>
     * <li>target point measurements are tidied up</li>
     * <li>control point measurements are tidied up</li>
     * </ul>
     * The simple 'intelligence' of RyCON tries to handle special structures and indifferent
     * line sequences which can be identified by the order or a special naming, like 'STKE'
     * for example, what is the identification for the control points.
     *
     * @param holdStations      hold the station lines
     * @param holdControlPoints hold the control point lines
     *
     * @return tidied up measurement file
     */
    public ArrayList<String> processTidyUp(boolean holdStations, boolean holdControlPoints) {
        ArrayList<String> result = new ArrayList<>();

        // remove one or more station line at the beginning when the checkbox hold stations is not set

        if (!holdStations) {
            Iterator<String> iterator = readStringLines.iterator();
            while (iterator.hasNext()) {
                if (checkIsStationLine(iterator.next())) {
                    iterator.remove();
                } else {
                    break;
                }
            }

            // remove duplicate station lines in the file when the checkbox hold stations is not set
            boolean currentIsStationLine = false;

            Iterator<String> iterator2 = readStringLines.iterator();
            while (iterator2.hasNext()) {
                if (checkIsStationLine(iterator2.next())) {
                    if (currentIsStationLine) {
                        iterator2.remove();
                    } else {
                        currentIsStationLine = true;
                    }
                } else {
                    currentIsStationLine = false;
                }
            }
        }

        // Use a helper array to identify the different lines by 'type'.
        final lineType[] helperArray = new lineType[readStringLines.size()];

        /*
         * Detect target points in one or two face measurements for control point identification.
         *
         * One face measurements are indicated by a measurement line that contains
         * only zero coordinates. The next line has either a different point number
         * with zero coordinates or is a free station line.
         *
         * Two face measurements are indicated by two lines with the same point number.
         * The first line contains the coordinates of the control point, the second one
         * contains only zero coordinates.
         *
         * It is difficult to detect incomplete two face measurement lines or broken
         * stations.
         */
        String
                currLine,
                nextLine;

        Set<String> targetPointNumbers = new HashSet<>();

        for (int i = 0; i < readStringLines.size(); i++) {
            currLine = readStringLines.get(i);

            if (i < readStringLines.size() - 1) {
                nextLine = readStringLines.get(i + 1);
            } else {
                nextLine = currLine;
            }

            // detect target point one face measurement
            if (BaseToolsGsi.isTargetLine(currLine)) {
                helperArray[i] = lineType.TARGET_POINT;

                targetPointNumbers.add(BaseToolsGsi.getPointNumber(currLine));
            } else {
                // detect target point two face measurement
                if (BaseToolsGsi.isTargetLine(nextLine)) {
                    if (BaseToolsGsi.getPointNumber(currLine).equalsIgnoreCase(BaseToolsGsi.getPointNumber(nextLine))) {
                        helperArray[i] = lineType.TARGET_POINT;

                        targetPointNumbers.add(BaseToolsGsi.getPointNumber(currLine));
                    } else {
                        // check for control point -> measurement
                        if (checkIsStationLine(currLine)) { // detect station line
                            helperArray[i] = lineType.STATION;
                        } else if (checkIsControlPoint(currLine)) { // detect control points after the station
                            final String currentPoint = BaseToolsGsi.getPointNumber(currLine);
                            final String number = "0000" + currentPoint.substring(0, currentPoint.length() - 4);

                            if (targetPointNumbers.contains(number)) {
                                helperArray[i] = lineType.CONTROL_POINT;
                            } else {
                                helperArray[i] = lineType.MEASUREMENT;
                            }
                        } else {
                            helperArray[i] = lineType.MEASUREMENT;
                        }
                    }
                } else if (checkIsStationLine(currLine)) { // detect station line
                    helperArray[i] = lineType.STATION;
                } else if (checkIsControlPoint(currLine)) { // detect control points after the station
                    final String currentPoint = BaseToolsGsi.getPointNumber(currLine);
                    final String number = "0000" + currentPoint.substring(0, currentPoint.length() - 4);

                    if (targetPointNumbers.contains(number)) {
                        helperArray[i] = lineType.CONTROL_POINT;
                    } else {
                        helperArray[i] = lineType.MEASUREMENT;
                    }
                } else {
                    helperArray[i] = lineType.MEASUREMENT;
                }
            }
        }

        // add needed points to the result ArrayList<String>
        for (int i = 0; i < helperArray.length; i++) {
            switch (helperArray[i]) {
                case CONTROL_POINT:
                    if (holdControlPoints) {
                        result.add(readStringLines.get(i));
                    }

                    break;
                case MEASUREMENT:
                    result.add(readStringLines.get(i));

                    break;
                case STATION:
                    if (holdStations) {
                        result.add(readStringLines.get(i));
                    }

                    break;
                case TARGET_POINT:
                    // delete always

                    break;
            }
        }

        return result;
    }

    private boolean checkIsControlPoint(String line) {
        final String controlPointIdentifier = Main.pref.getUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING);

        return line.toUpperCase().contains(controlPointIdentifier);
    }

    private boolean checkIsStationLine(String line) {
        // initialize identifiers and add leading zeros for unique string comparison
        String freeStationIdentifier = "0000" + Main.pref.getUserPreference(PreferenceKeys.PARAM_FREE_STATION_STRING);
        String stationIdentifier = "0000" + Main.pref.getUserPreference(PreferenceKeys.PARAM_KNOWN_STATION_STRING);

        // adjust identifier for GSI16 format
        if (line.startsWith("*")) {
            freeStationIdentifier = "00000000" + freeStationIdentifier;
            stationIdentifier = "00000000" + stationIdentifier;
        }

        return line.toUpperCase().contains(freeStationIdentifier) || line.toUpperCase().contains(stationIdentifier);
    }

    private enum lineType {CONTROL_POINT, MEASUREMENT, STATION, TARGET_POINT}

} // end of GsiTidyUp
