/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter
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
package de.ryanthara.ja.rycon.core.converter;

import java.util.ArrayList;

/**
 * This describes the minimum function of a converter for the levelling to cad widget of {@code RyCON}.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public abstract class Converter {

    /**
     * The separator sign which is used for the intermediate step within the 2Asc conversion
     * to establish point numbers with whitespace characters.
     */
    public static final String SEPARATOR = "'°_°'";

    /**
     * Does the conversion and returns the result as {@code ArrayList<String>}.
     *
     * @return the converted lines
     */
    public abstract ArrayList<String> convert();

} // end of Converter
