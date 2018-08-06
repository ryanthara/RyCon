/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.core.converter.gsi;

import de.ryanthara.ja.rycon.core.converter.toporail.FileType;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.util.SortHelper;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instances of this class provides functions to convert Toporail measurement
 * and coordinate files into Leica Geosystems GSI8 and GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class Toporail2Gsi {

    private final static Logger logger = Logger.getLogger(Toporail2Gsi.class.getName());

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given an {@code ArrayList<String>} that contains
     * the read Toporail coordinate or measurement file.
     *
     * @param readStringLines read lines from Toporail file
     */
    public Toporail2Gsi(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Toporail file (measurement or coordinate file) into a Leica Geosystems GSI formatted file.
     * <p>
     * The GSI format decision is done by a parameter in the constructor.
     *
     * @param fileType file type of the read Toporail file (MEP or PTS)
     * @param isGSI16  decision which GSI format is used
     *
     * @return converted {@code ArrayList<String>>} with lines
     */
    public ArrayList<String> convertToporail2Gsi(FileType fileType, boolean isGSI16) {
        if (fileType == FileType.MEP) {
            return convertMep2Gsi(isGSI16);
        } else {
            return convertPts2Gsi(isGSI16);
        }
    }

    private ArrayList<String> convertMep2Gsi(boolean isGSI16) {
        ArrayList<GsiBlock> blocks;
        ArrayList<ArrayList<GsiBlock>> blocksInLines = new ArrayList<>();

        // check for being a valid Toporail coordinate file
        if (readStringLines.get(0).startsWith("@MEP")) {

            int lineCounter = 1;

            // skip first line
            for (int i = 1; i < readStringLines.size(); i++) {
                blocks = new ArrayList<>();
                String[] tokens = readStringLines.get(i).split("\t");

                switch (tokens[0]) {
                    case "K": // control measurement line
                        blocks = transformControlMeasurementLine(tokens, isGSI16, lineCounter);
                        break;
                    case "M": // measurement line
                        blocks = transformMeasurementLine(tokens, isGSI16, lineCounter);
                        break;
                    case "P": // coordinate line
                        blocks = transformCoordinateLine(tokens, isGSI16, lineCounter);
                        break;
                    case "S": // station line
                        blocks = transformStationLine(tokens, isGSI16, lineCounter);
                        break;
                    default:
                        logger.log(Level.SEVERE, "Found unsuported token: " + tokens[0]);
                }

                if (blocks.size() > 0) {
                    lineCounter = lineCounter + 1;

                    // sort every 'line' of GSI blocks by word index (WI)
                    SortHelper.sortByWordIndex(blocks);

                    blocksInLines.add(blocks);
                }
            }
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

    private ArrayList<String> convertPts2Gsi(boolean isGSI16) {
        ArrayList<GsiBlock> blocks;
        ArrayList<ArrayList<GsiBlock>> blocksInLines = new ArrayList<>();

        // check for being a valid Toporail coordinate file
        if (readStringLines.get(0).startsWith("@PTS")) {

            int lineCounter = 1;

            // skip first line
            for (int i = 1; i < readStringLines.size(); i++) {
                blocks = new ArrayList<>();
                String[] tokens = readStringLines.get(i).split("\t");

                for (int j = 0; j < tokens.length; j++) {
                    switch (j) {
                        case 0: // numeric code
                            blocks.add(new GsiBlock(isGSI16, 41, tokens[0]));
                            break;
                        case 1: // point number
                            blocks.add(new GsiBlock(isGSI16, 11, lineCounter, tokens[1]));
                            break;
                        case 2: // easting
                            blocks.add(new GsiBlock(isGSI16, 81, tokens[2]));
                            break;
                        case 3: // northing
                            blocks.add(new GsiBlock(isGSI16, 82, tokens[3]));
                            break;
                        case 4: // height
                            blocks.add(new GsiBlock(isGSI16, 83, tokens[4]));
                            break;
                        case 5: // date YYYYMMDD
                            if (tokens[5].length() == 8) {
                                String date = tokens[5];

                                String year = date.substring(0, 4);
                                String month = date.substring(4, 6);
                                String day = date.substring(6, 8);

                                // blocks.add(new GsiBlock(isGSI16, 17, day + month + year));
                                blocks.add(new GsiBlock(isGSI16, 18, year.substring(2, 4) + "000000"));
                                blocks.add(new GsiBlock(isGSI16, 19, month + day + "0000"));
                            }
                            break;
                        case 6: // author
                            blocks.add(new GsiBlock(isGSI16, 71, tokens[6]));
                            break;
                        case 7: // comment
                            blocks.add(new GsiBlock(isGSI16, 72, tokens[7]));
                            break;
                        case 8: // overhauling (mm)
                            blocks.add(new GsiBlock(isGSI16, 73, tokens[8]));
                            break;
                        case 9: // azimuth (gon)
                            blocks.add(new GsiBlock(isGSI16, 21, tokens[9]));
                            break;
                        default:
                            logger.log(Level.SEVERE, "Found one more token: " + j);
                    }
                }

                // check for at least one or more added elements to prevent writing empty lines
                if (blocks.size() > 0) {
                    lineCounter = lineCounter + 1;

                    // sort every 'line' of GSI blocks by word index (WI)
                    SortHelper.sortByWordIndex(blocks);

                    blocksInLines.add(blocks);
                }
            }
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

    private ArrayList<GsiBlock> transformControlMeasurementLine(String[] tokens, boolean isGSI16, int lineCounter) {
        ArrayList<GsiBlock> blocks = new ArrayList<>();

        for (int j = 1; j < tokens.length; j++) {
            switch (j) {
                case 1: // numeric code
                    blocks.add(new GsiBlock(isGSI16, 41, tokens[1]));
                    break;
                case 2: // point number
                    blocks.add(new GsiBlock(isGSI16, 71, lineCounter, tokens[2]));
                    break;
                case 3: // numeric code
                    blocks.add(new GsiBlock(isGSI16, 42, tokens[3]));
                    break;
                case 4: // point number
                    blocks.add(new GsiBlock(isGSI16, 72, tokens[4]));
                    break;
                case 5: // slope distance or horizontal distance -> valued in 'height difference'
                    break;
                case 6: // height difference
                    if (tokens[6].equalsIgnoreCase("")) {
                        // slope distance
                        blocks.add(new GsiBlock(isGSI16, 31, tokens[5]));
                    } else {
                        // horizontal distance
                        blocks.add(new GsiBlock(isGSI16, 32, tokens[5]));
                    }

                    // height difference
                    blocks.add(new GsiBlock(isGSI16, 33, tokens[6]));
                    break;
                case 7: // comment
                    blocks.add(new GsiBlock(isGSI16, 72, tokens[7]));
                    break;
                default:
                    logger.log(Level.SEVERE, "Found one more token: " + j);
            }
        }

        // check for at least one or more added elements to prevent writing empty lines
        if (blocks.size() > 0) {
            // sort every 'line' of GSI blocks by word index (WI)
            SortHelper.sortByWordIndex(blocks);
        }

        return blocks;
    }

    private ArrayList<GsiBlock> transformCoordinateLine(String[] tokens, boolean isGSI16, int lineCounter) {
        ArrayList<GsiBlock> blocks = new ArrayList<>();

        for (int j = 1; j < tokens.length; j++) {
            switch (j) {
                case 1: // numeric code
                    blocks.add(new GsiBlock(isGSI16, 41, tokens[1]));
                    break;
                case 2: // point number
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, tokens[2]));
                    break;
                case 3: // usage
                    blocks.add(new GsiBlock(isGSI16, 71, tokens[3]));
                    break;
                case 4: // easting
                    blocks.add(new GsiBlock(isGSI16, 81, tokens[4]));
                    break;
                case 5: // northing
                    blocks.add(new GsiBlock(isGSI16, 82, tokens[5]));
                    break;
                case 6: // height
                    blocks.add(new GsiBlock(isGSI16, 83, tokens[6]));
                    break;
                case 7: // comment
                    blocks.add(new GsiBlock(isGSI16, 72, tokens[7]));
                    break;
                default:
                    logger.log(Level.SEVERE, "Found one more token: " + j);
            }
        }

        // check for at least one or more added elements to prevent writing empty lines
        if (blocks.size() > 0) {
            // sort every 'line' of GSI blocks by word index (WI)
            SortHelper.sortByWordIndex(blocks);
        }

        return blocks;
    }

    private ArrayList<GsiBlock> transformMeasurementLine(String[] tokens, boolean isGSI16, int lineCounter) {
        ArrayList<GsiBlock> blocks = new ArrayList<>();

        for (int j = 1; j < tokens.length; j++) {
            switch (j) {
                case 1: // numeric code
                    blocks.add(new GsiBlock(isGSI16, 41, tokens[1]));
                    break;
                case 2: // point number
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, tokens[2]));
                    break;
                case 3: // slope distance (m)
                    blocks.add(new GsiBlock(isGSI16, 31, tokens[3]));
                    break;
                case 4: // horizontal angle (gon)
                    blocks.add(new GsiBlock(isGSI16, 21, tokens[4]));
                    break;
                case 5: // vertical angle (gon)
                    blocks.add(new GsiBlock(isGSI16, 22, tokens[5]));
                    break;
                case 6: // reflector height (m)
                    blocks.add(new GsiBlock(isGSI16, 87, tokens[6]));
                    break;
                case 7: // longitudinal shift
                    blocks.add(new GsiBlock(isGSI16, 71, tokens[7]));
                    break;
                case 8: // lateral shift
                    blocks.add(new GsiBlock(isGSI16, 72, tokens[8]));
                    break;
                case 9: // comment
                    blocks.add(new GsiBlock(isGSI16, 72, tokens[9]));
                    break;
                case 10: // overhauling (mm)
                    blocks.add(new GsiBlock(isGSI16, 73, tokens[10]));
                    break;
                case 11: // azimuth (gon)
                    blocks.add(new GsiBlock(isGSI16, 74, tokens[11]));
                    break;
                default:
                    logger.log(Level.SEVERE, "Found one more token: " + j);
            }
        }

        // check for at least one or more added elements to prevent writing empty lines
        if (blocks.size() > 0) {
            // sort every 'line' of GSI blocks by word index (WI)
            SortHelper.sortByWordIndex(blocks);
        }

        return blocks;
    }

    private ArrayList<GsiBlock> transformStationLine(String[] tokens, boolean isGSI16, int lineCounter) {
        ArrayList<GsiBlock> blocks = new ArrayList<>();

        for (int j = 1; j < tokens.length; j++) {
            switch (j) {
                case 1: // numeric code
                    blocks.add(new GsiBlock(isGSI16, 41, tokens[1]));
                    break;
                case 2: // point number
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, tokens[2]));
                    break;
                case 3: // station type
                    blocks.add(new GsiBlock(isGSI16, 71, tokens[3]));
                    break;
                case 4: // instrument height (m)
                    // TODO: 19.11.17 Check for unit (metre)
                    blocks.add(new GsiBlock(isGSI16, 88, tokens[4]));
                    break;
                case 5: // temperature in °C
                    blocks.add(new GsiBlock(isGSI16, 72, tokens[5]));
                    break;
                case 6: // air pressure in Mbar
                    blocks.add(new GsiBlock(isGSI16, 73, tokens[6]));
                    break;
                case 7: // comment
                    blocks.add(new GsiBlock(isGSI16, 74, tokens[7]));
                    break;
                default:
                    logger.log(Level.SEVERE, "Found one more token: " + j);
            }
        }

        // check for at least one or more added elements to prevent writing empty lines
        if (blocks.size() > 0) {
            // sort every 'line' of GSI blocks by word index (WI)
            SortHelper.sortByWordIndex(blocks);
        }

        return blocks;
    }

} // end of Toporail2Gsi
