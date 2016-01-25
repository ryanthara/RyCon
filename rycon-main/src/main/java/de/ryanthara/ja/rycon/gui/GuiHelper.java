/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.data
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

import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.File;

/**
 * This class brings gui helper functionality to RyCON.
 * <p>
 * Some gui elements and tools are used in different classes. These methods are combined as little helpers here.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 7
 */
public class GuiHelper {

    /**
     * Default constructor.
     */
    public GuiHelper() {}


    /**
     * Shows a directory dialog which is used in different classes of RyCON.
     * <p>
     * The filter path is checked if there is a stored value in the preferences file of RyCON.
     *
     * @param innerShell shell object
     * @param textField text field object
     * @param title title of the directory dialog
     * @param message message of the directory dialog
     * @param checkedFilterPath filter path of the director dialog
     * @since 7
     */
    public static void showAdvancedDirectoryDialog(Shell innerShell, Text textField, String title, String message, String checkedFilterPath) {
        DirectoryDialog directoryDialog = new DirectoryDialog(innerShell);
        directoryDialog.setText(title);
        directoryDialog.setMessage(message);
        directoryDialog.setFilterPath(PreferenceHandler.checkUserPrefPathExist(checkedFilterPath));

        String path = directoryDialog.open();

        if (path != null) {
            File checkDirDestination = new File(path);
            if (!checkDirDestination.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirDestinationNotExistWarning());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                textField.setText(path);
            }
        }
    }

} // end of GuiHelper
