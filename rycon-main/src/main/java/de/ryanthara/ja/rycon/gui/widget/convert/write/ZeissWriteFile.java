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

import de.ryanthara.ja.rycon.converter.zeiss.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing Zeiss REC files and it's dialects (R4, R5, REC500 and M5)
 * from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class ZeissWriteFile implements WriteFile {

    private ArrayList<String> readStringFile;
    private List<String[]> readCSVFile;
    private WriteParameter parameter;

    /**
     * Constructs the {@link ZeissWriteFile} with a set of parameters.
     *
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public ZeissWriteFile(ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
                GSI2Zeiss gsi2Zeiss = new GSI2Zeiss(readStringFile);
                writeFile = gsi2Zeiss.convertGSI2REC(parameter.getDialect());
                break;

            case 2:     // TXT format
                TXT2Zeiss txt2Zeiss = new TXT2Zeiss(readStringFile);
                writeFile = txt2Zeiss.convertTXT2REC(parameter.getDialect());
                break;

            case 3:     // CSV format (comma or semicolon separated)
                CSV2Zeiss csv2Zeiss = new CSV2Zeiss(readCSVFile);
                writeFile = csv2Zeiss.convertCSV2REC(parameter.getDialect());
                break;

            case 4:     // CAPLAN K format
                K2Zeiss k2Zeiss = new K2Zeiss(readStringFile);
                writeFile = k2Zeiss.convertK2REC(parameter.getDialect());
                break;

            case 5:     // Zeiss REC format and it's dialects (not possible or into dialects?)
                break;

            case 6:     // cadwork node.dat from cadwork CAD program
                Cadwork2Zeiss cadwork2Zeiss = new Cadwork2Zeiss(readStringFile);
                writeFile = cadwork2Zeiss.convertCadwork2REC(parameter.getDialect());
                break;

            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                CSVBaselStadt2Zeiss csvBaselStadt2Zeiss = new CSVBaselStadt2Zeiss(readCSVFile);
                writeFile = csvBaselStadt2Zeiss.convertCSVBaselStadt2REC(parameter.getDialect());
                break;

            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                TXTBaselLandschaft2Zeiss txtBaselLandschaft2Zeiss = new TXTBaselLandschaft2Zeiss(readStringFile);
                writeFile = txtBaselLandschaft2Zeiss.convertTXTBaselLandschaft2REC(parameter.getDialect());
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

} // end of ZeissWriteFile
