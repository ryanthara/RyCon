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
package de.ryanthara.ja.rycon.converter.gsi;

import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.ArrayList;

/**
 * This class provides functions to convert measurement files from Zeiss REC format
 * and it's dialects (R4, R5, REC500 and M5) into Leica GSI8 or GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2GSI {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based Zeiss REC files in different dialects.
     * <p>
     * The differentiation of the content is done by the called method.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Zeiss2GSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Convert a Zeiss REC file (R4, R5, M5 or REC500) into a GSI formatted file.
     * <p>
     * This method can differ between different Zeiss REC dialects because of the
     * different structure and line length.
     *
     * @param isGSI16 distinguish between GSI8 or GSI16 output
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertZeiss2GSI(boolean isGSI16) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        int
                ptIDA = -1, ptIDB = -1, ptC = -1, ptD = -1,
                wb1A = -1, wb1B = -1, wb1C = -1, wb1D = -1, wb1E = -1, wb1F = -1,
                wb2A = -1, wb2B = -1, wb2C = -1, wb2D = -1, wb2E = -1, wb2F = -1,
                wb3A = -1, wb3B = -1, wb3C = -1, wb3D = -1, wb3E = -1, wb3F = -1;

        String
                error = "", lineNumber = "", pointNumber = "", pointIdentification = "",
                type1 = "", type2 = "", type3 = "",
                value1 = "", value2 = "", value3 = "",
                unit1 = "", unit2 = "", unit3 = "";

        int lineCounter = 1;

        for (String line : readStringLines) {
            // skip empty lines
            if (line.trim().length() > 0) {
                blocks = new ArrayList<>();

                // check dialect with substring operation
                if (line.startsWith("For")) { // R4, R5 or M5
                    // differ dialect with special kind of substring variable (a, b) with different values
                    if (line.startsWith("For R4") || line.startsWith("For_R4")) {
                        ptIDA = 7;
                        ptIDB = 9;
                        ptC = 10;
                        ptD = 17;
                        wb1A = 18;
                        wb1B = 20;
                        wb1C = 21;
                        wb1D = 32;
                        wb1E = 33;
                        wb1F = 37;
                        wb2A = 38;
                        wb2B = 40;
                        wb2C = 41;
                        wb2D = 51;
                        wb2E = 54;
                        wb2F = 56;
                        wb3A = 58;
                        wb3B = 60;
                        wb3C = 61;
                        wb3D = 72;
                        wb3E = 73;
                        wb3F = 77;
                    } else if (line.startsWith("For R5") || line.startsWith("For_R5")) {
                        ptIDA = 16;
                        ptIDB = 18;
                        ptC = 19;
                        ptD = 26;
                        wb1A = 27;
                        wb1B = 29;
                        wb1C = 30;
                        wb1D = 41;
                        wb1E = 42;
                        wb1F = 46;
                        wb2A = 47;
                        wb2B = 49;
                        wb2C = 50;
                        wb2D = 61;
                        wb2E = 62;
                        wb2F = 66;
                        wb3A = 67;
                        wb3B = 69;
                        wb3C = 70;
                        wb3D = 81;
                        wb3E = 82;
                        wb3F = 86;

                        // special for R5
                        lineNumber = line.substring(11, 14).trim();
                    } else if (line.startsWith("For M5") || line.startsWith("For_M5")) {
                        ptIDA = 17;
                        ptIDB = 20;
                        ptC = 21;
                        ptD = 48;
                        wb1A = 49;
                        wb1B = 51;
                        wb1C = 52;
                        wb1D = 66;
                        wb1E = 67;
                        wb1F = 71;
                        wb2A = 72;
                        wb2B = 74;
                        wb2C = 75;
                        wb2D = 89;
                        wb2E = 90;
                        wb2F = 94;
                        wb3A = 95;
                        wb3B = 97;
                        wb3C = 98;
                        wb3D = 112;
                        wb3E = 113;
                        wb3F = 116;

                        // special for M5
                        lineNumber = line.substring(11, 17).trim();
                        error = line.substring(118);
                    }

                    pointIdentification = line.substring(ptIDA, ptIDB).trim();
                    pointNumber = line.substring(ptC, ptD).trim();

                    type1 = line.substring(wb1A, wb1B).trim();
                    value1 = line.substring(wb1C, wb1D).trim();
                    unit1 = line.substring(wb1E, wb1F);

                    if (line.length() > wb2A - 1) {
                        type2 = line.substring(wb2A, wb2B).trim();
                        value2 = line.substring(wb2C, wb2D).trim();
                        unit2 = line.substring(wb2E, wb2F);

                        if (line.length() > wb3A - 1) {
                            type3 = line.substring(wb3A, wb3B).trim();
                            value3 = line.substring(wb3C, wb3D).trim();
                            unit3 = line.substring(wb3E, wb3F);
                        }
                    }

                    lineCounter++;

                    // valid REC 500 lines starts with three space signs and are not empty or filled with spaces
                } else if (line.startsWith("   ") & line.trim().length() > 0) {
                    lineCounter = Integer.parseInt(line.substring(3, 7).trim());
                    pointNumber = line.substring(8, 22).trim();
                    pointIdentification = line.substring(22, 35).trim();

                    type1 = line.substring(36, 38).trim();
                    value1 = line.substring(38, 50).trim();

                    if (line.length() > 50) {
                        type2 = line.substring(51, 53).trim();
                        value2 = line.substring(53, 66).trim();

                        if (line.length() > 66) {
                            type3 = line.substring(67, 69).trim();
                            value3 = line.substring(69, 78).trim();
                        }
                    }
                }

                // fill in the values into the GSI format expressions
                blocks.add(new GSIBlock(isGSI16, 11, lineCounter, pointNumber));

                // use point identification (e.g. code, point classes, ...)
                if (pointIdentification.trim().length() > 0) {
                    blocks.add(new GSIBlock(isGSI16, 71, lineCounter, pointIdentification));
                }

                if (value1.trim().length() > 0) {
                    switch (type1.trim()) {
                        case "ih":
                            blocks.add(new GSIBlock(isGSI16, 88, lineCounter, value1));
                            break;
                        case "th":
                            blocks.add(new GSIBlock(isGSI16, 87, lineCounter, value1));
                            break;
                        case "Hz":
                            blocks.add(new GSIBlock(isGSI16, 21, lineCounter, value1));
                            break;
                        case "Y":
                            blocks.add(new GSIBlock(isGSI16, 81, lineCounter, value1));
                            break;
                    }
                }

                if (value2.trim().length() > 0) {
                    switch (type2.trim()) {
                        case "V":
                            blocks.add(new GSIBlock(isGSI16, 22, lineCounter, value2));
                            break;
                        case "X":
                            blocks.add(new GSIBlock(isGSI16, 82, lineCounter, value2));
                            break;
                    }
                }

                if (value3.trim().length() > 0) {
                    switch (type3.trim()) {
                        case "D":
                            blocks.add(new GSIBlock(isGSI16, 31, lineCounter, value3));
                            break;
                        case "Z":
                            blocks.add(new GSIBlock(isGSI16, 83, lineCounter, value3));
                            break;
                    }
                }

                // check for at least one or more added elements to prevent writing empty lines
                if (blocks.size() > 0) {
                    lineCounter++;
                    blocksInLines.add(blocks);
                }
            }
        }

        return BaseToolsGSI.lineTransformation(isGSI16, blocksInLines);
    }

} // end of Zeiss2GSI
