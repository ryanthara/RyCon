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
import de.ryanthara.ja.rycon.converter.ltop.GSI2MES;
import de.ryanthara.ja.rycon.converter.ltop.Zeiss2LTOP;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.util.ArrayList;

/**
 * Instances of this class are used for writing LTOP MES files from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class LtopMESWriteFile implements WriteFile {

    private ArrayList<String> readStringFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link LtopMESWriteFile} with a set of parameters.
     *
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public LtopMESWriteFile(ArrayList<String> readStringFile, WriteParameter parameter) {
        this.readStringFile = readStringFile;
        this.parameter = parameter;
    }

    /**
     * Returns the prepared {@link SpreadsheetDocument} for file writing.
     * <p>
     * This method is used vise versa with {@link #writeStringFile()} and {@link #writeWorkbookFile()}.
     * The ones which are not used, returns null for indication.
     *
     * @return array list for file writing
     */
    @Override
    public SpreadsheetDocument writeSpreadsheetDocument() {
        return null;
    }

    /**
     * Returns the prepared {@link ArrayList} for file writing.
     *
     * @return array list for file writing
     */
    @Override
    public ArrayList<String> writeStringFile() {
        ArrayList<String> writeFile;

        switch (parameter.getSourceNumber()) {
            case 0:     // fall through for GSI8 format
            case 1:     // GSI16 format
                GSI2MES gsi2MES = new GSI2MES(readStringFile);
                writeFile = gsi2MES.convertGSI2MES(Boolean.parseBoolean(Main.pref.getUserPref(
                        PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                break;

            case 5:     // Zeiss REC format and it's dialects
                Zeiss2LTOP zeiss2LTOP = new Zeiss2LTOP(readStringFile);
                writeFile = zeiss2LTOP.convertZeiss2MES(Boolean.parseBoolean(Main.pref.getUserPref(
                        PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                break;

            default:
                writeFile = null;
                break;
        }

        return writeFile;
    }

    /**
     * Returns the prepared {@link Workbook} for file writing.
     * <p>
     * This method is used vise versa with {@link #writeStringFile()} and {@link #writeSpreadsheetDocument()}.
     * The ones which are not used, returns null for indication.
     *
     * @return array list for file writing
     */
    @Override
    public Workbook writeWorkbookFile() {
        return null;
    }

} // end of LtopMESWriteFile
