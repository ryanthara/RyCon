/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.asc
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
package de.ryanthara.ja.rycon.core.converter.asc;

import de.ryanthara.ja.rycon.core.converter.Converter;
import de.ryanthara.ja.rycon.core.converter.Separator;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert comma separated (CSV) coordinate files into ASCII text files.
 *
 * <p>
 * The line based output ascii file contains one point (no x y z) in every line.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class Asc2Asc extends Converter {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based ascii altitude
     * register file and an option for the choice of the separator sign.
     *
     * @param lines ascii altitude register lines
     */
    public Asc2Asc(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Does the conversion from ascii (nr x y z) with special separator sign sequence
     * to an ascii file with whitespace separator sign in an {@code List<String>}.
     * <p>
     * The {@link Converter#SEPARATOR separator} sign.
     *
     * @return the converted lines
     * @see Converter
     */
    @Override
    public List<String> convert() {
        List<String> result = new ArrayList<>(lines.size());

        for (String line : lines) {
            result.add(line.replace(Converter.SEPARATOR, Separator.WHITESPACE.getSign()));
        }

        return List.copyOf(result);
    }

}
