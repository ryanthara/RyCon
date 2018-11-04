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

import java.util.List;

/**
 * This describes the minimum function of a converter class for the
 * {@link de.ryanthara.ja.rycon.ui.widgets.ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public abstract class Converter {

    /**
     * Use a special separator sign for an intermediate step within the '*2Asc'
     * conversion to establish point numbers with whitespace characters.
     */
    public static final String SEPARATOR = "<(-.-)>";

    /**
     * Does the conversion and returns the result as {@code List<String>}.
     *
     * @return the converted lines
     */
    public abstract List<String> convert();

}
