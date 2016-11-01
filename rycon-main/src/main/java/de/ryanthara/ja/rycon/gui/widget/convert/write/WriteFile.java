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

import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
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
     * Returns the prepared {@link SpreadsheetDocument} for file writing.
     * <p>
     * This method is used vise versa with {@link #writeStringFile()} and {@link #writeWorkbookFile()}.
     * The ones which are not used, returns null for indication.
     *
     * @return array list for file writing
     */
    SpreadsheetDocument writeSpreadsheetDocument();

    /**
     * Returns the prepared {@link ArrayList} for file writing.
     * <p>
     * This method is used vise versa with {@link #writeSpreadsheetDocument()} and {@link #writeWorkbookFile()}.
     * The ones which are not used, returns null for indication.
     *
     * @return array list for file writing
     */
    ArrayList<String> writeStringFile();

    /**
     * Returns the prepared {@link Workbook} for file writing.
     * <p>
     * This method is used vise versa with {@link #writeStringFile()} and {@link #writeSpreadsheetDocument()}.
     * The ones which are not used, returns null for indication.
     *
     * @return array list for file writing
     */
    Workbook writeWorkbookFile();

} // end of WriteFile
