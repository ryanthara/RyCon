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
 * {@code ValidatorDouble} is a validator which checks {@code Double} values for being valid.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class ValidatorDouble implements Validator<Double> {

    private final double max;
    private final double min;

    /**
     * Creates a new instance with upper and lower bound as parameters.
     *
     * @param min minimum value
     * @param max maximum value
     */
    public ValidatorDouble(final double min, final double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Checks if the given value is valid.
     *
     * @param d value to be checked
     *
     * @return true if is valid
     */
    @Override
    public boolean isValid(final Double d) {
        return (d >= min) && (d <= max);
    }

} // end of ValidatorDouble
