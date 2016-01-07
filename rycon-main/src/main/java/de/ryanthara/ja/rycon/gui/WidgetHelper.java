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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
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
            MessageBox msgBox = new MessageBox(Main.shell, SWT.ICON_WARNING);
            msgBox.setMessage(I18N.getMsgEmptyTextFieldWarning());
            msgBox.setText(I18N.getMsgBoxTitleWarning());
            msgBox.open();

            return new File[0];
        } else if (chosenFiles == null) {
              // TODO check for spaces in file names or directory names (not trivial)
            StringTokenizer st = new StringTokenizer(source.getText());

            int counter = st.countTokens();

            for (int i = 0; i < counter; i++) {
                String s = st.nextToken();
                File sourceFile = new File(s);

                if (sourceFile.isFile()) {
                    files2read[i] = sourceFile;
                } else {
                    MessageBox msgBox = new MessageBox(Main.shell, SWT.ICON_WARNING);
                    msgBox.setMessage(I18N.getMsgFileNotExist());
                    msgBox.setText(I18N.getMsgBoxTitleWarning());
                    msgBox.open();
                }
            }

            File destinationPath = new File(destination.getText());

            if (destinationPath.isDirectory() && files2read.length > 0) {
                return files2read;
            } else {
                return new File[0];
            }
        } else if (chosenFiles.length > 0) {
            return chosenFiles;
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

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].canRead()) {
                for (int j = 0; j < acceptableFileSuffix.length; j++) {
                    String reducedSuffix = acceptableFileSuffix[j].toString().substring(2, acceptableFileSuffix[j].length());

                    if (files[i].getName().toLowerCase().endsWith(reducedSuffix)) {
                        temp.add(files[i]);
                    }
                }
            }
        }

        return temp.toArray(new File[0]);
    }

}
