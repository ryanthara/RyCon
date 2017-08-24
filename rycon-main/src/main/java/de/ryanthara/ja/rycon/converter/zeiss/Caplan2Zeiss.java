/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.converter.zeiss
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
package de.ryanthara.ja.rycon.converter.zeiss;

import de.ryanthara.ja.rycon.elements.CaplanBlock;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert a Caplan K formatted coordinate file
 * into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2Zeiss {

    private final ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with the read Caplan K file {@link ArrayList} string as parameter.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in Caplan K format
     */
    public Caplan2Zeiss(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Caplan K formatted coordinate file into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     *
     * @return string lines of the target file
     */
    public ArrayList<String> convertK2REC(ZeissDialect dialect) {
        ArrayList<String> result = new ArrayList<>();

        int lineNumber = 0;

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                String number = "", easting = "", northing = "", height = "", code = "";

                lineNumber = lineNumber + 1;

                CaplanBlock caplanBlock = new CaplanBlock(line);

                if (caplanBlock.getNumber() != null) {
                    number = caplanBlock.getNumber();
                }

                if (caplanBlock.getEasting() != null) {
                    easting = caplanBlock.getEasting();
                }

                if (caplanBlock.getNorthing() != null) {
                    northing = caplanBlock.getNorthing();
                }

                if (caplanBlock.getHeight() != null) {
                    height = caplanBlock.getHeight();
                }

                if (caplanBlock.getCode() != null) {
                    code = caplanBlock.getCode();
                }

                result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
            }
        }

        return result;
    }

} // end of Caplan2Zeiss
