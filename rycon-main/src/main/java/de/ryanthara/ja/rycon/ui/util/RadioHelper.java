/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui
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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * This class implements different functionality for radio buttons in RyCON.
 * <p>
 * The access to the simple functions of this helper class is implemented with static access.
 *
 * @author sebastian
 * @version 2
 * @since 2
 */
public class RadioHelper {

    /**
     * Returns the number of a selected radio button from a given control group.
     * <p>
     * Pay attention to the fact, that the first button in the field has the number '0'.
     *
     * @param control {@code Control[]} object with radio buttons
     * @return number of the selected radio button
     */
    public static int getSelectedBtn(Control... control) {
        int number = -1;

        for (int i = 0; i < control.length; i++) {
            Control child = control[i];
            if (child instanceof Button) {
                Button button = (Button) child;
                if (button.getSelection()) {
                    number = i;
                }
            }
        }

        return number;
    }

    /**
     * Enables a certain radio button in the given control group.
     *
     * @param control Control with radio buttons
     * @param number  Button to enable
     */
    public static void selectBtn(Control[] control, int number) {
        for (int i = 0; i < control.length; i++) {
            Control child = control[i];
            if (child instanceof Button) {
                Button button = (Button) child;
                if (number == i) {
                    button.setSelection(true);
                } else {
                    button.setSelection(false);
                }
            }
        }
    }

}  // end of RadioHelper
