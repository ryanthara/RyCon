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
package de.ryanthara.ja.rycon.core.converter.gsi;

import de.ryanthara.ja.rycon.core.elements.GSIBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert ASCII text coordinate
 * files into Leica Geosystems GSI8 and GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Txt2Gsi {

    private static final Logger logger = LoggerFactory.getLogger(Txt2Gsi.class.getName());

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based ASCII text file.
     *
     * @param lines list with ASCII text lines
     */
    public Txt2Gsi(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a text file (space or tabulator separated) into a GSI formatted file.
     * <p>
     * RyCON uses space or tab as separation sign and not a fixed column position.
     * <p>
     * The GSI format decision is done by a parameter in the constructor.
     *
     * @param isGSI16                  decision which GSI format is used
     * @param sourceContainsCodeColumn if source file contains a code column
     * @return converted {@code ArrayList<String>>} with lines
     */
    public List<String> convert(boolean isGSI16, boolean sourceContainsCodeColumn) {
        List<List<GSIBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        for (String line : lines) {
            List<GSIBlock> blocks = new ArrayList<>();

            String[] values = line.trim().split("\\s+");
            switch (values.length) {
                case 1:     // prevent fall through
                    break;

                case 2:     // no, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, values[0]));
                    blocks.add(new GSIBlock(isGSI16, 83, values[1]));
                    break;

                case 3:     // no, code, height or no, easting, northing
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, values[0]));
                    if (sourceContainsCodeColumn) {
                        blocks.add(new GSIBlock(isGSI16, 71, values[1]));
                        blocks.add(new GSIBlock(isGSI16, 83, values[2]));
                    } else {
                        blocks.add(new GSIBlock(isGSI16, 81, values[1]));
                        blocks.add(new GSIBlock(isGSI16, 82, values[2]));
                    }
                    break;

                case 4:     // no, easting, northing, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, values[0]));
                    blocks.add(new GSIBlock(isGSI16, 81, values[1]));
                    blocks.add(new GSIBlock(isGSI16, 82, values[2]));

                    // necessary because of Basel Stadt CSV distinguish between points without height
                    if (!values[3].equals("-9999")) {
                        blocks.add(new GSIBlock(isGSI16, 83, values[3]));
                    }
                    break;

                case 5:     // no, code, easting, northing, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, values[0]));
                    blocks.add(new GSIBlock(isGSI16, 71, values[1]));
                    blocks.add(new GSIBlock(isGSI16, 81, values[2]));
                    blocks.add(new GSIBlock(isGSI16, 82, values[3]));
                    blocks.add(new GSIBlock(isGSI16, 83, values[4]));
                    break;

                default:
                    logger.trace("Line contains less or more tokens ({}) than needed or allowed.", values.length);
                    break;
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter = lineCounter + 1;
                blocksInLines.add(blocks);
            }
        }

        // return sortOutputFile ? BaseToolsLtop.sortResult(result) : result;

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

}
