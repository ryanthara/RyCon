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
package de.ryanthara.ja.rycon.ui.custom;

import de.ryanthara.ja.rycon.core.converter.Separator;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * {@code FileDialogs} implements a simple static access to swt {@link FileDialog} and it's functionality to RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class FileDialogs {

    private static Path[] handleFile(FileDialog fileDialog, Text source) {
        String[] files = fileDialog.getFileNames();

        Path[] files2read = new Path[files.length];

        String concatString = "";
        String workingDir = fileDialog.getFilterPath();

        for (int i = 0; i < files.length; i++) {
            concatString = concatString.concat(workingDir);
            concatString = concatString.concat(FileSystems.getDefault().getSeparator());
            concatString = concatString.concat(files[i]);

            if (i < files.length - 1) {
                concatString = concatString.concat(Separator.WHITESPACE.getSign());
            }

            files2read[i] = Paths.get(workingDir + FileSystems.getDefault().getSeparator() + files[i]);
        }

        source.setText(concatString);

        return files2read;
    }

    private static Path[] handleFile(FileDialog fileDialog, Text source, Text target) {
        String[] files = fileDialog.getFileNames();

        Path[] files2read = new Path[files.length];

        // hack for displaying file names without path in text field
        String concatString = "";
        String workingDir = fileDialog.getFilterPath();

        for (int i = 0; i < files.length; i++) {
            concatString = concatString.concat(files[i]);
            concatString = concatString.concat(Separator.WHITESPACE.getSign());

            files2read[i] = Paths.get(workingDir + FileSystems.getDefault().getSeparator() + files[i]);
        }

        source.setText(concatString);
        target.setText(fileDialog.getFilterPath());

        return files2read;
    }

    /**
     * Shows a {@link FileDialog} which is used in different widgets of RyCON's ui.
     * <p>
     * With a special number of parameters the dialog is fully customizable.
     *
     * @param innerShell       shell object
     * @param filterPath       filter path of the file dialog
     * @param text             title of the file dialog
     * @param filterExtensions allowed extensions
     * @param filterNames      description of allowed extensions
     * @param source           source text field
     * @return chosen files as {@link Path} array
     */
    public static Optional<Path[]> showAdvancedFileDialog(Shell innerShell, String filterPath, String text, String[] filterExtensions,
                                                          String[] filterNames, Text source) {
        FileDialog fileDialog = new FileDialog(innerShell, org.eclipse.swt.SWT.MULTI);
        fileDialog.setFilterPath(filterPath);
        fileDialog.setText(text);
        fileDialog.setFilterExtensions(filterExtensions);
        fileDialog.setFilterNames(filterNames);

        if (fileDialog.open() != null) {
            return Optional.of(handleFile(fileDialog, source));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Shows a {@link FileDialog} which is used in different widgets of RyCON's ui.
     * <p>
     * With a special number of parameters the dialog is fully customizable.
     *
     * @param innerShell       shell object
     * @param filterPath       filter path of the file dialog
     * @param text             title of the file dialog
     * @param filterExtensions allowed extensions
     * @param filterNames      description of allowed extensions
     * @param source           source text field
     * @param target           target text field
     * @return chosen files as {@link Path} array
     */
    public static Optional<Path[]> showAdvancedFileDialog(Shell innerShell, String filterPath, String text, String[] filterExtensions,
                                                          String[] filterNames, Text source, Text target) {
        FileDialog fileDialog = new FileDialog(innerShell, org.eclipse.swt.SWT.MULTI);
        fileDialog.setFilterPath(filterPath);
        fileDialog.setText(text);
        fileDialog.setFilterExtensions(filterExtensions);
        fileDialog.setFilterNames(filterNames);

        if (fileDialog.open() != null) {
            return Optional.of(handleFile(fileDialog, source, target));
        } else {
            return Optional.empty();
        }
    }

}
