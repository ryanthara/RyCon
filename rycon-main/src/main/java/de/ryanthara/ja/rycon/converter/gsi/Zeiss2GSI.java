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

import de.ryanthara.ja.rycon.converter.zeiss.BaseToolsZeiss;
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
     * Converts a Zeiss REC file (R4, R5, M5 or REC500) into a Leica GSI formatted file.
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
                        final int[] R4 = BaseToolsZeiss.R4_LINE_POSITIONS;
                        ptIDA = R4[0];
                        ptIDB = R4[1];
                        ptC = R4[2];
                        ptD = R4[3];
                        wb1A = R4[4];
                        wb1B = R4[5];
                        wb1C = R4[6];
                        wb1D = R4[7];
                        wb1E = R4[8];
                        wb1F = R4[9];
                        wb2B = R4[10];
                        wb2A = R4[11];
                        wb2C = R4[12];
                        wb2D = R4[13];
                        wb2E = R4[14];
                        wb2F = R4[15];
                        wb3A = R4[16];
                        wb3B = R4[17];
                        wb3C = R4[18];
                        wb3D = R4[19];
                        wb3E = R4[20];
                        wb3F = R4[21];
                    } else if (line.startsWith("For R5") || line.startsWith("For_R5")) {
                        final int[] R5 = BaseToolsZeiss.R5_LINE_POSITIONS;
                        lineNumber = line.substring(R5[0], R5[1]).trim();
                        ptIDA = R5[2];
                        ptIDB = R5[3];
                        ptC = R5[4];
                        ptD = R5[5];
                        wb1A = R5[6];
                        wb1B = R5[7];
                        wb1C = R5[8];
                        wb1D = R5[9];
                        wb1E = R5[10];
                        wb1F = R5[11];
                        wb2B = R5[12];
                        wb2A = R5[13];
                        wb2C = R5[14];
                        wb2D = R5[15];
                        wb2E = R5[16];
                        wb2F = R5[17];
                        wb3A = R5[18];
                        wb3B = R5[19];
                        wb3C = R5[20];
                        wb3D = R5[21];
                        wb3E = R5[22];
                        wb3F = R5[23];
                    } else if (line.startsWith("For M5") || line.startsWith("For_M5")) {
                        final int[] M5 = BaseToolsZeiss.M5_LINE_POSITIONS;
                        lineNumber = line.substring(M5[0], M5[1]).trim();
                        ptIDA = M5[2];
                        ptIDB = M5[3];
                        ptC = M5[4];
                        ptD = M5[5];
                        wb1A = M5[6];
                        wb1B = M5[7];
                        wb1C = M5[8];
                        wb1D = M5[9];
                        wb1E = M5[10];
                        wb1F = M5[11];
                        wb2B = M5[12];
                        wb2A = M5[13];
                        wb2C = M5[14];
                        wb2D = M5[15];
                        wb2E = M5[16];
                        wb2F = M5[17];
                        wb3A = M5[18];
                        wb3B = M5[19];
                        wb3C = M5[20];
                        wb3D = M5[21];
                        wb3E = M5[22];
                        wb3F = M5[23];
                        error = line.substring(M5[24]);
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
                    final int[] REC500 = BaseToolsZeiss.REC500_LINE_POSITIONS;
                    lineCounter = Integer.parseInt(line.substring(REC500[0], REC500[1]).trim());
                    pointNumber = line.substring(REC500[2], REC500[3]).trim();
                    pointIdentification = line.substring(REC500[4], REC500[5]).trim();

                    type1 = line.substring(REC500[6], REC500[7]).trim();
                    value1 = line.substring(REC500[8], REC500[9]).trim();

                    if (line.length() > 50) {
                        type2 = line.substring(REC500[10], REC500[11]).trim();
                        value2 = line.substring(REC500[12], REC500[13]).trim();

                        if (line.length() > 66) {
                            type3 = line.substring(REC500[14], REC500[15]).trim();
                            value3 = line.substring(REC500[16], REC500[17]).trim();
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
