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
 * NumberHelper implements basic helper operations on numbers.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements, documantation updates</li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 8
 */
class NumberHelper {

    /**
     * Fill a double value as string with a number of zeros to a defined last decimal place.
     *
     * @param lastDecimalLength length of the last decimal place
     * @return filled up string value
     */
    static String fillDecimalPlace(String doubleAsString, int lastDecimalLength) {
        double d = Double.parseDouble(doubleAsString);

        // change the decimal separator sign to '.'
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');

        DecimalFormat df = new DecimalFormat("");

        switch (lastDecimalLength) {
            case 1:
                df = new DecimalFormat("#0.0", otherSymbols);
                break;

            case 2:
                df = new DecimalFormat("#0.00", otherSymbols);
                break;

            case 3:
                df = new DecimalFormat("#0.000", otherSymbols);
                break;

            case 4:
                df = new DecimalFormat("#0.0000", otherSymbols);
                break;

            case 5:
                df = new DecimalFormat( "#0.00000", otherSymbols);
                break;
        }

        return df.format(d);
    }

} // end of NumberHelper
