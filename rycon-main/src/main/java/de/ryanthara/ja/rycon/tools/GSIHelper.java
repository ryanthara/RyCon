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

/**
 * This class defines a special helper object for smart handling and better sorting.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
class GSIHelper {

    private final int code;
    private final String line;

    /**
     * Simple definition with the code as int and a string for the complete {@code GSIBlocks}.
     *
     * @param code code of the {@code GSIBlocks}
     * @param line {@code String} of the {@code GSIBlocks}
     */
    GSIHelper(int code, String line) {
        this.code = code;
        this.line = line;
    }

    /**
     * Return the code as Integer value.
     *
     * @return code as Integer value
     */
    public int getCode() {
        return code;
    }

    /**
     * Return the line as String.
     *
     * @return line as String
     */
    public String getLine() {
        return line;
    }


} // end of GSIHelper
