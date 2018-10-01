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

import de.ryanthara.ja.rycon.core.logfile.leica.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Instances of {@link LogfileClearUp} provides functions to clear up a logfile
 * in the <tt>Leica Geosystems</tt> format which contains a simple structure.
 * <p>
 * Due to some reasons the 'standard' logfile.txt file from <tt>Leica Geosystems</tt>
 * total stations and gnss receivers writes some unnecessary and useless lines into
 * the logfile.txt. These lines need a lot of space and contains no useful information.
 * <p>
 * Therefore <tt>RyCON</tt> has this simple tool to delete this lines and logfile contents.
 */
public final class LogfileClearUp {

    private static final Logger logger = LoggerFactory.getLogger(LogfileClearUp.class.getName());

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given reader line based <tt>Leica Geosystems</tt>
     * logfile.txt file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public LogfileClearUp(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Cleans the <tt>Leica Geosystems</tt> logfile.txt format with different kind of
     * approaches to identify regular structures like not needed lines or useless blocks
     * without any information for the user.
     * <p>
     * Empty blocks has no lines between the start and end line and deleted automatically.
     * <p>
     * Useless blocks are for example stake out logs without saved points, or free station
     * lines without a set station. There are much more ones.
     *
     * @param cleanBlocksByContent Ignore the 'intelligence' to identify useless blocks by content.
     *                             Only open and instantly closed blocks are removed.
     * @return clear up logfile as {@link ArrayList} with the lines as {@link String}
     */
    public ArrayList<String> processClean(boolean cleanBlocksByContent) {
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
        Identifier currentBlock = null;

        for (int i = 0; i < readStringLines.size(); i++) {
            currentLine = readStringLines.get(i);

            final String blockBegin = "Logfile - Begin";
            final String blockEnd = "Logfile - End";

            if (currentLine.contains(blockBegin)) {
                start = i;

                // Insert first two lines of the logfile and two empty lines to get a correct logfile start
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
                            case REFERENCE_PLANE:
                                temp = cleanReferencePlane(readStringLines.subList(start, end));
                                break;
                            case SETUP:
                                temp = cleanSetup(readStringLines.subList(start, end));
                                break;
                            case STAKEOUT:
                                temp = cleanStakeOut(readStringLines.subList(start, end));
                                break;
                            case VOLUME_CALCULATIONS:
                                temp = cleanVolumeCalculations(readStringLines.subList(start, end));
                                break;
                            default:
                                logger.trace("Found unknown logfile structure '{}'.", currentBlock.getIdentifier());
                        }

                        // Reset currentBlock due to begin-end delete
                        currentBlock = null;
                    }
                } else {
                    temp = new ArrayList<>(readStringLines.subList(start, end));
                }

                if (temp != null && temp.size() > 0) {
                    result.addAll(temp);
                }

                start = 0;
            }

            // Detect block type
            if (i == start + 5) {
                if (currentLine.contains(Identifier.COGO.getIdentifier())) {
                    currentBlock = Identifier.COGO;
                }

                if (currentLine.contains(Identifier.REFERENCE_LINE.getIdentifier())) {
                    currentBlock = Identifier.REFERENCE_LINE;
                }

                if (currentLine.contains(Identifier.SETUP.getIdentifier())) {
                    currentBlock = Identifier.SETUP;
                }

                if (currentLine.contains(Identifier.STAKEOUT.getIdentifier())) {
                    currentBlock = Identifier.STAKEOUT;
                }
            }

            // Insert last two lines of the logfile to get a correct logfile ending
            if (i == readStringLines.size() - 1 && currentLine.contains(blockEnd)) {
                result.add(readStringLines.get(i - 1));
                result.add(readStringLines.get(i));
            }
        }

        return new ArrayList<>(result);
    }

    /**
     * Returns a full cleaned logfile that contains neither header, footer nor empty lines.
     * <p>
     * This function is mainly used in the {@link de.ryanthara.ja.rycon.core.logfile.LogfileAnalyzer}
     * and uses the {@link #processClean(boolean) processClean} first.
     *
     * @return full cleaned logfile
     */
    public ArrayList<String> processCleanFull() {
        ArrayList<String> list = processClean(true);
        list.removeAll(Arrays.asList("", null));

        return new ArrayList<>(list.subList(3, list.size() - 2));
    }

    /*
     * Cleans the unnecessary lines from the logfile.txt file that was written
     * by the <tt>COGO</tt> program.
     */
    private ArrayList<String> cleanCogo(List<String> block) {
        ArrayList<String> result = null;

        if (block != null) {
            result = new ArrayList<>();

            final String[] identifiers = {"Computed", "Base Point", "Inverse", "Offset Point", "Traverse", "Stakeout Diff"};
            boolean noCalculatedPoints = true;

            for (int i = 0; i < block.size(); i++) {
                // skip first two and last 4 lines
                if (i > 1 && i < block.size() - 4) {
                    final String s = block.get(i);

                    // detect identifier -> block is not empty
                    if (Arrays.stream(identifiers).parallel().anyMatch(s::contains)) {
                        noCalculatedPoints = false;
                    }

                    result.add(s);
                }
            }

            if (noCalculatedPoints) {
                return null;
            }
        }

        assert result != null;
        return new ArrayList<>(result);
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

        return new ArrayList<>(Objects.requireNonNull(result));
    }

    /*
     * Cleans the unnecessary lines from the logfile.txt file that was written
     * by the <tt>REFERENCE PLANE</tt> program.
     *
     * The clean algorithm detects empty lines.
     */
    private ArrayList<String> cleanReferencePlane(List<String> strings) {
        ArrayList<String> result;

        System.out.println(strings.size());

        // TODO add reference plane clean
        result = (ArrayList<String>) strings;

        return new ArrayList<>(result);
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

        return new ArrayList<>(Objects.requireNonNull(result));
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

        return new ArrayList<>(Objects.requireNonNull(result));
    }

    /*
     * Cleans the unnecessary lines from the logfile.txt file that was written
     * by the <tt>VOLUME CALCULATIONS</tt> program.
     */
    private ArrayList<String> cleanVolumeCalculations(List<String> block) {
        ArrayList<String> result = null;

        // TODO add volume calculations clean

        return new ArrayList<>(Objects.requireNonNull(result));
    }

} // end of LogfileClearUp
