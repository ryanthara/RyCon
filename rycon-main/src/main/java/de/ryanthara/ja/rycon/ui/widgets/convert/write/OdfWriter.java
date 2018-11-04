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
import org.odftoolkit.simple.SpreadsheetDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A writer for writing OpenDocument spreadsheet files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class OdfWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(OdfWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link OdfWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param csv       read csv file
     * @param lines     read string based file
     * @param parameter the writer parameter object
     */
    public OdfWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
    }

    /**
     * Returns true if the prepared {@link SpreadsheetDocument} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeSpreadsheetDocument() {
        SpreadsheetDocument spreadsheetDocument = null;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi2Odf gsi2Odf = new Gsi2Odf(lines);
                if (gsi2Odf.convert(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = gsi2Odf.getSpreadsheetDocument();
                }
                break;

            case TXT:
                Txt2Odf txt2Odf = new Txt2Odf(lines);
                if (txt2Odf.convertTXT2Ods(path.getFileName())) {
                    spreadsheetDocument = txt2Odf.getSpreadsheetDocument();
                }
                break;

            case CSV:
                Csv2Odf csv2Odf = new Csv2Odf(csv);
                if (csv2Odf.convert(path.getFileName())) {
                    spreadsheetDocument = csv2Odf.getSpreadsheetDocument();
                }
                break;

            case CAPLAN_K:
                Caplan2Odf caplan2Odf = new Caplan2Odf(lines);
                if (caplan2Odf.convert(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = caplan2Odf.getSpreadsheetDocument();
                }
                break;

            case ZEISS_REC:
                Zeiss2Odf zeiss2Odf = new Zeiss2Odf(lines);
                if (zeiss2Odf.convertZeiss2Ods(path.getFileName())) {
                    spreadsheetDocument = zeiss2Odf.getSpreadsheetDocument();
                }
                break;

            case CADWORK:
                Cadwork2Odf cadwork2Odf = new Cadwork2Odf(lines);
                if (cadwork2Odf.convert(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = cadwork2Odf.getSpreadsheetDocument();
                }
                break;

            case BASEL_STADT:
                CsvBaselStadt2Odf csvBaselStadt2Odf = new CsvBaselStadt2Odf(csv);
                if (csvBaselStadt2Odf.convert(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = csvBaselStadt2Odf.getSpreadsheetDocument();
                }
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Odf txtBaselLandschaft2Odf = new TxtBaselLandschaft2Odf(lines);
                if (txtBaselLandschaft2Odf.convert(path.getFileName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = txtBaselLandschaft2Odf.getSpreadsheetDocument();
                }
                break;

            default:
                spreadsheetDocument = null;

                logger.warn("Can not write {} file format to Open Document spreadsheet file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteOdf2Disk.writeOds2Disk(path, spreadsheetDocument);
    }

}
