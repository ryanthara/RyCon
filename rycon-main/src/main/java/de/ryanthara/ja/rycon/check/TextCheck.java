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
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Warnings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.ERRORS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WARNINGS;

/**
 * This class implements different kind of checks for {@link Text} fields.
 * <p>
 * It is used by all main widgets of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class TextCheck {

    /**
     * Checks the source and target {@link Text} fields for being valid files and returns the valid ones
     * as a {@link Path} array.
     *
     * @param source      the source text field
     * @param target the target text field
     * @param chosenFiles the chosen files to be checked
     *
     * @return the valid chosen files
     */
    public static Path[] checkSourceAndTargetText(Text source, Text target, Path... chosenFiles) {
        Path[] files2read = null;

        if (isEmpty(source) || isEmpty(target)) {
            MessageBoxes.showMessageBox(Main.shell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));

            files2read = new Path[0];
        } else if (chosenFiles == null) {
            StringTokenizer st = new StringTokenizer(source.getText());
            files2read = new Path[st.countTokens()];

            for (int i = 0; i < st.countTokens(); i++) {
                String s = st.nextToken();

                if (PathCheck.isFile(s)) {
                    files2read[i] = Paths.get(s);
                } else {
                    MessageBoxes.showMessageBox(Main.shell, SWT.ICON_WARNING,
                            ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                            ResourceBundleUtils.getLangString(ERRORS, Errors.fileExistsNot));
                }
            }

            Path targetPath = Paths.get(target.getText());

            if (!(Files.isDirectory(targetPath) && files2read.length > 0)) {
                files2read = new Path[0];
            }
        } else if (chosenFiles.length > 0) {
            files2read = chosenFiles;
        }

        return files2read;
    }

    /**
     * Checks the content of the {@link Text} field if it is a valid directory in the file system.
     *
     * @param textField text field which content has to be checked
     *
     * @return true if directory exist
     */
    public static boolean isDirExists(Text textField) {
        return PathCheck.isDirectory(textField.getText());
    }

    /**
     * Checks the content of a {@link Text} field if it is a valid double value.
     *
     * @param textField text to be checked
     *
     * @return success of the check
     */
    public static boolean isDoubleValue(Text textField) {
        boolean isDoubleValue;

        try {
            Double.valueOf(textField.getText());
            isDoubleValue = true;
        } catch (NumberFormatException ex) {
            System.err.println("Text field contains a value that can't be parsed into a double value!");
            isDoubleValue = false;
        }

        return isDoubleValue;
    }

    /**
     * Checks if the {@link Text} field contains an empty String.
     *
     * @param textField text field to be checked
     *
     * @return true if text field contains an empty string
     */
    public static boolean isEmpty(Text textField) {
        return textField.getText().isEmpty();
    }

    /**
     * Checks the content of the {@link Text} field if it is a valid file in the file system.
     *
     * @param textField text field which content has to be checked
     *
     * @return true if file exist
     */
    public static boolean isFileExists(Text textField) {
        return PathCheck.isFile(textField.getText());
    }

} // end of TextCheck
