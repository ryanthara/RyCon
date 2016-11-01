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

import de.ryanthara.ja.rycon.converter.odf.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing OpenDocument spreadsheet files
 * from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class ODFWriteFile implements WriteFile {

    private File file;
    private ArrayList<String> readStringFile;
    private List<String[]> readCSVFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link ODFWriteFile} with a set of parameters.
     *
     * @param file           output file
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public ODFWriteFile(File file, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
        this.file = file;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
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
        SpreadsheetDocument spreadsheetDocument = null;

        switch (parameter.getSourceNumber()) {
            case 0:     // fall through for GSI8 format
            case 1:     // GSI16 format
                GSI2ODF gsi2ODF = new GSI2ODF(readStringFile);
                if (gsi2ODF.convertGSI2ODS(file.getName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = gsi2ODF.getSpreadsheetDocument();
                }
                break;

            case 2:     // TXT format (space or tabulator separated)
                TXT2ODF txt2ODF = new TXT2ODF(readStringFile);
                if (txt2ODF.convertTXT2ODS(file.getName())) {
                    spreadsheetDocument = txt2ODF.getSpreadsheetDocument();
                }
                break;

            case 3:     // CSV format (comma or semicolon separated)
                CSV2ODF csv2ODF = new CSV2ODF(readCSVFile);
                if (csv2ODF.convertCSV2ODS(file.getName())) {
                    spreadsheetDocument = csv2ODF.getSpreadsheetDocument();
                }
                break;

            case 4:     // CAPLAN K format
                K2ODF k2ODF = new K2ODF(readStringFile);
                if (k2ODF.convertCaplan2ODS(file.getName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = k2ODF.getSpreadsheetDocument();
                }
                break;

            case 5:     // Zeiss M5 format and it's dialects
                Zeiss2ODF zeiss2ODF = new Zeiss2ODF(readStringFile);
                if (zeiss2ODF.convertZeiss2ODS(file.getName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = zeiss2ODF.getSpreadsheetDocument();
                }
                break;

            case 6:     // cadwork node.dat from cadwork CAD program
                Cadwork2ODF cadwork2ODF = new Cadwork2ODF(readStringFile);
                if (cadwork2ODF.convertCadwork2ODS(file.getName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = cadwork2ODF.getSpreadsheetDocument();
                }
                break;

            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                CSVBaselStadt2ODF csvBaselStadt2ODF = new CSVBaselStadt2ODF(readCSVFile);
                if (csvBaselStadt2ODF.convertCSVBaselStadt2ODS(file.getName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = csvBaselStadt2ODF.getSpreadsheetDocument();
                }
                break;

            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                TXTBaselLandschaft2ODF txtBaselLandschaft2ODF = new TXTBaselLandschaft2ODF(readStringFile);
                if (txtBaselLandschaft2ODF.convertTXTBaselLandschaft2ODS(file.getName(), parameter.isWriteCommentLine())) {
                    spreadsheetDocument = txtBaselLandschaft2ODF.getSpreadsheetDocument();
                }
                break;

            default:
                spreadsheetDocument = null;
                break;
        }

        return spreadsheetDocument;
    }

    /**
     * Returns the prepared {@link ArrayList} for file writing.
     * <p>
     * This method is used vise versa with {@link #writeSpreadsheetDocument()} and {@link #writeWorkbookFile()}.
     * The ones which are not used, returns null for indication.
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
        return null;
    }

} // end of ODFWriteFile
