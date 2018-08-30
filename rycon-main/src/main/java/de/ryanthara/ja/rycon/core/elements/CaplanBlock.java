/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.elements
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
package de.ryanthara.ja.rycon.core.elements;

import java.util.ArrayList;

/**
 * Instances of this class represents an object to store and handle the values of a Caplan K file line.
 * <p>
 * The Caplan K file format was developed by Cremer Programmentwicklung GmbH to storage coordinate files
 * in with different coordinate systems and reference frames.
 * <p>
 * Example K file:
 * <p>
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8
 * !-------------------------------------------------------------------------------
 * ! The following data was created by RyCON Build xxx on YYYY-MM-DD.
 * !-------------------------------------------------------------------------------
 * GB1            7  2612259.5681  1256789.1990    256.90815 |10
 * GB2            7  2612259.5681  1256789.1990    256.90815 |10
 * 1003           7  2612259.5681  1256789.1990    256.90815 |10|Att1|Att2
 * 1062           7  2612259.5681  1256789.1990    256.90815 |10
 * TF 1067G       4  2612259.5681  1256789.1990    256.90815 |10
 * NG 2156U       3  2612259.5681  1256789.1990      0.00000 |10
 *
 * @author sebastian
 * @version 5
 * @since 8
 */
public class CaplanBlock {

    private final String line;
    private final ArrayList<String> attributes;
    private final boolean isConverted;
    private int valency = -1;
    private String number, easting, northing, height, code;

    /**
     * Constructs a {@link CaplanBlock} from a given string line which is set as parameter.
     * <p>
     * The core.core.transformation of the values is initialized from here.
     *
     * @param line Caplan K file string line
     */
    public CaplanBlock(String line) {
        this.line = line;

        attributes = new ArrayList<>();
        number = null;
        easting = null;
        northing = null;
        height = null;
        code = null;

        isConverted = lineTransformation();
    }

    /**
     * Returns the attributes as {@link ArrayList}.
     * <p>
     * Due to some reasons the first attribute is stored in the code field!
     *
     * @return the attributes
     */
    public ArrayList<String> getAttributes() {
        return attributes;
    }

    /**
     * Returns the code (first attribute).
     *
     * @return the code (first attribute)
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the easting coordinate.
     *
     * @return easting
     */
    public String getEasting() {
        return easting;
    }

    /**
     * Returns the northing coordinate.
     *
     * @return northing
     */
    public String getHeight() {
        return height;
    }

    /**
     * Returns the height coordinate.
     *
     * @return the height
     */
    public String getNorthing() {
        return northing;
    }

    /**
     * Returns the number of the {@link CaplanBlock}.
     *
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Returns the valency indicator.
     *
     * @return the valency
     */
    public int getValency() {
        return valency;
    }

    /**
     * Returns true if the conversion was successful.
     *
     * @return conversion success
     */
    public boolean isConverted() {
        return isConverted;
    }

    private boolean lineTransformation() {
        boolean success = false;
        int val = 0;

        // comment lines starting with '!' are ignored
        if (!line.startsWith("!")) {
            if (line.length() >= 16) {
                String s = line.substring(0, 16).trim();

                // point number (no '*', ',' and ';'), column 1 - 16
                if (!(s.contains("*") || s.contains(",") || s.contains(";"))) {
                    number = s;
                } else {
                    // forces a 'false' return value
                    val = val + 10;
                }

                // valency, column 18
                if (line.length() >= 18) {
                    String valencyString = line.substring(18, 18);
                    if (!valencyString.isEmpty()) {
                        valency = Integer.parseInt(valencyString);

                        if (valency < 0 | valency > 7) {
                            valency = -1;
                        }
                    }
                }

                // easting E, column 19-32
                if (line.length() >= 32) {
                    easting = line.substring(20, 32).trim();
                    val = val + 1;
                }

                // northing N, column 33-46
                if (line.length() >= 46) {
                    northing = line.substring(34, 46).trim();
                    val = val + 2;
                }
                // height H, column 47-59
                if (line.length() >= 59) {
                    height = line.substring(48, 59).trim();
                    val = val + 4;
                }

                // code and attributes, column 62...
                if (line.length() >= 62) {
                    String[] lineSplit = line.substring(61).trim().split("\\|+");

                    // code is the same as object type, column 62...
                    code = lineSplit[0].trim();
                    success = true;

                    for (int i = 1; i < lineSplit.length; i++) {
                        String attr = lineSplit[i].trim();
                        attributes.add(attr);
                        success = true;
                    }
                }

                // returns converting success and found valency
                if (val == 3 || val == 4 || val == 7) {
                    valency = val;
                    success = true;
                }
            }
        }

        return success;
    }

} // end of CaplanBlock
