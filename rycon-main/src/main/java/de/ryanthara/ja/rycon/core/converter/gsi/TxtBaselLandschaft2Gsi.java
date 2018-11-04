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

import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from the geodata server
 * Basel Landschaft (Switzerland) into Leica Geosystems GSI8 and GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Gsi {

    private static final Logger logger = LoggerFactory.getLogger(TxtBaselLandschaft2Gsi.class.getName());

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text files
     * from the geodata server Basel Landschaft (Switzerland).
     *
     * @param lines list with coordinate lines
     */
    public TxtBaselLandschaft2Gsi(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a text file from the geodata server Basel Landschaft (Switzerland) into a GSI formatted file.
     * <p>
     * This method can differ between LFP and HFP files, which has a different structure.
     * With a parameter it is possible to distinguish between GSI8 and GSI16.
     *
     * @param isGSI16             distinguish between GSI8 or GSI16 output
     * @param useAnnotationColumn writer additional information as annotation column (WI 71)
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(boolean isGSI16, boolean useAnnotationColumn) {
        List<GsiBlock> blocks;
        List<List<GsiBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        // remove comment line
        lines.remove(0);

        for (String line : lines) {
            blocks = new ArrayList<>();

            String[] values = line.trim().split("\\t", -1);

            switch (values.length) {
                case 5:     // HFP file
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, values[1]));

                    if (useAnnotationColumn) {
                        blocks.add(new GsiBlock(isGSI16, 71, values[0]));
                    }

                    blocks.add(new GsiBlock(isGSI16, 81, values[2]));
                    blocks.add(new GsiBlock(isGSI16, 82, values[3]));
                    blocks.add(new GsiBlock(isGSI16, 83, values[4]));
                    break;

                case 6:     // LFP file
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, values[1]));

                    if (useAnnotationColumn) {
                        if (values[2].equals("NULL")) {
                            blocks.add(new GsiBlock(isGSI16, 41, "-1"));
                        } else {
                            blocks.add(new GsiBlock(isGSI16, 41, values[2]));
                        }
                        blocks.add(new GsiBlock(isGSI16, 71, values[0]));
                    }

                    blocks.add(new GsiBlock(isGSI16, 81, values[3]));
                    blocks.add(new GsiBlock(isGSI16, 82, values[4]));

                    // prevent 'NULL' element in height
                    if (!values[5].equals("NULL")) {
                        blocks.add(new GsiBlock(isGSI16, 83, values[5]));
                    }

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

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

}
