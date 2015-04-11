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

import org.eclipse.swt.widgets.Text;

/**
 * This class enables helper operations like checks for emptyness or valid files
 * to text fields.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 4
 */
public class TextHelper {

    /**
     * Constructor without parameter.
     */
    public TextHelper() {}

    /**
     * Checks if an text field contains an empty String.
     *
     * @param textField text field to be checked
     * @return true if text field contains an empty string
     */
    public static boolean checkIsEmpty(Text textField) {
        return textField.getText().trim().equals("");
    }


    /**
     * Checks if the content of the text field is a directory in the file system.
     *
     * @param textField text field which has to be checked
     * @return true if directory exist
     */
    public static boolean checkIfDirExists(Text textField) {
        boolean exist = false;

        String content = textField.getText();

//        if (content !=)

        return exist;
    }

    /**
     * Checks if the content of the text field is a file in the file system.
     *
     * @param textField text field which has to be checked
     * @return true if file exist
     */
    public static boolean checkIfFileExists(Text textField) {
        boolean exist = false;

        return exist;
    }

} // end of TextHelper