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
import org.eclipse.swt.widgets.*;

import java.io.File;

/**
 * This class brings gui helper functionality to RyCON.
 * <p>
 * Some gui elements and tools are used in different classes. These methods are combined as little helpers here.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: improvements and new functions </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 7
 */
public class GuiHelper {

    /**
     * Shows a directory dialog which is used in different classes of RyCON.
     * <p>
     * The filter path is checked if there is a stored value in the preferences file of RyCON.
     *
     * @param innerShell shell object
     * @param textField text field object
     * @param title title of the directory dialog
     * @param message message of the directory dialog
     * @param checkedFilterPath filter path of the directory dialog
     * @since 2
     */
    static void showAdvancedDirectoryDialog(Shell innerShell, Text textField, String title, String message, String checkedFilterPath) {
        DirectoryDialog directoryDialog = new DirectoryDialog(innerShell);
        directoryDialog.setText(title);
        directoryDialog.setMessage(message);
        directoryDialog.setFilterPath(PreferenceHandler.checkUserPrefPathExist(checkedFilterPath));

        String path = directoryDialog.open();

        if (path != null) {
            File checkDirDestination = new File(path);
            if (!checkDirDestination.exists()) {
                showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirNotFound());
            } else {
                textField.setText(path);
            }
        }
    }

    /**
     * Shows a file dialog which is used in different classes of RyCON.
     *
     * @param innerShell shell object
     * @param multiSelection allows multi selection
     * @param filterPath filter path of the file dialog
     * @param text title of the file dialog
     * @param filterExtensions allowed extensions
     * @param filterNames description of allowed extensions
     * @param source source text field
     * @param destination destination text field
     * @return chosen files as String array
     * @since 2
     */
    static File[] showAdvancedFileDialog(Shell innerShell, int multiSelection, String filterPath, String text,
                                         String[] filterExtensions, String[] filterNames, Text source, Text destination) {

        File[] files2read = null;

        FileDialog fileDialog = new FileDialog(innerShell, multiSelection);
        fileDialog.setFilterPath(filterPath);
        fileDialog.setText(text);
        fileDialog.setFilterExtensions(filterExtensions);
        fileDialog.setFilterNames(filterNames);

        String firstFile = fileDialog.open();

        if (firstFile != null) {
            String[] files = fileDialog.getFileNames();

            files2read = new File[files.length];

            // hack for displaying file names without path in text field
            String concatString = "";

            String workingDir = fileDialog.getFilterPath();

            //for (String element : files) {
            for (int i = 0; i < files.length; i++) {
                concatString = concatString.concat(files[i]);
                concatString = concatString.concat(" ");

                files2read[i] = new File(workingDir + File.separator + files[i]);
            }

            destination.setText(fileDialog.getFilterPath());
            source.setText(concatString);
        }

        return files2read;

    }

    /**
     * Shows a swt MessageBox and returns an Integer value as indicator for being shown.
     * <p>
     * The boolean is used for error indication in different classes.
     * @param innerShell the inner shell object
     * @param icon the icon of the message box
     * @param text the headline text of the message box
     * @param message the message of the message box
     * @return int value
     */
    public static int showMessageBox(Shell innerShell, int icon, String text, String message) {
        MessageBox messageBox = new MessageBox(innerShell, icon);
        messageBox.setText(text);
        messageBox.setMessage(message);
        return messageBox.open();
    }

} // end of GuiHelper
