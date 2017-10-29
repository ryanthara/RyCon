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
package de.ryanthara.ja.rycon.core;

import de.ryanthara.ja.rycon.core.converter.gsi.BaseToolsGsi;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.core.elements.RyBlock;
import de.ryanthara.ja.rycon.util.SortHelper;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Instances of this class provides functions to split a Leica GSI file by code into separate files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class GsiCodeSplit {

    private ArrayList<String> readStringLines;
    private TreeSet<Integer> foundCodes;

    /**
     * Constructs a new instance of this class given a reader line based text file in Leica GSI format.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GsiCodeSplit(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
        foundCodes = new TreeSet<>();
    }

    /**
     * Returns the found codes as {@code TreeSet<Integer>}.
     * <p>
     * This method is necessary because of the elimination of the code in the string of the reader lines.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Splits a code based Leica GSI file into separate files by code.
     * <p>
     * RyCON needs a valid GSI format file with code blocks (WI 71). The block order is equal.
     * A separate file is generated for every existing code. Lines without code will get the pseudo code '987789'.
     *
     * @param insertCodeBlock       if code block is insert into the result string
     * @param writeLinesWithoutCode if lines without code should be written to a separate file
     *
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<ArrayList<String>> processCodeSplit(boolean insertCodeBlock, boolean writeLinesWithoutCode) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<RyBlock> linesWithCode = new ArrayList<>();
        ArrayList<RyBlock> linesWithOutCode = new ArrayList<>();

        // transform lines into GSI-Blocks
        BaseToolsGsi baseToolsGsi = new BaseToolsGsi(readStringLines);
        ArrayList<ArrayList<GsiBlock>> gsiBlocks = baseToolsGsi.getEncodedLinesOfGSIBlocks();

        for (ArrayList<GsiBlock> blocksInLines : gsiBlocks) {
            // helper for code handling inside the switch statements
            int code = -1;
            int validCheckHelperValue = 0;
            String newLine = null;

            for (GsiBlock block : blocksInLines) {
                switch (block.getWordIndex()) {
                    case 11:
                        newLine = block.toString();
                        break;

                    case 71:
                        code = Integer.parseInt(block.getDataGSI());
                        if (insertCodeBlock) {
                            newLine = newLine != null ? newLine.concat(" " + block.toString()) : null;
                        }
                        break;

                    case 81:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 1;
                        break;

                    case 82:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 3;
                        break;

                    case 83:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 6;
                        break;

                    default:
                        System.err.println("GsiCodeSplit.processCodeSplit() : found wrong word index " + block.toPrintFormatCSV());
                }
            }

            newLine = BaseToolsGsi.prepareLineEnding(newLine);

            // split lines with and without code
            if (((code != -1) & (newLine != null)) & validCheckHelperValue > 1) {
                foundCodes.add(code);
                linesWithCode.add(new RyBlock(code, newLine));
            } else {
                // use 'blind' code '987789' for this
                linesWithOutCode.add(new RyBlock(-987789, newLine));
            }
        }

        SortHelper.sortByCode(linesWithCode);

        // helpers for generating a new array for every found code
        if (linesWithCode.size() > 0) {
            int code = linesWithCode.get(0).getNumber();
            ArrayList<String> lineStorage = new ArrayList<>();

            // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
            for (RyBlock ryBlock : linesWithCode) {
                if (code == ryBlock.getNumber()) {
                    lineStorage.add(ryBlock.getString());
                } else {
                    result.add(lineStorage);
                    lineStorage = new ArrayList<>(); // do not use temp.clear()!!!
                    lineStorage.add(ryBlock.getString());
                }

                code = ryBlock.getNumber();
            }

            // insert last element
            result.add(lineStorage);
        }

        // insert lines without code for writing
        if (writeLinesWithoutCode && (linesWithOutCode.size() > 0)) {
            ArrayList<String> temp = new ArrayList<>();

            for (RyBlock ryBlock : linesWithOutCode) {
                temp.add(ryBlock.getString());
            }

            foundCodes.add(987789);
            result.add(temp);
        }

        return result;
    }

} // end of GsiCodeSplit
