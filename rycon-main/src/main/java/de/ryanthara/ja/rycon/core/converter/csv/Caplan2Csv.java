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

import de.ryanthara.ja.rycon.core.elements.CaplanBlock;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert coordinate files from Caplan K format
 * into a comma separated values file (csv format).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2Csv {

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with the reader Caplan K file {@link ArrayList} string as parameter.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in Caplan K format
     */
    public Caplan2Csv(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Caplan K file to a comma separated values (CSV) formatted file.
     *
     * @param separator         used separator sign
     * @param writeSimpleFormat option to writer a reduced K file which is compatible to Z+F LaserControl
     * @param writeCommentLine  option to writer a comment line into the K file with basic information
     * @param writeCodeColumn   option to writer a found code into the K file
     *
     * @return converted K file as {@code ArrayList<String>}
     */
    public ArrayList<String> convertK2CSV(String separator, boolean writeSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            String commentLine = "";

            if (writeSimpleFormat) {
                commentLine = "nr" + separator + "x" + separator + "y" + separator + "z";
            } else if (writeCodeColumn) {
                commentLine = "nr" + separator + "code" + separator + "x" + separator + "y" + separator + "z" + separator + "attribute";
            }

            result.add(commentLine);
        }

        for (String line : readStringLines) {
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

        return result;

    }

} // end of Caplan2Csv
