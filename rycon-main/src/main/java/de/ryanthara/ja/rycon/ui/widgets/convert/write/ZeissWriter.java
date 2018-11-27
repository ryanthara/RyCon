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

import de.ryanthara.ja.rycon.core.converter.zeiss.*;
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
 * A writer for writing ZEISS REC files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class ZeissWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(ZeissWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link ZeissWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param csv       read csv file
     * @param lines     read string based file
     * @param parameter the writer parameter object
     */
    public ZeissWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
    }

    /**
     * Writes a Zeiss REC file and its dialects depends on the source file format.
     *
     * @return write success
     */
    @Override
    public boolean writeStringFile() {
        List<String> writeFile = null;

        switch (Objects.requireNonNull(SourceButton.fromIndex(parameter.getSourceNumber()).orElse(null))) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi2Zeiss gsi2Zeiss = new Gsi2Zeiss(lines);
                writeFile = gsi2Zeiss.convert(parameter.getDialect());
                break;

            case TXT:
                Txt2Zeiss txt2Zeiss = new Txt2Zeiss(lines);
                writeFile = txt2Zeiss.convert(parameter.getDialect());
                break;

            case CSV:
                Csv2Zeiss csv2Zeiss = new Csv2Zeiss(csv);
                writeFile = csv2Zeiss.convert(parameter.getDialect());
                break;

            case CAPLAN_K:
                Caplan2Zeiss caplan2Zeiss = new Caplan2Zeiss(lines);
                writeFile = caplan2Zeiss.convert(parameter.getDialect());
                break;

            case ZEISS_REC:
                // writing the same file format is not supported
                break;

            case CADWORK:
                Cadwork2Zeiss cadwork2Zeiss = new Cadwork2Zeiss(lines);
                writeFile = cadwork2Zeiss.convert(parameter.getDialect());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Zeiss csvBaselStadt2Zeiss = new CsvBaselStadt2Zeiss(csv);
                writeFile = csvBaselStadt2Zeiss.convert(parameter.getDialect());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Zeiss txtBaselLandschaft2Zeiss = new TxtBaselLandschaft2Zeiss(lines);
                writeFile = txtBaselLandschaft2Zeiss.convert(parameter.getDialect());
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to Zeiss REC file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.REC.getExtension());
    }

}
