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
 * The <tt>ValidatorDouble</tt> class checks a given Double for being valid.
 * <p>
 * The main idea behind this way of preference handler was implemented by Fabian Prasser.
 * See {@link https://github.com/prasser/swtpreferences} for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
 class ValidatorDouble implements Validator<Double> {

    /**
     * The maximum border.
     */
    private final double max;

    /**
     * The minimum border.
     */
    private final double min;

    /**
     * Creates a new instance.
     *
     * @param min the minimum border
     * @param max the maximum border
     */
    ValidatorDouble(final double min, final double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Checks if a double value is between minimum and maximum.
     * <p>
     * The border values are included.
     *
     * @param d double to be checked
     *
     * @return true if double is between minimum and maximum
     */
    @Override
    public boolean isValid(final Double d) {
        return (d >= min) && (d <= max);
    }

} // end of ValidatorDouble
