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

import de.ryanthara.ja.rycon.core.converter.gsi.*;
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
 * A writer for writing Leica Geosystems GSI files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GsiWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(GsiWriter.class.getName());

    private final boolean isGSI16;
    private final Path path;
    private final List<String> lines;
    private final List<String[]> csv;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link GsiWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param csv       read csv file
     * @param lines     read string based file
     * @param parameter the writer parameter object
     * @param isGSI16   true if is GSI16 format
     */
    public GsiWriter(Path path, List<String> lines, List<String[]> csv, WriteParameter parameter, boolean isGSI16) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.csv = new ArrayList<>(csv);
        this.parameter = parameter;
        this.isGSI16 = isGSI16;
    }

    /**
     * Writes a Leica Geosystems GSI file depends on the source file format.
     *
     * @return write success
     */
    @Override
    public boolean writeStringFile() {
        List<String> writeFile;

        switch (Objects.requireNonNull(SourceButton.fromIndex(parameter.getSourceNumber()).orElse(null))) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi8vsGsi16 gsi8VsGsi16 = new Gsi8vsGsi16(lines);
                writeFile = gsi8VsGsi16.convert(isGSI16, parameter.isSortOutputFileByNumber());
                break;

            case TXT:
                Txt2Gsi txt2Gsi = new Txt2Gsi(lines);
                writeFile = txt2Gsi.convert(isGSI16, parameter.sourceContainsCode());
                break;

            case CSV:
                Csv2Gsi csv2GSI = new Csv2Gsi(csv);
                writeFile = csv2GSI.convert(isGSI16, parameter.sourceContainsCode());
                break;

            case CAPLAN_K:
                Caplan2Gsi caplan2GSI = new Caplan2Gsi(lines);
                writeFile = caplan2GSI.convert(isGSI16, parameter.isWriteCodeColumn());
                break;

            case ZEISS_REC:
                Zeiss2Gsi zeiss2Gsi = new Zeiss2Gsi(lines);
                writeFile = zeiss2Gsi.convert(isGSI16);
                break;

            case CADWORK:
                Cadwork2Gsi cadwork2Gsi = new Cadwork2Gsi(lines);
                writeFile = cadwork2Gsi.convert(isGSI16, parameter.isWriteCodeColumn(), parameter.isCadworkUseZeroHeights());
                break;

            case BASEL_STADT:
                CsvBaselStadt2Gsi csvBaselStadt2Gsi = new CsvBaselStadt2Gsi(csv);
                writeFile = csvBaselStadt2Gsi.convert(isGSI16, parameter.sourceContainsCode());
                break;

            case BASEL_LANDSCHAFT:
                TxtBaselLandschaft2Gsi txtBaselLandschaft2Gsi = new TxtBaselLandschaft2Gsi(lines);
                writeFile = txtBaselLandschaft2Gsi.convert(isGSI16, parameter.isWriteCodeColumn());
                break;

            case TOPORAIL_MEP:
                Toporail2Gsi toporailMep2Gsi = new Toporail2Gsi(lines);
                writeFile = toporailMep2Gsi.convertToporail2Gsi(FileFormat.MEP, isGSI16);
                break;

            case TOPORAIL_PTS:
                Toporail2Gsi toporailPts2Gsi = new Toporail2Gsi(lines);
                writeFile = toporailPts2Gsi.convertToporail2Gsi(FileFormat.PTS, isGSI16);
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to Leica Geosystems GSI file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.LEICA_GSI.getExtension());
    }

}
