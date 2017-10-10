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

import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.util.ArrayList;

/**
 * Interface for writing operations in the {@link ConverterWidget}.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public interface WriteFile {

    /**
     * Returns true if the prepared {@link SpreadsheetDocument} for file writing was written to the file system.
     *
     * @return write success
     */
    boolean writeSpreadsheetDocument();

    /**
     * Returns true if the prepared {@link ArrayList} for file writing was written to the file system.
     *
     * @return write success
     */
    boolean writeStringFile();

    /**
     * Returns true if the prepared {@link Workbook} for file writing was written to the file system.
     *
     * @return write success
     */
    boolean writeWorkbookFile();

} // end of WriteFile
