/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.widget.convert.write
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
package de.ryanthara.ja.rycon.ui.widgets.convert.write;

import de.ryanthara.ja.rycon.data.DefaultKeys;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Warnings;
import de.ryanthara.ja.rycon.nio.LineWriter;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WARNINGS;

/**
 * This class implements static file writing functions for line based files output files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
final class WriteFile2Disk {

    private static Path prepareOutputFileName(final Path path, final String fileNameExtension) {
        final String fileNameWithoutExtension = path.toString().substring(0, path.toString().length() - 4);
        final String newFileName = fileNameWithoutExtension + "_" + DefaultKeys.PARAM_EDIT_STRING.getValue() + fileNameExtension;

        return Paths.get(newFileName);
    }

    /**
     * Writes a line based string file from an {@link ArrayList} to the file system and returns the write success.
     * <p>
     * Within this method a check for existing files is implemented. If a file exists, the user ist asked if the
     * file has to be overwritten or not.
     *
     * @param path              {@link Path} object where to write into
     * @param writeFile         prepared string lines for writing
     * @param fileNameExtension file file name extension
     *
     * @return write success
     */
    static boolean writeFile2Disk(Path path, ArrayList<String> writeFile, String fileNameExtension) {
        boolean success;

        final Path outputFile = prepareOutputFileName(path, fileNameExtension);

        if (Files.exists(outputFile)) {
            final Shell shell = Display.getCurrent().getActiveShell();

            int returnValue = MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    String.format(ResourceBundleUtils.getLangString(WARNINGS, Warnings.fileExistsOverwrite), outputFile.getFileName().toString()));

            if (returnValue == SWT.YES) {
                LineWriter lineWriter = new LineWriter(outputFile);

                success = lineWriter.writeFile(writeFile);
            } else {
                success = false;
            }
        } else {
            success = new LineWriter(outputFile).writeFile(writeFile);
        }

        return success;
    }

} // end of WriteFile2Disk
