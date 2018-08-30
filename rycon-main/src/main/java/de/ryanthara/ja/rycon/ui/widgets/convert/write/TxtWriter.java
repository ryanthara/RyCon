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

import de.ryanthara.ja.rycon.core.converter.text.*;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing text files from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class TxtWriter implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(TxtWriter.class.getName());

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link TxtWriter} with a set of parameters.
     *
     * @param path           reader file object for writing
     * @param readCSVFile    reader csv file
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object
     */
    public TxtWriter(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
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
        ArrayList<String> writeFile = null;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                Gsi2Txt gsi2Txt = new Gsi2Txt(readStringFile);
                writeFile = gsi2Txt.convertGSI2TXT(parameter.getSeparatorTXT(), parameter.isGSI16(), parameter.isWriteCommentLine());
                break;

            case TXT:
                break;

            case CSV:
                Csv2Txt csv2Txt = new Csv2Txt(readCSVFile);
                writeFile = csv2Txt.convertCSV2TXT(parameter.getSeparatorTXT());
                break;

            case CAPLAN_K:
                Caplan2Txt caplan2Txt = new Caplan2Txt(readStringFile);
                writeFile = caplan2Txt.convertK2TXT(parameter.getSeparatorTXT(), parameter.isKFormatUseSimpleFormat(),
                        parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2Txt zeiss2Txt = new Zeiss2Txt(readStringFile);
                writeFile = zeiss2Txt.convertZeiss2TXT(parameter.getSeparatorTXT());
                break;

            case CADWORK:
                Cadwork2Txt cadwork2Txt = new Cadwork2Txt(readStringFile);
                writeFile = cadwork2Txt.convertCadwork2TXT(parameter.getSeparatorTXT(), parameter.isWriteCodeColumn(),
                        parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Txt csvBaselStadt2Txt = new CsvBaselStadt2Txt(readCSVFile, parameter.isWriteZeroHeights());
                writeFile = csvBaselStadt2Txt.convertCSVBaselStadt2TXT(parameter.getSeparatorTXT());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Txt TxtBaselLandschaft2Txt = new TxtBaselLandschaft2Txt(readStringFile);
                writeFile = TxtBaselLandschaft2Txt.convertTXTBaselLandschaft2TXT(parameter.getSeparatorTXT(), parameter.isWriteCodeColumn());
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to text file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.TXT.getExtension())) {
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

} // end of TxtWriter
