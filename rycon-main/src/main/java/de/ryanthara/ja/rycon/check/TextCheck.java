/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

package de.ryanthara.ja.rycon.check;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.i18n.I18N;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.util.StringTokenizer;

/**
 * This class implements different kind of checks for {@link Text} fields.
 * <p>
 * It is used by all main widgets of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TextCheck {

    /**
     * Checks the content of the {@link Text} field if it is a valid directory in the file system.
     *
     * @param textField text field which content has to be checked
     *
     * @return true if directory exist
     */
    public static boolean checkDirExists(Text textField) {
        return FileCheck.checkIsDirectory(textField.getText());
    }

    /**
     * Checks the content of the {@link Text} field if it is a valid file in the file system.
     *
     * @param textField text field which content has to be checked
     *
     * @return true if file exist
     */
    public static boolean checkFileExists(Text textField) {
        return FileCheck.checkIsFile(textField.getText());
    }

    /**
     * Checks the content of a {@link Text} field if it is a valid double value.
     *
     * @param textField text to be checked
     *
     * @return success of the check
     */
    public static boolean checkIsDoubleValue(Text textField) {
        try {
            Double.parseDouble(textField.getText());
            return true;
        } catch (NumberFormatException ex) {
            System.err.println("Text field contains a value that can't be parsed into a double value!");
            return false;
        }
    }

    /**
     * Checks the {@link Text} field if it contains an empty String.
     *
     * @param textField text field to be checked
     *
     * @return true if text field contains an empty string
     */
    public static boolean checkIsEmpty(Text textField) {
        return textField != null & (textField != null && textField.getText().trim().equals(""));
    }

    /**
     * Checks the source and destination {@link Text} fields for valid files and returns the valid ones as a file array.
     *
     * @param source      the source text field
     * @param destination the destination text field
     * @param chosenFiles the chosen files to be checked
     *
     * @return the valid chosen files
     */
    public static File[] checkSourceAndDestinationText(Text source, Text destination, File[] chosenFiles) {
        File[] files2read = null;

        if (checkIsEmpty(source) || checkIsEmpty(destination)) {
            MessageBoxes.showMessageBox(Main.shell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgEmptyTextFieldWarning());

            files2read = new File[0];
        } else if (chosenFiles == null) {
            StringTokenizer st = new StringTokenizer(source.getText());
            files2read = new File[st.countTokens()];

            for (int i = 0; i < st.countTokens(); i++) {
                String s = st.nextToken();

                if (FileCheck.checkIsFile(s)) {
                    files2read[i] = new File(s);
                } else {
                    MessageBoxes.showMessageBox(Main.shell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgFileNotExist());
                }
            }

            File destinationPath = new File(destination.getText());

            if (!(destinationPath.isDirectory() && files2read.length > 0)) {
                files2read = new File[0];
            }
        } else if (chosenFiles.length > 0) {
            files2read = chosenFiles;
        }

        return files2read;
    }

} // end of TextCheck
