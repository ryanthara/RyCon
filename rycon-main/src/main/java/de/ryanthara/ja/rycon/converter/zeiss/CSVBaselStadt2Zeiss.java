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
import java.util.List;

/**
 * Created by sebastian on 15.09.16.
 */
public class CSVBaselStadt2Zeiss {

    private List<String[]> readCSVLines = null;

    /**
     * Class constructor for read line based CSV files from the geodata server Basel Stadt (Switzerland).
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CSVBaselStadt2Zeiss(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a comma separated coordinate file from the geodata server Basel Stadt (Switzerland)
     * into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return converted Zeiss REC file as {@code ArrayList<String>}
     */
    public ArrayList<String> convertCSVBaselStadt2REC(String dialect) {
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
} // end of CSVBaselStadt2Zeiss
