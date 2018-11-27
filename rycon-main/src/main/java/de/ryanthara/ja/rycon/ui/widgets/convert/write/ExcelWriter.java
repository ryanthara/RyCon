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

import de.ryanthara.ja.rycon.core.converter.excel.*;
import de.ryanthara.ja.rycon.nio.FileFormat;
import de.ryanthara.ja.rycon.nio.WriteExcel2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A writer for writing Microsoft Excel (XLS or XLSX) files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class ExcelWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(ExcelWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;
    private final FileFormat fileFormat;

    /**
     * Constructs the {@link ExcelWriter} with a set of parameters.
     *
     * @param path       file path to write into
     * @param csv        read csv file
     * @param lines      read string based file
     * @param parameter  the writer parameter object
     * @param fileFormat the file format
     */
    public ExcelWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter, FileFormat fileFormat) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
        this.fileFormat = fileFormat;
    }

    /**
     * Returns true if the prepared {@link Workbook} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeWorkbookFile() {
        String fileName = "";
        Workbook workbook = null;

        if (path != null) {
            Path p = path.getFileName();

            if (p != null) {
                fileName = p.toString();
            }
        }

        switch (Objects.requireNonNull(SourceButton.fromIndex(parameter.getSourceNumber()).orElse(null))) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi2Excel gsi2Excel = new Gsi2Excel(lines);
                if (gsi2Excel.convert(fileFormat, fileName, parameter.isWriteCommentLine())) {
                    workbook = gsi2Excel.getWorkbook();
                }
                break;

            case TXT:
                Txt2Excel txt2Excel = new Txt2Excel(lines);
                if (txt2Excel.convert(fileFormat, fileName)) {
                    workbook = txt2Excel.getWorkbook();
                }
                break;

            case CSV:
                Csv2Excel csv2Excel = new Csv2Excel(csv);
                if (csv2Excel.convert(fileFormat, fileName)) {
                    workbook = csv2Excel.getWorkbook();
                }
                break;

            case CAPLAN_K:
                Caplan2Excel caplan2Excel = new Caplan2Excel(lines);
                if (caplan2Excel.convert(fileFormat, fileName, parameter.isWriteCommentLine())) {
                    workbook = caplan2Excel.getWorkbook();
                }
                break;

            case ZEISS_REC:
                Zeiss2Excel zeiss2Excel = new Zeiss2Excel(lines);
                if (zeiss2Excel.convert(fileFormat, fileName, parameter.isWriteCommentLine())) {
                    workbook = zeiss2Excel.getWorkbook();
                }
                break;

            case CADWORK:
                Cadwork2Excel cadwork2Excel = new Cadwork2Excel(lines);
                if (cadwork2Excel.convert(fileFormat, fileName, parameter.isWriteCommentLine())) {
                    workbook = cadwork2Excel.getWorkbook();
                }
                break;

            case BASEL_STADT:
                CsvBaselStadt2Excel csvBaselStadt2Excel = new CsvBaselStadt2Excel(csv);
                if (csvBaselStadt2Excel.convert(fileFormat, fileName, parameter.isWriteCommentLine())) {
                    workbook = csvBaselStadt2Excel.getWorkbook();
                }
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Excel txtBaselLandschaft2Excel = new TxtBaselLandschaft2Excel(lines);
                if (txtBaselLandschaft2Excel.convert(fileFormat, fileName, parameter.isWriteCommentLine())) {
                    workbook = txtBaselLandschaft2Excel.getWorkbook();
                }
                break;

            default:
                workbook = null;

                logger.warn("Can not write {} file format to Excel spreadsheet file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        String suffix = fileFormat.getSuffix();

        return WriteExcel2Disk.writeExcel2Disk(path, workbook, suffix);
    }

}
