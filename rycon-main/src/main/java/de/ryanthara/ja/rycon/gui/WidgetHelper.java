/*
 * License: GPL. Copyright 2015- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
import java.util.StringTokenizer;

/**
 * This class implements functionality which is used by all four main widgets of RyCON.
 * <p>
 * Later on, here will be more flexible stuff implemented.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 4
 */
public class WidgetHelper {

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

}
