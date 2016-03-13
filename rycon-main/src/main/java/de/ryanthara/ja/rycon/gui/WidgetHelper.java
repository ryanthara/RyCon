/*
 * License: GPL. Copyright 2015- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

package de.ryanthara.ja.rycon.gui;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class implements functionality which is used by all main widgets of RyCON.
 * <p>
 * Later on, here will be more flexible stuff implemented.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>3: check for valid files and suffixes </li>
 *     <li>2: documentation, optimization and new features </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 3
 * @since 4
 */
public class WidgetHelper {

    /**
     * Checks the source and destination text fields for valid files and returns the valid chosen files as file object.
     *
     * @param source the source text field
     * @param destination the destination text field
     * @param chosenFiles the chosen files to be checked
     * @return the valid chosen files
     */
    public static File[] checkSourceAndDestinationTextFields(Text source, Text destination, File[] chosenFiles) {
        File[] files2read = null;

        if (TextHelper.checkIsEmpty(source) || TextHelper.checkIsEmpty(destination)) {
            GuiHelper.showMessageBox(Main.shell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgEmptyTextFieldWarning());

            files2read = new File[0];
        } else if (chosenFiles == null) {
            // TODO check for spaces in file names or directory names (not easy)
            StringTokenizer st = new StringTokenizer(source.getText());

            for (int i = 0; i < st.countTokens(); i++) {
                String s = st.nextToken();

                if (FileUtils.checkIsFile(s)) {
                    files2read[i] = new File(s);
                } else {
                    GuiHelper.showMessageBox(Main.shell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgFileNotExist());
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

    /**
     * Checks the content of an file array for valid files.
     * <p>
     * Non readable file objects and directories will be not included in the returned file array.
     *
     * @param files file array to be checked
     * @param acceptableFileSuffix string array with the acceptable file suffixes to be accepted
     * @return check file array with only valid and readable file obejcts
     */
    public static File[] checkForValidFiles(File[] files, String[] acceptableFileSuffix) {
        ArrayList<File> temp = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && file.canRead()) {
                for (String anAcceptableFileSuffix : acceptableFileSuffix) {
                    String reducedSuffix = anAcceptableFileSuffix.substring(2, anAcceptableFileSuffix.length());

                    if (file.getName().toLowerCase().endsWith(reducedSuffix)) {
                        temp.add(file);
                    }
                }
            }
        }

        return temp.toArray(new File[temp.size()]);
    }

}
