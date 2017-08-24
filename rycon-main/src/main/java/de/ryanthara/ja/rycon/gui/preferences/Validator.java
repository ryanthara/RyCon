/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.preferences
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
package de.ryanthara.ja.rycon.gui.preferences;

/**
 * This is the abstract base class for the different <tt>ValidatorXXX</tt> classes of RyCON.
 * <p>
 * The generic parameter <tt>T</tt> holds the data type.
 * <p>
 * The main idea behind this way of preference handler was implemented by Fabian Prasser.
 * See {@link https://github.com/prasser/swtpreferences} for details.
 *
 * @param <T> data typ
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
interface Validator<T> {

    /**
     * Checks the validity of the generic parameter.
     *
     * @param param parameter to be checked
     *
     * @return validity of checked parameter
     */
    public abstract boolean isValid(final T param);

} // end of Validator
