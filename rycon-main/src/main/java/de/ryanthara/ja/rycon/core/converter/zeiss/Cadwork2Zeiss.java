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

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files from Cadwork
 * CAD program into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2Zeiss {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text file from Cadwork CAD program.
     *
     * @param lines list with read node.dat lines
     */
    public Cadwork2Zeiss(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public List<String> convert(ZeissDialect dialect) {
        List<String> result = new ArrayList<>();

        removeHeadLines();

        int lineNumber = 0;

        String number, code, easting, northing, height;

        for (String line : lines) {
            lineNumber = lineNumber + 1;
            String[] values = line.trim().split("\\s+", -1);

            number = values[5];
            code = values[4];
            easting = values[1].substring(0, values[1].lastIndexOf('.') + 4);
            northing = values[2].substring(0, values[2].lastIndexOf('.') + 4);
            height = values[3].substring(0, values[3].lastIndexOf('.') + 5);

            result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
        }

        return List.copyOf(result);
    }

    private void removeHeadLines() {
        lines.subList(0, 3).clear();
    }

}
