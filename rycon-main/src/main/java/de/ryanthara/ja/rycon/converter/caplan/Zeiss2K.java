/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.converter.caplan;

import java.util.ArrayList;

/**
 * This class provides functions to convert measurement files from Zeiss REC format
 * and it's dialects (REC500, R4, R5 and M5) into Caplan K files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2K {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line base text files in the Zeiss REC format and it's dialects.
     * <p>
     * The differentiation of the content is done by the called method.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Zeiss2K(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a measurement file from Zeiss REC format (REC500, R4, R5 or M5) into a Caplan K formatted file.
     *
     * @param useSimpleFormat  output file with a simple structure
     * @param writeCodeColumn  write code column to output file
     * @param writeCommentLine write comment line to output file
     *
     * @return converted K file
     */
    public ArrayList<String> convertZeiss2K(boolean useSimpleFormat, boolean writeCodeColumn, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }
}
