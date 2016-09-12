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
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.*;

/**
 * This class implements several basic operations on Leica GSI files.
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
 * @version 12
 * @since 1
 */
public class BaseToolsGSI {

    private boolean isGSI16 = false;

    private ArrayList<String> readStringLines = null;
    private List<String[]> readCSVLines = null;
    private TreeSet<Integer> foundWordIndices;

    /**
     * Default constructor without additional functions.
     */
    public BaseToolsGSI() {
        foundWordIndices = new TreeSet<>();
    }

    /**
     * Return the found word indices (WI) as {@code TreeSet<Integer>}.
     *
     * @return found word indices as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundWordIndices() {
        return foundWordIndices;
    }

    /**
     * Encodes a read GSI string line into an ArrayList of GSIBlocks.
     *
     * @param lines read string lines with GSI content
     *
     * @return encoded ArrayList of GSIBlocks
     */
    ArrayList<ArrayList<GSIBlock>> blockEncoder(ArrayList<String> lines) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        for (String line : lines) {
            int size;
            blocks = new ArrayList<>();

            if (line.startsWith("*")) {
                size = 24;
                line = line.substring(1, line.length());
            } else {
                size = 16;
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
                foundWordIndices.add(block.getWordIndex());
            }

            // sort every 'line' of GSI blocks by word index (WI)
            Collections.sort(blocks, new Comparator<GSIBlock>() {
                @Override
                public int compare(GSIBlock o1, GSIBlock o2) {
                    if (o1.getWordIndex() > o2.getWordIndex()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            // fill in the sorted 'line' of blocks into an array
            blocksInLines.add(blocks);
        }

        return blocksInLines;
    }

    /**
     * Encodes a read string line that contains gsi data into an encapsulated <code>ArrayList</code> of
     * <code>GSIBlock</code>s.
     * <p>
     * Depending on the constructor pasted line type, the right encoding will be done.
     *
     * @return encoded GSIBlocks
     */
    public ArrayList<ArrayList<GSIBlock>> getEncodedLinesOfGSIBlocks() {
        if (readCSVLines != null && readCSVLines.size() > 0) {
            CSV2GSI csv2GSI = new CSV2GSI(readCSVLines);
            return blockEncoder(csv2GSI.convertCSV2GSI(isGSI16, false));
        } else if (readStringLines != null && readStringLines.size() > 0) {
            return blockEncoder(readStringLines);
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<String> lineTransformation(boolean isGSI16, ArrayList<ArrayList<GSIBlock>> encodedGSIBlocks) {
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

                counter++;
            }

            newLine = prepareLineEnding(newLine);

            result.add(newLine);
        }

        return result;
    }

    /*
     * For some reasons (e.g. self written Autocad VBA tools) it is necessary to add an additional white space
     * at the line ending. This is done with this helper.
     */
    private String prepareLineEnding(String stringToPrepare) {
        boolean concatBlankAtLineEnding = Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.GSI_SETTING_LINE_ENDING_WITH_BLANK));

        if (concatBlankAtLineEnding) {
            if (!stringToPrepare.endsWith(" ")) {
                stringToPrepare = stringToPrepare.concat(" ");
            }
        }

        return stringToPrepare;
    }

} // end of BaseToolsGSI
