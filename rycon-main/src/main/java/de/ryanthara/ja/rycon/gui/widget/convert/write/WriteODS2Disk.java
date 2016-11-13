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
import de.ryanthara.ja.rycon.file.FileToolsODF;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.i18n.I18N;
import org.eclipse.swt.SWT;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class implements static file writing functions for Open Document spreadsheet files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
class WriteODS2Disk {

    private static String prepareOutputFileName(Path path, String suffix) {
        return path.toString().substring(0, path.toString().length() - 4) + "_" + Main.getParamEditString() + suffix;
    }

    /**
     * Writes a Open Document spreadsheet from a {@link SpreadsheetDocument} to the file system and returns write success.
     *
     * @param path                path object
     * @param spreadsheetDocument prepared {@link SpreadsheetDocument} for writing
     * @param suffix              file suffix
     *
     * @return write success
     */
    static boolean writeODS2Disk(Path path, SpreadsheetDocument spreadsheetDocument, String suffix) {
        boolean writeSuccess;
        String fileName = prepareOutputFileName(path, suffix);

        FileToolsODF fileToolsODF = new FileToolsODF(spreadsheetDocument);

        if (Files.exists(Paths.get(fileName))) {
            int returnValue = MessageBoxes.showMessageBox(Main.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    I18N.getMsgBoxTitleWarning(), String.format(I18N.getMsgFileExist(), fileName));

            writeSuccess = returnValue == SWT.YES && fileToolsODF.writeODS(fileName);
        } else {
            writeSuccess = fileToolsODF.writeODS(fileName);
        }

        return writeSuccess;
    }

} // end of WriteODS2Disk
