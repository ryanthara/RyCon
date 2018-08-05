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

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert an ascii altitude register file
 * with the special separation sequence into a standard whitespace separated ascii file.
 * <p>
 * The line based output ascii file contains one point (no x y z) in every line.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class Asc2Asc extends Converter {

    private final ArrayList<String> lines;

    /**
     * Constructs a new instance of this class with a parameter for the ascii altitude register
     * as {@code ArrayList<String>}.
     *
     * @param lines ascii altitude register lines
     */
    public Asc2Asc(ArrayList<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Does the conversion from ascii (nr x y z) with special separator sign sequence
     * to an ascii file with whitespace separator sign in an {@code ArrayList<String>}.
     * <p>
     * The {@link Converter#SEPARATOR separator} sign.
     *
     * @return the converted lines
     *
     * @see Converter
     */
    @Override
    public ArrayList<String> convert() {
        ArrayList<String> result = new ArrayList<>(lines.size());

        for (String line : lines) {
            result.add(line.replace(Converter.SEPARATOR, " "));
        }

        return new ArrayList<>(result);
    }

} // end of Asc2Asc
