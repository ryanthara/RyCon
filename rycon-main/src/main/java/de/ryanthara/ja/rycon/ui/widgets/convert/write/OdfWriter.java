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

import de.ryanthara.ja.rycon.core.converter.odf.*;
import de.ryanthara.ja.rycon.nio.WriteOdf2Disk;
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
 * Instances of this class are used for writing OpenDocument spreadsheet files
 * from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class OdfWriter implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(OdfWriter.class.getName());

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link OdfWriter} with a set of parameters.
     *
     * @param path           output file
     * @param readCSVFile    reader csv file
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object
     */
    public OdfWriter(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
        boolean success = false;
        SpreadsheetDocument spreadsheetDocument = null;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                Gsi2Odf gsi2Odf = new Gsi2Odf(readStringFile);
                if (gsi2Odf.convertGSI2Ods(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = gsi2Odf.getSpreadsheetDocument();
                }
                break;

            case TXT:
                Txt2Odf txt2Odf = new Txt2Odf(readStringFile);
                if (txt2Odf.convertTXT2Ods(path.getFileName())) {
                    spreadsheetDocument = txt2Odf.getSpreadsheetDocument();
                }
                break;

            case CSV:
                Csv2Odf csv2Odf = new Csv2Odf(readCSVFile);
                if (csv2Odf.convertCSV2Ods(path.getFileName())) {
                    spreadsheetDocument = csv2Odf.getSpreadsheetDocument();
                }
                break;

            case CAPLAN_K:
                Caplan2Odf caplan2Odf = new Caplan2Odf(readStringFile);
                if (caplan2Odf.convertCaplan2Ods(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = caplan2Odf.getSpreadsheetDocument();
                }
                break;

            case ZEISS_REC:
                Zeiss2Odf zeiss2Odf = new Zeiss2Odf(readStringFile);
                if (zeiss2Odf.convertZeiss2Ods(path.getFileName())) {
                    spreadsheetDocument = zeiss2Odf.getSpreadsheetDocument();
                }
                break;

            case CADWORK:
                Cadwork2Odf cadwork2Odf = new Cadwork2Odf(readStringFile);
                if (cadwork2Odf.convertCadwork2Ods(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = cadwork2Odf.getSpreadsheetDocument();
                }
                break;

            case BASEL_STADT:
                CsvBaselStadt2Odf csvBaselStadt2Odf = new CsvBaselStadt2Odf(readCSVFile);
                if (csvBaselStadt2Odf.convertCsvBaselStadt2Ods(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = csvBaselStadt2Odf.getSpreadsheetDocument();
                }
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Odf txtBaselLandschaft2Odf = new TxtBaselLandschaft2Odf(readStringFile);
                if (txtBaselLandschaft2Odf.convertTXTBaselLandschaft2Ods(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = txtBaselLandschaft2Odf.getSpreadsheetDocument();
                }
                break;

            default:
                spreadsheetDocument = null;

                logger.warn("Can not write {} file format to Open Document spreadsheet file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteOdf2Disk.writeOds2Disk(path, spreadsheetDocument)) {
            success = true;
        }

        return success;
    }

    /**
     * Returns true if the prepared {@link ArrayList} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeStringFile() {
        return false;
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

} // end of OdfWriter
