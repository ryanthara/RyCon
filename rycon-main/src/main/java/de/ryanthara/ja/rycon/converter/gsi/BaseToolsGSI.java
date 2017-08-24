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
package de.ryanthara.ja.rycon.converter.gsi;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.elements.GSIBlock;
import de.ryanthara.ja.rycon.tools.SortHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Instances of this class implements several basic operations on Leica GSI files.
 * <p>
 * The Leica Geo Serial Interface (GSI) is a general purpose, serial data
 * interface for bi-directional communication between TPS Total Stations,
 * Levelling instruments and computers.
 * <p>
 * The GSI interface is composed in a sequence of blocks, ending with a
 * terminator (CR or CR/LF). The later introduced enhanced GSI16 format
 * starts every line with a <code>*</code> sign.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class BaseToolsGSI {

    private ArrayList<ArrayList<GSIBlock>> encodedBlocks;
    private ArrayList<String> readStringLines;
    private TreeSet<Integer> foundAllWordIndices;

    /**
     * Constructs a new instance of this class with a parameter for the read line based Leica GSI8 or GSI16 file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public BaseToolsGSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
        this.foundAllWordIndices = new TreeSet<>();
        this.encodedBlocks = blockEncoder(readStringLines);
    }

    /**
     * Returns the block size (number of characters) of a GSI block depending on it's format (GSI8 = 16, GSI16 = 24).
     *
     * @param line line to check
     *
     * @return block size
     */
    public static int getBlockSize(String line) {
        if (line.startsWith("*")) {
            return 24;
        } else {
            return 16;
        }
    }

    /**
     * Returns the point number for the line as string without encoding it into blocks.
     *
     * @param line Leica GSI formatted line
     *
     * @return point number
     */
    public static String getPointNumber(String line) {
        if (line.startsWith("*")) {
            return line.substring(8, 24);
        } else {
            return line.substring(8, 16);
        }
    }

    /**
     * Checks a valid Leica GSI formatted string line for being a target line (three times the coordinate is zero)
     * or for being a free station line.
     *
     * @param line line to be checked
     *
     * @return true if line is a target line
     */
    public static boolean isTargetLine(String line) {
        if (line.startsWith("*")) {
            return (line.split("0000000000000000").length - 1) == 3;
        } else {
            return (line.split("00000000").length - 1) == 3;
        }
    }

    /**
     * Transforms a line of encoded {@code GSIBlock}s into a string line and fill it up into an
     * {@code ArrayList<String>} for later on file writing.
     *
     * @param isGSI16          distinguish between GSI8 or GSI16 output format
     * @param encodedGSIBlocks ArrayList<ArrayList<GSIBlock>> of encoded GSIBlocks
     *
     * @return transformed string line with GSI content
     */
    static ArrayList<String> lineTransformation(boolean isGSI16, ArrayList<ArrayList<GSIBlock>> encodedGSIBlocks) {
        ArrayList<String> result = new ArrayList<>();

        for (ArrayList<GSIBlock> blocksInLines : encodedGSIBlocks) {
            String newLine = "";

            if (isGSI16) {
                newLine = "*";
            }

            int counter = 0;

            for (GSIBlock block : blocksInLines) {
                newLine = newLine.concat(block.toString(isGSI16));

                if (counter < blocksInLines.size()) {
                    newLine = newLine.concat(" ");
                }

                counter = counter + 1;
            }

            newLine = prepareLineEnding(newLine);

            result.add(newLine);
        }

        return result;
    }

    /**
     * Prepares the line ending with an additional white space character.
     * <p>
     * For some reasons (e.g. self written Autocad VBA tools) it is necessary to add an additional white space
     * at the end of a line. This is done with this helper.
     *
     * @param stringToPrepare string to prepare with line ending
     *
     * @return prepared string
     */
    public static String prepareLineEnding(String stringToPrepare) {
        boolean concatBlankAtLineEnding = Boolean.parseBoolean(Main.pref.getUserPreference(PreferenceKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK));

        if (concatBlankAtLineEnding) {
            if (!stringToPrepare.endsWith(" ")) {
                stringToPrepare = stringToPrepare.concat(" ");
            }
        }

        return stringToPrepare;
    }

    /**
     * Encodes a read string line that contains gsi data into an encapsulated <code>ArrayList</code> of
     * <code>GSIBlock</code>s.
     *
     * @return encoded GSIBlocks
     */
    public ArrayList<ArrayList<GSIBlock>> getEncodedLinesOfGSIBlocks() {
        if (readStringLines != null && readStringLines.size() > 0) {
            return encodedBlocks;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Returns all found word indices (WI) from the complete Leica GSI file as one {@code TreeSet<Integer>}
     * without duplicates.
     *
     * @return all found word indices as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundAllWordIndices() {
        return foundAllWordIndices;
    }

    /**
     * Encodes a read GSI string line into an ArrayList of GSIBlocks.
     *
     * @param lines read string lines with GSI content
     *
     * @return encoded ArrayList of GSIBlocks
     */
    private ArrayList<ArrayList<GSIBlock>> blockEncoder(ArrayList<String> lines) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        for (String line : lines) {
            blocks = new ArrayList<>();

            int size = BaseToolsGSI.getBlockSize(line);

            if (size == 24) {
                line = line.substring(1, line.length());
            }

            // split read line into separate Strings
            List<String> lineSplit = new ArrayList<>((line.length() + size - 1) / size);
            for (int i = 0; i < line.length(); i += size) {
                lineSplit.add(line.substring(i, Math.min(line.length(), i + size)));
            }

            // used instead of 'deprecated' StringTokenizer here
            for (String blockAsString : lineSplit) {
                GSIBlock block = new GSIBlock(blockAsString);
                blocks.add(block);
                foundAllWordIndices.add(block.getWordIndex());
            }

            // sort every 'line' of GSI blocks by word index (WI)
            SortHelper.sortByWordIndex(blocks);

            // fill in the sorted 'line' of blocks into an array
            blocksInLines.add(blocks);
        }

        return blocksInLines;
    }

} // end of BaseToolsGSI
