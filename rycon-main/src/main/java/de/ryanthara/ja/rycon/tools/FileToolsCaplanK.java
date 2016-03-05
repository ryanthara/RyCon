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

import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class implements several basic operations on CAPLAN K files.
 * <p>
 * The CAPLAN K file format is a line based and column orientated file format developed
 * by Cremer Programmentwicklung GmbH to store coordinates in different formats.
 * <p>
 * Example file:
 *
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8
 * !-------------------------------------------------------------------------------
 * ! Folgende Daten wurden von RyCON Build xxx am 03.02.2016 erzeugt.
 * !-------------------------------------------------------------------------------
 *              GB1 7  2612259.5681  1256789.1990    256.90815 |10
 *              GB2 7  2612259.5681  1256789.1990    256.90815 |10
 *             1003 7  2612259.5681  1256789.1990    256.90815 |10|Att1|Att2
 *             1062 7  2612259.5681  1256789.1990    256.90815 |10
 *         TF 1067G 4  2612259.5681  1256789.1990    256.90815 |10
 *         NG 2156U 3  2612259.5681  1256789.1990      0.00000 |10
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 7
 */
public class FileToolsCaplanK {

    // prevent wrong output with empty strings of defined length
    private final String valency = "  ";
    private final String easting = "              ";
    private final String northing = "              ";
    private final String height = "             ";
    private final String freeSpace = " ";
    private final String objectTyp = "";
    private final String attr = "";

    private ArrayList<String> readStringLines;
    private List<String[]> readCSVLines;

    /**
     * Class constructor with parameter for the read lines as {@code ArrayList<String>} object.
     * <p>
     * This constructor is used for reading line based text files without a special separator sign.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public FileToolsCaplanK(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Class constructor with parameter for the read lines as {@code List<String[]>} object.
     * <p>
     * This constructor is used for reading csv file lines.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public FileToolsCaplanK(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }


    /**
     * Converts a CSV file (nr;x;y;z or nr;code;x;y;z) into a K format file.
     *
     * @param useSimpleFormat option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertCSV2K(boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            writeCommentLine(result);
        }

        for (String[] stringField : readCSVLines) {
            int valencyIndicator = 0;

            StringBuilder stringBuilder = new StringBuilder();

            String valency = this.valency;
            String freeSpace = this.freeSpace;
            String objectTyp = this.objectTyp;
            String easting = this.easting;
            String northing = this.northing;
            String height = this.height;

            // point number (no '*', ',' and ';'), column 1 - 16
            String number = stringField[0].replaceAll("\\s+", "").trim();
            number = number.replaceAll("\\*", "#");
            number = number.replaceAll(",", ".");
            number = number.replaceAll(";", ":");
            number = String.format("%16s", number);

            switch (stringField.length) {
                case 3:     // contains nr x y
                    // easting E, column 19-32
                    easting = String.format("%14s", fillZeroDigits(stringField[1], 4));

                    // northing N, column 33-46
                    northing = String.format("%14s", fillZeroDigits(stringField[2], 4));
                    valencyIndicator = 3;
                    break;

                case 4:     // contains nr x y z
                    // easting E, column 19-32
                    easting = String.format("%14s", fillZeroDigits(stringField[1], 4));

                    // northing N, column 33-46
                    northing = String.format("%14s", fillZeroDigits(stringField[2], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 5, but not always valued
                    height = "";
                    if (!stringField[4].equals("")) {
                        // height H, column 47-59
                        height = String.format("%13s", fillZeroDigits(stringField[3], 5));
                        Double d = Double.parseDouble(height);
                        if (d != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;

                case 5:     // contains nr code x y z
                    // code is in column 2 and the same as object type, column 62...
                    if (writeCodeColumn) {
                        objectTyp = "|".concat(stringField[1]);
                    }

                    // easting E, column 19-32
                    easting = String.format("%14s", fillZeroDigits(stringField[2], 4));

                    // northing N, column 33-46
                    northing = String.format("%14s", fillZeroDigits(stringField[3], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 5, but not always valued
                    height = "";
                    if (!stringField[4].equals("")) {
                        // height H, column 47-59
                        height = String.format("%13s", fillZeroDigits(stringField[4], 5));
                        Double d = Double.parseDouble(height);
                        if (d != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;

            }

            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            // 2. pick up the relevant elements from the blocks from every line, check ZF option
            // if ZF option is checked, then use only no 7 x y z for K file
            if (useSimpleFormat) {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
            } else {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
                if (!objectTyp.equals("")) {
                    stringBuilder.append(freeSpace);
                    stringBuilder.append(objectTyp);
                }
            }

            result.add(stringBuilder.toString());
        }
        return result;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (switzerland) into a K format file.
     *
     * @param useSimpleFormat option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertCSVBaselStadt2K(boolean useSimpleFormat, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            writeCommentLine(result);
        }

        for (String[] stringField : readCSVLines) {
            int valencyIndicator;

            StringBuilder stringBuilder = new StringBuilder();

            String valency = this.valency;
            String freeSpace = this.freeSpace;
            String objectTyp = this.objectTyp;

            // point number (no '*', ',' and ';'), column 1 - 16
            String number = stringField[0].replaceAll("\\s+", "").trim();
            number = number.replaceAll("\\*", "#");
            number = number.replaceAll(",", ".");
            number = number.replaceAll(";", ":");
            number = String.format("%16s", number);

            // easting E, column 19-32
            String easting = String.format("%14s", fillZeroDigits(stringField[2], 4));

            // northing N, column 33-46
            String northing = String.format("%14s", fillZeroDigits(stringField[3], 4));
            valencyIndicator = 3;

            // height (Z) is in column 5, but not always valued
            String height = "";
            if (!stringField[4].equals("")) {
                // height H, column 47-59
                height = String.format("%13s", fillZeroDigits(stringField[4], 5));
                Double d = Double.parseDouble(height);
                if (d != 0d) {
                    valencyIndicator += 4;
                }
            }

            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            // 2. pick up the relevant elements from the blocks from every line, check ZF option
            // if ZF option is checked, then use only no 7 x y z for K file
            if (useSimpleFormat) {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
            } else {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
                if (!objectTyp.equals("")) {
                    stringBuilder.append(freeSpace);
                    stringBuilder.append(objectTyp);
                }
            }

            result.add(stringBuilder.toString());
        }
        return result;
    }

    /**
     * Converts a cadwork node.dat file into K file.
     * <p>
     *
     * @param useSimpleFormat option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     * @param writeCodeColumn option to write the code column into the K file
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertCadwork2KFile(boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            writeCommentLine(result);
        }

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        for (String line : readStringLines) {
            int valencyIndicator;

            String[] lineSplit = line.trim().split("\\s+");

            StringBuilder stringBuilder = new StringBuilder();

            String valency = this.valency;
            String freeSpace = this.freeSpace;
            String objectTyp = this.objectTyp;

            // point number (no '*', ',' and ';'), column 1 - 16
            String number = lineSplit[5];
            number = number.replaceAll("\\*", "#");
            number = number.replaceAll(",", ".");
            number = number.replaceAll(";", ":");
            number = String.format("%16s", number);

            // easting E, column 19-32
            String easting = String.format("%14s", fillZeroDigits(lineSplit[1], 4));

            // northing N, column 33-46
            String northing = String.format("%14s", fillZeroDigits(lineSplit[2], 4));
            valencyIndicator = 3;

            // height H, column 47-59
            String height = String.format("%13s", fillZeroDigits(lineSplit[3], 5));
            if (Double.parseDouble(height) != 0d) {
                valencyIndicator += 4;
            }

            // code is the same as object type, column 62...
            if (writeCodeColumn) {
                objectTyp = "|".concat(lineSplit[4]);
            }

            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            // 2. pick up the relevant elements from the blocks from every line, check ZF option
            // if ZF option is checked, then use only no 7 x y z for K file
            if (useSimpleFormat) {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
            } else {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
                if (!objectTyp.equals("")) {
                    stringBuilder.append(freeSpace);
                    stringBuilder.append(objectTyp);
                }
            }

            result.add(stringBuilder.toString());
        }

        return result;
    }

    /**
     * Converts a Leica GSI file into CAPLAN K file.
     * <p>
     *
     * @param useSimpleFormat option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertGSI2KFile(boolean useSimpleFormat, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();
        FileToolsLeicaGSI gsiTools = new FileToolsLeicaGSI(readStringLines);

        if (writeCommentLine) {
            writeCommentLine(result);
        }

        // 1. convert lines into GSI-Blocks with BlockEncoder
        ArrayList<ArrayList<GSIBlock>> blocksInLines = gsiTools.getEncodedGSIBlocks();

        for (ArrayList<GSIBlock> blocksAsLines : blocksInLines) {
            StringBuilder stringBuilder = new StringBuilder();

            // prevent wrong output with empty strings of defined length from class
            String number = "";
            String valency = this.valency;
            String easting = this.easting;
            String northing = this.northing;
            String height = this.height;
            String freeSpace = this.freeSpace;
            String objectTyp = this.objectTyp;
            String attr = this.attr;

            for (int i = 0; i < gsiTools.getFoundWordIndices().size(); i++) {
                int valencyIndicator = 0;

                for (GSIBlock block : blocksAsLines) {
                    String s = block.toPrintFormatCSV();

                    int wordIndex = block.getWordIndex();

                    switch (wordIndex) {
                        case 11:        // point number (no '*', ',' and ';'), column 1 - 16
                            s = s.replaceAll("\\*", "#");
                            s = s.replaceAll(",", ".");
                            s = s.replaceAll(";", ":");

                            number = String.format("%16s", s);
                            break;
                        case 41:        // code is the same as object type, column 62...
                            objectTyp = "|".concat(s);
                            break;
                        case 71:        // comment 1, used as Attr1
                        case 72:        // comment 2, used as Attr2
                        case 73:        // comment 3, used as Attr3
                        case 74:        // comment 4, used as Attr4
                        case 75:        // comment 5, used as Attr5
                        case 76:        // comment 6, used as Attr6
                        case 77:        // comment 7, used as Attr7
                        case 78:        // comment 8, used as Attr8
                        case 79:        // comment 9, used as Attr9
                            attr = attr.concat("|".concat(s));
                            break;
                        case 81:        // easting E, column 19-32
                            easting = String.format("%14s", fillZeroDigits(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 82:        // northing N, column 33-46
                            northing = String.format("%14s", fillZeroDigits(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 83:        // height H, column 47-59
                            height = String.format("%13s", fillZeroDigits(s, 5));
                            valencyIndicator += 4;
                            break;
                        case 84:        // easting E0, column 19-32
                            easting = String.format("%14s", fillZeroDigits(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 85:        // northing N0, column 33-46
                            northing = String.format("%14s", fillZeroDigits(s, 4));
                            valencyIndicator = 3;
                            break;
                        case 86:        // height H0, column 47-59
                            height = String.format("%13s", fillZeroDigits(s, 5));
                            valencyIndicator += 4;
                            break;
                    }

                    if (valencyIndicator > 0) {
                        valency = " ".concat(Integer.toString(valencyIndicator));
                    }
                }

                // 2. pick up the relevant elements from the blocks from every line, check ZF option
                // if ZF option is checked, then use only no 7 x y z for K file
                stringBuilder = new StringBuilder();

                if (useSimpleFormat) {
                    stringBuilder.append(number);
                    stringBuilder.append(valency);
                    stringBuilder.append(easting);
                    stringBuilder.append(northing);
                    stringBuilder.append(height);
                } else {
                    stringBuilder.append(number);
                    stringBuilder.append(valency);
                    stringBuilder.append(easting);
                    stringBuilder.append(northing);
                    stringBuilder.append(height);
                    if (!objectTyp.equals("") | !attr.equals("")) {
                        stringBuilder.append(freeSpace);
                        stringBuilder.append(objectTyp);
                        stringBuilder.append(attr);
                    }
                }

                // clean up some variables after line reading is finished
                attr = "";
            }
            result.add(stringBuilder.toString());
        }

        return result;
    }

    /**
     * Converts a text file (nr x y z or nr code x y z) into a K format file.
     * <p>
     *
     * @param useSimpleFormat option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCommentLine option to write a comment line into the K file with basic information
     * @param writeCodeColumn option to write a found code into the K file
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertTXT2K(boolean useSimpleFormat, boolean writeCommentLine, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            writeCommentLine(result);
        }

        for (String line : readStringLines) {
            int valencyIndicator = -1;

            String[] lineSplit = line.trim().split("\\s+");

            StringBuilder stringBuilder = new StringBuilder();

            String valency = this.valency;
            String freeSpace = this.freeSpace;
            String objectTyp = this.objectTyp;
            String northing = this.northing;
            String easting = this.easting;
            String height = this.height;

            // point number is always in column 1 (no '*', ',' and ';'), column 1 - 16
            String number = lineSplit[0];
            number = number.replaceAll("\\*", "#");
            number = number.replaceAll(",", ".");
            number = number.replaceAll(";", ":");
            number = String.format("%16s", number);

            switch (lineSplit.length) {
                case 3:     // line contains no height
                    // easting (Y) is in column 2 -> column 19-32
                    easting = String.format("%14s", fillZeroDigits(lineSplit[1], 4));

                    // northing (X) is in column 3 -> column 33-46
                    northing = String.format("%14s", fillZeroDigits(lineSplit[2], 4));
                    valencyIndicator = 3;
                    break;

                case 4:     // line contains no code
                    // easting (Y) is in column 2 -> column 19-32
                    easting = String.format("%14s", fillZeroDigits(lineSplit[1], 4));

                    // northing (X) is in column 3 -> column 33-46
                    northing = String.format("%14s", fillZeroDigits(lineSplit[2], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 4 -> column 47-59
                    height = String.format("%13s", fillZeroDigits(lineSplit[3], 5));
                    Double d = Double.parseDouble(height);
                    if (d != 0d) {
                        valencyIndicator += 4;
                    }
                    break;

                case 6:     // line contains code at second position and height
                    // code is in column 2 -> column 62...
                    if (writeCodeColumn) {
                        objectTyp = "|".concat(lineSplit[1]);
                    }

                    // easting (Y) is in column 4 -> column 19-32
                    easting = String.format("%14s", fillZeroDigits(lineSplit[2], 4));

                    // northing (X) is in column 5 -> column 33-46
                    northing = String.format("%14s", fillZeroDigits(lineSplit[3], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 6, and not always valued (LFP file) -> column 47-59
                    if (lineSplit[5].equals("NULL")) {
                        height = String.format("%13s", fillZeroDigits("-9999", 4));
                    } else {
                        height = String.format("%13s", fillZeroDigits(lineSplit[4], 5));
                        if (Double.parseDouble(height) != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;
            }
            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            // 2. pick up the relevant elements from the blocks from every line, check ZF option
            // if ZF option is checked, then use only no 7 x y z for K file
            if (useSimpleFormat) {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
            } else {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
                if (!objectTyp.equals("")) {
                    stringBuilder.append(freeSpace);
                    stringBuilder.append(objectTyp);
                }
            }

            result.add(stringBuilder.toString());

        }

        return result;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (switzerland) into a K format file.
     * <p>
     * @param useSimpleFormat option to write a reduced K file which is compatible to ZF LaserControl
     * @param writeCodeColumn option to write a found code into the K file
     * @param writeCommentLine option to write a comment line into the K file with basic information
     *
     * @return converted K file as ArrayList<String>
     */
    public ArrayList<String> convertTXTBaselLandschaft2K(boolean useSimpleFormat, boolean writeCodeColumn,
                                                         boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<>();

        readStringLines.remove(0);  // remove comment line

        if (writeCommentLine) {
            writeCommentLine(result);
        }

        for (String line : readStringLines) {
            int valencyIndicator = -1;

            String[] lineSplit = line.trim().split("\\s+");

            StringBuilder stringBuilder = new StringBuilder();

            String valency = this.valency;
            String freeSpace = this.freeSpace;
            String objectTyp = this.objectTyp;
            String northing = this.northing;
            String easting = this.easting;
            String height = this.height;

            // point number is always in column 1 (no '*', ',' and ';'), column 1 - 16
            String number = lineSplit[1];
            number = number.replaceAll("\\*", "#");
            number = number.replaceAll(",", ".");
            number = number.replaceAll(";", ":");
            number = String.format("%16s", number);

            switch (lineSplit.length) {
                case 5:     // HFP file
                    // easting (Y) is in column 3 -> column 19-32
                    easting = String.format("%14s", fillZeroDigits(lineSplit[2], 4));

                    // northing (X) is in column 4 -> column 33-46
                    northing = String.format("%14s", fillZeroDigits(lineSplit[3], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 5, and not always valued (LFP file) -> column 47-59
                    height = String.format("%13s", fillZeroDigits(lineSplit[4], 5));
                    Double d = Double.parseDouble(height);
                    if (d != 0d) {
                        valencyIndicator += 4;
                    }
                    break;

                case 6:     // LFP file
                    // use 'Versicherungsart' as code. It is in column 3 -> column 62...
                    if (writeCodeColumn) {
                        objectTyp = "|".concat(lineSplit[2]);
                    }

                    // easting (Y) is in column 4 -> column 19-32
                    easting = String.format("%14s", fillZeroDigits(lineSplit[3], 4));

                    // northing (X) is in column 5 -> column 33-46
                    northing = String.format("%14s", fillZeroDigits(lineSplit[4], 4));
                    valencyIndicator = 3;

                    // height (Z) is in column 6, and not always valued (LFP file) -> column 47-59
                    if (lineSplit[5].equals("NULL")) {
                        height = String.format("%13s", fillZeroDigits("-9999", 5));
                    } else {
                        height = String.format("%13s", fillZeroDigits(lineSplit[5], 5));
                        if (Double.parseDouble(height) != 0d) {
                            valencyIndicator += 4;
                        }
                    }
                    break;
            }
            if (valencyIndicator > 0) {
                valency = " ".concat(Integer.toString(valencyIndicator));
            }

            // 2. pick up the relevant elements from the blocks from every line, check ZF option
            // if ZF option is checked, then use only no 7 x y z for K file
            if (useSimpleFormat) {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
            } else {
                stringBuilder.append(number);
                stringBuilder.append(valency);
                stringBuilder.append(easting);
                stringBuilder.append(northing);
                stringBuilder.append(height);
                if (!objectTyp.equals("")) {
                    stringBuilder.append(freeSpace);
                    stringBuilder.append(objectTyp);
                }
            }

            result.add(stringBuilder.toString());

        }
        return result;
    }

    /**
     * Writes the comment line into an given ArrayList<String>.
     * @param result ArrayList<String> to write in
     */
    private void writeCommentLine(ArrayList<String> result) {
        String commentLine1 = "!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8";
        String commentLine2 = "!-------------------------------------------------------------------------------";

        result.add(commentLine1);
        result.add(commentLine2);

        // insert RyCON version, date and time
        Date d = new Date();
        DateFormat df;
        df = DateFormat.getDateTimeInstance(/* dateStyle */ DateFormat.LONG,
                                            /* timeStyle */ DateFormat.MEDIUM );
        result.add(String.format(I18N.getStrCaplanCommentLine(), Version.getVersion(), df.format(d)));
        result.add(commentLine2);
    }

    /**
     * Fills a string value with a number of zeros to a defined last decimal place.
     *
     * @param lastDecimalLength length of the last decimal place
     * @return filled up string value
     */
    private String fillZeroDigits(String s, int lastDecimalLength) {
        int decimalSignPosition = s.lastIndexOf('.');
        int length = s.length();
        int numberOfMissingZeros = lastDecimalLength - (length - decimalSignPosition - 1);

        if (numberOfMissingZeros > 0) {
            for (int i = 0; i < numberOfMissingZeros; i++) {
                s = s.concat("0");
            }
        } else if (numberOfMissingZeros < 0) {
            s = s.substring(0, length + numberOfMissingZeros);
        }

        return s;
    }

} // end of FileToolsCaplanK
