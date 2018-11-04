/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.core.splitter;

import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.core.elements.RyBlock;
import de.ryanthara.ja.rycon.util.SortUtils;
import de.ryanthara.ja.rycon.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Instances of this class provides functions to split text based measurement and coordinate files
 * by code into separate files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files.
 *
 * @author sebastian
 * @version 5
 * @since 1
 */
public class TextCodeSplit {

    private final List<String> lines;
    private final TreeSet<Integer> foundCodes;

    /**
     * Constructs a new instance of this class given a read line based text file with a specified format.
     *
     * @param lines {@code List<String>} with lines in text format
     */
    public TextCodeSplit(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        foundCodes = new TreeSet<>();
    }

    /**
     * Returns the found codes as an integer array.
     * <p>
     * This is necessary because of the elimination of the code in the string line.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Splits a code based text file into separate files by code.
     * <p>
     * A separate file is generated for every existing code. Lines without code will get the pseudo code '987789'.
     * RyCON need a text file format that is no, code, x, y, z and divided by blank or tab.
     *
     * @param insertCodeBlock       if code block is insert into the result string
     * @param writeLinesWithoutCode if lines without code should be written to a separate file
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public List<ArrayList<String>> processCodeSplit(boolean insertCodeBlock, boolean writeLinesWithoutCode) {
        StringTokenizer stringTokenizer;

        List<ArrayList<String>> result = new ArrayList<>();
        List<RyBlock> linesWithCode = new ArrayList<>();
        List<String> linesWithOutCode = new ArrayList<>();

        for (String line : lines) {
            stringTokenizer = new StringTokenizer(line);

            if (stringTokenizer.countTokens() == 4) {
                // a line without code contains 4 tokens (no, y, y, z)
                // number
                String resultLine = stringTokenizer.nextToken();

                if (insertCodeBlock) {
                    resultLine = resultLine.concat(" 987789");
                }

                String easting = stringTokenizer.nextToken();
                resultLine = resultLine.concat(Separator.WHITESPACE.getSign() + easting);

                String northing = stringTokenizer.nextToken();
                resultLine = resultLine.concat(Separator.WHITESPACE.getSign() + northing);

                String height = stringTokenizer.nextToken();
                resultLine = resultLine.concat(Separator.WHITESPACE.getSign() + height);

                linesWithOutCode.add(resultLine);
            } else if (stringTokenizer.countTokens() == 5) {
                // a line with code contains 5 tokens (no, code, y, y, z)

                // number
                String resultLine = stringTokenizer.nextToken();

                String code = stringTokenizer.nextToken();
                int codeValue = StringUtils.parseIntegerValue(code);

                foundCodes.add(codeValue);

                if (insertCodeBlock) {
                    resultLine = resultLine.concat(Separator.WHITESPACE.getSign() + code);
                }

                String easting = stringTokenizer.nextToken();
                resultLine = resultLine.concat(Separator.WHITESPACE.getSign() + easting);

                String northing = stringTokenizer.nextToken();
                resultLine = resultLine.concat(Separator.WHITESPACE.getSign() + northing);

                String height = stringTokenizer.nextToken();
                resultLine = resultLine.concat(Separator.WHITESPACE.getSign() + height);

                linesWithCode.add(new RyBlock(codeValue, resultLine));
            }
        }

        SortUtils.sortByCode(linesWithCode);

        if (linesWithCode.size() > 0) {
            // helpers for generating a new array for every found code
            int code = linesWithCode.get(0).getNumber();
            ArrayList<String> lineStorage = new ArrayList<>();

            // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing
            for (RyBlock ryBlock : linesWithCode) {
                if (code == ryBlock.getNumber()) {
                    lineStorage.add(ryBlock.getString());
                } else {
                    result.add(lineStorage);
                    lineStorage = new ArrayList<>();
                    lineStorage.add(ryBlock.getString());
                }

                code = ryBlock.getNumber();
            }

            // insert last element
            result.add(lineStorage);
        }

        // insert lines without code for writing
        if (writeLinesWithoutCode && (linesWithOutCode.size() > 0)) {

            ArrayList<String> lineStorage = new ArrayList<>(linesWithOutCode);

            foundCodes.add(987789);
            result.add(lineStorage);
        }

        return List.copyOf(result);
    }

}
