/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core
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

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of {@link LogfileClean} provides functions to clean a logfile in the
 * <tt>Leica Geosystems</tt> format which contains a simple structure.
 * <p>
 * Due to some reasons the 'standard' logfile.txt file from <tt>Leica Geosystems</tt>
 * total stations and gnss receivers writes some unnecessary and useless lines into
 * the logfile.txt. These lines need a lot of space and contains no real information.
 * <p>
 * Therefore <tt>RyCON</tt> has this simple tool to delete this lines and logfile contents.
 */
public final class LogfileClean {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given reader line based <tt>Leica Geosystems</tt>
     * logfile.txt file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public LogfileClean(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Tidy up the <tt>Leica Geosystems</tt> logfile.txt format with different kind of
     * approaches to identify regular structures like not needed lines or useless blocks
     * without any information for the user.
     * <p>
     * Empty blocks has no lines between the start and end line and deleted automatically.
     * <p>
     * Useless blocks are for example stake out logs without saved points, or free station
     * lines without set station.
     *
     * @param cleanBlocksByContent Ignore the 'intelligence' to identify useless blocks by content.
     *                             Only open and instantly closed blocks are removed.
     *
     * @return cleaned logfile as {@link ArrayList} with the lines as {@link String}
     */
    public ArrayList<String> processTidyUp(boolean cleanBlocksByContent) {
        /*
         * The main idea to clean up the logfile.txt based on a two step process.
         *
         * 1. Find the blocks by the 'BEGIN' and 'END' sequence and delete the empty ones
         * 2. Analyze block by block and delete either the full useless block or
         *    the not needed entries (cleanBlocksByContent option)
         *
         * Therefore the complete block is given to sub routines and returned from them.
         */

        int start = -1;
        String currentLine;
        ArrayList<String> result = new ArrayList<>();
        LogfileBlocks currentBlock = null;

        for (int i = 0; i < readStringLines.size(); i++) {
            currentLine = readStringLines.get(i);

            final String blockBegin = "System 1200 Logfile - Begin";
            final String blockEnd = "System 1200 Logfile - End";

            if (currentLine.contains(blockBegin)) {
                start = i;

                // insert first two lines of the logfile and two empty lines to get a correct logfile start
                if (i == 0 && currentLine.contains(blockBegin)) {
                    result.add(readStringLines.get(i));
                    result.add(readStringLines.get(i + 1));
                }
            } else if (currentLine.contains(blockEnd)) {
                int end = i + 1;

                ArrayList<String> temp = null;

                if (cleanBlocksByContent) {
                    if (currentBlock != null) {
                        switch (currentBlock) {
                            case COGO:
                                temp = cleanCogo(readStringLines.subList(start, end));
                                break;
                            case REFERENCE_LINE:
                                temp = cleanReferenceLine(readStringLines.subList(start, end));
                                break;
                            case SETUP:
                                temp = cleanSetup(readStringLines.subList(start, end));
                                break;
                            case STAKEOUT:
                                temp = cleanStakeOut(readStringLines.subList(start, end));
                                break;
                        }

                        // reset currentBlock due to begin-end delete
                        currentBlock = null;
                    }
                } else {
                    temp = new ArrayList<>();
                    temp.addAll(readStringLines.subList(start, end));
                }

                if (temp != null && temp.size() > 0) {
                    result.addAll(temp);
                }

                start = 0;
            }

            // detect block type
            if (i == start + 5) {
                if (currentLine.contains(LogfileBlocks.COGO.getIdentifier())) {
                    currentBlock = LogfileBlocks.COGO;
                }

                if (currentLine.contains(LogfileBlocks.REFERENCE_LINE.getIdentifier())) {
                    currentBlock = LogfileBlocks.REFERENCE_LINE;
                }

                if (currentLine.contains(LogfileBlocks.SETUP.getIdentifier())) {
                    currentBlock = LogfileBlocks.SETUP;
                }

                if (currentLine.contains(LogfileBlocks.STAKEOUT.getIdentifier())) {
                    currentBlock = LogfileBlocks.STAKEOUT;
                }
            }

            // insert last two lines of the logfile to get a correct logfile ending
            if (i == readStringLines.size() - 1 && currentLine.contains(blockEnd)) {
                result.add(readStringLines.get(i - 1));
                result.add(readStringLines.get(i));
            }
        }

        return result;
    }

    /*
     * Cleans the unnecessary lines from the logfile.txt file that was written
     * by the <tt>COGO</tt> program.
     */
    private ArrayList<String> cleanCogo(List<String> block) {
        ArrayList<String> result = null;

        if (block != null) {
            result = new ArrayList<>();

            final String identifier = "Stakeout Diff";
            boolean noCalculatedPoints = true;

            for (int i = 0; i < block.size(); i++) {
                // skip first two and last 5 lines
                if (i > 1 && i < block.size() - 5) {
                    final String s = block.get(i);

                    // detect identifier -> block is not empty
                    if (s.contains(identifier)) {
                        noCalculatedPoints = false;
                    }

                    result.add(s);
                }
            }

            if (noCalculatedPoints) {
                return null;
            }
        }

        return result;
    }

    /*
     * Cleans the unnecessary lines from the logfile.txt file that was written
     * by the <tt>Reference Line</tt> program.
     */
    private ArrayList<String> cleanReferenceLine(List<String> block) {
        ArrayList<String> result = null;

        if (block != null) {
            result = new ArrayList<>();

            boolean noStakedPoints = true;
            final String[] identifiers = {"Measured Point", "Stakeout Point"};

            for (int i = 0; i < block.size(); i++) {
                // skip first two and last 5 lines
                if (i > 1 && i < block.size() - 5) {
                    final String s = block.get(i);

                    // detect identifier -> block is not empty
                    if (s.contains(identifiers[0]) || s.contains(identifiers[1])) {
                        noStakedPoints = false;
                    }

                    result.add(s);
                }
            }

            if (noStakedPoints) {
                return null;
            }
        }

        return result;
    }

    /*
     * Cleans the unnecessary lines from the logfile.txt file that was written
     * by the <tt>Setup</tt> program.
     *
     * The clean algorithm detects stations that are not set. Although it cleans not needed lines.
     */
    private ArrayList<String> cleanSetup(List<String> block) {
        ArrayList<String> result = null;

        if (block != null) {
            result = new ArrayList<>();

            boolean noStationSet = true;
            final String identifier = "Results";

            for (int i = 0; i < block.size(); i++) {
                // skip first two and last 5 lines
                if (i > 1 && i < block.size() - 5) {
                    final String s = block.get(i);

                    // detect identifier -> block is not empty
                    if (s.contains(identifier)) {
                        noStationSet = false;
                    }

                    result.add(s);
                }
            }

            if (noStationSet) {
                return null;
            }
        }

        return result;
    }

    /*
     * Cleans the unnecessary lines from the logfile.txt file that was written
     * by the <tt>Stake Out</tt> program.
     */
    private ArrayList<String> cleanStakeOut(List<String> block) {
        ArrayList<String> result = null;

        if (block != null) {
            result = new ArrayList<>();

            boolean noStakedPoints = true;
            final String identifier = "Point ID";

            for (int i = 0; i < block.size(); i++) {
                // skip first two and last 5 lines
                if (i > 1 && i < block.size() - 5) {
                    final String s = block.get(i);

                    // detect identifier -> block is not empty
                    if (s.contains(identifier)) {
                        noStakedPoints = false;
                    }

                    result.add(s);
                }
            }

            if (noStakedPoints) {
                return null;
            }
        }

        return result;
    }

} // end of LogfileClean
