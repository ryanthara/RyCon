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

import java.util.ArrayList;

/**
 * Created by sebastian on 15.09.16.
 */
public class TXT2Zeiss {

    private final ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TXT2Zeiss(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text formatted measurement or coordinate based file into an Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public ArrayList<String> convertTXT2REC(String dialect) {
        ArrayList<String> result = null;

        switch (dialect) {
            case "R4":
                break;
            case "R5":
                break;
            case "REC500":
                break;
            case "M5":
                break;
        }

        return result;
    }

} // end of TXT2Zeiss
