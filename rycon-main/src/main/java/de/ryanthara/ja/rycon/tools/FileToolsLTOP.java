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
package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;
import de.ryanthara.ja.rycon.tools.elements.RyPoint;

import java.text.DateFormat;
import java.util.*;

/**
 * FileToolsLTOP implements basic operations on text based measurement and coordinate files for LTOP.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 * <li>2: code improvements, clean up and new functions (e.g. write MES files)</li>
 * <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 8
 */
public class FileToolsLTOP {

    // prevent wrong output with empty strings of defined length for KOO and MES files
    private final String number = "          ";
    private final String pointType = "    ";
    private final String toleranceCategory = "  ";
    private final String easting = "            ";
    private final String northing = "            ";
    private final String height = "          ";
    private final String geoid = "        ";
    private final String eta = "      ";
    private final String xi = "      ";
    private final String emptySpace4 = "    ";
    private final String emptySpace6 = "      ";
    private final String emptySpace8 = "        ";

    // prevent wrong output with empty strings of defined length for MES files
    private final String weather = "            ";
    private final String meanError = "      ";

    private final String cartesianCoordsIdentifier = "$$PK";
    // private final String ellipsoidCoordsIdentifier = "$$EL";
    private final String measurementLineIdentifier = "$$ME";

    private FileToolsLeicaGSI toolsLeicaGSI;

    private ArrayList<String> readStringLines;
    private List<String[]> readCSVLines;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public FileToolsLTOP(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Class constructor for read line based Leica GSI files.
     * <p>
     * Due to some details of the Leica GSI format it is easier to get access to the {@link FileToolsLeicaGSI} object
     * instead of having a couple of more parameters.
     *
     * @param toolsLeicaGSI {@link FileToolsLeicaGSI} object
     */
    public FileToolsLTOP(FileToolsLeicaGSI toolsLeicaGSI) {
        this.toolsLeicaGSI = toolsLeicaGSI;
    }

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public FileToolsLTOP(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Convert a CSV coordinate file (nr x y z) into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertCSV2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        writeCommendLine(result, cartesianCoordsIdentifier);

        for (String[] stringField : readCSVLines) {
            // prevent wrong output with empty strings of defined length from class
            pointType = this.pointType;
            toleranceCategory = this.toleranceCategory;
            height = this.height;
            geoid = this.geoid;
            eta = this.eta;
            xi = this.xi;

            // point number, column 1-10, aligned left
            number = String.format("%-10s", stringField[0].replaceAll("\\s+", "").trim());

            // easting (Y) is in column 3
            easting = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[1], 4));

            // northing (X) is in column 4
            northing = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[2], 4));

            // height (Z) is in column 5, but not always valued
            if (!stringField[3].equals("")) {
                height = String.format("%10s", NumberHelper.fillDecimalPlace(stringField[3], 4));
            }

            // pick up the relevant elements from the blocks from every line
            resultLine = prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                    height, geoid, eta, xi);

            // fill elements in a special object structure for duplicate elimination
            if (eliminateDuplicates) {
                fillRyPoints(ryPoints, easting, northing, height, resultLine);
            }

            if (!resultLine.isEmpty()) {
                result.add(resultLine);
            }
        }

        result = eliminateDuplicates ? eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? sortResult(result) : result;
    }

    /**
     * Convert a CSV coordinate file from the geodata server Basel Stadt into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertCSVBaselStadt2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        writeCommendLine(result, cartesianCoordsIdentifier);

        // remove comment line
        readCSVLines.remove(0);

        for (String[] stringField : readCSVLines) {
            // prevent wrong output with empty strings of defined length from class
            pointType = this.pointType;
            toleranceCategory = this.toleranceCategory;
            height = this.height;
            geoid = this.geoid;
            eta = this.eta;
            xi = this.xi;

            // point number, column 1-10, aligned left
            number = String.format("%-10s", stringField[0].replaceAll("\\s+", "").trim());

            // easting (Y) is in column 3
            easting = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[2], 4));

            // northing (X) is in column 4
            northing = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[3], 4));

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                height = String.format("%10s", NumberHelper.fillDecimalPlace(stringField[4], 4));
            }

            // pick up the relevant elements from the blocks from every line
            resultLine = prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                    height, geoid, eta, xi);

            // fill elements in a special object structure for duplicate elimination
            if (eliminateDuplicates) {
                fillRyPoints(ryPoints, easting, northing, height, resultLine);
            }

            if (!resultLine.isEmpty()) {
                result.add(resultLine);
            }
        }
        result = eliminateDuplicates ? eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? sortResult(result) : result;
    }

    /**
     * Convert a cadwork node.dat coordinate file into a KOO file for LTOP.
     *
     * @param useZeroHeights      use zero value for not given height values
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertCadwork2KOO(boolean useZeroHeights, boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        writeCommendLine(result, cartesianCoordsIdentifier);

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                // prevent wrong output with empty strings of defined length from class
                pointType = this.pointType;
                toleranceCategory = this.toleranceCategory;
                height = this.height;
                geoid = this.geoid;
                eta = this.eta;
                xi = this.xi;

                String[] lineSplit = line.trim().split("\\t", -1);

                // point number, column 1-10, aligned left
                number = String.format("%-10s", lineSplit[5]);

                // easting E, column 33-44
                easting = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[1], 4));

                // northing N, column 45-56
                northing = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[2], 4));

                // height H, column 61-70
                if (useZeroHeights) {
                    height = String.format("%10s", NumberHelper.fillDecimalPlace(lineSplit[3], 4));
                } else {
                    if (!lineSplit[3].equals("0.000000")) {
                        height = String.format("%10s", NumberHelper.fillDecimalPlace(lineSplit[3], 4));
                    }
                }

                // pick up the relevant elements from the blocks from every line
                resultLine = prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                        height, geoid, eta, xi);
                // fill elements in a special object structure for duplicate elimination
                if (eliminateDuplicates) {
                    fillRyPoints(ryPoints, easting, northing, height, resultLine);
                }


                if (!resultLine.isEmpty()) {
                    result.add(resultLine);
                }
            }
        }

        result = eliminateDuplicates ? eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? sortResult(result) : result;
    }

    /**
     * Convert a Leica GSI coordinate file into a KOO file for LTOP.
     * <p>
     * In this RyCON version only the WIs 81 till 86 are supported.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertGSI2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();

        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        writeCommendLine(result, cartesianCoordsIdentifier);

        for (ArrayList<GSIBlock> blocksAsLine : toolsLeicaGSI.getEncodedLinesOfGSIBlocks()) {
            // prevent wrong output with empty strings of defined length from class
            number = this.number;
            pointType = this.pointType;
            toleranceCategory = this.toleranceCategory;
            easting = this.easting;
            northing = this.northing;
            height = this.height;
            geoid = this.geoid;
            eta = this.eta;
            xi = this.xi;

            for (int i = 0; i < toolsLeicaGSI.getFoundWordIndices().size(); i++) {
                for (GSIBlock block : blocksAsLine) {
                    String s = block.toPrintFormatCSV();

                    switch (block.getWordIndex()) {
                        case 11:        // point number, column 1-10, aligned left
                            number = String.format("%-10s", s);
                            break;
                        case 81:        // easting E, column 33-44
                            easting = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 82:        // northing N, column 45-56
                            northing = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 83:        // height H, column 61-70
                            height = String.format("%10s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 84:        // easting E0, column 33-44
                            easting = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 85:        // northing N0, column 45-56
                            northing = String.format("%12s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                        case 86:        // height H0, column 61-70
                            height = String.format("%10s", NumberHelper.fillDecimalPlace(s, 4));
                            break;
                    }
                }
            }

            // pick up the relevant elements from the blocks from every line
            resultLine = prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                    height, geoid, eta, xi);

            // fill elements in a special object structure for duplicate elimination
            if (eliminateDuplicates) {
                fillRyPoints(ryPoints, easting, northing, height, resultLine);
            }

            if (!resultLine.isEmpty()) {
                result.add(resultLine);
            }
        }

        result = eliminateDuplicates ? eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? sortResult(result) : result;
    }

    /**
     * Converts a Leica GSI file with polar measurement elements into a LTOP MES file.
     * <p>
     * RyCON can differ between GSI8 and GSI16 files automatically. The first version of this function can't
     * middle between first and second face. (2ALL measurements are needed).
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

        writeCommendLine(result, measurementLineIdentifier);

        for (ArrayList<GSIBlock> blocksAsLine : toolsLeicaGSI.getEncodedLinesOfGSIBlocks()) {
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
                    hzAngle = String.format("%12s", NumberHelper.fillDecimalPlace(blocksAsLine.get(1).toPrintFormatCSV(), 5));

                    verticalAngle = blocksAsLine.get(2).toPrintFormatCSV();

                    Double d = Double.parseDouble(verticalAngle);

                    if (useZenithDistance) {
                        verticalAngle = String.format("%12s", NumberHelper.fillDecimalPlace(Double.toString(d), 5));
                    } else {
                        double heightAngle = 100d - d;
                        verticalAngle = String.format("%12s", NumberHelper.fillDecimalPlace(Double.toString(heightAngle), 5));
                    }

                    slopeDistance = String.format("%12s", NumberHelper.fillDecimalPlace(blocksAsLine.get(3).toPrintFormatCSV(), 5));

                    // differ target foil and prism
                    if (blocksAsLine.get(4).toString().trim().endsWith("344")) {
                        ppmAndPrismConstant = "5";
                    } else if (blocksAsLine.get(4).toString().trim().endsWith("000")) {
                        ppmAndPrismConstant = "4";
                    }

                    targetHeight = String.format("%6s", NumberHelper.fillDecimalPlace(blocksAsLine.get(5).toPrintFormatCSV(), 3));

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

    /**
     * Convert a CAPLAN K coordinate file into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertK2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        writeCommendLine(result, cartesianCoordsIdentifier);

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                // prevent wrong output with empty strings of defined length from class
                number = this.number;
                pointType = this.pointType;
                toleranceCategory = this.toleranceCategory;
                easting = this.easting;
                northing = this.northing;
                height = this.height;
                geoid = this.geoid;
                eta = this.eta;
                xi = this.xi;

                if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                    if (line.length() >= 16) {  // point number (no '*', ',' and ';'), column 1 - 16
                        number = String.format("%10s", line.substring(0, 16).trim());
                    }

                    if (line.length() >= 32) {  // easting E, column 19-32
                        easting = String.format("%12s", NumberHelper.fillDecimalPlace(line.substring(20, 32).trim(), 4));
                    }

                    if (line.length() >= 46) {  // northing N, column 33-46
                        northing = String.format("%12s", NumberHelper.fillDecimalPlace(line.substring(34, 46).trim(), 4));
                    }

                    if (line.length() >= 59) {  // height H, column 61-70
                        height = String.format("%10s", NumberHelper.fillDecimalPlace(line.substring(48, 59).trim(), 4));
                    }

                    // pick up the relevant elements from the blocks from every line
                    resultLine = prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                            height, geoid, eta, xi);

                    // fill elements in a special object structure for duplicate elimination
                    if (eliminateDuplicates) {
                        fillRyPoints(ryPoints, easting, northing, height, resultLine);
                    }

                    if (!resultLine.isEmpty()) {
                        result.add(resultLine);
                    }
                }

            }
        }

        result = eliminateDuplicates ? eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? sortResult(result) : result;
    }

    /**
     * Convert a TXT coordinate file into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertTXT2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();
        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        writeCommendLine(result, cartesianCoordsIdentifier);

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                // prevent wrong output with empty strings of defined length from class
                number = this.number;
                pointType = this.pointType;
                toleranceCategory = this.toleranceCategory;
                easting = this.easting;
                northing = this.northing;
                height = this.height;
                geoid = this.geoid;
                eta = this.eta;
                xi = this.xi;

                String[] lineSplit = line.trim().split("\\s+");

                switch (lineSplit.length) {
                    case 4:     // nr x y z
                        number = String.format("%10s", lineSplit[0].trim());
                        easting = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[1], 4));
                        northing = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[2], 4));
                        height = String.format("%10s", NumberHelper.fillDecimalPlace(lineSplit[3], 4));
                        break;

                    case 5:     // nr code x y z
                        number = String.format("%10s", lineSplit[0]);
                        easting = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[2], 4));
                        northing = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[3], 4));
                        height = String.format("%10s", NumberHelper.fillDecimalPlace(lineSplit[4], 4));
                        break;
                }

                // pick up the relevant elements from the blocks from every line
                resultLine = prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                        height, geoid, eta, xi);

                // fill elements in a special object structure for duplicate elimination
                if (eliminateDuplicates) {
                    fillRyPoints(ryPoints, easting, northing, height, resultLine);
                }

                if (!resultLine.isEmpty()) {
                    result.add(resultLine);
                }
            }
        }

        result = eliminateDuplicates ? eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? sortResult(result) : result;
    }

    /**
     * Convert a TXT coordinate file from the geodata server Basel Landschaft into a KOO file for LTOP.
     *
     * @param eliminateDuplicates eliminate duplicate coordinates within 3cm radius
     * @param sortOutputFile      sort an output file by point number
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertTXTBaselLandschaft2KOO(boolean eliminateDuplicates, boolean sortOutputFile) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<RyPoint> ryPoints = new ArrayList<>();

        String number, pointType, toleranceCategory, easting, northing, height, geoid, eta, xi;
        String resultLine;

        writeCommendLine(result, cartesianCoordsIdentifier);

        // remove comment line
        readStringLines.remove(0);

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                // prevent wrong output with empty strings of defined length from class
                number = this.number;
                pointType = this.pointType;
                toleranceCategory = this.toleranceCategory;
                easting = this.easting;
                northing = this.northing;
                height = this.height;
                geoid = this.geoid;
                eta = this.eta;
                xi = this.xi;

                String[] lineSplit = line.trim().split("\\t", -1);

                switch (lineSplit.length) {
                    case 5:     // HFP file
                        number = String.format("%10s", lineSplit[1].trim());
                        easting = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[2], 4));
                        northing = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[3], 4));
                        height = String.format("%10s", NumberHelper.fillDecimalPlace(lineSplit[4], 4));
                        break;

                    case 6:     // LFP file
                        number = String.format("%10s", lineSplit[1]);
                        easting = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[3], 4));
                        northing = String.format("%12s", NumberHelper.fillDecimalPlace(lineSplit[4], 4));

                        // prevent 'NULL' element in height
                        if (!lineSplit[5].equals("NULL")) {
                            height = String.format("%10s", NumberHelper.fillDecimalPlace(lineSplit[5], 4));
                        }
                        break;
                }

                // pick up the relevant elements from the blocks from every line
                resultLine = prepareStringForKOO(number, pointType, toleranceCategory, easting, northing,
                        height, geoid, eta, xi);

                // fill elements in a special object structure for duplicate elimination
                if (eliminateDuplicates) {
                    fillRyPoints(ryPoints, easting, northing, height, resultLine);
                }

                if (!resultLine.isEmpty()) {
                    result.add(resultLine);
                }
            }
        }

        result = eliminateDuplicates ? eliminateDuplicatePoints(ryPoints) : result;

        return sortOutputFile ? sortResult(result) : result;
    }

    public ArrayList<String> convertZeiss2KOO(boolean selection, boolean selection1) {
        ArrayList<String> result = new ArrayList<>();
        return result;
    }

    /**
     * Eliminate duplicate points from an ArrayList<String>.
     * <p>
     * Points are identical if the 3D distance is less than 3cm and the point number is the same. The point number is
     * used for find wrong numbered points.
     *
     * @param arrayList unsorted ArrayList<String>
     *
     * @return sorted ArrayList<String>
     */
    private ArrayList<String> eliminateDuplicatePoints(ArrayList<RyPoint> arrayList) {
        ArrayList<String> result = new ArrayList<>();

        // set minDistance to default value and try to parse the settings value
        double d = 0.03;

        try {
            d = Double.parseDouble(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE));
        } catch (NumberFormatException e) {
            System.err.println("Can't convert maximum distance to double in eliminateDuplicatePoints()");
            e.printStackTrace();
        }

        final double minDistance = d;

        // sort the tree set of RyPoints
        TreeSet<RyPoint> set = new TreeSet<>(new Comparator<RyPoint>() {
            @Override
            //public int compare(ArrayList<RyPoint> p1, ArrayList<RyPoint> p2) {
            public int compare(RyPoint pt1, RyPoint pt2) {
                /*
                Compare at the three distances x, y and z before calculating the slope distance because of reducing
                calculation time and therefore increase the speed.

                Points are equal if they are in a slope distance of 'maxDistance' and have the same number!
                 */
                if ((pt1.getSlopeDistance(pt2) < minDistance) & (pt1.getNumber().equalsIgnoreCase(pt2.getNumber()))) {
                    System.out.println(pt1.getNumber() + " : " + pt2.getNumber() + " " + pt1.getSlopeDistance(pt2));
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // add the ArrayList anc check for duplicate points
        set.addAll(arrayList);

        // bring back the points into an ArrayList<String>
        for (RyPoint ryPoint : set) {
            if (!ryPoint.getPrintLine().trim().equalsIgnoreCase("")) {
                result.add(ryPoint.getPrintLine());
            }
        }

        return result;
    }

    /**
     * Fill the ArrayList<RyPoint> with ryPoint objects.
     *
     * @param ryPoints   the ArrayList<RyPoint>
     * @param easting    easting value
     * @param northing   northing value
     * @param height     height value
     * @param resultLine result line as string
     */
    private void fillRyPoints(ArrayList<RyPoint> ryPoints, String easting, String northing, String height, String resultLine) {
        double x = Double.NaN, y = Double.NaN, z = Double.NaN;

        try {
            x = Double.parseDouble(easting);
            y = Double.parseDouble(northing);
            z = Double.parseDouble(height);
        } catch (NumberFormatException e) {
            System.err.println("Can't convert string to double in FileToolsLTOP:fillRyPoints()");
            System.err.println("Wrong line: " + resultLine);
        }

        ryPoints.add(new RyPoint(number, x, y, z, resultLine));
    }

    private String prepareStringForKOO(String number, String pointType, String toleranceCategory,
                                       String easting, String northing, String height,
                                       String geoid, String eta, String xi) {

        // check for null coordinate
        if (Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE))) {
            String xyz = northing.trim().concat(easting.trim()).concat(height.trim());
            xyz = xyz.replace('.', '0');

            if (xyz.matches("^[0]+$")) {
                return "";
            }
        }

        return number +
                pointType +
                emptySpace8 +
                toleranceCategory +
                emptySpace8 +
                easting +
                northing +
                emptySpace4 +
                height +
                emptySpace6 +
                geoid +
                emptySpace6 +
                eta +
                xi;
    }

    /**
     * Sorts an ArrayList<String> by 'first token'.
     *
     * @param arrayList unsorted ArrayList<String>
     *
     * @return sorted ArrayList<String>
     */
    private ArrayList<String> sortResult(ArrayList<String> arrayList) {
        Collections.sort(arrayList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        return arrayList;
    }

    /**
     * Write the comment line into a given ArrayList<String>.
     * <p>
     * The following identifiers for the file type are used:
     * - $$ME for measurement file
     * - $$PK for cartesian coordinates
     * - $$EL for geographic coordinates
     *
     * @param result              ArrayList<String> to write in
     * @param firstLineIdentifier identifier for different file type ($$ME, $$PK or $$EL)
     */
    private void writeCommendLine(ArrayList<String> result, String firstLineIdentifier) {
        // insert RyCON version, date and time
        Date d = new Date();
        DateFormat df;
        df = DateFormat.getDateTimeInstance(/* dateStyle */ DateFormat.LONG,
                                            /* timeStyle */ DateFormat.MEDIUM);

        result.add(String.format(firstLineIdentifier + " " + I18N.getStrLTOPCommentLine(), Version.getVersion(), df.format(d)));
    }

} // end of FileToolsLTOp
