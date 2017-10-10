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

import de.ryanthara.ja.rycon.converter.zeiss.*;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing Zeiss REC files and it's dialects (R4, R5, REC500 and M5)
 * from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class ZeissWriteFile implements WriteFile {

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link ZeissWriteFile} with a set of parameters.
     *
     * @param path           read file object for writing
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public ZeissWriteFile(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
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
        boolean success = false;
        ArrayList<String> writeFile = null;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                GSI2Zeiss gsi2Zeiss = new GSI2Zeiss(readStringFile);
                writeFile = gsi2Zeiss.convertGSI2REC(parameter.getDialect());
                break;

            case TXT:
                TXT2Zeiss txt2Zeiss = new TXT2Zeiss(readStringFile);
                writeFile = txt2Zeiss.convertTXT2REC(parameter.getDialect());
                break;

            case CSV:
                CSV2Zeiss csv2Zeiss = new CSV2Zeiss(readCSVFile);
                writeFile = csv2Zeiss.convertCSV2REC(parameter.getDialect());
                break;

            case CAPLAN_K:
                Caplan2Zeiss caplan2Zeiss = new Caplan2Zeiss(readStringFile);
                writeFile = caplan2Zeiss.convertK2REC(parameter.getDialect());
                break;

            case ZEISS_REC:
                break;

            case CADWORK:
                Cadwork2Zeiss cadwork2Zeiss = new Cadwork2Zeiss(readStringFile);
                writeFile = cadwork2Zeiss.convertCadwork2REC(parameter.getDialect());
                break;

            case BASEL_STADT:
                CSVBaselStadt2Zeiss csvBaselStadt2Zeiss = new CSVBaselStadt2Zeiss(readCSVFile);
                writeFile = csvBaselStadt2Zeiss.convertCSVBaselStadt2REC(parameter.getDialect());
                break;

            case BASEL_LANDSCHAFT:
                TXTBaselLandschaft2Zeiss txtBaselLandschaft2Zeiss = new TXTBaselLandschaft2Zeiss(readStringFile);
                writeFile = txtBaselLandschaft2Zeiss.convertTXTBaselLandschaft2REC(parameter.getDialect());
                break;

            default:
                writeFile = null;
                System.err.println("ZeissWriteFile.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, ".REC")) {
            success = true;
        }

        return success;
    }

    /**
     * Returns true if the prepared {@link Workbook} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeWorkbookFile() {
        return false;
    }

} // end of ZeissWriteFile
