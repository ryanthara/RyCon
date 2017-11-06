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

/**
 * This class implements basic operations for text based measurement and coordinate files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files.
 *
 * @author sebastian
 * @version 12
 * @since 1
 */
public class BaseToolsTxt {

    /**
     * Use white space (' ') as separator sign.
     */
    public static final String SEPARATOR_SPACE = " ";

    /**
     * Use the tabulator ('\t') as separator sign.
     */
    public static final String SEPARATOR_TAB = "\t";

    /**
     * Adds trailing zeroes to the maximum number of digits.
     *
     * @param s      string with missing trailing zeroes
     * @param number maximum number of digits
     */
    public static String addTrailingZeroes(final String s, final int number) {
        String zeroes = "";

        int pos = s.length() - s.lastIndexOf(".");

        for (int i = pos - 1; i < number; i++) {
            zeroes = zeroes + "0";
        }

        return s + zeroes;
    }

} // end of BaseToolsTxt
