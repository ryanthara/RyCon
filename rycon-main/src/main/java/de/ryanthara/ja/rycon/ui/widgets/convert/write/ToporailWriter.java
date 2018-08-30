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

import de.ryanthara.ja.rycon.core.converter.toporail.FileType;
import de.ryanthara.ja.rycon.core.converter.toporail.Gsi2Toporail;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for writing text files from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class ToporailWriter implements Writer {

    private static final Logger logger = LoggerFactory.getLogger(ToporailWriter.class.getName());
    private final Path path;
    private final ArrayList<String> readStringFile;
    private final List<String[]> readCSVFile;
    private final WriteParameter parameter;
    private String fileNameExtension;

    /**
     * Constructs the {@link ToporailWriter} with a set of parameters.
     *
     * @param path           reader file object for writing
     * @param readCSVFile    reader csv file
     * @param readStringFile reader string file
     * @param parameter      the writer parameter object
     * @param fileTyp        the file type
     */
    public ToporailWriter(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter, FileType fileTyp) {
        this.path = path;
        this.readStringFile = readStringFile;
        this.readCSVFile = readCSVFile;
        this.parameter = parameter;

        switch (fileTyp) {
            case MEP:
                this.fileNameExtension = FileNameExtension.MEP.getExtension();
                break;
            case PTS:
                this.fileNameExtension = FileNameExtension.TPS.getExtension();
                break;
        }
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
     * Returns true if the prepared {@link ArrayList} for file writing was written to the file system.
     *
     * @return write success
     */
    @Override
    public boolean writeStringFile() {
        return false;
    }

    /**
     * Returns the prepared {@link ArrayList} for file writing.
     *
     * @param fileType file typ of the output file (MEP or PTS)
     *
     * @return array list for file writing
     */
    public boolean writeStringFile(FileType fileType) {
        boolean success = false;
        ArrayList<String> writeFile = null;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
            case GSI16:
                Gsi2Toporail gsi2Toporail = new Gsi2Toporail(readStringFile);
                writeFile = gsi2Toporail.convertGsi2Toporail(fileType);
                break;

            case TXT:
                break;

            case CSV:
                break;

            case CAPLAN_K:
                break;

            case ZEISS_REC:
                break;

            case CADWORK:
                break;

            case BASEL_STADT:
                break;

            case BASEL_LANDSCHAFT:
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to Toporail MEP or TPS file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        if (WriteFile2Disk.writeFile2Disk(path, writeFile, "", fileNameExtension)) {
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

} // end of ToporailWriter
