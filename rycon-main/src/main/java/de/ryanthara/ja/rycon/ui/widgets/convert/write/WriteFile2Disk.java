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
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Warnings;
import de.ryanthara.ja.rycon.io.LineWriter;
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
 * This class implements static file writing functions for line based files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
class WriteFile2Disk {

    private static String prepareOutputFileName(Path path, String suffix) {
        final String paramEditString = DefaultKeys.PARAM_EDIT_STRING.getValue();

        return path.toString().substring(0, path.toString().length() - 4) + "_" + paramEditString + suffix;
    }

    /**
     * Writes a line based string file from an {@link ArrayList} to the file system and returns write success.
     *
     * @param path      {@link Path} object
     * @param writeFile prepared string lines for writing
     * @param suffix    file suffix
     *
     * @return write success
     */
    static boolean writeFile2Disk(Path path, ArrayList<String> writeFile, String suffix) {
        boolean success;
        String outputFileName = prepareOutputFileName(path, suffix);

        if (Files.exists(Paths.get(outputFileName))) {
            final Shell shell = Display.getCurrent().getActiveShell();

            int returnValue = MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    String.format(ResourceBundleUtils.getLangString(WARNINGS, Warnings.fileExists), outputFileName));

            if (returnValue == SWT.YES) {
                LineWriter lineWriter = new LineWriter(outputFileName);

                success = lineWriter.writeFile(writeFile);
            } else {
                success = false;
            }
        } else {
            success = new LineWriter(outputFileName).writeFile(writeFile);
        }

        return success;
    }

} // end of WriteFile2Disk
