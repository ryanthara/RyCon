/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.util
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
package de.ryanthara.ja.rycon.ui.util;

/**
 * This class provides static helper methods for simple string operations.
 * <p>
 * These are functions like adding whitespaces or zeros to strings with a defined length.
 */
public class StringHelper {

    /**
     * Fills the given string with whitespaces up to the given length.
     *
     * @param length the length
     * @param input  the input string
     *
     * @return with whitespace filled string of defined length
     */
    public static String fillWithSpaces(int length, String input) {
        String format = "%" + length + "." + length + "s";
        return String.format(format, input);
    }

    /**
     * Fills the given string with '0' up to the given length.
     *
     * @param length the length
     * @param input  the input string
     *
     * @return with whitespace filled string of defined length
     */
    public static String fillWithZeros(int length, String input) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length - input.length(); i++) {
            builder.append("0");
        }

        return builder.append(input).toString();
    }

} // end of StringHelper
