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
package de.ryanthara.ja.rycon.converter.csv;

import java.util.ArrayList;

/**
 * Created by sebastian on 12.09.16.
 */
public class Zeiss2CSV {

    private ArrayList<String> readStringLines;

    public Zeiss2CSV(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a Zeiss REC file (and it's dialects R4, R5, R500 and M5) into a K format file.
     *
     * @param separator         used separator sign
     * @param writeCommentLine  option to write a comment line into the K file with basic information
     * @param writeCodeColumn   option to write a found code into the K file
     * @param useSimpleFormat   option to write a reduced K file which is compatible to ZF LaserControl
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertZeiss2CSV(String separator, boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        return null;
    }


} // Zeiss2CSV
