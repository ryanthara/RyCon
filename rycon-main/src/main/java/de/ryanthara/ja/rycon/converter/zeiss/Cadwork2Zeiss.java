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
 * This class provides functions to convert coordinate files from Cadwork CAD program into Zeiss REC files with
 * it's dialects (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2Zeiss {

    private final ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based text files from Cadwork CAD program in node.dat file format.
     *
     * @param readStringLines {@code ArrayList<String>} with read lines from node.dat file
     */
    public Cadwork2Zeiss(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a cadwork node.dat file into Leica GSI8 or GS16 format.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16        Output file is GSI16 format
     * @param useCodeColumn  Use the code column from node.dat
     * @param useZeroHeights Use heights with zero (0.000) values
     *
     * @return converted {@code ArrayList<String>} with lines of GSI8 or GSI16 format
     */


    /**
     * Converts a coordinate file from Cadwork (node.dat) into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public ArrayList<String> convertCadwork2REC(String dialect) {
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

} // end of Cadwork2Zeiss
