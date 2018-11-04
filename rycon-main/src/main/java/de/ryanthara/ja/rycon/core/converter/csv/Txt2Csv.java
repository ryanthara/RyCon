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
package de.ryanthara.ja.rycon.core.converter.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert ASCII text coordinate
 * files into comma separated values (CSV) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Txt2Csv {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based ASCII text file.
     *
     * @param lines list with ASCII text lines
     */
    public Txt2Csv(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a text formatted file into a CSV file with the given separator sign.
     * <p>
     * Due to some reasons the text file could not use white space characters in point numbers or code blocks.
     *
     * @param separator separator sign to use for conversion
     * @return converted CSV file
     */
    public List<String> convert(String separator) {
        List<String> result = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            result.add(line.replaceAll("\\s+", separator));
        }

        return List.copyOf(result);
    }

}
