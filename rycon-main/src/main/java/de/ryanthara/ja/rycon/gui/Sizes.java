/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.i18n
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
package de.ryanthara.ja.rycon.gui;

/**
 * The <tt>Sizes</tt> enumeration holds all the default size values for the gui of <tt>RyCON</tt>.
 * <p>
 * This enumeration is used for encapsulating the data.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum Sizes {

    RyCON_GRID_WIDTH(325),
    RyCON_GRID_HEIGHT(135),
    RyCON_WIDGET_WIDTH(666),
    RyCON_WIDGET_HEIGHT(412);

    private int value;

    Sizes(int value) {
        this.value = value;
    }

    /**
     * Returns the default key size as integer value.
     *
     * @return default key size
     */
    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return Integer.toString(value);
    }

} // end of Sizes
