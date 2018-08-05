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

import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Warnings;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Files;
import java.nio.file.Path;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WARNINGS;

/**
 * This class implements static file writing functions for Open Document spreadsheet files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class WriteOdf2Disk {

    /**
     * Writes a Open Document spreadsheet from a {@link SpreadsheetDocument} to the file system and returns writer success.
     *
     * @param path                path object
     * @param spreadsheetDocument prepared {@link SpreadsheetDocument} for writing
     *
     * @return writer success
     */
    public static boolean writeOds2Disk(Path path, SpreadsheetDocument spreadsheetDocument) {
        boolean writeSuccess;
        final Path outputFileName = PathUtils.prepareOutputFileName(path, "", FileNameExtension.ODS.getExtension());
        FileToolsOdf fileToolsOdf = new FileToolsOdf(spreadsheetDocument);

        if (Files.exists(outputFileName)) {
            final Shell shell = Display.getCurrent().getActiveShell();

            int returnValue = MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    String.format(ResourceBundleUtils.getLangString(WARNINGS, Warnings.fileExistsOverwrite), outputFileName));

            writeSuccess = returnValue == SWT.YES && fileToolsOdf.writeODS(outputFileName);
        } else {
            writeSuccess = fileToolsOdf.writeODS(outputFileName);
        }

        return writeSuccess;
    }

} // end of WriteOdf2Disk
