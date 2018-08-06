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

/**
 * This class provides functions to convert coordinate files from the geodata server 'Basel Landschaft' (Switzerland)
 * into csv (comma separated values) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Csv {

    private final ArrayList<String> readStringLines;

    /**
     * Class constructor for reader line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TxtBaselLandschaft2Csv(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a text formatted file from the geodata server Basel Landschaft (Switzerland) into a CSV formatted file.
     * <p>
     * With a parameter it is possible to distinguish between comma or semicolon as separator.
     *
     * @param separator separator sign as {@code String}
     *
     * @return converted {@code ArrayList<String>} with lines of CSV format
     */
    public ArrayList<String> convertTXTBaselLandschaft2CSV(String separator) {
        TxtBaselLandschaft2Txt txtBaselLandschaft2Txt = new TxtBaselLandschaft2Txt(readStringLines);

        return txtBaselLandschaft2Txt.convertTXTBaselLandschaft2TXT(separator, false);
    }

} // end of TxtBaselLandschaft2Csv
