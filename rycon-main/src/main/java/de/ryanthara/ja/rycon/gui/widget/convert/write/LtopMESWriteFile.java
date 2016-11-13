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
import de.ryanthara.ja.rycon.gui.widget.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Instances of this class are used for writing LTOP MES files from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class LtopMESWriteFile implements WriteFile {

    private final Path path;
    private ArrayList<String> readStringFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link LtopMESWriteFile} with a set of parameters.
     *
     * @param path           read file object for writing
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public LtopMESWriteFile(Path path, ArrayList<String> readStringFile, WriteParameter parameter) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.parameter = parameter;
    }

    /**
     * Returns true if the prepared {@link SpreadsheetDocument} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeSpreadsheetDocument() {
        return false;
    }

    /**
     * Returns the prepared {@link ArrayList} for file writing.
     *
     * @return array list for file writing
     */
    @Override
    public boolean writeStringFile() {
        boolean success = false;
        ArrayList<String> writeFile;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                GSI2MES gsi2MES = new GSI2MES(readStringFile);
                writeFile = gsi2MES.convertGSI2MES(Boolean.parseBoolean(Main.pref.getUserPref(
                        PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                break;

            case ZEISS_REC:
                Zeiss2LTOP zeiss2LTOP = new Zeiss2LTOP(readStringFile);
                writeFile = zeiss2LTOP.convertZeiss2MES(Boolean.parseBoolean(Main.pref.getUserPref(
                        PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                break;

            default:
                writeFile = null;
                System.err.println("LtopMESWriteFile.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, ".MES")) {
            success = true;
        }

        return success;
    }

    /**
     * Returns true if the prepared {@link Workbook} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeWorkbookFile() {
        return false;
    }

} // end of LtopMESWriteFile
