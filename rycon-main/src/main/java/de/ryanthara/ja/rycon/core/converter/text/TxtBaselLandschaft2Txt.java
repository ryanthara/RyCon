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

import java.util.ArrayList;

/**
 * This class provides functions to convert a txt formatted coordinate file from the geodata server
 * Basel Landschaft (Switzerland) into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Txt {

    private ArrayList<String> readStringLines = null;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public TxtBaselLandschaft2Txt(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a text file from the geodata server Basel Landschaft (Switzerland)
     * into a text formatted file (no code x y z).
     * <p>
     * This method can differ between LFP and HFP files, which has a given different structure.
     * With a parameter it is possible to distinguish between tabulator and space divided files.
     *
     * @param separator       distinguish between tabulator or space as division sign
     * @param writeCodeColumn use 'Versicherungsart' (LFP) as code column on second position
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertTXTBaselLandschaft2TXT(String separator, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        // remove comment line
        readStringLines.remove(0);

        for (String line : readStringLines) {
            String s;

            String[] lineSplit = line.trim().split("\\t", -1);

            // point number is in column 2
            s = lineSplit[1];
            s = s.concat(separator);

            switch (lineSplit.length) {
                case 5:     // HFP file
                    // easting (Y) is in column 3
                    s = s.concat(lineSplit[2]);
                    s = s.concat(separator);

                    // northing (X) is in column 4
                    s = s.concat(lineSplit[3]);
                    s = s.concat(separator);

                    // height (Z) is in column 5, and always valued (HFP file)
                    s = s.concat(lineSplit[4]);
                    s = s.concat(separator);

                    result.add(s.trim());
                    break;

                case 6:     // LFP file
                    // use 'Versicherungsart' as code. It is in column 3
                    if (writeCodeColumn) {
                        s = s.concat(lineSplit[2]);
                        s = s.concat(separator);
                    }

                    // easting (Y) is in column 4
                    s = s.concat(lineSplit[3]);
                    s = s.concat(separator);

                    // northing (X) is in column 5
                    s = s.concat(lineSplit[4]);
                    s = s.concat(separator);

                    // height (Z) is in column 6, and not always valued (LFP file)
                    if (lineSplit[5].equals("NULL")) {
                        s = s.concat("-9999");
                    } else {
                        s = s.concat(lineSplit[5]);
                    }

                    result.add(s.trim());
                    break;

                default:
                    System.err.println("Error in convertTXTBaselLandschaft2TXT: line length doesn't match 5 or 6 elements");
            }
        }
        return result;
    }

} // end of TxtBaselLandschaft2Txt
