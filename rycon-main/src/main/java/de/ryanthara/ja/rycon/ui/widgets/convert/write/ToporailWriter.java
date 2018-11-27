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

import de.ryanthara.ja.rycon.core.converter.toporail.Gsi2Toporail;
import de.ryanthara.ja.rycon.nio.FileFormat;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A writer for writing Toporail MEP and PTS files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class ToporailWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(ToporailWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;
    private String fileNameExtension;

    /**
     * Constructs the {@link ToporailWriter} with a set of parameters.
     *
     * @param path       file path to write into
     * @param csv        read csv file
     * @param lines      read string based file
     * @param parameter  the writer parameter object
     * @param fileFormat the file format
     */
    public ToporailWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter, FileFormat fileFormat) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;

        switch (fileFormat) {
            case MEP:
                this.fileNameExtension = FileNameExtension.MEP.getExtension();
                break;
            case PTS:
                this.fileNameExtension = FileNameExtension.TPS.getExtension();
                break;
        }
    }

    /**
     * Writes a Toporail MEP or PTS file depends on the source file format.
     *
     * @param fileFormat file typ of the output file (MEP or PTS)
     * @return write success
     */
    public boolean writeStringFile(FileFormat fileFormat) {
        boolean success = false;
        java.util.List<String> writeFile = null;

        switch (Objects.requireNonNull(SourceButton.fromIndex(parameter.getSourceNumber()).orElse(null))) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi2Toporail gsi2Toporail = new Gsi2Toporail(lines);
                writeFile = gsi2Toporail.convertGsi2Toporail(fileFormat);
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

}
