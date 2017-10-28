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

import de.ryanthara.ja.rycon.core.converter.gsi.BaseToolsGsi;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class provides functions to convert Leica GSI formatted files into
 * a text formatted measurement or coordinate file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi2Txt {

    private BaseToolsGsi baseToolsGsi;

    /**
     * Class constructor for read line based GSI files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Gsi2Txt(ArrayList<String> readStringLines) {
        baseToolsGsi = new BaseToolsGsi(readStringLines);
    }

    /**
     * Converts a GSI file into a space or tab delimited text file.
     * <p>
     * With parameter it is possible to set the separation char (space or tab).
     *
     * @param separator        separator sign as {@code String}
     * @param isGSI16          true if GSI16 format is used
     * @param writeCommentLine if comment line should be written
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertGSI2TXT(String separator, boolean isGSI16, boolean writeCommentLine) {
        String commentLine = "";
        ArrayList<String> result = new ArrayList<>();

        String sep = separator.equals(" ") ? "    " : separator;

        TreeSet<Integer> foundWordIndices = baseToolsGsi.getFoundAllWordIndices();

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

        for (ArrayList<GsiBlock> blocksInLine : baseToolsGsi.getEncodedLinesOfGSIBlocks()) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndex = it.next();

                String intern = "";

                for (GsiBlock block : blocksInLine) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndex == block.getWordIndex()) {
                        intern = block.toPrintFormatTXT();
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
        return result;
    }

} // end of Gsi2Txt
