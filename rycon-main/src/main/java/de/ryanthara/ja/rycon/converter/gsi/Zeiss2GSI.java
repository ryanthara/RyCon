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

import de.ryanthara.ja.rycon.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.elements.GSIBlock;
import de.ryanthara.ja.rycon.elements.ZeissBlock;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert measurement and coordinate files from Zeiss REC format
 * and it's dialects (R4, R5, REC500 and M5) into Leica GSI8 or GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2GSI {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given an {@code ArrayList<String} that contains the read lines
     * in the Zeiss REC format and it's dialects (R4, R5, REC500 and M5).
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
     * <p>
     * Because of the fixed number of three blocks in every line, additional information is stored in the next line
     * with the same point number.
     *
     * @param isGSI16 distinguish between GSI8 or GSI16 output
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertZeiss2GSI(boolean isGSI16) {
        ArrayList<GSIBlock> blocks = new ArrayList<>();
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        String pointNumber = "";

        int readLineCounter = 1, writeLineCounter = 1;

        for (String line : readStringLines) {

            // skip empty lines
            if (line.trim().length() > 0) {
                ZeissDecoder decoder = new ZeissDecoder();

                if (decoder.decodeRecLine(line)) {
                    // use the decoded lines and differ e.g. code column by dialect
                    // TODO: 29.10.16 Implement the correct unit handling
                    switch (decoder.getDialect()) {
                        case R4:
                            break;
                        case R5:
                            break;
                        case M5:
                            break;
                        case REC500:
                            break;
                    }

                    readLineCounter = readLineCounter + 1;

                    // check if point values are in one or more lines stored
                    if (!pointNumber.equals(decoder.getPointNumber())) {
                        /*
                        finishing the current point and flush the blocks to the result array and although check
                        for at least one or more added elements to prevent writing empty lines
                         */
                        if (blocks.size() > 0) {
                            readLineCounter = readLineCounter + 1;
                            blocksInLines.add(blocks);
                            blocks = new ArrayList<>();
                        }

                        // prepare the read line results for the new point
                        pointNumber = decoder.getPointNumber();

                        writeLineCounter = writeLineCounter + 1;

                        // process the new line information
                        blocks.add(new GSIBlock(isGSI16, 11, readLineCounter, pointNumber));
                    }

                    for (ZeissBlock zeissBlock : decoder.getZeissBlocks()) {
                        fillValuesIntoBlocks(zeissBlock, blocks, isGSI16);
                    }
                }
            }
        }

        // flush last element if exists
        if (blocks.size() > 0) {
            blocksInLines.add(blocks);
        }

        return BaseToolsGSI.lineTransformation(isGSI16, blocksInLines);
    }

    private void fillValuesIntoBlocks(ZeissBlock zeissBlock, ArrayList<GSIBlock> blocks, boolean isGSI16) {
        switch (zeissBlock.getTypeIdentifier()) {
            case ih:
                blocks.add(new GSIBlock(isGSI16, 88, zeissBlock.getValue()));
                break;

            case th:
                blocks.add(new GSIBlock(isGSI16, 87, zeissBlock.getValue()));
                break;

            case D:
                blocks.add(new GSIBlock(isGSI16, 31, zeissBlock.getValue()));
                break;

            case Hz:
                blocks.add(new GSIBlock(isGSI16, 21, zeissBlock.getValue()));
                break;

            case V1:
                blocks.add(new GSIBlock(isGSI16, 22, zeissBlock.getValue()));
                break;

            case X:
                blocks.add(new GSIBlock(isGSI16, 82, zeissBlock.getValue()));
                break;

            case Y:
                blocks.add(new GSIBlock(isGSI16, 81, zeissBlock.getValue()));
                break;

            case Z:
                blocks.add(new GSIBlock(isGSI16, 83, zeissBlock.getValue()));
                break;

            default:
                System.err.println("Zeiss2GSI.convertZeiss2GSI() : line contains less or more tokens " + zeissBlock.getTypeIdentifier());
        }
    }

} // end of Zeiss2GSI
