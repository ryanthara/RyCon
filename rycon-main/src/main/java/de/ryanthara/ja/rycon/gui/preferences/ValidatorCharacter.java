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
 * The <tt>ValidatorCharacter</tt> class checks a given String for being a char.
 * <p>
 * The main idea behind this way of preference handler was implemented by Fabian Prasser.
 * See {@link https://github.com/prasser/swtpreferences} for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
 class ValidatorCharacter implements Validator<String> {

    /**
     * Checks a string for being a valid character.
     *
     * @param s string to be checked
     * @return true if string is a valid char
     */
    @Override
    public boolean isValid(final String s) {
        return s != null && s.length() == 1;
    }

} // end of ValidatorCharacter
