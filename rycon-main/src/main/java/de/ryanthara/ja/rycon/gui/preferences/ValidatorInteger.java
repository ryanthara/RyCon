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
 * The <tt>ValidatorInteger</tt> class checks a given Integer for being valid.
 * <p>
 * The main idea behind this way of preference handler was implemented by Fabian Prasser.
 * See {@link https://github.com/prasser/swtpreferences} for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
 class ValidatorInteger implements Validator<Integer> {

    /**
     * The maximum border.
     */
    private final int max;

    /**
     * The minimum border.
     */
    private final int min;

    /**
     * Creates a new instance.
     *
     * @param min the minimum border
     * @param max the maximum border
     */
    ValidatorInteger(final int min, final int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Checks if an integer value is between min and max.
     * <p>
     * The border values are included.
     *
     * @param i integer to be checked
     *
     * @return true if integer is between min and max
     */
    @Override
    public boolean isValid(final Integer i) {
        return (i >= min) && (i <= max);
    }

} // end of ValidatorInteger
