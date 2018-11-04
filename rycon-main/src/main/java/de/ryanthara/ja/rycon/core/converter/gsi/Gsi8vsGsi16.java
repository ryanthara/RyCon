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
package de.ryanthara.ja.rycon.core.converter.gsi;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert between Leica Geosystems GSI8 and GSI16 vice versa.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi8vsGsi16 {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based
     * Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public Gsi8vsGsi16(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a GSI8 formatted file into a GSI16 formatted file and vice versa.
     *
     * @param isGSI16                output file is GSI16 formatted
     * @param sortOutputFileByNumber ascending sort for output file
     * @return converted GSI file
     */
    public List<String> convert(boolean isGSI16, boolean sortOutputFileByNumber) {
        GsiDecoder gsiDecoder = new GsiDecoder(lines);
        List<String> result = BaseToolsGsi.lineTransformation(isGSI16, gsiDecoder.getDecodedLinesOfGsiBlocks());

        return sortOutputFileByNumber ? BaseToolsGsi.sortResult(result) : new ArrayList<>(result);
    }

}
