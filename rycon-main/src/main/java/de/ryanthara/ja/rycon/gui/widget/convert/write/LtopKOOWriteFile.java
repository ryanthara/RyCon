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

import de.ryanthara.ja.rycon.converter.ltop.*;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import de.ryanthara.ja.rycon.gui.widget.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing LTOP KOO files from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class LtopKOOWriteFile implements WriteFile {

    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link LtopKOOWriteFile} with a set of parameters.
     *
     * @param path           read path object for writing
     * @param readCSVFile    read csv file
     * @param readStringFile read string file
     * @param parameter      the write parameter object
     */
    public LtopKOOWriteFile(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
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
        ArrayList<String> writeFile;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                GSI2LTOP gsi2LTOP = new GSI2LTOP(readStringFile);
                writeFile = gsi2LTOP.convertGSI2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case TXT:
                TXT2LTOP txt2LTOP = new TXT2LTOP(readStringFile);
                writeFile = txt2LTOP.convertTXT2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case CSV:
                CSV2LTOP csv2LTOP = new CSV2LTOP(readCSVFile);
                writeFile = csv2LTOP.convertCSV2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case CAPLAN_K:
                Caplan2LTOP caplan2LTOP = new Caplan2LTOP(readStringFile);
                writeFile = caplan2LTOP.convertK2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case ZEISS_REC:
                Zeiss2LTOP zeiss2LTOP = new Zeiss2LTOP(readStringFile);
                writeFile = zeiss2LTOP.convertZeiss2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case CADWORK:
                Cadwork2LTOP cadwork2LTOP = new Cadwork2LTOP(readStringFile);
                writeFile = cadwork2LTOP.convertCadwork2KOO(parameter.isCadworkUseZeroHeights(),
                        parameter.isLtopEliminateDuplicatePoints(), parameter.isLtopSortOutputFileByNumber());
                break;

            case BASEL_STADT:
                CSVBaselStadt2LTOP csvBaselStadt2LTOP = new CSVBaselStadt2LTOP(readCSVFile);
                writeFile = csvBaselStadt2LTOP.convertCSVBaselStadt2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            case BASEL_LANDSCHAFT:
                TXTBaselLandschaft2LTOP txtBaselLandschaft2LTOP = new TXTBaselLandschaft2LTOP(readStringFile);
                writeFile = txtBaselLandschaft2LTOP.convertTXTBaselLandschaft2KOO(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isLtopSortOutputFileByNumber());
                break;

            default:
                writeFile = null;
                System.err.println("LtopKOOWriteFile.writeStringFile() : unknown file format " + SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, ".KOO")) {
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

} // end of LtopKOOWriteFile
