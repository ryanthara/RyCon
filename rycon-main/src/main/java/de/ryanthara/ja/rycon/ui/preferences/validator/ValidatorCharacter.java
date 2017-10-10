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

package de.ryanthara.ja.rycon.ui.preferences.validator;

/**
 * {@code ValidatorCharacter} is a validator which checks {@code Character} inputs for being valid.
 * <p>
 * Due to some reasons {@code Character} chars are represented by a {@link String}
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class ValidatorCharacter implements Validator<String> {

    /**
     * Checks if the given string is valid.
     *
     * @param s value to be checked
     *
     * @return true if is valid
     */
    @Override
    public boolean isValid(final String s) {
        return s != null && s.length() == 1;
    }

} // end of ValidatorCharacter
