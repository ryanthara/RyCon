/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.converter.csv;

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class provides functions to convert measurement files from Leica GSI format (GSI8 and GSI16)
 * into comma separated values (csv format).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSI2CSV {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public GSI2CSV(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a GSI file into a comma or semicolon delimited CSV file.
     * <p>
     * With parameter it is possible to set the separation char (comma or semicolon).
     *
     * @param separator        separator sign as {@code String}
     * @param writeCommentLine if comment line should be written
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertGSI2CSV(String separator, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();
        BaseToolsGSI baseToolsGSI = new BaseToolsGSI();
        TreeSet<Integer> foundWordIndices = baseToolsGSI.getFoundWordIndices();

        // prepare comment line if necessary
        if (writeCommentLine) {
            StringBuilder builder = new StringBuilder();

            int counter = 0;

            for (Integer wordIndex : foundWordIndices) {
                builder.append("WI_");
                builder.append(wordIndex.toString());

                if (counter < foundWordIndices.size() - 1) {
                    builder.append(separator);
                }

                counter++;
            }

            result.add(0, builder.toString());
        }

        for (ArrayList<GSIBlock> blocksAsLines : baseToolsGSI.getEncodedLinesOfGSIBlocks()) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndex = it.next();
                String intern = "";

                for (GSIBlock block : blocksAsLines) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndex == block.getWordIndex()) {
                        intern = block.toPrintFormatCSV();
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

        return result;
    }

} // end of GSI2CSV
