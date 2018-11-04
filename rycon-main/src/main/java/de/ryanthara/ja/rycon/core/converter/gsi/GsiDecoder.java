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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * An decoder with functions to decode Leica Geosystems GSI8 or GSI16 files.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public class GsiDecoder {

    private final List<String> lines;
    private final List<List<GsiBlock>> decodedBlocks;
    private Set<Integer> foundWordIndices;

    /**
     * Creates a decoder with a list for the read line based Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public GsiDecoder(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        this.decodedBlocks = decodeToBlocks(lines);
        this.foundWordIndices = new TreeSet<>();
    }

    private List<List<GsiBlock>> decodeToBlocks(List<String> lines) {
        List<List<GsiBlock>> blocksInLines = new ArrayList<>();

        GsiLineDecoder lineDecoder = new GsiLineDecoder();

        for (String line : lines) {
            blocksInLines.add(lineDecoder.decode(line));
        }

        foundWordIndices = lineDecoder.getFoundWordIndices();

        return blocksInLines;
    }

    /**
     * Returns the decoded list of {@link GsiBlock}.
     *
     * @return decoded GSIBlocks
     */
    public List<List<GsiBlock>> getDecodedLinesOfGsiBlocks() {
        if (lines != null && lines.size() > 0) {
            return decodedBlocks;
        } else {
            return List.of();
        }
    }

    /**
     * Returns all found word indices (WI) without duplicates.
     *
     * @return all found word indices
     */
    public TreeSet<Integer> getFoundWordIndices() {
        return new TreeSet<>(foundWordIndices);
    }

}
