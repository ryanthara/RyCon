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
 * A writer for writing LTOP KOO files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class LtopKooWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(LtopKooWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link LtopKooWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param csv       read csv file
     * @param lines     read string based file
     * @param parameter the writer parameter object
     */
    public LtopKooWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
    }

    /**
     * Writes a LTOP KOO file depends on the source file format.
     *
     * @return write success
     */
    @Override
    public boolean writeStringFile() {
        List<String> writeFile;

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi2Ltop gsi2Ltop = new Gsi2Ltop(lines);
                writeFile = gsi2Ltop.convert(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isSortOutputFileByNumber());
                break;

            case TXT:
                Txt2Ltop txt2Ltop = new Txt2Ltop(lines);
                writeFile = txt2Ltop.convert(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isSortOutputFileByNumber());
                break;

            case CSV:
                Csv2Ltop csv2Ltop = new Csv2Ltop(csv);
                writeFile = csv2Ltop.convert(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isSortOutputFileByNumber());
                break;

            case CAPLAN_K:
                Caplan2Ltop caplan2Ltop = new Caplan2Ltop(lines);
                writeFile = caplan2Ltop.convert(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isSortOutputFileByNumber());
                break;

            case ZEISS_REC:
                Zeiss2Ltop zeiss2Ltop = new Zeiss2Ltop(lines);
                writeFile = zeiss2Ltop.convertZeiss2Koo(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isSortOutputFileByNumber());
                break;

            case CADWORK:
                Cadwork2Ltop cadwork2Ltop = new Cadwork2Ltop(lines);
                writeFile = cadwork2Ltop.convert(parameter.isCadworkUseZeroHeights(),
                        parameter.isLtopEliminateDuplicatePoints(), parameter.isSortOutputFileByNumber());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Ltop csvBaselStadt2Ltop = new CsvBaselStadt2Ltop(csv);
                writeFile = csvBaselStadt2Ltop.convert(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isSortOutputFileByNumber());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Ltop txtBaselLandschaft2Ltop = new TxtBaselLandschaft2Ltop(lines);
                writeFile = txtBaselLandschaft2Ltop.convert(parameter.isLtopEliminateDuplicatePoints(),
                        parameter.isSortOutputFileByNumber());
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to LTOP KOO file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.KOO.getExtension());
    }

}
