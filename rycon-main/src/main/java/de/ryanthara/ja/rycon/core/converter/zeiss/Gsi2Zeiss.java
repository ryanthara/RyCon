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

import de.ryanthara.ja.rycon.core.converter.gsi.BaseToolsGsi;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert coordinate and measurement files from Leica GSI format
 * into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 */
public class Gsi2Zeiss {

    private BaseToolsGsi baseToolsGsi;

    /**
     * Constructs a new instance of this class given an {@code ArrayList<String>} with
     * the reader GSI lines as {@code String}.
     * <p>
     *
     * @param readStringLines reader GSI lines
     */
    public Gsi2Zeiss(ArrayList<String> readStringLines) {
        baseToolsGsi = new BaseToolsGsi(readStringLines);
    }

    /**
     * Converts a Leica GSI formatted measurement or coordinate based file into a Zeiss REC formatted file.
     *
     * @param dialect dialect of the target file
     *
     * @return string lines of the target file
     */
    public ArrayList<String> convertGSI2REC(ZeissDialect dialect) {
        ArrayList<String> result = new ArrayList<>();
        int lineNumber = 0;

        for (ArrayList<GsiBlock> blocksInLine : baseToolsGsi.getEncodedLinesOfGSIBlocks()) {
            lineNumber = lineNumber + 1;

            boolean isStationLine = false;
            boolean isTargetLine = false;

            String number = "", code = "", easting = "", northing = "", height = "", instrumentHeight = "";
            String horizontalAngle = "", verticalAngle = "", slopeDistance = "", targetHeight = "";

            // grab all the information from one line and fill them into place holders
            for (GsiBlock block : blocksInLine) {
                int wordIndex = block.getWordIndex();

                switch (wordIndex) {
                    case 11:
                        number = block.toPrintFormatCSV();
                        break;

                    case 21:
                        horizontalAngle = block.toPrintFormatCSV();
                        break;

                    case 22:
                        verticalAngle = block.toPrintFormatCSV();
                        break;

                    case 31:
                        slopeDistance = block.toPrintFormatCSV();
                        break;

                    case 32:
                        slopeDistance = block.toPrintFormatCSV();
                        break;

                    case 41:
                        code = block.toPrintFormatCSV();
                        break;

                    case 81:
                        easting = block.toPrintFormatCSV();
                        isTargetLine = true;
                        break;

                    case 82:
                        northing = block.toPrintFormatCSV();
                        isTargetLine = true;
                        break;

                    case 83:
                        height = block.toPrintFormatCSV();
                        isTargetLine = true;
                        break;

                    case 84:
                        easting = block.toPrintFormatCSV();
                        isStationLine = true;
                        break;

                    case 85:
                        northing = block.toPrintFormatCSV();
                        isStationLine = true;
                        break;

                    case 86:
                        height = block.toPrintFormatCSV();
                        isStationLine = true;
                        break;

                    case 87:
                        targetHeight = block.toPrintFormatCSV();
                        isTargetLine = true;
                        break;

                    case 88:
                        instrumentHeight = block.toPrintFormatCSV();
                        isStationLine = true;
                        break;

                    default:
                        System.err.println("Gsi2Zeiss.convertGSI2REC() : found unknown word index " + block.toPrintFormatCSV());
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

        return result;
    }

} // end of Gsi2Zeiss
