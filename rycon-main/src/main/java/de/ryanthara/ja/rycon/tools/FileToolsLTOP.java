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

import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class implements basic operations on text based measurement and coordinate files for LTOP.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 8
 */
public class FileToolsLTOP {

    // prevent wrong output with empty strings of defined length
    private final String number = "          ";
    private final String pointType = "    ";
    private final String emptySpaceTY = "        ";
    private final String toleranceCategory = "  ";
    private final String emptySpaceTK = "        ";
    private final String easting = "            ";
    private final String northing = "            ";
    private final String emptySpaceX = "    ";
    private final String height = "          ";
    private final String emptySpaceH = "      ";
    private final String geoid = "        ";
    private final String emptySpaceGEOID = "      ";
    private final String eta = "      ";
    private final String xi = "      ";

    private ArrayList<String> readStringLines;
    private List<String[]> readCSVLines = null;

    /**
     * Class Constructor with parameter.
     * <p>
     * As parameter the {@code ArrayList<String>} object with the lines in text format is used.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public FileToolsLTOP(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Class constructor with parameter for the read lines as {@code List<String[]>} object.
     * <p>
     * This constructor is used for reading csv file lines.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public FileToolsLTOP(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts an CSV coordinate file (nr x y z) into an KOO file for LTOP.
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertCSV2KOO() {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder;

        String number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting,
                northing, emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi;

        writeHeadline(result);

        for (String[] stringField : readCSVLines) {
            // prevent wrong output with empty strings of defined length from class
            pointType = this.pointType;
            emptySpaceTY = this.emptySpaceTY;
            toleranceCategory = this.toleranceCategory;
            emptySpaceTK = this.emptySpaceTK;
            emptySpaceX = this.emptySpaceX;
            height = this.height;
            emptySpaceH = this.emptySpaceH;
            geoid = this.geoid;
            emptySpaceGEOID = this.emptySpaceGEOID;
            eta = this.eta;
            xi = this.xi;

            // point number is in column 1
            number = String.format("%10s", stringField[0].replaceAll("\\s+", "").trim());

            // easting (Y) is in column 3
            easting = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[1], 4));

            // northing (X) is in column 4
            northing = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[2], 4));

            // height (Z) is in column 5, but not always valued
            if (!stringField[3].equals("")) {
                height = String.format("%10s", NumberHelper.fillDecimalPlace(stringField[3], 4));
            }

            // preserve write empty lines
            if (number.trim().length() > 0) {
                // 2. pick up the relevant elements from the blocks from every line
                stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

                result.add(stringBuilder.toString());
            }
        }
        return result;
    }

    /**
     * Converts an CSV coordinate file from the geodata server Basel Stadt into an KOO file for LTOP.
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertCSVBaselStadt2KOO() {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder;

        String number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting,
                northing, emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi;

        writeHeadline(result);

        for (String[] stringField : readCSVLines) {
            // prevent wrong output with empty strings of defined length from class
            pointType = this.pointType;
            emptySpaceTY = this.emptySpaceTY;
            toleranceCategory = this.toleranceCategory;
            emptySpaceTK = this.emptySpaceTK;
            emptySpaceX = this.emptySpaceX;
            height = this.height;
            emptySpaceH = this.emptySpaceH;
            geoid = this.geoid;
            emptySpaceGEOID = this.emptySpaceGEOID;
            eta = this.eta;
            xi = this.xi;

            // point number is in column 1
            number = String.format("%10s", stringField[0].replaceAll("\\s+", "").trim());

            // easting (Y) is in column 3
            easting = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[2], 4));

            // northing (X) is in column 4
            northing = String.format("%12s", NumberHelper.fillDecimalPlace(stringField[3], 4));

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                height = String.format("%10s", NumberHelper.fillDecimalPlace(stringField[4], 4));
            }

            // preserve write empty lines
            if (number.trim().length() > 0) {
                // 2. pick up the relevant elements from the blocks from every line
                stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

                result.add(stringBuilder.toString());
            }
        }
        return result;
    }

    /**
     * Converts an cadwork node.dat coordinate file into an KOO file for LTOP.
     * <p>
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertCadwork2KOO(boolean useZeroHeights) {
        ArrayList<String> result = new ArrayList<>();

        String number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting,
                northing, emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi;

        StringBuilder stringBuilder;

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        writeHeadline(result);

        for (String line : readStringLines) {
            // prevent wrong output with empty strings of defined length from class
            pointType = this.pointType;
            emptySpaceTY = this.emptySpaceTY;
            toleranceCategory = this.toleranceCategory;
            emptySpaceTK = this.emptySpaceTK;
            emptySpaceX = this.emptySpaceX;
            height = this.height;
            emptySpaceH = this.emptySpaceH;
            geoid = this.geoid;
            emptySpaceGEOID = this.emptySpaceGEOID;
            eta = this.eta;
            xi = this.xi;

            String[] lineSplit = line.trim().split("\\s+");

            // point number, column 1-10
            number = String.format("%10s", lineSplit[5]);

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

            // preserve write empty lines
            if (number.trim().length() > 0) {
                // 2. pick up the relevant elements from the blocks from every line
                stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

                result.add(stringBuilder.toString());
            }
        }
        return result;
    }

    /**
     * Converts an Leica GSI coordinate file into an KOO file for LTOP.
     * <p>
     * The WIs 81 till 86 are supported.
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertGSI2KOO() {
        ArrayList<String> result = new ArrayList<>();

        FileToolsLeicaGSI gsiTools = new FileToolsLeicaGSI(readStringLines);

        String number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting,
                northing, emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi;

        writeHeadline(result);

        // 1. convert lines into GSI-Blocks with BlockEncoder
        ArrayList<ArrayList<GSIBlock>> blocksInLines = gsiTools.getEncodedGSIBlocks();

        for (ArrayList<GSIBlock> blocksAsLines : blocksInLines) {
            StringBuilder stringBuilder;

            // prevent wrong output with empty strings of defined length from class
            number = this.number;
            pointType = this.pointType;
            emptySpaceTY = this.emptySpaceTY;
            toleranceCategory = this.toleranceCategory;
            emptySpaceTK = this.emptySpaceTK;
            easting = this.easting;
            northing = this.northing;
            emptySpaceX = this.emptySpaceX;
            height = this.height;
            emptySpaceH = this.emptySpaceH;
            geoid = this.geoid;
            emptySpaceGEOID = this.emptySpaceGEOID;
            eta = this.eta;
            xi = this.xi;

            for (int i = 0; i < gsiTools.getFoundWordIndices().size(); i++) {
                for (GSIBlock block : blocksAsLines) {
                    String s = block.toPrintFormatCSV();

                    switch (block.getWordIndex()) {
                        case 11:        // point number, column 1-10
                            number = String.format("%10s", s);
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
            // preserve write empty lines
            if (number.trim().length() > 0) {
                // 2. pick up the relevant elements from the blocks from every line
                stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

                result.add(stringBuilder.toString());
            }
        }
        return result;
    }

    /**
     * Converts an CAPLAN K coordinate file into an KOO file for LTOP.
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertK2KOO() {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder;

        String number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting,
                northing, emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi;

        writeHeadline(result);

        for (String line : readStringLines) {
            // prevent wrong output with empty strings of defined length from class
            number = this.number;
            pointType = this.pointType;
            emptySpaceTY = this.emptySpaceTY;
            toleranceCategory = this.toleranceCategory;
            emptySpaceTK = this.emptySpaceTK;
            easting = this.easting;
            northing = this.northing;
            emptySpaceX = this.emptySpaceX;
            height = this.height;
            emptySpaceH = this.emptySpaceH;
            geoid = this.geoid;
            emptySpaceGEOID = this.emptySpaceGEOID;
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

                // preserve write empty lines
                if (line.trim().length() > 0) {
                    // 2. pick up the relevant elements from the blocks from every line
                    stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

                    result.add(stringBuilder.toString());
                }
            }
        }
        return result;
    }

    /**
     * Converts an TXT coordinate file from the geodata server Basel Landschaft into an KOO file for LTOP.
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertTXT2KOO() {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder;

        String number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting,
                northing, emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi;

        writeHeadline(result);

        for (String line : readStringLines) {
            // prevent wrong output with empty strings of defined length from class
            number = this.number;
            pointType = this.pointType;
            emptySpaceTY = this.emptySpaceTY;
            toleranceCategory = this.toleranceCategory;
            emptySpaceTK = this.emptySpaceTK;
            easting = this.easting;
            northing = this.northing;
            emptySpaceX = this.emptySpaceX;
            height = this.height;
            emptySpaceH = this.emptySpaceH;
            geoid = this.geoid;
            emptySpaceGEOID = this.emptySpaceGEOID;
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

            // preserve write empty lines
            if (line.trim().length() > 0) {
                // 2. pick up the relevant elements from the blocks from every line
                stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

                result.add(stringBuilder.toString());
            }
        }
        return result;
    }

    /**
     * Converts an TXT coordinate file from the geodata server Basel Landschaft into an KOO file for LTOP.
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertTXTBaselLandschaft2KOO() {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder;

        String number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting,
                northing, emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi;

        writeHeadline(result);

        // remove comment line
        readStringLines.remove(0);

        for (String line : readStringLines) {
            // prevent wrong output with empty strings of defined length from class
            number = this.number;
            pointType = this.pointType;
            emptySpaceTY = this.emptySpaceTY;
            toleranceCategory = this.toleranceCategory;
            emptySpaceTK = this.emptySpaceTK;
            easting = this.easting;
            northing = this.northing;
            emptySpaceX = this.emptySpaceX;
            height = this.height;
            emptySpaceH = this.emptySpaceH;
            geoid = this.geoid;
            emptySpaceGEOID = this.emptySpaceGEOID;
            eta = this.eta;
            xi = this.xi;

            String[] lineSplit = line.trim().split("\\s+");

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

            // preserve write empty lines
            if (line.trim().length() > 0) {
                // 2. pick up the relevant elements from the blocks from every line
                stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

                result.add(stringBuilder.toString());
            }
        }
        return result;
    }

    private StringBuilder prepareStringBuilder(String number, String pointType, String emptySpaceTY, String toleranceCategory,
                                               String emptySpaceTK, String easting, String northing, String emptySpaceX, String height,
                                               String emptySpaceH, String geoid, String emptySpaceGEOID, String eta, String xi) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(number);
        stringBuilder.append(pointType);
        stringBuilder.append(emptySpaceTY);
        stringBuilder.append(toleranceCategory);
        stringBuilder.append(emptySpaceTK);
        stringBuilder.append(easting);
        stringBuilder.append(northing);
        stringBuilder.append(emptySpaceX);
        stringBuilder.append(height);
        stringBuilder.append(emptySpaceH);
        stringBuilder.append(geoid);
        stringBuilder.append(emptySpaceGEOID);
        stringBuilder.append(eta);
        stringBuilder.append(xi);

        return stringBuilder;
    }

    /**
     * Writes the comment line into an given ArrayList<String>.
     * @param result ArrayList<String> to write in
     */
    private void writeHeadline(ArrayList<String> result) {
        // insert RyCON version, date and time
        Date d = new Date();
        DateFormat df;
        df = DateFormat.getDateTimeInstance(/* dateStyle */ DateFormat.LONG,
                                            /* timeStyle */ DateFormat.MEDIUM );

        // $$PK for cartesian coordinates
        // $$EL for geographic coordinates

        result.add(String.format("$$PK " + I18N.getStrLTOPCommentLine(), Version.getVersion(), df.format(d)));
    }

} // end of FileToolsLTOp
