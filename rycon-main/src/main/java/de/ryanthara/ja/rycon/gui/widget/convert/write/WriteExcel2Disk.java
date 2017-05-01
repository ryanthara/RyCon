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
package de.ryanthara.ja.rycon.gui.widget.convert.write;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.file.FileToolsExcel;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.Warnings;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.SWT;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class implements static file writing functions for line based files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
class WriteExcel2Disk {

    private static String prepareOutputFileName(Path path, String suffix) {
        return path.toString().substring(0, path.toString().length() - 4) + "_" + Main.getParamEditString() + suffix;
    }

    /**
     * Writes a Microsoft Excel (.XLS or .XLSX) file from a {@link Workbook} to the file system and returns write success.
     *
     * @param path     path object
     * @param workbook prepared {@link Workbook} for writing
     * @param suffix   file suffix
     *
     * @return write success
     */
    static boolean writeExcel2Disk(Path path, Workbook workbook, String suffix) {
        boolean writeSuccess;
        String outputFileName = prepareOutputFileName(path, suffix);

        System.out.println(outputFileName);

        FileToolsExcel fileToolsExcel = new FileToolsExcel(workbook);

        if (Files.exists(Paths.get(outputFileName))) {
            int returnValue = MessageBoxes.showMessageBox(Main.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    Labels.getString("warningTextMsgBox"), String.format(Warnings.getString("fileExists"), outputFileName));

            if (returnValue == SWT.YES) {
                if (suffix.equalsIgnoreCase(".xls")) {
                    writeSuccess = fileToolsExcel.writeXLS(Paths.get(outputFileName));
                } else
                    writeSuccess = suffix.equalsIgnoreCase(".xlsx") && fileToolsExcel.writeXLSX(Paths.get(outputFileName));
            } else {
                writeSuccess = false;
            }
        } else {
            if (suffix.equalsIgnoreCase(".xls")) {
                writeSuccess = fileToolsExcel.writeXLS(Paths.get(outputFileName));
            } else
                writeSuccess = suffix.equalsIgnoreCase(".xlsx") && fileToolsExcel.writeXLSX(Paths.get(outputFileName));
        }

        return writeSuccess;
    }

} // end of WriteExcel2Disk
