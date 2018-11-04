/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.excel
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
package de.ryanthara.ja.rycon.core.converter.excel;

/**
 * Provides the format strings for double value formatting
 * with a defined count of digits for cell values.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public enum Format {

    DIGITS_3("#,##0.000"), DIGITS_4("#,##0.0000");

    private final String formatString;

    /**
     * Constructs the format with the format string parameter.
     *
     * @param formatString the format string
     */
    Format(String formatString) {
        this.formatString = formatString;
    }

    /**
     * Returns the format string for the count of digits.
     *
     * @return format string
     */
    public String getString() {
        return formatString;
    }

}
