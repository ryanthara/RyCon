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

import de.ryanthara.ja.rycon.converter.text.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import de.ryanthara.ja.rycon.gui.widget.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing text files from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class TXTWriteFile implements WriteFile {

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link TXTWriteFile} with a set of parameters.
     *
     * @param path           read file object for writing
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public TXTWriteFile(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
                GSI2TXT gsi2TXT = new GSI2TXT(readStringFile);
                writeFile = gsi2TXT.convertGSI2TXT(parameter.getSeparatorTXT(), parameter.isGSI16(), parameter.isWriteCommentLine());
                break;

            case TXT:
                break;

            case CSV:
                CSV2TXT csv2TXT = new CSV2TXT(readCSVFile);
                writeFile = csv2TXT.convertCSV2TXT(parameter.getSeparatorTXT());
                break;

            case CAPLAN_K:
                Caplan2TXT caplan2TXT = new Caplan2TXT(readStringFile);
                writeFile = caplan2TXT.convertK2TXT(parameter.getSeparatorTXT(), parameter.isKFormatUseSimpleFormat(),
                        parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2TXT zeiss2TXT = new Zeiss2TXT(readStringFile);
                writeFile = zeiss2TXT.convertZeiss2TXT(parameter.getSeparatorTXT());
                break;

            case CADWORK:
                Cadwork2TXT cadwork2TXT = new Cadwork2TXT(readStringFile);
                writeFile = cadwork2TXT.convertCadwork2TXT(parameter.getSeparatorTXT(), parameter.isWriteCodeColumn(),
                        parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CSVBaselStadt2TXT csvBaselStadt2TXT = new CSVBaselStadt2TXT(readCSVFile);
                writeFile = csvBaselStadt2TXT.convertCSVBaselStadt2TXT(parameter.getSeparatorTXT());
                break;

            case BASEL_LANDSCHAFT:
                TXTBaselLandschaft2TXT txtBaselLandschaft2TXT = new TXTBaselLandschaft2TXT(readStringFile);
                writeFile = txtBaselLandschaft2TXT.convertTXTBaselLandschaft2TXT(parameter.getSeparatorTXT(), parameter.isWriteCodeColumn());
                break;

            default:
                writeFile = null;
                System.err.println("TXTWriteFile.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, ".TXT")) {
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

} // end of TXTWriteFile
