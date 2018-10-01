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
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.nio.file.Files;
import java.nio.file.Path;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXTS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WARNINGS;

/**
 * This class implements static file writing functions for line based files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class WriteExcel2Disk {

    /**
     * Writes a Microsoft Excel (.XLS or .XLSX) file from a {@link Workbook} to the file system and returns writer success.
     *
     * @param path              path object
     * @param workbook          prepared {@link Workbook} for writing
     * @param filenameExtension filename extension
     *
     * @return write success
     */
    public static boolean writeExcel2Disk(Path path, Workbook workbook, String filenameExtension) {
        boolean writeSuccess;
        final Path outputFileName = PathUtils.prepareOutputFileName(path, "", filenameExtension);
        FileToolsExcel fileToolsExcel = new FileToolsExcel(workbook);

        if (Files.exists(outputFileName)) {
            final Shell shell = Display.getCurrent().getActiveShell();

            int returnValue = MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    ResourceBundleUtils.getLangStringFromXml(TEXTS, Texts.msgBox_Warning),
                    String.format(ResourceBundleUtils.getLangString(WARNINGS, Warnings.fileExistsOverwrite), outputFileName));

            if (returnValue == SWT.YES) {
                if (filenameExtension.equalsIgnoreCase(FileNameExtension.XLS.getExtension())) {
                    writeSuccess = fileToolsExcel.writeXls(outputFileName);
                } else
                    writeSuccess = filenameExtension.equalsIgnoreCase(FileNameExtension.XLSX.getExtension()) && fileToolsExcel.writeXlsx(outputFileName);
            } else {
                writeSuccess = false;
            }
        } else {
            if (filenameExtension.equalsIgnoreCase(FileNameExtension.XLS.getExtension())) {
                writeSuccess = fileToolsExcel.writeXls(outputFileName);
            } else
                writeSuccess = filenameExtension.equalsIgnoreCase(FileNameExtension.XLSX.getExtension()) && fileToolsExcel.writeXlsx(outputFileName);
        }

        return writeSuccess;
    }

} // end of WriteExcel2Disk
