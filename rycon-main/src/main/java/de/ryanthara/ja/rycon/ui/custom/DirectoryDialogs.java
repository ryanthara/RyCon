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
package de.ryanthara.ja.rycon.ui.custom;

import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.nio.file.Files;
import java.nio.file.Paths;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.ERRORS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;

/**
 * This class implements a simple static access to swt {@link DirectoryDialog} and it's functionality to RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class DirectoryDialogs {

    private static void handlePath(Shell innerShell, Text textField, String pathAsString) {
        if ((pathAsString != null) && (Files.exists(Paths.get(pathAsString)))) {
            textField.setText(pathAsString);
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.directoryNotFound));
        }
    }

    /**
     * Shows a {@link DirectoryDialog} which is used in different widgets of RyCON's ui.
     * <p>
     * The filter path is checked against a stored value in the pref file of RyCON.
     *
     * @param innerShell        shell object
     * @param textField         text field object
     * @param title             title of the directory dialog
     * @param message           message of the directory dialog
     * @param checkedFilterPath filter path of the directory dialog
     */
    public static void showAdvancedDirectoryDialog(Shell innerShell, Text textField, String title, String message, String checkedFilterPath) {
        DirectoryDialog directoryDialog = new DirectoryDialog(innerShell);
        directoryDialog.setText(title);
        directoryDialog.setMessage(message);
        directoryDialog.setFilterPath(PreferenceHandler.checkUserPrefPathExist(checkedFilterPath));

        handlePath(innerShell, textField, directoryDialog.open());
    }

} // end of DirectoryDialogs
