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

import de.ryanthara.ja.rycon.core.converter.Converter;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A converter with functions to convert an ascii altitude register file
 * into a Leica Geosystems GSI file.
 *
 * <p>
 * The line based ascii file contains one point (no x y z) in every line
 * which coordinates are separated by a special character sequence.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class Asc2Gsi extends Converter {

    private final List<String> lines;
    private final boolean isGsi16;

    /**
     * Creates a converter with a list for the read line based ascii altitude
     * register file and an option for the choice of GSI8/GSI16 output.
     *
     * @param lines   ascii altitude register lines
     * @param isGsi16 true if GSI16 format
     */
    public Asc2Gsi(List<String> lines, boolean isGsi16) {
        this.lines = new ArrayList<>(lines);
        this.isGsi16 = isGsi16;
    }

    /**
     * Does the conversion from ascii (nr x y z) to Leica Geosystems GSI 8/16 and returns
     * the result as {@code List<String>}.
     * <p>
     * The {@link Converter#SEPARATOR separator} sign.
     *
     * @return the converted lines
     * @see Converter
     */
    @Override
    public List<String> convert() {
        List<List<GsiBlock>> blocksInLines = new ArrayList<>(lines.size());

        // check for at least one or more added elements to prevent writing empty lines
        IntStream.range(0, lines.size()).forEach(i -> {
            List<GsiBlock> blocks = new ArrayList<>(4);
            final String line = lines.get(i);
            final String[] split = line.split(Converter.SEPARATOR);

            if (split.length == 4) {
                blocks.add(new GsiBlock(isGsi16, 11, i + 1, split[0]));
                blocks.add(new GsiBlock(isGsi16, 81, split[1]));
                blocks.add(new GsiBlock(isGsi16, 82, split[2]));
                blocks.add(new GsiBlock(isGsi16, 83, split[3]));
            }

            if (blocks.size() > 0) {
                blocksInLines.add(blocks);
            }
        });

        return List.copyOf(BaseToolsGsi.lineTransformation(isGsi16, blocksInLines));
    }

}
