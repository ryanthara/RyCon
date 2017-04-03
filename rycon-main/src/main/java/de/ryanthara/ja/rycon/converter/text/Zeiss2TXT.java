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
package de.ryanthara.ja.rycon.converter.text;

import de.ryanthara.ja.rycon.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.elements.ZeissBlock;

import java.util.ArrayList;

/**
 * This class provides functions to convert measurement files from Zeiss REC format
 * and it's dialects (R4, R5, REC500and M5) into text formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2TXT {

    private ArrayList<String> readStringLines;

    /**
     * Constructs the {@link Zeiss2TXT} with a bunch of parameters.
     * <p>
     * The differentiation of the content is done by the called method.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Zeiss2TXT(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }


    /**
     * Converts a Zeiss REC file (R4, R5, M5 or REC500) into a text formatted file.
     * <p>
     * This method can differ between different Zeiss REC dialects because of the
     * different structure and line length.
     *
     * @param separator used separator sign
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertZeiss2TXT(String separator) {
        ArrayList<String> result = new ArrayList<>();

        int readLineCounter = 0;

        for (String line : readStringLines) {

            // skip empty lines
            if (line.trim().length() > 0) {
                ZeissDecoder decoder = new ZeissDecoder();

                readLineCounter = readLineCounter + 1;

                for (ZeissBlock zeissBlock : decoder.getZeissBlocks()) {
                    result.add(zeissBlock.getValue() + separator);
                }
            }
        }

        return result;
    }

} // end of Zeiss2TXT
