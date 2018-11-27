/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.util
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
package de.ryanthara.ja.rycon.ui.util;

import org.eclipse.swt.widgets.Combo;

/**
 * Helper class containing different functionality for {@link org.eclipse.swt.custom.CCombo} list choosers.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public class ComboHelper {

    /**
     * ComboHelper is non-instantiable.
     */
    private ComboHelper() {
        throw new AssertionError();
    }

    /**
     * Returns the selected item from the list.
     *
     * @param list list with a selected item
     * @return selected item
     */
    public static int getSelectedItem(Combo list) {
        return list.getSelectionIndex();
    }

}
