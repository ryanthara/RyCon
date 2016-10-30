/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.converter.ltop;

import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.tools.NumberFormatter;
import de.ryanthara.ja.rycon.elements.GSIBlock;

import java.util.ArrayList;

/**
 * Created by sebastian on 13.09.16.
 */
public class GSI2MES {

    private BaseToolsGSI baseToolsGSI;

    /**
     * Class constructor for read line based text files.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GSI2MES(ArrayList<String> readStringLines) {
        baseToolsGSI = new BaseToolsGSI(readStringLines);
    }

    /**
     * Converts a Leica GSI file with polar measurement elements into a LTOP MES file.
     * <p>
     * RyCON can differ between GSI8 and GSI16 files automatically. The first version of this function can't
     * middle between first and second face. (2ALL measurements or first face measurements are needed).
     *
     * @param useZenithDistance true if zenith distance should be used instead of height angle for vertical angle
     *
     * @return converted MES file
     */
    public ArrayList<String> convertGSI2MES(boolean useZenithDistance) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> horizontalAngleGroup = new ArrayList<>();
        ArrayList<String> verticalAngleGroup = new ArrayList<>();
        ArrayList<String> slopeDistanceGroup = new ArrayList<>();

        String
                stationNumber, instrumentHeight, number, hzAngle, verticalAngle,
                slopeDistance, ppmAndPrismConstant = "", targetHeight;

        BaseToolsLTOP.writeCommendLine(result, BaseToolsLTOP.measurementLineIdentifier);

        for (ArrayList<GSIBlock> blocksAsLine : baseToolsGSI.getEncodedLinesOfGSIBlocks()) {
            /*
            110001+0000FS01 84..16+61720467 85..16+23483343 86..16+02593776 88..16+00000000
            110002+00009004 21.322+21956015 22.322+09463619 31..06+00253959 51..1.+0005+344 87..16+00000000
            °
            °
            $$ME IPMS, Überwachungsmessung Tankstation, Nullmessung, 2018-13-32
            STFS01                                         0.000
            RI9004                     219.56015
            RIBG15                     240.95318
            HW9004                       5.36381                 0.000
            HWBG15                       5.23547                 0.000
            DS9004                      25.39590      5          0.000
            DSBG15                      30.90180      5          0.000
            */

            switch (blocksAsLine.size()) {
                case 5:     // line contains free station
                    /*
                    110001+0000FS01 84..16+61720467 85..16+23483343 86..16+02593776 88..16+00000000
                    |               |               |               |               |
                    |               |               |               |               +-> instrument height
                    |               |               |               +-> height
                    |               |               +-> northing
                    |               +-> easting
                    +-> number

                    ||
                    \/

                    12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                    1        2         3         4         5         6         7         8         9         0         1         2
                    Stationszeile:
                    KA<--PUNKT-><TY>        <--WETTER--><-MF-><GR><-IH-><F-BUCH><-VERANTW.+DATUM->      <ZENT>
                    */

                    stationNumber = String.format("%-10s", blocksAsLine.get(0).toPrintFormatCSV());
                    instrumentHeight = String.format("%6s", blocksAsLine.get(4).toPrintFormatCSV());

                    String stationLine = "ST".concat(stationNumber).concat("                                  ").concat(instrumentHeight);

                    if (horizontalAngleGroup.size() != 0 & verticalAngleGroup.size() != 0 & slopeDistanceGroup.size() != 0) {
                        // write the ArrayLists
                        for (String elevationLine : horizontalAngleGroup) {
                            result.add(elevationLine);
                        }

                        for (String gridBearingLine : verticalAngleGroup) {
                            result.add(gridBearingLine);
                        }

                        for (String slopeDistanceLine : slopeDistanceGroup) {
                            result.add(slopeDistanceLine);
                        }

                        // empty the ArrayLists
                        horizontalAngleGroup = new ArrayList<>();
                        verticalAngleGroup = new ArrayList<>();
                        slopeDistanceGroup = new ArrayList<>();
                    }

                    result.add(stationLine);
                    break;
                case 6:
                    /*
                    110002+00009004 21.322+21956015 22.322+09463619 31..06+00253959 51..1.+0005+344 87..16+00000000
                    |               |               |               |               |               |
                    |               |               |               |               |               +-> targetHeight
                    |               |               |               |               +-> ppm and prism
                    |               |               |               +-> slopeDistance
                    |               |               +-> verticalAngle
                    |               +-> hzAngle
                    +-> number

                    ||
                    \/

                    12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                    1        2         3         4         5         6         7         8         9         0         1         2
                    Messzeile:
                    KA<--PUNKT-><TY>        <-MESSWERT-><-MF-><GR><-IH-><-SH->  <ZENT>

                    */
                    number = String.format("%-10s", blocksAsLine.get(0).toPrintFormatCSV());
                    hzAngle = String.format("%12s", NumberFormatter.fillDecimalPlace(blocksAsLine.get(1).toPrintFormatCSV(), 5));

                    verticalAngle = blocksAsLine.get(2).toPrintFormatCSV();

                    Double d = Double.parseDouble(verticalAngle);

                    if (useZenithDistance) {
                        verticalAngle = String.format("%12s", NumberFormatter.fillDecimalPlace(Double.toString(d), 5));
                    } else {
                        double heightAngle = 100d - d;
                        verticalAngle = String.format("%12s", NumberFormatter.fillDecimalPlace(Double.toString(heightAngle), 5));
                    }

                    slopeDistance = String.format("%12s", NumberFormatter.fillDecimalPlace(blocksAsLine.get(3).toPrintFormatCSV(), 5));

                    // differ target foil and prism
                    if (blocksAsLine.get(4).toString().trim().endsWith("344")) {
                        ppmAndPrismConstant = "5";
                    } else if (blocksAsLine.get(4).toString().trim().endsWith("000")) {
                        ppmAndPrismConstant = "4";
                    }

                    targetHeight = String.format("%6s", NumberFormatter.fillDecimalPlace(blocksAsLine.get(5).toPrintFormatCSV(), 3));

                    /*
                    KA<--PUNKT-><TY>        <-MESSWERT-><-MF-><GR><-IH-><-SH->  <ZENT>

                    RI9001                     177.49806
                    HW9004                       5.36381                 0.000
                    DS9004                      25.39590      5          0.000

                    HW9001                       64.2710  0.000
                    RI9004                     219.56010
                    DS9004                       2.53959      5          0.000

                     */

                    String horizontalAngleLine = "RI" + number + "            " + hzAngle;

                    String verticalAngleLine = useZenithDistance ? "ZD" : "HW";
                    verticalAngleLine = verticalAngleLine + number + "            " + verticalAngle + "                "
                            + targetHeight;

                    String slopedDistanceLine = "DS" + number + "            " + slopeDistance + "      "
                            + ppmAndPrismConstant + "         " + targetHeight;

                    horizontalAngleGroup.add(horizontalAngleLine);
                    verticalAngleGroup.add(verticalAngleLine);
                    slopeDistanceGroup.add(slopedDistanceLine);
                    break;
            }
        }
        // write the ArrayLists for the last station
        for (String elevationLine : horizontalAngleGroup) {
            result.add(elevationLine);
        }

        for (String gridBearingLine : verticalAngleGroup) {
            result.add(gridBearingLine);
        }

        for (String slopeDistanceLine : slopeDistanceGroup) {
            result.add(slopeDistanceLine);
        }

        return result;
    }

} // end of GSI2MES
