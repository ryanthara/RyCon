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
package de.ryanthara.ja.rycon.converter.caplan;

import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.I18N;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class provides basic and helper functions that are used for converting different
 * file formats into text based Caplan K formatted coordinate or measurement files.
 * <p>
 * The CAPLAN K file format is a line based and column orientated file format developed
 * by Cremer Programmentwicklung GmbH to store coordinates from different formats.
 * <p>
 * Example K file:
 * <p>
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8
 * !-------------------------------------------------------------------------------
 * ! The following data was created by RyCON Build xxx on 2016-09-06.
 * !-------------------------------------------------------------------------------
 * GB1      7  2612259.5681  1256789.1990    256.90815 |10
 * GB2      7  2612259.5681  1256789.1990    256.90815 |10
 * 1003     7  2612259.5681  1256789.1990    256.90815 |10|Att1|Att2
 * 1062     7  2612259.5681  1256789.1990    256.90815 |10
 * TF 1067G 4  2612259.5681  1256789.1990    256.90815 |10
 * NG 2156U 3  2612259.5681  1256789.1990      0.00000 |10
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
class BaseToolsCaplanK {

    // prevent wrong output with empty strings of defined length
    /**
     * Default string with defined length for the valency value.
     */
    public static final String valency = "  ";

    /**
     * Default string with defined length for the easting coordinate.
     */
    public static final String easting = "              ";

    /**
     * Default string with defined length for the northing coordinate.
     */
    public static final String northing = "              ";

    /**
     * Default string with defined length for the height coordinate.
     */
    public static final String height = "             ";

    /**
     * Default string with defined length for the free space sign.
     */
    static final String freeSpace = " ";

    /**
     * Default string with defined length for the object type.
     */
    static final String objectTyp = "";

    /**
     * Eliminates not allowed chars like '*', ',' and ';' from the point number string.
     *
     * @param number number to be checked and cleaned
     */
    static String cleanPointNumberString(String number) {
        String s = number.replaceAll("\\*", "#");
        s = s.replaceAll(",", ".");
        s = s.replaceAll(";", ":");
        return String.format("%16s", s);
    }

    /**
     * Prepares an output line as a string with a defined format and defined values.
     * <p>
     * All input string parameters of this method are automatically transformed to the
     * right length with cut off oversize strings.
     *
     * @param useSimpleFormat write simple formatted output file
     * @param number          point number
     * @param valency         valency value
     * @param easting         easting coordinate
     * @param northing        northing coordinate
     * @param height          height coordinate
     * @param freeSpace       free space delimiter
     * @param objectTyp       object typ
     *
     * @return prepared output string
     */
    static StringBuilder prepareCaplanLine(boolean useSimpleFormat, String number, String valency, String easting,
                                           String northing, String height, String freeSpace, String objectTyp) {
        StringBuilder stringBuilder = new StringBuilder();
        // TODO use the right string format methods here instead of the calling methods if possible

        stringBuilder.append(number);
        stringBuilder.append(valency);
        stringBuilder.append(easting);
        stringBuilder.append(northing);
        stringBuilder.append(height);

        if (!useSimpleFormat) {
            if (!objectTyp.equals("")) {
                stringBuilder.append(freeSpace);
                stringBuilder.append(objectTyp);
            }
        }

        return stringBuilder;
    }

    /**
     * Writes the comment line into a given {@code ArrayList<String>} object.
     *
     * @param result ArrayList<String> to write in
     */
    public static void writeCommentLine(ArrayList<String> result) {
        String commentLine1 = "!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8";
        String commentLine2 = "!-------------------------------------------------------------------------------";

        result.add(commentLine1);
        result.add(commentLine2);

        // insert RyCON version, date and time
        Date d = new Date();
        DateFormat df;
        df = DateFormat.getDateTimeInstance(/* dateStyle */ DateFormat.LONG,
                                            /* timeStyle */ DateFormat.MEDIUM);
        result.add(String.format(I18N.getStrCaplanCommentLine(), Version.getVersion(), df.format(d)));
        result.add(commentLine2);
    }

} // end of BaseToolsCaplanK
