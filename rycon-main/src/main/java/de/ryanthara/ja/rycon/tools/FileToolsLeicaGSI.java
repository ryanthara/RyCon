/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.*;

/**
 * This class implements several basic operations on Leica GSI files.
 * <p>
 * The Leica Geo Serial Interface (GSI) is a general purpose, serial data
 * interface for bi-directional communication between TPS Total Stations,
 * Levelling instruments and computers.
 * <p>
 * The GSI interface is composed in a sequence of blocks, ending with a
 * terminator (CR or CR/LF). The later introduced enhanced GSI16 format
 * starts every line with a <code>*</code> sign.
 *
 * @author sebastian
 * @version 7
 * @since 1
 */
public class FileToolsLeicaGSI {

    private boolean isGSI16 = false;
    private ArrayList<String> readStringLines = null;
    private TreeSet<Integer> foundCodes = new TreeSet<>();

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public FileToolsLeicaGSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Return the found codes as {@code TreeSet<Integer>}.
     * <p>
     * This method is necessary because of the elimination of the code in the string of the read lines.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Splits a code based file into separate files by code.
     * <p>
     * A separate file is generated for every existing code. Lines without code will be ignored.
     * RyCON need a valid GSI format file with code blocks (WI 71). The block order is equal.
     *
     * @param dropCode              if code block should dropped out of the result string
     * @param writeLinesWithoutCode if lines without code should be written
     *
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<ArrayList<String>> processCodeSplit(boolean dropCode, boolean writeLinesWithoutCode) {
        ArrayList<GSIHelper> linesWithCode = new ArrayList<>();
        ArrayList<GSIHelper> linesWithOutCode = new ArrayList<>();
        String newLine = null;

        // transform lines into GSI-Blocks
        BaseToolsGSI baseToolsGSI = new BaseToolsGSI(readStringLines);
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = baseToolsGSI.getEncodedLinesOfGSIBlocks();

        // one top level for every code
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (ArrayList<GSIBlock> blocksInLines : gsiBlocks) {

            // helper for code handling inside the switch statements
            int code = -1;
            int validCheckHelperValue = 0;

            for (GSIBlock block : blocksInLines) {
                switch (block.getWordIndex()) {
                    case 11:
                        newLine = block.toString();
                        break;

                    case 71:
                        code = Integer.parseInt(block.getDataGSI());
                        if (dropCode) {
                            newLine = newLine != null ? newLine.concat(" " + block.toString()) : null;
                        }
                        break;

                    case 81:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 1;
                        break;

                    case 82:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 3;
                        break;

                    case 83:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 6;
                        break;
                }
            }

            newLine = BaseToolsGSI.prepareLineEnding(newLine);

            // split lines with and without code
            if (((code != -1) & (newLine != null)) & validCheckHelperValue > 1) {
                foundCodes.add(code);
                linesWithCode.add(new GSIHelper(code, newLine));
            } else {
                // use 'blind' code '987789' for this
                linesWithOutCode.add(new GSIHelper(-987789, newLine));
            }
        }

        // sorting the ArrayList
        Collections.sort(linesWithCode, new Comparator<GSIHelper>() {
            @Override
            public int compare(GSIHelper o1, GSIHelper o2) {
                if (o1.getCode() > o2.getCode()) {
                    return 1;
                } else if (o1.getCode() == o2.getCode()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        // helpers for generating a new array for every found code
        if (linesWithCode.size() > 0) {

            int code = linesWithCode.get(0).getCode();
            ArrayList<String> temp = new ArrayList<>();

            // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
            for (GSIHelper gsiHelpers : linesWithCode) {
                if (code == gsiHelpers.getCode()) {
                    temp.add(gsiHelpers.getLine());
                } else {
                    result.add(temp);
                    temp = new ArrayList<>(); // do not use temp.clear()!!!
                    temp.add(gsiHelpers.getLine());
                }

                code = gsiHelpers.getCode();
            }
            // insert last element
            result.add(temp);
        }

        // insert lines without code for writing
        if (writeLinesWithoutCode && (linesWithOutCode.size() > 0)) {
            ArrayList<String> temp = new ArrayList<>();

            for (GSIHelper gsiHelper : linesWithOutCode) {
                temp.add(gsiHelper.getLine());
            }

            foundCodes.add(987789);
            result.add(temp);
        }

        return result;
    }

    /**
     * Convert a levelling file to a coordinate one (no, x, y, z) in GSI format for cad import.
     * <p>
     * Within this conversation a x, y coordinate will be generated from the line number. The units are
     * rounded down to 1/10mm.
     *
     * @param ignoreChangePoints if change points with number '0' has to be ignored
     *
     * @return Converted {@code ArrayList<String>} for cad import
     */
    public ArrayList<String> processLevelling2Cad(boolean ignoreChangePoints) {
        int lineCounter = 1;
        int lineNumber = -1;
        String newLine;

        ArrayList<String> result = new ArrayList<>();

        /*
        Strategy:
            - identify a station line (one token)
            - identify point line with height (four tokens)
            - identify change points and maybe ignore them (point number is '0')
            - grab the relevant information and prepare the write output
         */

        for (String line : readStringLines) {
            int size;

            if (line.startsWith("*")) {
                size = 24;
                line = line.substring(1, line.length());
            } else {
                size = 16;
            }

            // split read line into separate Strings
            List<String> lineSplit = new ArrayList<>((line.length() + size - 1) / size);
            for (int i = 0; i < line.length(); i += size) {
                lineSplit.add(line.substring(i, Math.min(line.length(), i + size)));
            }


            switch (lineSplit.size()) {
                // new levelling line has only one token
                case 1:
                    lineNumber = lineNumber + 1;
                    break;

                // line with height information from levelling has four tokens in GSI format
                case 4:
                    // number - the GSI16 format identifier has to be add to the first block
                    newLine = size == 24 ? "*" + lineSplit.get(0) : lineSplit.get(0);

                    // detect change points (number = 0) with regex
                    if (!(newLine.substring(8, newLine.length()).matches("[0]+") & ignoreChangePoints)) {
                        /*
                        x and y in 1/10 mm with the same value -> diagonal line later on...
                        for every new levelling line the y coordinate is raised with 10
                         */
                        int coordinate = lineCounter * 10000;
                        String valueX = Integer.toString(coordinate);
                        String valueY = Integer.toString(coordinate + 100000 * lineNumber);

                        GSIBlock x = new GSIBlock(isGSI16, 81, "..46", "+", valueX);
                        GSIBlock y = new GSIBlock(isGSI16, 82, "..46", "+", valueY);

                        newLine = newLine.concat(" " + x.toString());
                        newLine = newLine.concat(" " + y.toString());

                        // leveled height rounded to 1/10mm (RAPP AG hack)
                        String leveled = lineSplit.get(3);
                        String leveledRounded = leveled.substring(0, 4) + "26" + leveled.substring(6, 7) + "0" + leveled.substring(7, leveled.length() - 1);

                        newLine = newLine.concat(" " + leveledRounded);
                        newLine = BaseToolsGSI.prepareLineEnding(newLine);

                        result.add(newLine);
                        lineCounter = lineCounter + 1;
                    }
                    break;
            }
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

    /**
     * Defines an inner object for better handling and the ability to sort easily.
     * <p>
     * Later on, this could be done better.
     */
    private static class GSIHelper {

        private final int code;
        private final String line;

        /**
         * Simple definition with the code as int and a String for the complete {@code GSIBlocks}.
         *
         * @param code code of the {@code GSIBlocks}
         * @param line {@code String} of the {@code GSIBlocks}
         */
        GSIHelper(int code, String line) {
            this.code = code;
            this.line = line;
        }

        /**
         * Return the code as Integer value.
         *
         * @return code as Integer value
         */
        public int getCode() {
            return code;
        }

        /**
         * Return the line as String.
         *
         * @return line as String
         */
        public String getLine() {
            return line;
        }

    } // end of inner class GSIHelper

} // end of FileToolsLeicaGSI
