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
     * Converts an Leica GSI coordinate file into an KOO file for LTOP.
     * <p>
     * The WIs 81 till 86 are supported.
     *
     * @return converted KOO file
     */
    public ArrayList<String> convertGSI2KOO() {
        ArrayList<String> result = new ArrayList<>();

        FileToolsLeicaGSI gsiTools = new FileToolsLeicaGSI(readStringLines);

        writeHeadline(result);

        // 1. convert lines into GSI-Blocks with BlockEncoder
        ArrayList<ArrayList<GSIBlock>> blocksInLines = gsiTools.getEncodedGSIBlocks();

        for (ArrayList<GSIBlock> blocksAsLines : blocksInLines) {
            StringBuilder stringBuilder = new StringBuilder();

            // prevent wrong output with empty strings of defined length from class
            String number = this.number;
            String pointType = this.pointType;
            String emptySpaceTY = this.emptySpaceTY;
            String toleranceCategory = this.toleranceCategory;
            String emptySpaceTK = this.emptySpaceTK;
            String easting = this.easting;
            String northing = this.northing;
            String emptySpaceX = this.emptySpaceX;
            String height = this.height;
            String emptySpaceH = this.emptySpaceH;
            String geoid = this.geoid;
            String emptySpaceGEOID = this.emptySpaceGEOID;
            String eta = this.eta;
            String xi = this.xi;

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

                // 2. pick up the relevant elements from the blocks from every line
                stringBuilder = prepareStringBuilder(number, pointType, emptySpaceTY, toleranceCategory, emptySpaceTK, easting, northing,
                        emptySpaceX, height, emptySpaceH, geoid, emptySpaceGEOID, eta, xi);

            }
            result.add(stringBuilder.toString());
        }

        return result;
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


    private StringBuilder prepareStringBuilder(String number, String pointType, String emptySpaceTY, String toleranceCategory, String emptySpaceTK,
                                               String easting, String northing, String emptySpaceX, String height,
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

} // end of FileToolsLTOp
