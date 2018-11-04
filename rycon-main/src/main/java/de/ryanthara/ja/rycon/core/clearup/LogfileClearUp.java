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
package de.ryanthara.ja.rycon.core.clearup;

import de.ryanthara.ja.rycon.core.clearup.processor.*;
import de.ryanthara.ja.rycon.core.logfile.leica.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Clears up a Leica Geosystems logfile (logfile.txt).
 *
 * <p>
 * Due to some reasons the 'standard' logfile.txt file from Leica Geosystems total
 * stations and gnss receivers writes some unnecessary and useless lines into the output file.
 * These lines need a lot of vertical space and contains no relevant information.
 *
 * <p>
 * Therefore RyCON has this simple tool to delete this lines and clear up the logfile content.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public final class LogfileClearUp {

    private static final Logger logger = LoggerFactory.getLogger(LogfileClearUp.class.getName());

    private final List<String> lines;

    /**
     * Creates a new logfile clear up with a list for the read
     * line based Leica Geosystems logfile.txt file.
     *
     * @param lines read logfile.txt lines
     */
    public LogfileClearUp(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Cleans up the Leica Geosystems logfile.txt format with different kind of
     * approaches to identify regular structures like not needed lines or useless blocks
     * without any information for the user.
     *
     * <p>
     * Empty blocks has no lines between the start and end line sequence and are deleted automatically.
     *
     * <p>
     * Useless blocks are for example stake out logs without saved points, or free station
     * lines without a set station. There are much more ones.
     *
     * @param cleanBlocksByContent Ignore the 'intelligence' to identify useless blocks by content.
     *                             Only open and instantly closed blocks are removed.
     * @return clear up logfile as {@link List} with the lines as {@link String}
     */
    public List<String> process(boolean cleanBlocksByContent) {
        List<String> result = new ArrayList<>();

        final String blockBegin = "Logfile - Begin";
        final String blockEnd = "Logfile - End";

        /*
         * The main idea to clean up the logfile.txt based on a two step run.
         *
         * 1. Find the blocks by the 'BEGIN' and 'END' sequence and delete the empty ones
         * 2. Analyze block by block and delete either the full useless block or
         *    the not needed entries (cleanBlocksByContent option)
         *
         * Therefore the complete block is given to sub routines and returned from there.
         */

        int start = -1;
        Identifier currentBlock = null;

        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);

            if (currentLine.contains(blockBegin)) {
                start = i;

                // Insert first two lines of the logfile and two empty lines to get a correct logfile start
                if (i == 0 && currentLine.contains(blockBegin)) {
                    result.add(lines.get(i));
                    result.add(lines.get(i + 1));
                }
            } else if (currentLine.contains(blockEnd)) {
                int end = i + 1;

                List<String> temp = null;

                if (cleanBlocksByContent) {
                    if (currentBlock != null) {
                        switch (currentBlock) {
                            case COGO:
                                temp = CogoProcessor.run(lines.subList(start, end));
                                break;
                            case REFERENCE_LINE:
                                temp = ReferenceLineProcessor.run(lines.subList(start, end));
                                break;
                            case REFERENCE_PLANE:
                                temp = ReferencePlaneProcessor.run(lines.subList(start, end));
                                break;
                            case SETUP:
                                temp = SetupProcessor.run(lines.subList(start, end));
                                break;
                            case STAKEOUT:
                                temp = StakeOutProcessor.run(lines.subList(start, end));
                                break;
                            case VOLUME_CALCULATIONS:
                                temp = VolumeProcessor.run(lines.subList(start, end));
                                break;
                            default:
                                logger.trace("Found unknown logfile structure '{}'.", currentBlock.getIdentifier());
                        }

                        // Reset currentBlock due to begin-end delete
                        currentBlock = null;
                    }
                } else {
                    temp = new ArrayList<>(lines.subList(start, end));
                }

                if (temp != null && temp.size() > 0) {
                    result.addAll(temp);
                }

                start = 0;
            }

            // Detect block type
            if (i == start + 5) {
                currentBlock = getIdentifier(currentLine, currentBlock);
            }

            // Insert last two lines of the logfile to get a correct logfile ending
            if (i == lines.size() - 1 && currentLine.contains(blockEnd)) {
                result.add(lines.get(i - 1));
                result.add(lines.get(i));
            }
        }

        return List.copyOf(result);
    }

    private Identifier getIdentifier(String currentLine, Identifier currentBlock) {
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

        return currentBlock;
    }

    /**
     * Returns a full cleared up logfile that contains neither header, footer nor empty lines.
     *
     * <p>
     * This function is mainly used in the {@link de.ryanthara.ja.rycon.core.logfile.LogfileAnalyzer}
     * and uses the {@link #process(boolean) run} first.
     *
     * @return full cleared up logfile
     */
    public List<String> processFullClearUp() {
        List<String> list = process(true);

        List<String> filtered = list.stream()
                .filter(Objects::nonNull)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());

        return List.copyOf(filtered.subList(3, filtered.size() - 2));
    }

}
