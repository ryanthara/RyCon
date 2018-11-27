/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.converter.zeiss
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
package de.ryanthara.ja.rycon.core.converter.zeiss;

import de.ryanthara.ja.rycon.core.converter.gsi.GsiDecoder;
import de.ryanthara.ja.rycon.core.elements.GSIBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert Leica Geosystems GSI format (GSI8 and GSI16) coordinate
 * and measurement files into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 */
public class Gsi2Zeiss {

    private static final Logger logger = LoggerFactory.getLogger(Gsi2Zeiss.class.getName());

    private final GsiDecoder gsiDecoder;

    /**
     * Creates a converter with a list for the read line based
     * Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public Gsi2Zeiss(List<String> lines) {
        gsiDecoder = new GsiDecoder(lines);
    }

    /**
     * Converts a Leica Geosystems GSI formatted measurement or coordinate based file into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     * @return string lines of the target file
     */
    public List<String> convert(ZeissDialect dialect) {
        List<String> result = new ArrayList<>();
        int lineNumber = 0;

        for (List<GSIBlock> blocksInLine : gsiDecoder.getDecodedLinesOfGsiBlocks()) {
            lineNumber = lineNumber + 1;

            boolean isStationLine = false;
            boolean isTargetLine = false;

            String number = "", code = "", easting = "", northing = "", height = "", instrumentHeight = "";
            String horizontalAngle = "", verticalAngle = "", slopeDistance = "", targetHeight = "";

            // grab all the information from one line and fill them into place holders
            for (GSIBlock block : blocksInLine) {
                int wordIndex = block.getWordIndex();

                switch (wordIndex) {
                    case 11:
                        number = block.toPrintFormatCsv();
                        break;

                    case 21:
                        horizontalAngle = block.toPrintFormatCsv();
                        break;

                    case 22:
                        verticalAngle = block.toPrintFormatCsv();
                        break;

                    case 31:
                        slopeDistance = block.toPrintFormatCsv();
                        break;

                    case 32:
                        slopeDistance = block.toPrintFormatCsv();
                        break;

                    case 41:
                        code = block.toPrintFormatCsv();
                        break;

                    case 81:
                        easting = block.toPrintFormatCsv();
                        isTargetLine = true;
                        break;

                    case 82:
                        northing = block.toPrintFormatCsv();
                        isTargetLine = true;
                        break;

                    case 83:
                        height = block.toPrintFormatCsv();
                        isTargetLine = true;
                        break;

                    case 84:
                        easting = block.toPrintFormatCsv();
                        isStationLine = true;
                        break;

                    case 85:
                        northing = block.toPrintFormatCsv();
                        isStationLine = true;
                        break;

                    case 86:
                        height = block.toPrintFormatCsv();
                        isStationLine = true;
                        break;

                    case 87:
                        targetHeight = block.toPrintFormatCsv();
                        isTargetLine = true;
                        break;

                    case 88:
                        instrumentHeight = block.toPrintFormatCsv();
                        isStationLine = true;
                        break;

                    default:
                        logger.trace("Line contains unknown word index ({}).", block.toPrintFormatCsv());
                        break;
                }
            }

            /*
             writer the information from the place holder value to the result array list and
             differ between coordinate or measurement files
             */
            if (isStationLine) {
                if (dialect.equals(ZeissDialect.REC500)) {
                    if (!instrumentHeight.isEmpty()) {
                        result.add(BaseToolsZeiss.prepareLineOfInstrumentOrTargetHeight(dialect,
                                BaseToolsZeiss.INSTRUMENT_HEIGHT, number, code, instrumentHeight, lineNumber));
                        lineNumber = lineNumber + 1;
                    }
                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                } else {
                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                    if (!instrumentHeight.isEmpty()) {
                        lineNumber = lineNumber + 1;
                        result.add(BaseToolsZeiss.prepareLineOfInstrumentOrTargetHeight(dialect,
                                BaseToolsZeiss.INSTRUMENT_HEIGHT, number, code, instrumentHeight, lineNumber));
                    }
                }
            } else if (isTargetLine) {
                if (!targetHeight.isEmpty()) {
                    result.add(BaseToolsZeiss.prepareLineOfInstrumentOrTargetHeight(dialect, BaseToolsZeiss.TARGET_HEIGHT,
                            number, code, targetHeight, lineNumber));
                    lineNumber = lineNumber + 1;
                }
                if (!horizontalAngle.isEmpty() && !verticalAngle.isEmpty() && !slopeDistance.isEmpty()) {
                    result.add(BaseToolsZeiss.prepareLineOfMeasurement(dialect, number, code, horizontalAngle, verticalAngle,
                            slopeDistance, lineNumber));
                    lineNumber = lineNumber + 1;
                }
                if (!easting.isEmpty() && !northing.isEmpty()) {
                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                    lineNumber = lineNumber + 1;
                }
            }
        }

        return List.copyOf(result);
    }

}
