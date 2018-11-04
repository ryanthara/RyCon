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
package de.ryanthara.ja.rycon.core.converter.text;

import de.ryanthara.ja.rycon.core.converter.Converter;
import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A converter with functions to convert comma separated (CSV) coordinate files into an ascii file.
 *
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 *
 * <p>
 * The point coordinates are taken from the text level file if present. Otherwise they will be set
 * to local values starting at 0,0 and raise in both axis by a constant value.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class Asc2Txt extends Converter {

    private final boolean useWhiteSpaceSeparator;
    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based ascii altitude
     * register file and an option for the choice of the separator sign.
     *
     * @param lines                  ascii altitude register lines
     * @param useWhiteSpaceSeparator true for whitespace instead of tab separator
     */
    public Asc2Txt(List<String> lines, boolean useWhiteSpaceSeparator) {
        this.lines = new ArrayList<>(lines);
        this.useWhiteSpaceSeparator = useWhiteSpaceSeparator;
    }

    /**
     * Does the conversion from ascii (nr x y z) with special separator sign sequence
     * to a text file with whitespace or tabulator separator sign into
     * an {@code List<String>}.
     * <p>
     * The {@link Converter#SEPARATOR separator} sign.
     *
     * @return the converted lines
     * @see Converter
     */
    @Override
    public List<String> convert() {
        List<String> result = new ArrayList<>(lines.size());

        String replacement = useWhiteSpaceSeparator ? Separator.WHITESPACE.getSign() : Separator.TABULATOR.getSign();

        for (String line : lines) {
            if (useWhiteSpaceSeparator) {
                String[] values = line.split(Converter.SEPARATOR);

                String number = StringUtils.fillWithSpacesFromBeginning(values[0], 14);
                String easting = StringUtils.fillWithSpacesFromBeginning(values[1], 14);
                String northing = StringUtils.fillWithSpacesFromBeginning(values[2], 14);
                String altitude = StringUtils.fillWithSpacesFromBeginning(values[3], 12);

                String builder = "     " +
                        number +
                        easting +
                        northing +
                        altitude;
                result.add(builder);
            } else {
                result.add(line.replace(Converter.SEPARATOR, replacement));
            }
        }

        return List.copyOf(result);
    }

}
