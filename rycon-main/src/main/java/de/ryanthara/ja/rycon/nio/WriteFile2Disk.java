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
package de.ryanthara.ja.rycon.nio;

import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Texts;
import de.ryanthara.ja.rycon.i18n.Warnings;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.util.check.PathCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.nio.file.Path;
import java.util.ArrayList;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXTS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WARNINGS;
import static de.ryanthara.ja.rycon.nio.PathUtils.prepareOutputFileName;

/**
 * This class implements static file writing functions for line based output files.
 * <p>
 * Due to some little enhancements and the move from {@link String} based filenames and paths to {@link Path},
 * some {@link PathUtils} functions are used. They based on the popular org.apache.commons.io.FilenameUtils.java class.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public final class WriteFile2Disk {

    /**
     * Writes a line based string file from an {@link ArrayList} to the file system and returns the writer success.
     * <p>
     * Within this method a check for existing files is implemented. If a file exists, the user is asked if the
     * file has to be overwritten or not.
     *
     * @param path              {@link Path} object where to writer into
     * @param writeFile         prepared string lines for writing
     * @param editString        the edit string which is inserted before the file name extension
     * @param fileNameExtension file file name extension
     *
     * @return write success
     */
    public static boolean writeFile2Disk(final Path path, final ArrayList<String> writeFile,
                                         final String editString, final String fileNameExtension) {
        boolean success = false;

        final Path outputFile = prepareOutputFileName(path, editString, fileNameExtension);

        if (outputFile != null) {
            if (PathCheck.fileExists(outputFile)) {
                final Shell shell = Display.getCurrent().getActiveShell();

                Path p = outputFile.getFileName();

                if (p != null) {
                    final String outputFilename = p.toString();

                    int returnValue = MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                            ResourceBundleUtils.getLangStringFromXml(TEXTS, Texts.msgBox_Warning),
                            String.format(ResourceBundleUtils.getLangString(WARNINGS, Warnings.fileExistsOverwrite), outputFilename));

                    if (returnValue == SWT.YES) {
                        LineWriter lineWriter = new LineWriter(outputFile);

                        success = lineWriter.writeFile(writeFile);
                    } else {
                        success = false;
                    }
                }
            } else {
                success = new LineWriter(outputFile).writeFile(writeFile);
            }
        }

        return success;
    }

} // end of WriteFile2Disk
