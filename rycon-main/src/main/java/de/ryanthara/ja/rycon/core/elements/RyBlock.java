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
package de.ryanthara.ja.rycon.core.elements;

/**
 * Instances of this class defines a special helper object for smart handling and better sorting
 * of strings by a given integer value.
 * <p>
 * This is used for sorting {@link GsiBlock} by number or text lines by code.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class RyBlock {

    private final int number;
    private final String string;

    /**
     * Constructs a new instance of this class given a number as int and a string for the complete block.
     *
     * @param number number as integer value
     * @param string string value (e.g. line, or block)
     */
    public RyBlock(int number, String string) {
        this.number = number;
        this.string = string;
    }

    /**
     * Returns the number as integer value.
     *
     * @return number as integer value
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the string as string.
     *
     * @return string as string
     */
    public String getString() {
        return string;
    }

    /**
     * Returns the number and the string as combined String with space char between.
     *
     * @return number and block as string
     */
    public String toString() {
        return number + " " + string;
    }

} // end of RyBlock
