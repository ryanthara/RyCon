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

import de.ryanthara.ja.rycon.converter.csv.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing comma separated values (CSV) files
 * from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CSVWriteFile implements WriteFile {

    private ArrayList<String> readStringFile;
    private List<String[]> readCSVFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link CSVWriteFile} with a set of parameters.
     *
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public CSVWriteFile(ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
        return null;
    }

    /**
     * Returns the prepared {@link ArrayList} for file writing.
     *
     * @return array list for file writing
     */
    @Override
    public ArrayList<String> writeStringFile() {
        ArrayList<String> writeFile = null;

        switch (parameter.getSourceNumber()) {
            case 0:     // fall through for GSI8 format
            case 1:     // GSI16 format
                GSI2CSV gsi2CSV = new GSI2CSV(readStringFile);
                writeFile = gsi2CSV.convertGSI2CSV(parameter.getSeparatorCSV(), parameter.isWriteCommentLine());
                break;

//            parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn()

            case 2:     // TXT format (space or tabulator separated)
                TXT2CSV txt2CSV = new TXT2CSV(readStringFile);
                writeFile = txt2CSV.convertTXT2CSV(parameter.getSeparatorCSV());
                break;

            case 3:     // CSV format (not possible)
                break;

            case 4:     // CAPLAN K format
                K2CSV k2CSV = new K2CSV(readStringFile);
                writeFile = k2CSV.convertK2CSV(parameter.getSeparatorCSV(), parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case 5:     // Zeiss REC format and it's dialects
                Zeiss2CSV zeiss2CSV = new Zeiss2CSV(readStringFile);
                zeiss2CSV.convertZeiss2CSV(parameter.getSeparatorCSV(), parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case 6:     // cadwork node.dat from cadwork CAD program
                Cadwork2CSV cadwork2CSV = new Cadwork2CSV(readStringFile);
                writeFile = cadwork2CSV.convertCadwork2CSV(parameter.getSeparatorCSV(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn(), parameter.isCadworkUseZeroHeights());
                break;

            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                CSVBaselStadt2CSV csvBaselStadt2CSV = new CSVBaselStadt2CSV(readCSVFile);
                writeFile = csvBaselStadt2CSV.convertCSVBaselStadt2CSV(parameter.getSeparatorCSV());
                break;

            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                TXTBaselLandschaft2CSV txtBaselLandschaft2CSV = new TXTBaselLandschaft2CSV(readStringFile);
                writeFile = txtBaselLandschaft2CSV.convertTXTBaselLandschaft2CSV(parameter.getSeparatorCSV());
                break;

            default:
                writeFile = null;
                break;
        }

        return writeFile;
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

} // end of CSVWriteFile
