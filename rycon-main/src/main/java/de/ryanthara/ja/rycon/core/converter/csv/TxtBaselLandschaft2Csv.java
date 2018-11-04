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

import de.ryanthara.ja.rycon.core.converter.text.TxtBaselLandschaft2Txt;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from the geodata server
 * Basel Landschaft (Switzerland) into comma separated values CSV files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Csv {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text files
     * from the geodata server Basel Landschaft (Switzerland).
     *
     * @param lines list with coordinate lines
     */
    public TxtBaselLandschaft2Csv(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Convert a text formatted file from the geodata server Basel Landschaft (Switzerland) into a CSV formatted file.
     * <p>
     * With a parameter it is possible to distinguish between comma or semicolon as separator.
     *
     * @param separator separator sign as {@code String}
     * @return converted {@code List<String>} with lines of CSV format
     */
    public List<String> convert(String separator) {
        TxtBaselLandschaft2Txt txtBaselLandschaft2Txt = new TxtBaselLandschaft2Txt(lines);

        return txtBaselLandschaft2Txt.convert(separator, false);
    }

}
