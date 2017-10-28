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

import de.ryanthara.ja.rycon.core.elements.ZeissBlock;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to decode a string line in Zeiss REC format and it's
 * dialects (R4, R5, REC500 and M5) into a bunch of elements.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class ZeissDecoder {

    private int lineNumber;
    private int numOfBlocks;
    private String error = "";
    private String pointIdentification, pointNumber;
    private ArrayList<ZeissBlock> zeissBlocks = new ArrayList<>();
    private ZeissBlock block1 = null;
    private ZeissBlock block3 = null;
    private ZeissBlock block2 = null;
    private ZeissDialect dialect;

    /**
     * Constructs a new instance of this class without any parameters.
     */
    public ZeissDecoder() {
        numOfBlocks = 0;
    }

    public boolean decodeRecLine(String line) {
        boolean success = false;

        int
                ptIDA = -1, ptIDB = -1, ptC = -1, ptD = -1,
                wb1A = -1, wb1B = -1, wb1C = -1, wb1D = -1, wb1E = -1, wb1F = -1,
                wb2A = -1, wb2B = -1, wb2C = -1, wb2D = -1, wb2E = -1, wb2F = -1,
                wb3A = -1, wb3B = -1, wb3C = -1, wb3D = -1, wb3E = -1, wb3F = -1;

        lineNumber = -1;

        // check dialect with substring operation
        if (line.startsWith("For")) { // R4, R5 or M5
            // differ dialect with special kind of substring variable (a, b) with different values
            if (line.startsWith("For R4") || line.startsWith("For_R4")) {
                final int[] R4 = BaseToolsZeiss.getLinePositions(dialect = ZeissDialect.R4);

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
                wb2A = R4[10];
                wb2B = R4[11];
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
                final int[] R5 = BaseToolsZeiss.getLinePositions(dialect = ZeissDialect.R5);

                lineNumber = Integer.parseInt(line.substring(R5[0], R5[1] + 1).trim());

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
                wb2A = R5[12];
                wb2B = R5[13];
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
                final int[] M5 = BaseToolsZeiss.getLinePositions(dialect = ZeissDialect.M5);

                lineNumber = Integer.parseInt(line.substring(M5[0], M5[1] + 1).trim());

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
                wb2A = M5[12];
                wb2B = M5[13];
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

            // correct lines have at least one block
            pointIdentification = line.substring(ptIDA, ptIDB + 1).trim();
            pointNumber = line.substring(ptC, ptD + 1).trim();

            // check for being empty
            if (line.substring(wb1A, wb1F + 1).trim().length() > 1) {
                block1 = new ZeissBlock(ZeissTypeIdentifier.valueOf(
                        line.substring(wb1A, wb1B + 1).trim()),
                        line.substring(wb1C, wb1D + 1).trim(),
                        line.substring(wb1E, wb1F + 1).trim());

                zeissBlocks.add(block1);
                numOfBlocks = numOfBlocks + 1;
            }

            // check for a second block
            if (line.substring(wb2A, wb2F + 1).trim().length() > 1) {
                block2 = new ZeissBlock(ZeissTypeIdentifier.valueOf(
                        line.substring(wb2A, wb2B + 1).trim()),
                        line.substring(wb2C, wb2D + 1).trim(),
                        line.substring(wb2E, wb2F + 1).trim());

                zeissBlocks.add(block2);
                numOfBlocks = numOfBlocks + 1;

                // check for a third block
                if (line.substring(wb3A, wb3F + 1).trim().length() > 1) {
                    block3 = new ZeissBlock(ZeissTypeIdentifier.valueOf(
                            line.substring(wb3A, wb3B + 1).trim()),
                            line.substring(wb3C, wb3D + 1).trim(),
                            line.substring(wb3E, wb3F + 1).trim());

                    zeissBlocks.add(block3);
                    numOfBlocks = numOfBlocks + 1;
                }
            }

            success = true;

        } else if (line.startsWith("   ") && line.trim().length() > 0) {
            final int[] REC500 = BaseToolsZeiss.getLinePositions(dialect = ZeissDialect.REC500);

            lineNumber = Integer.parseInt(line.substring(REC500[0], REC500[1] + 1).trim());
            pointNumber = line.substring(REC500[2], REC500[3] + 1).trim();
            pointIdentification = line.substring(REC500[4], REC500[5] + 1).trim();

            block1 = new ZeissBlock(ZeissTypeIdentifier.valueOf(
                    line.substring(REC500[6], REC500[7] + 1).trim()),
                    line.substring(REC500[8], REC500[9] + 1).trim(),
                    "");

            zeissBlocks.add(block1);
            numOfBlocks = numOfBlocks + 1;

            if (line.length() > 50) {
                block2 = new ZeissBlock(ZeissTypeIdentifier.valueOf(
                        line.substring(REC500[10], REC500[11] + 1).trim()),
                        line.substring(REC500[12], REC500[13] + 1).trim(),
                        "");

                zeissBlocks.add(block2);
                numOfBlocks = numOfBlocks + 1;


                System.out.println(line.length());
                if (line.length() > 66) {
                    block3 = new ZeissBlock(ZeissTypeIdentifier.valueOf(
                            line.substring(REC500[14], REC500[15] + 1).trim()),
                            line.substring(REC500[16], REC500[17] + 1).trim(),
                            "");

                    zeissBlocks.add(block3);
                    numOfBlocks = numOfBlocks + 1;
                }
            }

            success = true;
        }

        return success;
    }

    /**
     * Returns the first block.
     *
     * @return first block
     */
    public ZeissBlock getBlock1() {
        return block1;
    }

    /**
     * Returns the second block.
     *
     * @return second block
     */
    public ZeissBlock getBlock2() {
        return block2;
    }

    /**
     * Returns the third block.
     *
     * @return third block
     */
    public ZeissBlock getBlock3() {
        return block3;
    }

    /**
     * Returns the current dialect of the read Zeiss REC formatted string line.
     *
     * @return current dialect
     */
    public ZeissDialect getDialect() {
        return dialect;
    }

    /**
     * Returns the error of a M5 line.
     *
     * @return error of M5 line
     */
    public String getError() {
        return error;
    }

    /**
     * Returns the current line number of the read Zeiss REC formatted string line.
     *
     * @return current line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the number of found ZeissBlocks.
     *
     * @return number of ZeissBlocks
     */
    public int getNumOfBlocks() {
        return numOfBlocks;
    }

    /**
     * Returns the point identification string (only).
     *
     * @return point identification string
     */
    public String getPointIdentification() {
        return pointIdentification;
    }

    /**
     * Returns the point number string (only).
     *
     * @return point number string
     */
    public String getPointNumber() {
        return pointNumber;
    }

    /**
     * Returns the found {@link ZeissBlock}.
     * ^
     *
     * @return found {@link ZeissBlock}
     */
    public ArrayList<ZeissBlock> getZeissBlocks() {
        return zeissBlocks;
    }

} // end of ZeissDecoder
