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
package de.ryanthara.ja.rycon.converter.zeiss;

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.ArrayList;

/**
 * This class provides functions to convert coordinate and measurement files from Leica GSI format
 * into Zeiss REC files with it's dialects (R4, R5, REC500 and M5).
 */
public class GSI2Zeiss {

    private BaseToolsGSI baseToolsGSI;

    /**
     * Constructs a new instance of this class given an {@code ArrayList<String>} with
     * the read GSI lines as {@code String}.
     * <p>
     *
     * @param readStringLines read GSI lines
     */
    public GSI2Zeiss(ArrayList<String> readStringLines) {
        baseToolsGSI = new BaseToolsGSI(readStringLines);
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

        for (ArrayList<GSIBlock> blocksAsLines : baseToolsGSI.getEncodedLinesOfGSIBlocks()) {
            lineNumber = lineNumber + 1;

            boolean isStationLine = false;
            boolean isTargetLine = false;

            String number = "", code = "", easting = "", northing = "", height = "", instrumentHeight = "";
            String horizontalAngle = "", verticalAngle = "", slopeDistance = "", targetHeight = "";

            for (GSIBlock block : blocksAsLines) {
                int wordIndex = block.getWordIndex();
                System.out.println("Found WI: " + wordIndex + " ");

                switch (wordIndex) {
                    case 11:
                        number = block.toPrintFormatTXT();
                        break;
                    case 21:
                        horizontalAngle = block.toPrintFormatTXT();
                        break;
                    case 22:
                        verticalAngle = block.toPrintFormatTXT();
                        break;
                    case 31:
                        slopeDistance = block.toPrintFormatTXT();
                        break;
                    case 32:
                        slopeDistance = block.toPrintFormatTXT();
                        break;
                    case 41:
                        code = block.toPrintFormatTXT();
                        break;
                    case 81:
                        easting = block.toPrintFormatTXT();
                        isTargetLine = true;
                        break;
                    case 82:
                        northing = block.toPrintFormatTXT();
                        isTargetLine = true;
                        break;
                    case 83:
                        height = block.toPrintFormatTXT();
                        isTargetLine = true;
                        break;
                    case 84:
                        easting = block.toPrintFormatTXT();
                        isStationLine = true;
                        break;
                    case 85:
                        northing = block.toPrintFormatTXT();
                        isStationLine = true;
                        break;
                    case 86:
                        height = block.toPrintFormatTXT();
                        isStationLine = true;
                        break;
                    case 87:
                        targetHeight = block.toPrintFormatTXT();
                        break;
                    case 88:
                        instrumentHeight = block.toPrintFormatTXT();
                        isStationLine = true;
                        break;
                }
            }

            // differ between coordinate or measurement files
            if (isStationLine) {
                if (dialect.equals(ZeissDialect.REC500)) {
                    result.add(BaseToolsZeiss.prepareLineOfInstrumentHeight(dialect, number, instrumentHeight, lineNumber));
                    lineNumber = lineNumber + 1;
                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                } else {
                    result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
                    lineNumber = lineNumber + 1;
                    result.add(BaseToolsZeiss.prepareLineOfInstrumentHeight(dialect, number, instrumentHeight, lineNumber));
                }
            } else if (isTargetLine) {
                result.add(BaseToolsZeiss.prepareLineOfTargetHeight(dialect, number, targetHeight, lineNumber));
                lineNumber = lineNumber + 1;
                result.add(BaseToolsZeiss.prepareLineOfMeasurement(dialect, number, horizontalAngle, verticalAngle, slopeDistance, lineNumber));
                lineNumber = lineNumber + 1;
                result.add(BaseToolsZeiss.prepareLineOfCoordinates(dialect, number, code, easting, northing, height, lineNumber));
            }
        }

        return result;
    }

} // end of GSI2Zeiss
