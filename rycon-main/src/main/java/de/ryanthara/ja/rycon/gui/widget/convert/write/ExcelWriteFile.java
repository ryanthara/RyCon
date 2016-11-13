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

import de.ryanthara.ja.rycon.converter.excel.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import de.ryanthara.ja.rycon.gui.widget.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing Microsoft Excel (XLS or XLSX) files
 * from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class ExcelWriteFile implements WriteFile {

    private final boolean isXLSX;
    private final ArrayList<String> readStringFile;
    private final Path path;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link ExcelWriteFile} with a set of parameters.
     *
     * @param path           read file object for writing
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object^
     * @param isXLSX         true if is XLSX
     */
    public ExcelWriteFile(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter, boolean isXLSX) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
        this.isXLSX = isXLSX;
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
        Workbook workbook = null;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                GSI2Excel gsi2Excel = new GSI2Excel(readStringFile);
                if (gsi2Excel.convertGSI2Excel(isXLSX, path.getFileName().toString(), parameter.isWriteCommentLine())) {
                    workbook = gsi2Excel.getWorkbook();
                }
                break;

            case TXT:
                TXT2Excel txt2Excel = new TXT2Excel(readStringFile);
                if (txt2Excel.convertTXT2Excel(isXLSX, path.getFileName().toString())) {
                    workbook = txt2Excel.getWorkbook();
                }
                break;

            case CSV:
                CSV2Excel csv2Excel = new CSV2Excel(readCSVFile);
                if (csv2Excel.convertCSV2Excel(isXLSX, path.getFileName().toString())) {
                    workbook = csv2Excel.getWorkbook();
                }
                break;

            case CAPLAN_K:
                Caplan2Excel caplan2Excel = new Caplan2Excel(readStringFile);
                if (caplan2Excel.convertCaplan2Excel(isXLSX, path.getFileName().toString(), parameter.isWriteCommentLine())) {
                    workbook = caplan2Excel.getWorkbook();
                }
                break;

            case ZEISS_REC:
                Zeiss2Excel zeiss2Excel = new Zeiss2Excel(readStringFile);
                if (zeiss2Excel.convertZeiss2Excel(isXLSX, path.getFileName().toString(), parameter.isWriteCommentLine())) {
                    workbook = zeiss2Excel.getWorkbook();
                }
                break;

            case CADWORK:
                Cadwork2Excel cadwork2Excel = new Cadwork2Excel(readStringFile);
                if (cadwork2Excel.convertCadwork2Excel(isXLSX, path.getFileName().toString(), parameter.isWriteCommentLine())) {
                    workbook = cadwork2Excel.getWorkbook();
                }
                break;

            case BASEL_STADT:
                CSVBaselStadt2Excel csvBaselStadt2Excel = new CSVBaselStadt2Excel(readCSVFile);
                if (csvBaselStadt2Excel.convertCSVBaselStadt2Excel(isXLSX, path.getFileName().toString(), parameter.isWriteCommentLine())) {
                    workbook = csvBaselStadt2Excel.getWorkbook();
                }
                break;

            case BASEL_LANDSCHAFT:
                TXTBaselLandschaft2Excel txtBaselLandschaft2Excel = new TXTBaselLandschaft2Excel(readStringFile);
                if (txtBaselLandschaft2Excel.convertTXTBaselLand2Excel(isXLSX, path.getFileName().toString(), parameter.isWriteCommentLine())) {
                    workbook = txtBaselLandschaft2Excel.getWorkbook();
                }
                break;

            default:
                workbook = null;
                System.err.println("ExcelWriteFile.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));

        }

        String suffix = isXLSX ? ".xlsx" : ".xls";

        if (WriteExcel2Disk.writeExcel2Disk(path, workbook, suffix)) {
            success = true;
        }

        return success;
    }

} // end of ExcelWriteFile
