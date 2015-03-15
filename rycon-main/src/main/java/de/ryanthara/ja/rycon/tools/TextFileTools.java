/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
 *     <li>3: code improvements and clean up</li>
 *     <li>2: basic improvements
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public class TextFileTools {

    private ArrayList<String> arrayList;
    private List<String[]> list;
    private TreeSet<Integer> foundCodes = new TreeSet<Integer>();

    /**
     * Class Constructor with parameter.
     * <p>
     * As parameter the {@code ArrayList<String>} object with the lines in text format is used.
     *
     * @param arrayList {@code ArrayList<String>} with lines in text format
     */
    public TextFileTools(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    /**
     * Class Constructor with parameter.
     * <p>
     * As parameter the {@code List<String[]>} object with the lines in csv format is used.
     *
     * @param list {@code List<String[]>} with lines in csv format
     */
    public TextFileTools(List<String[]> list) {
        this.list = list;
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
     * Splits a code based file into separate files by code.
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

        ArrayList<TextHelper> linesWithCode = new ArrayList<TextHelper>();

        // one top level for every code
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (String line : arrayList) {
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
        ArrayList<String> lineStorage = new ArrayList<String>();

        // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
        for (TextHelper textBlock : linesWithCode) {
            if (code == textBlock.code) {
                lineStorage.add(textBlock.block);
            } else {
                result.add(lineStorage);
                lineStorage = new ArrayList<String>(); // do not use temp.clear()!!!
                lineStorage.add(textBlock.block);
            }
            code = textBlock.code;
        }

        // insert last element
        result.add(lineStorage);

        return result;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (switzerland) into a txt format file.
     * <p>
     * With a parameter it is possible to distinguish between space or tabulator as separator.
     *
     * @param delimiter delimiter sign as {@code String}
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> processFormatConversionCSVBaselStadt2TXT(String delimiter) {
        ArrayList<String> result = new ArrayList<String>();

        for (String[] stringField : list) {
            String line;

            // point number is in column 1
            line = stringField[0].replaceAll("\\s+", "").trim();
            line = line.concat(delimiter);

            // easting (Y) is in column 3
            line = line.concat(stringField[2]);
            line = line.concat(delimiter);

            // northing (X) is in column 4
            line = line.concat(stringField[3]);

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                line = line.concat(delimiter);
                line = line.concat(stringField[4]);
            }

            result.add(line.trim());
        }
        return result;
    }

    /**
     * Converts a CSV file into a TXT file with the given delimiter sign.
     *
     * @param delimiter delimiter sign to use for conversion
     * @return converted TXT file
     */
    public ArrayList<String> processConversionCSV2TXT(String delimiter) {
        ArrayList<String> result = new ArrayList<String>();

        // convert the List<String[]> into an ArrayList<String> and use known stuff (-:
        for (String[] stringField : list) {
            String line = "";

            for (String s : stringField) {
                line = line.concat(s);
                line = line.concat(delimiter);
            }

            line = line.trim();
            line = line.replace(',', '.');

            // skip empty lines
            if (!line.equals("")) {
                result.add(line);
            }
        }
        return result;
    }

    /**
     * Converts a TXT file into a CSV file with the given delimiter sign.
     *
     * @param delimiter delimiter sign to use for conversion
     * @return converted CSV file
     */
    public ArrayList<String> processConversionTXT2CSV(String delimiter) {
        ArrayList<String> result = new ArrayList<String>();

        for (String line : arrayList) {
            // get rid off one or more empty signs at the beginning and end of the given string
            line = line.trim();
            result.add(line.replaceAll("\\s+", delimiter));
        }
        return result;
    }

    /**
     * Defines an inner object for better access to elements and so on.
     * <p>
     * In the first version this TextHelper object is used only internally in this class.
     * Maybe later on, there will be a good reason to make an own public class from it.
     *
     * @author sebastian
     * @version 1
     * @since 2
     */
    private class TextHelper {

        final int code;
        final String block;

        /**
         * Constructor with parameters to build the block structure.
         *
         * @param code  code as integer value
         * @param block complete block as String
         */
        public TextHelper(int code, String block) {

            this.code = code;
            this.block = block;

        }

        /**
         * Returns the code and the block to String.
         *
         * @return code and block as String
         */
        public String toString() {
            return code + " " + block;
        }

    } // end of TextHelper

}  // end of TextFileTools
