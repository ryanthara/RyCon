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
 * Instances of {@code GsiClearUp} provides functions to clear up a
 * Leica Geosystems GSI formatted file with some smart functions.
 * <p>
 * With version 3 the clear up function is completely rewritten and
 * fully operational. It was formerly designed to work only with
 * point based (WI 81, 82 and 83) files.
 * <p>
 * With version 4 it works on point based (WI 81, 82 and 83) files
 * as well as on station based (WI 84, 85 and 86) files.
 *
 * @author sebastian
 * @version 4
 * @since 12
 */
public class GsiClearUp {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given a line based
     * Leica Geosystems GSI formatted file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GsiClearUp(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Clears up structured measurement files with a simple 'intelligence'.
     * <p>
     * The following aspects are become considerate.
     * <ul>
     * <li>Single or double station lines are cleared up</li>
     * <li>target point measurements are cleared up</li>
     * <li>control point measurements are cleared up</li>
     * </ul>
     * The simple 'intelligence' of <tt>RyCON</tt> tries to handle special structures and indifferent
     * line sequences which can be identified by the order or a special naming, like 'STKE'
     * for example, what is the set identification for the control points.
     * <p>
     * A structured measurement file from the perspective of Rycon is ordered 'time forwards'
     * and contains a station line, one or more target points and an optional control point.
     * <p>
     * Point based files only have the WI 81, 82 and 83 for the whole content. Against that
     * Station based files has the WI 84, 85 and 86 for stations and the WI 81, 82 and 83 for
     * the observations, control point measurements and target point lines.
     *
     * @param holdStations      hold the station lines
     * @param holdControlPoints hold the control point lines
     *
     * @return cleared up measurement file
     */
    public ArrayList<String> processClearUp(boolean holdStations, boolean holdControlPoints) {
        // station based files always has a station line with the WI84, 85 and 86 in the first line!
        String firstLine = readStringLines.get(0);

        if (checkIsStationLineStationBased(firstLine)) {
            return processStationBasedClearUp(holdStations, holdControlPoints);
        } else {
            return processPointBasedClearUp(holdStations, holdControlPoints);
        }
    }

    private boolean checkIsControlPoint(String line) {
        final String controlPointIdentifier = Main.pref.getUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING);

        return line.toUpperCase().contains(controlPointIdentifier);
    }

    private boolean checkIsStationLinePointBased(String line) {
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

    private boolean checkIsStationLineStationBased(String line) {
        return (line.contains("84..") && line.contains("85..") && line.contains("86.."));
    }

    private void detectControlPointAfterStationLine(lineType[] helperArray, String currLine, Set<String> targetPointNumbers, int i) {
        String number = prepareControlPointNumberForComparison(currLine);

        if (targetPointNumbers.contains(number)) {
            helperArray[i] = lineType.CONTROL_POINT;
        } else {
            helperArray[i] = lineType.MEASUREMENT;
        }
    }

    private String prepareControlPointNumberForComparison(String currLine) {
        final String controlPointIdentifier = Main.pref.getUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING);

        String prefix = "";
        for (int j = 0; j < controlPointIdentifier.length(); j++) {
            prefix = prefix.concat("0");
        }

        String currentPoint = BaseToolsGsi.getPointNumber(currLine);
        return prefix + currentPoint.substring(0, currentPoint.length() - controlPointIdentifier.length());
    }

    /*
     * This version is used to clear up point based (only WI 81, 82 and 83) files.
     * <p>
     * It was totally rewritten for version 3.
     *
     * @param holdStations      hold the station lines
     * @param holdControlPoints hold the control point lines
     *
     * @return cleared up measurement file
     */
    private ArrayList<String> processPointBasedClearUp(boolean holdStations, boolean holdControlPoints) {
        ArrayList<String> result = new ArrayList<>();

        // remove one or more station lines at the beginning and in the file
        // when the checkbox hold stations is not set
        if (!holdStations) {
            for (Iterator<String> iterator = readStringLines.iterator(); iterator.hasNext(); ) {
                if (checkIsStationLinePointBased(iterator.next())) {
                    iterator.remove();
                } else {
                    break;
                }
            }

            readStringLines = removeDuplicateStationLines(readStringLines);
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
        String currLine, nextLine;

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
                        // Check for control point -> measurement
                        if (checkIsStationLinePointBased(currLine)) { // detect station line
                            helperArray[i] = lineType.STATION;
                        } else if (checkIsControlPoint(currLine)) { // detect control points after the station
                            detectControlPointAfterStationLine(helperArray, currLine, targetPointNumbers, i);
                        } else {
                            helperArray[i] = lineType.MEASUREMENT;
                        }
                    }
                } else if (checkIsStationLinePointBased(currLine)) { // detect station line
                    helperArray[i] = lineType.STATION;
                } else if (checkIsControlPoint(currLine)) { // detect control points after the station
                    detectControlPointAfterStationLine(helperArray, currLine, targetPointNumbers, i);
                } else {
                    helperArray[i] = lineType.MEASUREMENT;
                }
            }
        }

        // Add needed points to the result ArrayList<String>
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
                default:
                    break;
            }
        }

        return new ArrayList<>(result);
    }

    /*
     * This version is used to clear up station based (WI 84, 85 and 86) for stations
     * and (WI 81, 82 and 83) for measurements files.
     * <p>
     * It was totally rewritten for version 4.
     *
     * @param holdStations      hold the station lines
     * @param holdControlPoints hold the control point lines
     *
     * @return cleared up measurement file
     */
    private ArrayList<String> processStationBasedClearUp(boolean holdStations, boolean holdControlPoints) {
        ArrayList<String> result = new ArrayList<>();

        // Remove directly sequent station lines
        ArrayList<String> reduced;
        if (!holdStations) {
            reduced = removeDuplicateStationLines(readStringLines);
        } else {
            reduced = new ArrayList<>(readStringLines);
        }

        /*
         * Store target point numbers in a hash set to avoid duplicate entries.
         * This hash set is used to identify control points correctly.
         */
        Set<String> targetPointNumbers = new HashSet<>();

        for (int i = 0; i < reduced.size(); i++) {
            String currentLine = reduced.get(i);
            String nextLine;
            if (i < reduced.size() - 1) {
                nextLine = reduced.get(i + 1);
            } else {
                nextLine = currentLine;
            }

            // Find station line (WI 84, 85 and 86)
            if (checkIsStationLineStationBased(currentLine)) {
                if (holdStations) {
                    result.add(currentLine);
                }
            } else {
                // Find target point in face one
                if (BaseToolsGsi.isTargetLine(currentLine)) {
                    targetPointNumbers.add(BaseToolsGsi.getPointNumber(currentLine));
                } else {
                    // Find target point in face two
                    if (BaseToolsGsi.isTargetLine(nextLine)) {
                        if (BaseToolsGsi.getPointNumber(currentLine).equals(BaseToolsGsi.getPointNumber(nextLine))) {
                            targetPointNumbers.add(BaseToolsGsi.getPointNumber(currentLine));
                        }
                    } else {
                        /*
                         * Find one or more orientation control points. They only can be
                         * found if they are measured as used target points.
                         */
                        if (checkIsControlPoint(currentLine)) {
                            System.out.println(currentLine);
                            if (targetPointNumbers.contains(prepareControlPointNumberForComparison(currentLine))) {
                                if (holdControlPoints) {
                                    result.add(currentLine);
                                }
                            } else {
                                // Add the unknown control or staked point
                                result.add(currentLine);
                            }
                        } else {
                            // Add measurement point
                            result.add(currentLine);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(result);
    }

    private ArrayList<String> removeDuplicateStationLines(ArrayList<String> lines) {
        boolean currentIsStationLine = false;

        for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            if (checkIsStationLinePointBased(iterator.next())) {
                if (currentIsStationLine) {
                    iterator.remove();
                } else {
                    currentIsStationLine = true;
                }
            } else {
                currentIsStationLine = false;
            }
        }

        return new ArrayList<>(lines);
    }

    private enum lineType {CONTROL_POINT, MEASUREMENT, STATION, TARGET_POINT}

} // end of GsiClearUp
