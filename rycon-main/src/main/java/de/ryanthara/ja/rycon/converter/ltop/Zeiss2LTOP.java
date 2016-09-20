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
package de.ryanthara.ja.rycon.converter.ltop;

import java.util.ArrayList;

/**
 * This class provides functions to convert measurement files from Zeiss REC format
 * and it's dialects (R4, R5, REC500 and M5) into KOO and MES files for LTOP.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */public class Zeiss2LTOP {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based Zeiss REC files in different dialects.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Zeiss2LTOP(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Zeiss REC file into a KOO file for LTOP.
     *
     * @param eliminateDuplicates   eliminate duplicate points from KOO file
     * @param sortFileByNumber      sort KOO file by point number
     * @return converted KOO file
     */
    public ArrayList<String> convertZeiss2KOO(boolean eliminateDuplicates, boolean sortFileByNumber) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }

    /**
     * Converts a Zeiss REC file into a MES file for LTOP.
     *
     * @param useZenithDistance use zenith distance instead of height angle
     * @return converted MES file
     */
    public ArrayList<String> convertZeiss2MES(boolean useZenithDistance) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }

} // end of Zeiss2LTOP
