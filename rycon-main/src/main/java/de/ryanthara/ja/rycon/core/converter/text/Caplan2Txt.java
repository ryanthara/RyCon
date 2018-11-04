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
package de.ryanthara.ja.rycon.core.converter.text;

import de.ryanthara.ja.rycon.core.elements.CaplanBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files
 * from Caplan K program into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2Txt {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based Caplan K file.
     *
     * @param lines list with Caplan K formatted lines
     */
    public Caplan2Txt(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a Caplan K file to a text formatted file.
     *
     * @param separator         distinguish between tabulator or space as division sign
     * @param writeSimpleFormat writes a simple format (nr x y z or nr code x y z)
     * @param writeCommentLine  writes a comment line into the file
     * @param writeCodeColumn   writes a code column (nr code x y z attr)
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(String separator, boolean writeSimpleFormat, boolean writeCommentLine,
                                     boolean writeCodeColumn) {

        List<String> result = new ArrayList<>();

        if (writeCommentLine) {
            String commentLine = "";

            if (writeSimpleFormat) {
                commentLine = "nr" + separator + "x" + separator + "y" + separator + "z";
            } else if (writeCodeColumn) {
                commentLine = "nr" + separator + "code" + separator + "x" + separator + "y" + separator + "z" + separator + "attribute";
            }

            result.add(commentLine);
        }

        for (String line : lines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                String s = "";

                CaplanBlock caplanBlock = new CaplanBlock(line);

                if (caplanBlock.getNumber() != null) {
                    s = caplanBlock.getNumber();
                }

                if ((caplanBlock.getCode() != null) && !writeSimpleFormat & writeCodeColumn) {
                    s = s.concat(separator);
                    s = s.concat(caplanBlock.getCode());
                }

                if (caplanBlock.getEasting() != null) {
                    s = s.concat(separator);
                    s = s.concat(caplanBlock.getEasting());
                }

                if (caplanBlock.getNorthing() != null) {
                    s = s.concat(separator);
                    s = s.concat(caplanBlock.getNorthing());
                }

                if (caplanBlock.getHeight() != null) {
                    s = s.concat(separator);
                    s = s.concat(caplanBlock.getHeight());
                }

                if (caplanBlock.getAttributes().size() > 0 && !writeSimpleFormat && writeCodeColumn) {
                    for (String attribute : caplanBlock.getAttributes()) {
                        s = s.concat(separator);
                        s = s.concat(attribute);
                    }
                }

                result.add(s.trim());
            }
        }

        return List.copyOf(result);
    }

}
