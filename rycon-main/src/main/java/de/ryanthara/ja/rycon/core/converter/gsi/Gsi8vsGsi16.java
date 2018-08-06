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

/**
 * Instances of this class provides functions to convert between Leica GSI8 and GSI16 vice versa.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Gsi8vsGsi16 {

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for the reader line based GSI formatted files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Gsi8vsGsi16(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a GSI8 formatted file into a GSI16 formatted file and vise versa.
     *
     * @param isGSI16 output file is GSI16 formatted
     *
     * @return converted GSI file
     */
    public ArrayList<String> convertGSI8vsGSI16(boolean isGSI16) {
        BaseToolsGsi baseToolsGsi = new BaseToolsGsi(readStringLines);
        return BaseToolsGsi.lineTransformation(isGSI16, baseToolsGsi.getEncodedLinesOfGSIBlocks());
    }

} // end of Gsi8vsGsi16
