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
package de.ryanthara.ja.rycon.i18n;

import de.ryanthara.ja.rycon.gui.preferences.PreferencesDialog;

/**
 * This enumeration is used for all the strings in the preference tabs of the {@link PreferencesDialog}.
 * <p>
 * Therefore the order of the enum is made by hand and follows the module order.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum Preferences implements ResourceKeys {

    // general tab
    generalTabTitle,
    generalTabToolTip,

    // path tab
    pathTabTitle,
    pathTabToolTip,

    // tidy up tab
    tidyUpTabTitle,
    tidyUpTabToolText,

    // converter tab
    converterTabTitle,
    converterTabToolText,
    converterTabGroupLTOP,
    converterTabGroupZeiss,

} // end of Preferences
