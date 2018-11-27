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

import de.ryanthara.ja.rycon.core.converter.text.*;
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
 * A writer for writing ASCII text files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class TxtWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(TxtWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link TxtWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param csv       read csv file
     * @param lines     read string based file
     * @param parameter the writer parameter object
     */
    public TxtWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
    }

    /**
     * Writes a ASCII text file depends on the source file format.
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
                Gsi2Txt gsi2Txt = new Gsi2Txt(lines);
                writeFile = gsi2Txt.convert(parameter.getSeparatorTXT(), parameter.isGSI16(), parameter.isWriteCommentLine());
                break;

            case TXT:
                // writing the same file format is not supported
                break;

            case CSV:
                Csv2Txt csv2Txt = new Csv2Txt(csv);
                writeFile = csv2Txt.convert(parameter.getSeparatorTXT());
                break;

            case CAPLAN_K:
                Caplan2Txt caplan2Txt = new Caplan2Txt(lines);
                writeFile = caplan2Txt.convert(parameter.getSeparatorTXT(), parameter.isKFormatUseSimpleFormat(),
                        parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2Txt zeiss2Txt = new Zeiss2Txt(lines);
                writeFile = zeiss2Txt.convert(parameter.getSeparatorTXT());
                break;

            case CADWORK:
                Cadwork2Txt cadwork2Txt = new Cadwork2Txt(lines);
                writeFile = cadwork2Txt.convert(parameter.getSeparatorTXT(), parameter.isWriteCodeColumn(),
                        parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Txt csvBaselStadt2Txt = new CsvBaselStadt2Txt(csv, parameter.isWriteZeroHeights());
                writeFile = csvBaselStadt2Txt.convert(parameter.getSeparatorTXT());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Txt TxtBaselLandschaft2Txt = new TxtBaselLandschaft2Txt(lines);
                writeFile = TxtBaselLandschaft2Txt.convert(parameter.getSeparatorTXT(), parameter.isWriteCodeColumn());
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to text file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.TXT.getExtension());
    }

}