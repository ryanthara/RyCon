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

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.core.converter.ltop.Gsi2Mes;
import de.ryanthara.ja.rycon.core.converter.ltop.Zeiss2Ltop;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instances of this class are used for writing LTOP MES files from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class LtopMesWriter implements Writer {

    private final static Logger logger = Logger.getLogger(LtopMesWriter.class.getName());

    private final Path path;
    private ArrayList<String> readStringFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link LtopMesWriter} with a set of parameters.
     *
     * @param path           reader file object for writing
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object
     */
    public LtopMesWriter(Path path, ArrayList<String> readStringFile, WriteParameter parameter) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.parameter = parameter;
    }

    /**
     * Returns true if the prepared {@link SpreadsheetDocument} for file writing was written to the file system.
     *
     * @return writer success
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
                Gsi2Mes gsi2Mes = new Gsi2Mes(readStringFile);
                writeFile = gsi2Mes.convertGSI2MES(Boolean.parseBoolean(Main.pref.getUserPreference(
                        PreferenceKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                break;

            case ZEISS_REC:
                Zeiss2Ltop zeiss2Ltop = new Zeiss2Ltop(readStringFile);
                writeFile = zeiss2Ltop.convertZeiss2MES(Boolean.parseBoolean(Main.pref.getUserPreference(
                        PreferenceKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                break;

            default:
                writeFile = null;

                logger.log(Level.SEVERE, "LtopMesWriter.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.MES.getExtension())) {
            success = true;
        }

        return success;
    }

    /**
     * Returns true if the prepared {@link Workbook} for file writing was written to the file system.
     *
     * @return writer success
     */
    @Override
    public boolean writeWorkbookFile() {
        return false;
    }

} // end of LtopMesWriter
