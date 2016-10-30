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

package de.ryanthara.ja.rycon.gui.custom;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.File;

/**
 * This class implements a simple static access to swt {@link FileDialog} and it's functionality to RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class FileDialogs {

    private static File[] handleFile(FileDialog fileDialog, Text source, Text destination) {
        String[] files = fileDialog.getFileNames();

        File[] files2read = new File[files.length];

        // hack for displaying file names without path in text field
        String concatString = "";
        String workingDir = fileDialog.getFilterPath();

        for (int i = 0; i < files.length; i++) {
            concatString = concatString.concat(files[i]);
            concatString = concatString.concat(" ");

            files2read[i] = new File(workingDir + File.separator + files[i]);
        }

        source.setText(concatString);
        destination.setText(fileDialog.getFilterPath());

        return files2read;
    }

    /**
     * Shows a {@link FileDialog} which is used in different widgets of RyCON's gui.
     * <p>
     * With a special number of parameters the dialog is fully customizable.
     *
     * @param innerShell       shell object
     * @param multiSelection   allows multi selection
     * @param filterPath       filter path of the file dialog
     * @param text             title of the file dialog
     * @param filterExtensions allowed extensions
     * @param filterNames      description of allowed extensions
     * @param source           source text field
     * @param destination      destination text field
     *
     * @return chosen files as String array
     */
    public static File[] showAdvancedFileDialog(Shell innerShell, int multiSelection, String filterPath, String text,
                                                String[] filterExtensions, String[] filterNames, Text source, Text destination) {
        FileDialog fileDialog = new FileDialog(innerShell, multiSelection);
        fileDialog.setFilterPath(filterPath);
        fileDialog.setText(text);
        fileDialog.setFilterExtensions(filterExtensions);
        fileDialog.setFilterNames(filterNames);

        if (fileDialog.open() != null) {
            return handleFile(fileDialog, source, destination);
        } else {
            return null;
        }
    }

} // end of FileDialogs
