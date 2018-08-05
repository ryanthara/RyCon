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
import de.ryanthara.ja.rycon.ui.util.StringHelper;

import java.util.ArrayList;


/**
 * Instances of this class provides functions to convert different text based level files into an ascii file.
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the text level file if present. Otherwise they will be set
 * to local values starting at 0,0 and raise in both axis by a constant value.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class Asc2Txt extends Converter {

    private boolean useWhiteSpaceSeparator;
    private ArrayList<String> lines;

    /**
     * Constructs a new instance of this class with a parameter for the read {@code ArrayList<String>}
     * from Nigra/NigraWin.
     *
     * @param lines                  read lines
     * @param useWhiteSpaceSeparator true for whitespace instead of tab separator
     */
    public Asc2Txt(ArrayList<String> lines, boolean useWhiteSpaceSeparator) {
        this.lines = new ArrayList<>(lines);
        this.useWhiteSpaceSeparator = useWhiteSpaceSeparator;
    }

    /**
     * Does the conversion from ascii (nr x y z) with special separator sign sequence
     * to a text file with whitespace or tabulator separator sign into
     * an {@code ArrayList<String>}.
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

        String replacement = useWhiteSpaceSeparator ? " " : "\t";

        for (String line : lines) {
            String output = "     ";
            if (useWhiteSpaceSeparator) {
                String[] split = line.split(Converter.SEPARATOR);

                String number = StringHelper.fillWithSpaces(14, split[0]);
                String easting = StringHelper.fillWithSpaces(14, split[1]);
                String northing = StringHelper.fillWithSpaces(14, split[2]);
                String altitude = StringHelper.fillWithSpaces(12, split[3]);

                output = output + number + easting + northing + altitude;
            } else {
                output = line.replace(Converter.SEPARATOR, replacement);
            }

            result.add(output);
        }

        return new ArrayList<>(result);
    }

} // end of Asc2Txt
