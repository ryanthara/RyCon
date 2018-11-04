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
package de.ryanthara.ja.rycon.core.converter.zeiss;

import de.ryanthara.ja.rycon.core.elements.CaplanBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files from Caplan K
 * program into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2Zeiss {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based Caplan K file.
     *
     * @param lines list with Caplan K formatted lines
     */
    public Caplan2Zeiss(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a Caplan K formatted coordinate file into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public List<String> convert(ZeissDialect dialect) {
        List<String> result = new ArrayList<>();

        int lineNumber = 0;

        for (String line : lines) {
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

        return List.copyOf(result);
    }

}
