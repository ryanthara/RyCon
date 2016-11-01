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

import de.ryanthara.ja.rycon.converter.gsi.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing Leica GSI files from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSIWriteFile implements WriteFile {

    private boolean isGSI16;
    private ArrayList<String> readStringFile;
    private List<String[]> readCSVFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link GSIWriteFile} with a set of parameters.
     *
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public GSIWriteFile(ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter, boolean isGSI16) {
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
        this.isGSI16 = isGSI16;
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
        ArrayList<String> writeFile;

        switch (parameter.getSourceNumber()) {
            case 0:     // GSI8 format
            case 1:     // GSI16 format
                GSI8vsGSI16 gsi8vsGSI16 = new GSI8vsGSI16(readStringFile);
                writeFile = gsi8vsGSI16.convertGSI8vsGSI16(isGSI16);
                break;

            case 2:     // TXT format (space or tabulator separated)
                TXT2GSI txt2GSI = new TXT2GSI(readStringFile);
                writeFile = txt2GSI.convertTXT2GSI(isGSI16, parameter.sourceContainsCode());
                break;

            case 3:     // CSV format (comma or semicolon separated)
                CSV2GSI csv2GSI = new CSV2GSI(readCSVFile);
                writeFile = csv2GSI.convertCSV2GSI(isGSI16, parameter.sourceContainsCode());
                break;

            case 4:     // CAPLAN K format
                K2GSI k2GSI = new K2GSI(readStringFile);
                writeFile = k2GSI.convertK2GSI(isGSI16, parameter.isWriteCodeColumn());
                break;

            case 5:     // Zeiss REC format and it's dialects
                Zeiss2GSI zeiss2GSI = new Zeiss2GSI(readStringFile);
                writeFile = zeiss2GSI.convertZeiss2GSI(isGSI16);
                break;

            case 6:     // cadwork node.dat from cadwork CAD program
                Cadwork2GSI cadwork2GSI = new Cadwork2GSI(readStringFile);
                writeFile = cadwork2GSI.convertCadwork2GSI(isGSI16, parameter.isWriteCodeColumn(), parameter.isCadworkUseZeroHeights());
                break;

            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                CSVBaselStadt2GSI csvBaselStadt2GSI = new CSVBaselStadt2GSI(readCSVFile);
                writeFile = csvBaselStadt2GSI.convertCSVBaselStadt2GSI(isGSI16, parameter.sourceContainsCode());
                break;

            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                TXTBaselLandschaft2GSI txtBaselLandschaft2GSI = new TXTBaselLandschaft2GSI(readStringFile);
                writeFile = txtBaselLandschaft2GSI.convertTXTBaselLandschaft2GSI(isGSI16, parameter.isWriteCodeColumn());
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

} // end of GSIWriteFile
