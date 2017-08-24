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
package de.ryanthara.ja.rycon.gui.widgets.convert.write;

import de.ryanthara.ja.rycon.converter.gsi.*;
import de.ryanthara.ja.rycon.gui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.gui.widgets.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
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

    private final Path path;
    private final boolean isGSI16;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link GSIWriteFile} with a set of parameters.
     *
     * @param path           read file object as {@link java.nio.file.Path} for writing
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public GSIWriteFile(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter, boolean isGSI16) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
        this.isGSI16 = isGSI16;
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
        ArrayList<String> writeFile;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                GSI8vsGSI16 gsi8vsGSI16 = new GSI8vsGSI16(readStringFile);
                writeFile = gsi8vsGSI16.convertGSI8vsGSI16(isGSI16);
                break;

            case TXT:
                TXT2GSI txt2GSI = new TXT2GSI(readStringFile);
                writeFile = txt2GSI.convertTXT2GSI(isGSI16, parameter.sourceContainsCode());
                break;

            case CSV:
                CSV2GSI csv2GSI = new CSV2GSI(readCSVFile);
                writeFile = csv2GSI.convertCSV2GSI(isGSI16, parameter.sourceContainsCode());
                break;

            case CAPLAN_K:
                Caplan2GSI caplan2GSI = new Caplan2GSI(readStringFile);
                writeFile = caplan2GSI.convertK2GSI(isGSI16, parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2GSI zeiss2GSI = new Zeiss2GSI(readStringFile);
                writeFile = zeiss2GSI.convertZeiss2GSI(isGSI16);
                break;

            case CADWORK:
                Cadwork2GSI cadwork2GSI = new Cadwork2GSI(readStringFile);
                writeFile = cadwork2GSI.convertCadwork2GSI(isGSI16, parameter.isWriteCodeColumn(), parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CSVBaselStadt2GSI csvBaselStadt2GSI = new CSVBaselStadt2GSI(readCSVFile);
                writeFile = csvBaselStadt2GSI.convertCSVBaselStadt2GSI(isGSI16, parameter.sourceContainsCode());
                break;

            case BASEL_LANDSCHAFT:
                TXTBaselLandschaft2GSI txtBaselLandschaft2GSI = new TXTBaselLandschaft2GSI(readStringFile);
                writeFile = txtBaselLandschaft2GSI.convertTXTBaselLandschaft2GSI(isGSI16, parameter.isWriteCodeColumn());
                break;

            default:
                writeFile = null;
                System.err.println("GSIWriteFile.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));

        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, ".GSI")) {
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

} // end of GSIWriteFile
