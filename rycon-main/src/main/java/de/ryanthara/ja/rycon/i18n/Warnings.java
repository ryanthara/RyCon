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
 * The <tt>Warnings</tt> enumeration holds all the texts for the warning messages of <tt>RyCON</tt>.
 * <p>
 * This enumeration is used for encapsulating the data. The interface {@link ResourceKeys}
 * is used to access different enumerations in the class {@link ResourceBundleUtils}.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum Warnings implements ResourceKeys {

    adminDirExists,
    bigDataDirExists,
    emptyTextField,
    fileExists,
    noControlPointsLTOP,
    projectDirExists

} // end of Warnings
