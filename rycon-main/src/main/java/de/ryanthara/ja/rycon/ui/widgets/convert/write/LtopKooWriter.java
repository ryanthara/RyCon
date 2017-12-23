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

import de.ryanthara.ja.rycon.core.converter.ltop.*;
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
 * Instances of this class are used for writing LTOP KOO files from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class LtopKooWriter implements Writer {

    private final static Logger logger = Logger.getLogger(LtopKooWriter.class.getName());

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link LtopKooWriter} with a set of parameters.
     *
     * @param path           reader path object for writing
     * @param readCSVFile    reader csv file
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object
     */
    public LtopKooWriter(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
        ArrayList<String> writeFile;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                Gsi2Ltop gsi2Ltop = new Gsi2Ltop(readStringFile);
                writeFile = gsi2Ltop.convertGSI2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case TXT:
                Txt2Ltop txt2Ltop = new Txt2Ltop(readStringFile);
                writeFile = txt2Ltop.convertTXT2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case CSV:
                Csv2Ltop csv2Ltop = new Csv2Ltop(readCSVFile);
                writeFile = csv2Ltop.convertCSV2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case CAPLAN_K:
                Caplan2Ltop caplan2Ltop = new Caplan2Ltop(readStringFile);
                writeFile = caplan2Ltop.convertK2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case ZEISS_REC:
                Zeiss2Ltop zeiss2Ltop = new Zeiss2Ltop(readStringFile);
                writeFile = zeiss2Ltop.convertZeiss2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case CADWORK:
                Cadwork2Ltop cadwork2Ltop = new Cadwork2Ltop(readStringFile);
                writeFile = cadwork2Ltop.convertCadwork2KOO(parameter.isCadworkUseZeroHeights(),
                        parameter.isLtopEliminateDuplicatePoints(), parameter.isLtopSortOutputFileByNumber());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Ltop csvBaselStadt2Ltop = new CsvBaselStadt2Ltop(readCSVFile);
                writeFile = csvBaselStadt2Ltop.convertCSVBaselStadt2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Ltop txtBaselLandschaft2Ltop = new TxtBaselLandschaft2Ltop(readStringFile);
                writeFile = txtBaselLandschaft2Ltop.convertTXTBaselLandschaft2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            default:
                writeFile = null;

                logger.log(Level.SEVERE, "LtopKooWriter.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, "", ".KOO")) {
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

} // end of LtopKooWriter
