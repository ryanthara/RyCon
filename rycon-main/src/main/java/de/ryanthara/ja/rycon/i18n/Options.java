/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.i18n;

/**
 * The {@code Options} enumeration holds all the option label strings of <tt>RyCON</tt>.
 * <p>
 * This enumeration is used for encapsulating the data. The interface {@link ResourceKeys}
 * is used to access different enumerations in the class {@link ResourceBundleUtils}.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum  Options implements ResourceKeys {

    converter_CadworkSource,
    converter_CaplanKTarget,
    converter_CsvSource,
    converter_CsvTarget,
    converter_GsiTarget,
    converter_LtopTarget,
    converter_TxtSource,
    converter_TxtTarget,
    general,

} // end of Options
