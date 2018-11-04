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
package de.ryanthara.ja.rycon.core.converter.text;

import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.core.converter.gsi.GsiDecoder;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A converter with functions to convert Leica Geosystems GSI format (GSI8 and GSI16)
 * coordinate and measurement files into a text formatted measurement or coordinate file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi2Txt {

    private final GsiDecoder gsiDecoder;

    /**
     * Creates a converter with a list for the read line based
     * Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public Gsi2Txt(List<String> lines) {
        gsiDecoder = new GsiDecoder(lines);
    }

    /**
     * Converts a Leica Geosystems GSI8 or GSI16 file into a space or tab delimited text file.
     *
     * @param separator        separator sign as {@code String}
     * @param isGSI16          true if GSI16 format is used
     * @param writeCommentLine if comment line should be written
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(String separator, boolean isGSI16, boolean writeCommentLine) {
        String commentLine = "";
        List<String> result = new ArrayList<>();

        String sep = separator.equals(Separator.WHITESPACE.getSign()) ? "    " : separator;

        Set<Integer> foundWordIndices = gsiDecoder.getFoundWordIndices();

        if (writeCommentLine) {
            int length;

            length = isGSI16 ? 16 : 8;

            String format = "%" + length + "." + length + "s";
            String s;

            int counter = 0;

            for (Integer wordIndex : foundWordIndices) {
                s = String.format(format, wordIndex.toString());
                commentLine = commentLine.concat(s);

                if (counter < foundWordIndices.size() - 1) {
                    commentLine = commentLine.concat(sep);
                }
                counter = counter + 1;
            }

            StringBuilder builder = new StringBuilder(commentLine);
            commentLine = builder.replace(0, 5, "# WI:").toString();

            result.add(0, commentLine);
        }

        for (List<GsiBlock> blocksInLine : gsiDecoder.getDecodedLinesOfGsiBlocks()) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndex = it.next();

                String intern = "";

                for (GsiBlock block : blocksInLine) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndex == block.getWordIndex()) {
                        intern = block.toPrintFormatTxt();
                        break; // important!!!
                    } else {
                        String emptyBlock;

                        if (isGSI16) {
                            emptyBlock = "                ";
                        } else {
                            emptyBlock = "        ";
                        }

                        intern = emptyBlock;
                    }
                }

                newLine = newLine.concat(intern);

                if (i < foundWordIndices.size() - 1) {
                    newLine = newLine.concat(sep);
                }
            }
            result.add(newLine);
        }

        return List.copyOf(result);
    }

}
