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
package de.ryanthara.ja.rycon.core.converter.gsi;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.util.SortHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Instances of <tt>BaseToolsGsi</tt> implements several basic operations
 * on Leica Geosystems GSI files.
 * <p>
 * The Leica Geo Serial Interface (GSI) is a general purpose, serial data
 * interface for bi-directional communication between TPS Total Stations,
 * Levelling instruments and computers.
 * <p>
 * The GSI interface is composed in a sequence of blocks, ending with a
 * terminator (CR or CR/LF). The later introduced enhanced GSI16 format
 * starts every line with a <code>*</code> sign.
 * <p>
 * Du to some issues or personal limitations sometimes a blank is added
 * to line endings. This is wrong, but <tt>RyCON</tt> can handle it.
 *
 * @author sebastian
 * @version 3
 * @since 12
 */
public class BaseToolsGsi {

    private static TreeSet<Integer> foundAllWordIndices = new TreeSet<>();
    private ArrayList<ArrayList<GsiBlock>> encodedBlocks;
    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for
     * the read line based Leica GSI8 or GSI16 file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public BaseToolsGsi(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
        this.encodedBlocks = blockEncoder(readStringLines);
    }

    private static String getBlockByWordIndex(final ArrayList<GsiBlock> blocks, final int wordIndex) {
        for (GsiBlock block : blocks) {
            if (block.getWordIndex() == wordIndex) {
                return block.toString();
            }
        }

        return "";
    }

    /**
     * Returns the block size (number of characters) of a GSI block depending on
     * it's format (GSI8 = 16, GSI16 = 24).
     *
     * @param line GSI formatted line to check for block size
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
     * @return point number or empty string if line is empty string
     */
    public static String getPointNumber(final String line) {
        if (!line.equalsIgnoreCase("")) {
            if (line.startsWith("*")) {
                return line.substring(8, 24);
            } else {
                return line.substring(8, 16);
            }
        } else {
            return "";
        }
    }

    /**
     * Checks a valid <tt>Leica Geosystems</tt> GSI formatted string line for being a one face target line.
     * <p>
     * The one face target line contains three times the zero coordinate.
     *
     * @param line line to be checked for one face target line
     *
     * @return true if line is a one face target line
     */
    public static boolean isTargetLine(String line) {
        ArrayList<GsiBlock> blocks = lineEncoder(line);

        final String block11 = getBlockByWordIndex(blocks, 11);
        final String block81 = getBlockByWordIndex(blocks, 81);
        final String block82 = getBlockByWordIndex(blocks, 82);
        final String block83 = getBlockByWordIndex(blocks, 83);

        final String decodedLine = block11 + " " + block81 + " " + block82 + " " + block83;

        String pattern;

        // differ between GSI8 and GSI16 format
        if (line.startsWith("*")) {
            pattern = "0000000000000000";
        } else {
            pattern = "00000000";
        }

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(decodedLine);
        int founds = 0;

        while (m.find()) {
            founds = founds + 1;
        }

        return founds == 3;
    }

    private static ArrayList<GsiBlock> lineEncoder(String line) {
        ArrayList<GsiBlock> blocks = new ArrayList<>();

        if (!line.equalsIgnoreCase("")) {
            int size = BaseToolsGsi.getBlockSize(line);

            if (size == 24) {
                line = line.substring(1);
            }

            // split reader line into separate Strings
            List<String> lineSplit = new ArrayList<>((line.length() + size - 1) / size);
            for (int i = 0; i < line.length(); i += size) {
                lineSplit.add(line.substring(i, Math.min(line.length(), i + size)));
            }

            // used instead of 'deprecated' StringTokenizer here
            for (String blockAsString : lineSplit) {
                GsiBlock block = new GsiBlock(blockAsString);
                blocks.add(block);
                foundAllWordIndices.add(block.getWordIndex());
            }

            // sort every 'line' of GSI blocks by word index (WI)
            SortHelper.sortByWordIndex(blocks);
        }

        return blocks;
    }

    /**
     * Transforms a line of encoded {@code GsiBlock}s into a string line and fill it up into an
     * {@code ArrayList<String>} for later file writing.
     *
     * @param isGSI16          distinguish between GSI8 or GSI16 output format
     * @param encodedGSIBlocks ArrayList<ArrayList<GsiBlock>> of encoded GSIBlocks
     *
     * @return transformed string line with GSI content
     */
    static ArrayList<String> lineTransformation(boolean isGSI16, ArrayList<ArrayList<GsiBlock>> encodedGSIBlocks) {
        ArrayList<String> result = new ArrayList<>();

        for (ArrayList<GsiBlock> blocksInLines : encodedGSIBlocks) {
            String newLine = "";

            if (isGSI16) {
                newLine = "*";
            }

            int counter = 0;

            for (GsiBlock block : blocksInLines) {
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
     * For some reasons (e.g. self written Autocad VBA utilities) it is necessary to add
     * an additional white space at the end of a line. This is done with this helper.
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
     * Encodes a reader string line that contains gsi data into an encapsulated <code>ArrayList</code>
     * of <code>GsiBlock</code>s.
     *
     * @return encoded GSIBlocks
     */
    public ArrayList<ArrayList<GsiBlock>> getEncodedLinesOfGSIBlocks() {
        if (readStringLines != null && readStringLines.size() > 0) {
            return encodedBlocks;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Returns all found word indices (WI) from the complete Leica Geosystems GSI file
     * as one {@code TreeSet<Integer>} without duplicates.
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
    private ArrayList<ArrayList<GsiBlock>> blockEncoder(ArrayList<String> lines) {
        ArrayList<ArrayList<GsiBlock>> blocksInLines = new ArrayList<>();

        for (String line : lines) {
            blocksInLines.add(lineEncoder(line));
        }

        return blocksInLines;
    }

} // end of BaseToolsGsi
