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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A writer for writing comma separated values (CSV) files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CsvWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(CsvWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link CsvWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param csv       read csv file
     * @param lines     read string based file
     * @param parameter the writer parameter object
     */
    public CsvWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
    }

    /**
     * Writes a comma separated values (CSV) file depends on the source file format.
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
                Gsi2Csv gsi2Csv = new Gsi2Csv(lines);
                writeFile = gsi2Csv.convert(parameter.getSeparatorCSV(), parameter.isWriteCommentLine());
                break;

            case TXT:
                Txt2Csv txt2Csv = new Txt2Csv(lines);
                writeFile = txt2Csv.convert(parameter.getSeparatorCSV());
                break;

            case CSV:
                // writing the same file format is not supported
                break;

            case CAPLAN_K:
                Caplan2Csv caplan2Csv = new Caplan2Csv(lines);
                writeFile = caplan2Csv.convert(parameter.getSeparatorCSV(), parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2Csv zeiss2Csv = new Zeiss2Csv(lines);
                writeFile = zeiss2Csv.convert(parameter.getSeparatorCSV());
                break;

            case CADWORK:
                Cadwork2Csv cadwork2Csv = new Cadwork2Csv(lines);
                writeFile = cadwork2Csv.convert(parameter.getSeparatorCSV(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn(), parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Csv csvBaselStadt2Csv = new CsvBaselStadt2Csv(csv);
                writeFile = csvBaselStadt2Csv.convert(parameter.getSeparatorCSV());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Csv txtBaselLandschaft2Csv = new TxtBaselLandschaft2Csv(lines);
                writeFile = txtBaselLandschaft2Csv.convert(parameter.getSeparatorCSV());
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to csv file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.CSV.getExtension());
    }

}
