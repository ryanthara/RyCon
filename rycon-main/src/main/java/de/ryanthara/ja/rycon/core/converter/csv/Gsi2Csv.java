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
package de.ryanthara.ja.rycon.core.converter.csv;

import de.ryanthara.ja.rycon.core.converter.gsi.GsiDecoder;
import de.ryanthara.ja.rycon.core.elements.GSIBlock;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.WordIndices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WORDINDEX;

/**
 * A converter with functions to convert Leica Geosystems GSI format (GSI8 and GSI16)
 * coordinate and measurement files into comma separated values (CSV) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi2Csv {

    private final GsiDecoder gsiDecoder;

    /**
     * Creates a converter with a list for the read line based
     * Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public Gsi2Csv(List<String> lines) {
        gsiDecoder = new GsiDecoder(lines);
    }

    /**
     * Converts a GSI file into a comma or semicolon delimited CSV file.
     * <p>
     * With parameter it is possible to set the separation char (comma or semicolon).
     *
     * @param separator        separator sign as {@code String}
     * @param writeCommentLine if comment line should be written
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(String separator, boolean writeCommentLine) {
        List<String> result = new ArrayList<>();
        Set<Integer> foundWordIndices = gsiDecoder.getFoundWordIndices();

        // prepare comment line if necessary
        if (writeCommentLine) {
            StringBuilder builder = new StringBuilder();

            int counter = 0;

            for (Integer wordIndex : foundWordIndices) {
                builder.append(ResourceBundleUtils.getLangString(WORDINDEX, WordIndices.valueOf("WI" + wordIndex)));

                if (counter < foundWordIndices.size() - 1) {
                    builder.append(separator);
                }

                counter = counter + 1;
            }

            result.add(0, builder.toString());
        }

        for (List<GSIBlock> blocksInLine : gsiDecoder.getDecodedLinesOfGsiBlocks()) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndex = it.next();
                String intern = "";

                for (GSIBlock block : blocksInLine) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndex == block.getWordIndex()) {
                        intern = block.toPrintFormatCsv();
                        break; // important if else statement will be added!!!
                    }
                }

                newLine = newLine.concat(intern);

                if (i < foundWordIndices.size() - 1) {
                    newLine = newLine.concat(separator);
                }
            }
            result.add(newLine);
        }

        return List.copyOf(result);
    }

}
