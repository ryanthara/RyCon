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
import de.ryanthara.ja.rycon.gui.widget.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing comma separated values (CSV) files
 * from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CSVWriteFile implements WriteFile {

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link CSVWriteFile} with a set of parameters.
     *
     * @param path           read path object for writing
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public CSVWriteFile(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
                GSI2CSV gsi2CSV = new GSI2CSV(readStringFile);
                writeFile = gsi2CSV.convertGSI2CSV(parameter.getSeparatorCSV(), parameter.isWriteCommentLine());
                break;

            case TXT:
                TXT2CSV txt2CSV = new TXT2CSV(readStringFile);
                writeFile = txt2CSV.convertTXT2CSV(parameter.getSeparatorCSV());
                break;

            case CSV:
                break;

            case CAPLAN_K:
                Caplan2CSV caplan2CSV = new Caplan2CSV(readStringFile);
                writeFile = caplan2CSV.convertK2CSV(parameter.getSeparatorCSV(), parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2CSV zeiss2CSV = new Zeiss2CSV(readStringFile);
                zeiss2CSV.convertZeiss2CSV(parameter.getSeparatorCSV(), parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case CADWORK:
                Cadwork2CSV cadwork2CSV = new Cadwork2CSV(readStringFile);
                writeFile = cadwork2CSV.convertCadwork2CSV(parameter.getSeparatorCSV(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn(), parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CSVBaselStadt2CSV csvBaselStadt2CSV = new CSVBaselStadt2CSV(readCSVFile);
                writeFile = csvBaselStadt2CSV.convertCSVBaselStadt2CSV(parameter.getSeparatorCSV());
                break;

            case BASEL_LANDSCHAFT:
                TXTBaselLandschaft2CSV txtBaselLandschaft2CSV = new TXTBaselLandschaft2CSV(readStringFile);
                writeFile = txtBaselLandschaft2CSV.convertTXTBaselLandschaft2CSV(parameter.getSeparatorCSV());
                break;

            default:
                writeFile = null;
                System.err.println("CSVWriteFile.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, ".CSV")) {
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

} // end of CSVWriteFile
