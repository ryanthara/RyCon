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

import de.ryanthara.ja.rycon.core.converter.caplan.*;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A writer for writing Caplan K files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CaplanWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(CaplanWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link CaplanWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param csv       read csv file
     * @param lines     read string based file
     * @param parameter the writer parameter object
     */
    public CaplanWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
    }

    /**
     * Writes a Caplan K file depends on the source file format.
     *
     * @return write success
     */
    @Override
    public boolean writeStringFile() {
        List<String> writeFile = null;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi2K gsi2K = new Gsi2K(lines);
                writeFile = gsi2K.convert(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;

            case TXT:
                Txt2K txt2K = new Txt2K(lines);
                writeFile = txt2K.convert(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case CSV:
                Csv2K csv2K = new Csv2K(csv);
                writeFile = csv2K.convert(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case CAPLAN_K:
                // writing the same file format is not supported
                break;

            case ZEISS_REC:
                Zeiss2K zeiss2K = new Zeiss2K(lines);
                writeFile = zeiss2K.convert(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;

            case CADWORK:
                Cadwork2K cadwork2K = new Cadwork2K(lines);
                writeFile = cadwork2K.convert(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            case BASEL_STADT:
                CsvBaselStadt2K csvBaselStadt2K = new CsvBaselStadt2K(csv);
                writeFile = csvBaselStadt2K.convert(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2K txtBaselLandschaft2K = new TxtBaselLandschaft2K(lines);
                writeFile = txtBaselLandschaft2K.convert(parameter.isKFormatUseSimpleFormat(), parameter.isWriteCommentLine(), parameter.isWriteCodeColumn());
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to Caplan K file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.K.getExtension());
    }

}
