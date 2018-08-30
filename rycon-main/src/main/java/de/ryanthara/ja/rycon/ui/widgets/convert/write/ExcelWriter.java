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
import de.ryanthara.ja.rycon.nio.WriteExcel2Disk;
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
 * Instances of this class are used for writing Microsoft Excel (XLS or XLSX) files
 * from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class ExcelWriter implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(ExcelWriter.class.getName());

    private final boolean isXLS;
    private final ArrayList<String> readStringFile;
    private final Path path;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link ExcelWriter} with a set of parameters.
     *
     * @param path           reader file object for writing
     * @param readCSVFile    reader csv file
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object^
     * @param isXLS          true if is XLS
     */
    public ExcelWriter(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile,
                       WriteParameter parameter, boolean isXLS) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
        this.isXLS = isXLS;
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
        return false;
    }

    /**
     * Returns true if the prepared {@link Workbook} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeWorkbookFile() {
        boolean success = false;
        String fileName = "";
        Workbook workbook = null;

        if (path != null) {
            Path p = path.getFileName();

            if (p != null) {
                fileName = p.toString();
            }
        }

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                Gsi2Excel gsi2Excel = new Gsi2Excel(readStringFile);
                if (gsi2Excel.convertGSI2Excel(isXLS, fileName, parameter.isWriteCommentLine())) {
                    workbook = gsi2Excel.getWorkbook();
                }
                break;

            case TXT:
                Txt2Excel txt2Excel = new Txt2Excel(readStringFile);
                if (txt2Excel.convertTXT2Excel(isXLS, fileName)) {
                    workbook = txt2Excel.getWorkbook();
                }
                break;

            case CSV:
                Csv2Excel csv2Excel = new Csv2Excel(readCSVFile);
                if (csv2Excel.convertCSV2Excel(isXLS, fileName)) {
                    workbook = csv2Excel.getWorkbook();
                }
                break;

            case CAPLAN_K:
                Caplan2Excel caplan2Excel = new Caplan2Excel(readStringFile);
                if (caplan2Excel.convertCaplan2Excel(isXLS, fileName, parameter.isWriteCommentLine())) {
                    workbook = caplan2Excel.getWorkbook();
                }
                break;

            case ZEISS_REC:
                Zeiss2Excel zeiss2Excel = new Zeiss2Excel(readStringFile);
                if (zeiss2Excel.convertZeiss2Excel(isXLS, fileName, parameter.isWriteCommentLine())) {
                    workbook = zeiss2Excel.getWorkbook();
                }
                break;

            case CADWORK:
                Cadwork2Excel cadwork2Excel = new Cadwork2Excel(readStringFile);
                if (cadwork2Excel.convertCadwork2Excel(isXLS, fileName, parameter.isWriteCommentLine())) {
                    workbook = cadwork2Excel.getWorkbook();
                }
                break;

            case BASEL_STADT:
                CsvBaselStadt2Excel csvBaselStadt2Excel = new CsvBaselStadt2Excel(readCSVFile);
                if (csvBaselStadt2Excel.convertCsvBaselStadt2Excel(isXLS, fileName, parameter.isWriteCommentLine())) {
                    workbook = csvBaselStadt2Excel.getWorkbook();
                }
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Excel txtBaselLandschaft2Excel = new TxtBaselLandschaft2Excel(readStringFile);
                if (txtBaselLandschaft2Excel.convertTXTBaselLand2Excel(isXLS, fileName, parameter.isWriteCommentLine())) {
                    workbook = txtBaselLandschaft2Excel.getWorkbook();
                }
                break;

            default:
                workbook = null;

                logger.warn("Can not write {} file format to Excel spreadsheet file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        String suffix = isXLS ? ".xls" : ".xlsx";

        if (WriteExcel2Disk.writeExcel2Disk(path, workbook, suffix)) {
            success = true;
        }

        return success;
    }

} // end of ExcelWriter
