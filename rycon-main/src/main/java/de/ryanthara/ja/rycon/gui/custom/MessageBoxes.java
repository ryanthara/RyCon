/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.custom
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
package de.ryanthara.ja.rycon.gui.custom;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * This class provides simple {@link MessageBox} with static access for RyCON's gui elements.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class MessageBoxes {

    /**
     * Shows a swt {@link MessageBox} and returns an integer value as indicator for being shown.
     *
     * @param innerShell the inner shell object
     * @param icon       the icon of the message box
     * @param text       the headline text of the message box
     * @param message    the message of the message box
     *
     * @return int value
     */
    public static int showMessageBox(final Shell innerShell, final int icon, final String text, final String message) {
        MessageBox messageBox = new MessageBox(innerShell, icon);

        messageBox.setText(text);
        messageBox.setMessage(message);

        return messageBox.open();
    }

} // end of MessageBoxes
