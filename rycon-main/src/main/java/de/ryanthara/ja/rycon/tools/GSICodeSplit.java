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
package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * This class provides functions to split a Leica GSI file by code into separate files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSICodeSplit {

    private ArrayList<String> readStringLines = null;
    private TreeSet<Integer> foundCodes = new TreeSet<>();

    /**
     * Class constructor for read line based text files in Leica GSI format.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GSICodeSplit(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Return the found codes as {@code TreeSet<Integer>}.
     * <p>
     * This method is necessary because of the elimination of the code in the string of the read lines.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Splits a code based file into separate files by code.
     * <p>
     * A separate file is generated for every existing code. Lines without code will be ignored.
     * RyCON need a valid GSI format file with code blocks (WI 71). The block order is equal.
     *
     * @param dropCode              if code block should dropped out of the result string
     * @param writeLinesWithoutCode if lines without code should be written
     *
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<ArrayList<String>> processCodeSplit(boolean dropCode, boolean writeLinesWithoutCode) {
        ArrayList<GSIHelper> linesWithCode = new ArrayList<>();
        ArrayList<GSIHelper> linesWithOutCode = new ArrayList<>();
        String newLine = null;

        // transform lines into GSI-Blocks
        BaseToolsGSI baseToolsGSI = new BaseToolsGSI(readStringLines);
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = baseToolsGSI.getEncodedLinesOfGSIBlocks();

        // one top level for every code
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (ArrayList<GSIBlock> blocksInLines : gsiBlocks) {

            // helper for code handling inside the switch statements
            int code = -1;
            int validCheckHelperValue = 0;

            for (GSIBlock block : blocksInLines) {
                switch (block.getWordIndex()) {
                    case 11:
                        newLine = block.toString();
                        break;

                    case 71:
                        code = Integer.parseInt(block.getDataGSI());
                        if (dropCode) {
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
                }
            }

            newLine = BaseToolsGSI.prepareLineEnding(newLine);

            // split lines with and without code
            if (((code != -1) & (newLine != null)) & validCheckHelperValue > 1) {
                foundCodes.add(code);
                linesWithCode.add(new GSIHelper(code, newLine));
            } else {
                // use 'blind' code '987789' for this
                linesWithOutCode.add(new GSIHelper(-987789, newLine));
            }
        }

        // sorting the ArrayList
        Collections.sort(linesWithCode, new Comparator<GSIHelper>() {
            @Override
            public int compare(GSIHelper o1, GSIHelper o2) {
                if (o1.getCode() > o2.getCode()) {
                    return 1;
                } else if (o1.getCode() == o2.getCode()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        // helpers for generating a new array for every found code
        if (linesWithCode.size() > 0) {

            int code = linesWithCode.get(0).getCode();
            ArrayList<String> temp = new ArrayList<>();

            // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
            for (GSIHelper gsiHelpers : linesWithCode) {
                if (code == gsiHelpers.getCode()) {
                    temp.add(gsiHelpers.getLine());
                } else {
                    result.add(temp);
                    temp = new ArrayList<>(); // do not use temp.clear()!!!
                    temp.add(gsiHelpers.getLine());
                }

                code = gsiHelpers.getCode();
            }
            // insert last element
            result.add(temp);
        }

        // insert lines without code for writing
        if (writeLinesWithoutCode && (linesWithOutCode.size() > 0)) {
            ArrayList<String> temp = new ArrayList<>();

            for (GSIHelper gsiHelper : linesWithOutCode) {
                temp.add(gsiHelper.getLine());
            }

            foundCodes.add(987789);
            result.add(temp);
        }

        return result;
    }

} // end of GSICodeSplit
