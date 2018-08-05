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

import de.ryanthara.ja.rycon.core.converter.csv.*;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instances of this class are used for writing comma separated values (CSV) files
 * from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CsvWriter implements Writer {

    private final static Logger logger = Logger.getLogger(CsvWriter.class.getName());

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link CsvWriter} with a set of parameters.
     *
     * @param path           reader path object for writing
     * @param readCSVFile    reader csv file
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object
     */
    public CsvWriter(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;
    }

    /**
     * Returns true if the prepared {@link SpreadsheetDocument} for file writing was written to the file system.
     *
     * @return writer success
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
                Gsi2Csv gsi2Csv = new Gsi2Csv(readStringFile);
                writeFile = gsi2Csv.convertGSI2CSV(parameter.getSeparatorCSV(), parameter.isWriteCommentLine());
                break;

            case TXT:
                Txt2Csv txt2Csv = new Txt2Csv(readStringFile);
                writeFile = txt2Csv.convertTXT2CSV(parameter.getSeparatorCSV());
                break;

            case CSV:
                break;

            case CAPLAN_K:
                Caplan2Csv caplan2Csv = new Caplan2Csv(readStringFile);
                writeFile = caplan2Csv.convertK2CSV(parameter.getSeparatorCSV(), parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2Csv zeiss2Csv = new Zeiss2Csv(readStringFile);
                writeFile = zeiss2Csv.convertZeiss2CSV(parameter.getSeparatorCSV());
                break;

            case CADWORK:
                Cadwork2Csv cadwork2Csv = new Cadwork2Csv(readStringFile);
                writeFile = cadwork2Csv.convertCadwork2CSV(parameter.getSeparatorCSV(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn(), parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Csv csvBaselStadt2Csv = new CsvBaselStadt2Csv(readCSVFile);
                writeFile = csvBaselStadt2Csv.convertCSVBaselStadt2CSV(parameter.getSeparatorCSV());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Csv txtBaselLandschaft2Csv = new TxtBaselLandschaft2Csv(readStringFile);
                writeFile = txtBaselLandschaft2Csv.convertTXTBaselLandschaft2CSV(parameter.getSeparatorCSV());
                break;

            default:
                writeFile = null;

                logger.log(Level.SEVERE, "CsvWriter.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.CSV.getExtension())) {
            success = true;
        }

        return success;
    }

    /**
     * Returns true if the prepared {@link Workbook} for file writing was written to the file system.
     *
     * @return writer success
     */
    @Override
    public boolean writeWorkbookFile() {
        return false;
    }

} // end of CsvWriter
