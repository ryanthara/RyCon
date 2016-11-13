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

package de.ryanthara.ja.rycon.tools;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * NumberFormatter implements basic number format operations for string stored double values.
 *
 * @author sebastian
 * @version 3
 * @since 8
 */
public class NumberFormatter {

    /**
     * Fills a double value as string with a number of zeros to a defined last decimal place.
     * <p>
     * This method can be used for 1 till 5 last decimal digits. If a value greater than 5 is used,
     * the default value 4 is used.
     *
     * @param lastDecimalLength length of the last decimal place
     *
     * @return filled up string value
     */
    public static String fillDecimalPlace(String doubleAsString, int lastDecimalLength) {
        double d = Double.parseDouble(doubleAsString);

        // change the decimal separator sign to '.'
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');

        // prepare the format string by length
        String formatString = "#0.";

        for (int i = 0; i < lastDecimalLength; i++) {
            formatString = formatString.concat("0");
        }

        return new DecimalFormat(formatString, otherSymbols).format(d);
    }

} // end of NumberFormatter
