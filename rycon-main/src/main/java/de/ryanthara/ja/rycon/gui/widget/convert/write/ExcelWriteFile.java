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
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing Microsoft Excel (XLS or XLSX) files
 * from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class ExcelWriteFile implements WriteFile {

    private boolean isXLSX;
    private ArrayList<String> readStringFile;
    private File file;
    private List<String[]> readCSVFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link ExcelWriteFile} with a set of parameters.
     *
     * @param file           read file name
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object^
     * @param isXLSX         true if is XLSX
     */
    public ExcelWriteFile(File file, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter, boolean isXLSX) {
        this.file = file;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
        this.isXLSX = isXLSX;
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
        return null;
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
        Workbook workbook = null;

        switch (parameter.getSourceNumber()) {
            case 0:     // fall through for GSI8 format
            case 1:     // GSI16 format
                GSI2Excel gsi2Excel = new GSI2Excel(readStringFile);
                if (gsi2Excel.convertGSI2Excel(isXLSX, file.getName(), parameter.isWriteCommentLine())) {
                    workbook = gsi2Excel.getWorkbook();
                }
                break;
            case 2:     // TXT format (space or tabulator separated)
                TXT2Excel txt2Excel = new TXT2Excel(readStringFile);
                if (txt2Excel.convertTXT2Excel(isXLSX, file.getName())) {
                    workbook = txt2Excel.getWorkbook();
                }
                break;
            case 3:     // CSV format (comma or semicolon separated)
                CSV2Excel csv2Excel = new CSV2Excel(readCSVFile);
                if (csv2Excel.convertCSV2Excel(isXLSX, file.getName())) {
                    workbook = csv2Excel.getWorkbook();
                }
                break;

            case 4:     // CAPLAN K format
                Caplan2Excel caplan2Excel = new Caplan2Excel(readStringFile);
                if (caplan2Excel.convertCaplan2Excel(isXLSX, file.getName(), parameter.isWriteCommentLine())) {
                    workbook = caplan2Excel.getWorkbook();
                }
                break;

            case 5:     // Zeiss REC format and it's dialects
                Zeiss2Excel zeiss2Excel = new Zeiss2Excel(readStringFile);
                if (zeiss2Excel.convertZeiss2Excel(isXLSX, file.getName(), parameter.isWriteCommentLine())) {
                    workbook = zeiss2Excel.getWorkbook();
                }
                break;

            case 6:     // cadwork node.dat from cadwork CAD program
                Cadwork2Excel cadwork2Excel = new Cadwork2Excel(readStringFile);
                if (cadwork2Excel.convertCadwork2Excel(isXLSX, file.getName(), parameter.isWriteCommentLine())) {
                    workbook = cadwork2Excel.getWorkbook();
                }
                break;

            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                CSVBaselStadt2Excel csvBaselStadt2Excel = new CSVBaselStadt2Excel(readCSVFile);
                if (csvBaselStadt2Excel.convertCSVBaselStadt2Excel(isXLSX, file.getName(), parameter.isWriteCommentLine())) {
                    workbook = csvBaselStadt2Excel.getWorkbook();
                }
                break;

            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                TXTBaselLandschaft2Excel txtBaselLandschaft2Excel = new TXTBaselLandschaft2Excel(readStringFile);
                if (txtBaselLandschaft2Excel.convertTXTBaselLand2Excel(isXLSX, file.getName(), parameter.isWriteCommentLine())) {
                    workbook = txtBaselLandschaft2Excel.getWorkbook();
                }
                break;

            default:
                workbook = null;
                break;

        }

        return workbook;
    }

} // end of ExcelWriteFile
