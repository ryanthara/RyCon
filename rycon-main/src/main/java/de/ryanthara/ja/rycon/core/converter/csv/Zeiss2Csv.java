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

import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.core.elements.ZeissBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert measurement and coordinate files from Zeiss REC format
 * and it's dialects (R4, R5, REC500 and M5) into comma separated values (CSV) files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2Csv {

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
    public Zeiss2Csv(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a Zeiss REC file (and it's dialects R4, R5, R500 and M5) into a K format file.
     *
     * @param separator used separator sign
     * @return converted K file as {@code List<String>}
     */
    public List<String> convert(String separator) {
        List<String> result = new ArrayList<>();

        int readLineCounter = 0;

        for (String line : lines) {
            // skip empty lines
            if (line.trim().length() > 0) {
                ZeissDecoder decoder = new ZeissDecoder();

                readLineCounter = readLineCounter + 1;

                for (ZeissBlock zeissBlock : decoder.getZeissBlocks()) {
                    result.add(zeissBlock.getValue() + separator);
                }
            }
        }

        return List.copyOf(result);
    }

}
