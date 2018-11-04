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

package de.ryanthara.ja.rycon.util;

import de.ryanthara.ja.rycon.core.converter.Separator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

/**
 * Provides basic number formatting functions for as string stored double values.
 *
 * @author sebastian
 * @version 3
 * @since 8
 */
public final class NumberFormatter {

    /**
     * NumberFormatter is non-instantiable.
     */
    private NumberFormatter() {
        throw new AssertionError();
    }

    /**
     * Formats the number stored as a character string with a defined number of decimal places.
     *
     * @param doubleAsString    number as character string
     * @param lastDecimalLength number of decimal places
     * @return filled up string value
     * @throws NullPointerException     will be thrown if input is null
     * @throws IllegalArgumentException will be thrown if length is smaller than 0
     */
    public static String fillDecimalPlaces(String doubleAsString, int lastDecimalLength) {
        Objects.requireNonNull(doubleAsString, "doubleAsString must not be null");

        if (lastDecimalLength < 0) {
            throw new IllegalArgumentException("last decimal length must be zero or larger!");
        }

        // change the decimal separator sign to '.'
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator(Separator.DOT.getSign().charAt(0));

        // prepare the format string by length
        String formatString = "#0.";

        for (int i = 0; i < lastDecimalLength; i++) {
            formatString = formatString.concat("0");
        }

        return new DecimalFormat(formatString, otherSymbols).format(StringUtils.parseDoubleValue(doubleAsString));
    }

}
