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
package de.ryanthara.ja.rycon.core.converter.ltop;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert measurement and coordinate files from Zeiss
 * REC format and it's dialects (R4, R5, REC500 and M5) into KOO and MES files for LTOP.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
// TODO implement methods
public class Zeiss2Ltop {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based
     * text files in the Zeiss REC format and it's dialects.
     *
     * <p>
     * The differentiation of the content is done by the called
     * method and it's content analyze functionality.
     *
     * @param lines list with Zeiss REC format lines
     */
    public Zeiss2Ltop(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a Zeiss REC file (R4, R5, M5 or REC500) into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate points from KOO file
     * @param sortFileByNumber    sort KOO file by point number
     * @return converted KOO file
     */
    public List<String> convertZeiss2Koo(boolean eliminateDuplicates, boolean sortFileByNumber) {
        List<String> result = new ArrayList<>();

        return List.copyOf(result);
    }

    /**
     * Converts a Zeiss REC file into a MES file for LTOP.
     *
     * @param useZenithDistance use zenith distance instead of height angle
     * @return converted MES file
     */
    public List<String> convertZeiss2Mes(boolean useZenithDistance) {
        List<String> result = new ArrayList<>();

        return List.copyOf(result);
    }

}
