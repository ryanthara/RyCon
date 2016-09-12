/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

import java.util.*;

/**
 * This class implements basic operations on text based measurement and coordinate files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>5: support for cadwork node.dat files, code clean up</li>
 *     <li>4: support for NIGRA levelling files</li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 5
 * @since 1
 */
public class FileToolsText {


    private ArrayList<String> readStringLines;
    private TreeSet<Integer> foundCodes = new TreeSet<>();

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param arrayList {@code ArrayList<String>} with lines in text format
     */
    public FileToolsText(ArrayList<String> arrayList) {
        this.readStringLines = arrayList;
    }

    /**
     * Return the found codes as an integer array.
     * <p>
     * This is necessary because of the elimination of the code in the string line.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Split a code based file into separate files by code.
     * <p>
     * A separate file is generated for every existing code. Lines without code will ignored.
     * RyCON need a text file format that is nr, code, x, y, z and divided by blank or tab.
     *
     * @param dropCode if code column should dropped out of the result
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<ArrayList<String>> processCodeSplit(boolean dropCode) {
        String newLine;
        StringTokenizer stringTokenizer;

        ArrayList<TextHelper> linesWithCode = new ArrayList<>();

        // one top level for every code
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (String line : readStringLines) {
            stringTokenizer = new StringTokenizer(line);

            // a line with code contains 5 tokens (nr, code, y, y, z)
            if (stringTokenizer.countTokens() == 5) {

                // number
                newLine = stringTokenizer.nextToken();

                // code
                String codeBlock = stringTokenizer.nextToken();
                foundCodes.add(Integer.parseInt(codeBlock));

                if (dropCode) {
                    newLine = newLine.concat(" " + codeBlock);
                }

                // x coordinate
                newLine = newLine.concat(" " + stringTokenizer.nextToken());

                // y coordinate
                newLine = newLine.concat(" " + stringTokenizer.nextToken());

                // z coordinate
                newLine = newLine.concat(" " + stringTokenizer.nextToken());

                linesWithCode.add(new TextHelper(Integer.parseInt(codeBlock), newLine));
            }
        }

        Collections.sort(linesWithCode, new Comparator<TextHelper>() {
            @Override
            public int compare(TextHelper o1, TextHelper o2) {
                if (o1.code > o2.code) {
                    return 1;
                } else if (o1.code == o2.code) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        // helpers for generating a new array for every found code
        // TODO a file without code is not supported
        int code = linesWithCode.get(0).code;
        ArrayList<String> lineStorage = new ArrayList<>();

        // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
        for (TextHelper textBlock : linesWithCode) {
            if (code == textBlock.code) {
                lineStorage.add(textBlock.block);
            } else {
                result.add(lineStorage);
                lineStorage = new ArrayList<>(); // do not use temp.clear()!!!
                lineStorage.add(textBlock.block);
            }
            code = textBlock.code;
        }

        // insert last element
        result.add(lineStorage);

        return result;
    }

    /**
     * Define an inner object for better access to read text elements.
     * <p>
     * In the first version this TextHelper object is used only internally in this class.
     * Maybe later on, there will be a good reason to make an own class from it.
     *
     * @author sebastian
     * @version 1
     * @since 2
     */
    private static class TextHelper {

        final int code;
        final String block;

        /**
         * Constructor with parameters to build the block structure.
         *
         * @param code  code as integer value
         * @param block complete block as String
         */
        TextHelper(int code, String block) {
            this.code = code;
            this.block = block;
        }

        /**
         * Return the code and the block to String.
         *
         * @return code and block as String
         */
        public String toString() {
            return code + " " + block;
        }

    } // end of inner class TextHelper

}  // end of TextFileTools
