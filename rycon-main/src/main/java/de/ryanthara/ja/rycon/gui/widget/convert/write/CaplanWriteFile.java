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

import de.ryanthara.ja.rycon.converter.caplan.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing Caplan K files from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CaplanWriteFile implements WriteFile {

    private ArrayList<String> readStringFile;
    private List<String[]> readCSVFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link CaplanWriteFile} with a set of parameters.
     *
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public CaplanWriteFile(ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
                GSI2K gsi2K = new GSI2K(readStringFile);
                writeFile = gsi2K.convertGSI2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;
            case 2:     // TXT format (space or tabulator separated)
                TXT2K txt2K = new TXT2K(readStringFile);
                writeFile = txt2K.convertTXT2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;
            case 3:     // CSV format (comma or semicolon separated)
                CSV2K csv2K = new CSV2K(readCSVFile);
                writeFile = csv2K.convertCSV2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case 4:     // CAPLAN K format (not possible)
                break;

            case 5:     // Zeiss REC format and it's dialects
                Zeiss2K zeiss2K = new Zeiss2K(readStringFile);
                writeFile = zeiss2K.convertZeiss2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case 6:     // cadwork node.dat from cadwork CAD program
                Cadwork2K cadwork2K = new Cadwork2K(readStringFile);
                writeFile = cadwork2K.convertCadwork2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                CSVBaselStadt2K csvBaselStadt2K = new CSVBaselStadt2K(readCSVFile);
                writeFile = csvBaselStadt2K.convertCSVBaselStadt2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;

            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                TXTBaselLandschaft2K txtBaselLandschaft2K = new TXTBaselLandschaft2K(readStringFile);
                writeFile = txtBaselLandschaft2K.convertTXTBaselLandschaft2K(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
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

} // end of CaplanWriteFile
