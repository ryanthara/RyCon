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

import de.ryanthara.ja.rycon.core.converter.ltop.Gsi2Mes;
import de.ryanthara.ja.rycon.core.converter.ltop.Zeiss2Ltop;
import de.ryanthara.ja.rycon.data.PreferenceKey;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A writer for writing LTOP MES files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class LtopMesWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(LtopMesWriter.class.getName());

    private final Path path;
    private final List<String> lines;
    private final WriteParameter parameter;

    /**
     * Constructs the {@link LtopMesWriter} with a set of parameters.
     *
     * @param path      file path to write into
     * @param lines     read string based file
     * @param parameter the writer parameter object
     */
    public LtopMesWriter(Path path, List<String> lines, WriteParameter parameter) {
        this.path = path;
        this.lines = new ArrayList<>(lines);
        this.parameter = parameter;
    }

    /**
     * Writes a LTOP MES file depends on the source file format.
     *
     * @return write success
     */
    @Override
    public boolean writeStringFile() {
        List<String> writeFile;

        boolean useZenithDistance = StringUtils.parseBooleanValue(PreferenceKey.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE);

        switch (SourceButton.fromIndex(parameter.getSourceNumber())) {
            case GSI8:
                // fall through for GSI8 format
            case GSI16:
                Gsi2Mes gsi2Mes = new Gsi2Mes(lines);
                writeFile = gsi2Mes.convert(useZenithDistance);
                break;

            case ZEISS_REC:
                Zeiss2Ltop zeiss2Ltop = new Zeiss2Ltop(lines);
                writeFile = zeiss2Ltop.convertZeiss2Mes(useZenithDistance);
                break;

            default:
                writeFile = null;

                logger.warn("Can not write {} file format to LTOP MES file.", SourceButton.fromIndex(parameter.getSourceNumber()));
        }

        return WriteFile2Disk.writeFile2Disk(path, writeFile, "", FileNameExtension.MES.getExtension());
    }

}
