/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.gsi
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

import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.util.SortUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A decoder with functions to decode a string line that contains Leica Geosystems GSI8 or GSI16 blocks.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
class GsiLineDecoder {

    private static final Set<Integer> foundWordIndices = new TreeSet<>();

    /**
     * Creates a decoder without functionality.
     */
    GsiLineDecoder() {
    }

    /**
     * Returns all found word indices (WI) without duplicates.
     *
     * @return all found word indices
     */
    Set<Integer> getFoundWordIndices() {
        return foundWordIndices;
    }

    /**
     * Decodes a single line of Leica Geosystems GSI8 or GSI16 blocks into an array list of {@link GsiBlock}.
     *
     * @param line line to convert
     * @return array list of converted GsiBlocks
     */
    List<GsiBlock> decode(String line) {
        ArrayList<GsiBlock> blocks = new ArrayList<>();

        if (!line.equalsIgnoreCase("")) {
            int size = BaseToolsGsi.getBlockSize(line);

            if (size == 24) {
                line = line.substring(1);
            }

            List<String> lineSplit = new ArrayList<>((line.length() + size - 1) / size);
            for (int i = 0; i < line.length(); i += size) {
                lineSplit.add(line.substring(i, Math.min(line.length(), i + size)));
            }

            for (String blockAsString : lineSplit) {
                GsiBlock block = new GsiBlock(blockAsString);
                blocks.add(block);
                foundWordIndices.add(block.getWordIndex());
            }

            SortUtils.sortByWordIndex(blocks);
        }

        return blocks;
    }

}
