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

import de.ryanthara.ja.rycon.core.converter.caplan.*;
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
 * Instances of this class are used for writing Caplan K files from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CaplanWriter implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(CaplanWriter.class.getName());

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link CaplanWriter} with a set of parameters.
     *
     * @param path           reader path object for writing
     * @param readCSVFile    reader csv file
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object
     */
    public CaplanWriter(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
                Gsi2K gsi2K = new Gsi2K(readStringFile);
                writeFile = gsi2K.convertGSI2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;

            case TXT:
                Txt2K txt2K = new Txt2K(readStringFile);
                writeFile = txt2K.convertTXT2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case CSV:
                Csv2K csv2K = new Csv2K(readCSVFile);
                writeFile = csv2K.convertCsv2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case CAPLAN_K:
                break;

            case ZEISS_REC:
                Zeiss2K zeiss2K = new Zeiss2K(readStringFile);
                writeFile = zeiss2K.convertZeiss2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;

            case CADWORK:
                Cadwork2K cadwork2K = new Cadwork2K(readStringFile);
                writeFile = cadwork2K.convertCadwork2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case BASEL_STADT:
                CsvBaselStadt2K csvBaselStadt2K = new CsvBaselStadt2K(readCSVFile);
                writeFile = csvBaselStadt2K.convertCSVBaselStadt2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2K txtBaselLandschaft2K = new TxtBaselLandschaft2K(readStringFile);
                writeFile = txtBaselLandschaft2K.convertTXTBaselLandschaft2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to Caplan K file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.K.getExtension())) {
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

} // end of CaplanWriter
