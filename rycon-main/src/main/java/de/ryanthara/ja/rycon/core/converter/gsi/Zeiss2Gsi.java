/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.core.converter.gsi;

import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.core.elements.GSIBlock;
import de.ryanthara.ja.rycon.core.elements.ZeissBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert measurement and coordinate files from Zeiss REC format
 * and it's dialects (R4, R5, REC500 and M5) into Leica Geosystems GSI8 or GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2Gsi {

    private static final Logger logger = LoggerFactory.getLogger(Zeiss2Gsi.class.getName());
    private final List<String> lines;
    private int dateLine = -1, timeLine = -1;
    private int ppmLine = -1, constantLine = -1;
    private String ppmAndAdditionConstant = "";
    private String format1 = "YYssmsms", format2 = "MMDDhhmm";

    /**
     * Creates a converter with a list for the read line based
     * text files in the Zeiss REC format and it's dialects.
     *
     * <p>
     * The differentiation of the content is done by the called
     * method and it's content analyze functionality.
     *
     * @param lines list with Zeiss REC format lines
     */
    public Zeiss2Gsi(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a Zeiss REC file (R4, R5, M5 or REC500) into a Leica Geosystems GSI formatted file.
     * <p>
     * This method can differ between different Zeiss REC dialects because of the
     * different structure and line length.
     * <p>
     * Because of the fixed number of three blocks in every line, additional information is stored in the next line
     * with the same point number.
     *
     * @param isGSI16 distinguish between GSI8 or GSI16 output
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(boolean isGSI16) {
        List<GSIBlock> blocks = new ArrayList<>();
        List<List<GSIBlock>> blocksInLines = new ArrayList<>();

        String pointNumber = "";

        int readLineCounter = 0, writeLineCounter = 0;

        for (String line : lines) {

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

                        // run the new line information
                        blocks.add(new GSIBlock(isGSI16, 11, readLineCounter, pointNumber));
                    }

                    for (ZeissBlock zeissBlock : decoder.getZeissBlocks()) {
                        fillValuesIntoBlocks(zeissBlock, blocks, isGSI16, readLineCounter);
                    }

                    writeMultiLinedValues(blocks, isGSI16);
                }
            }
        }

        // flush last element if exists
        if (blocks.size() > 0) {
            blocksInLines.add(blocks);
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

    private void fillValuesIntoBlocks(ZeissBlock zeissBlock, List<GSIBlock> blocks, boolean isGSI16, int readLineCounter) {
        switch (zeissBlock.getTypeIdentifier()) {
            case ah:
                // target height
                blocks.add(new GSIBlock(isGSI16, 87, zeissBlock.getValue()));
                break;

            case dR:
                // station difference (levelling)
                blocks.add(new GSIBlock(isGSI16, 571, zeissBlock.getValue()));
                break;

            case dx:
                // northing coordinate difference
                blocks.add(new GSIBlock(isGSI16, 85, zeissBlock.getValue()));
                break;

            case dy:
                // easting coordinate difference
                blocks.add(new GSIBlock(isGSI16, 84, zeissBlock.getValue()));
                break;

            case dz:
                // height coordinate difference
                blocks.add(new GSIBlock(isGSI16, 86, zeissBlock.getValue()));
                break;

            case h:
                // height difference
                blocks.add(new GSIBlock(isGSI16, 33, zeissBlock.getValue()));
                break;

            case hz:
                // desired horizontal angle
                blocks.add(new GSIBlock(isGSI16, 24, zeissBlock.getValue()));
                break;

            case ih:
                // instrument height
                blocks.add(new GSIBlock(isGSI16, 88, zeissBlock.getValue()));
                break;

            case pm:
                // correction in ppm
                ppmLine = readLineCounter;
                prepareConstants(zeissBlock);
                break;

            case th:
                // target height
                blocks.add(new GSIBlock(isGSI16, 87, zeissBlock.getValue()));
                break;

            case v1:
                // desired vertical angle
                blocks.add(new GSIBlock(isGSI16, 27, zeissBlock.getValue()));
                break;

            case x:
                // local easting coordinate
                blocks.add(new GSIBlock(isGSI16, 84, zeissBlock.getValue()));
                break;

            case y:
                // local northing coordinate
                blocks.add(new GSIBlock(isGSI16, 85, zeissBlock.getValue()));
                break;

            case A:
                // addition constant 1/10mm
                blocks.add(new GSIBlock(isGSI16, 58, zeissBlock.getValue()));
                break;

            case D:
                // slope distance
                blocks.add(new GSIBlock(isGSI16, 31, zeissBlock.getValue()));
                break;

            case Dh:
                // horizontal angle difference (Hz0 - Hz)
                blocks.add(new GSIBlock(isGSI16, 25, zeissBlock.getValue()));
                break;

            case Dv:
                // vertical angle difference (V0 - V)
                blocks.add(new GSIBlock(isGSI16, 28, zeissBlock.getValue()));
                break;

            case DI:
                // prism constant
                constantLine = readLineCounter;
                prepareConstants(zeissBlock);
                break;

            case E:
                // horizontal distance
                blocks.add(new GSIBlock(isGSI16, 32, zeissBlock.getValue()));
                break;

            case Hz:
                // horizontal angle
                blocks.add(new GSIBlock(isGSI16, 21, zeissBlock.getValue()));
                break;

            case HD:
                // horizontal distance
                blocks.add(new GSIBlock(isGSI16, 32, zeissBlock.getValue()));
                break;

            case HV:
                // offset, maybe horizontal rotation
                blocks.add(new GSIBlock(isGSI16, 26, zeissBlock.getValue()));
                break;

            case KA:
                // date
                dateLine = readLineCounter;
                prepareDateAndTime(zeissBlock);
                break;

            case KB:
                // time
                timeLine = readLineCounter;
                prepareDateAndTime(zeissBlock);
                break;

            case KD:
                // point identification: beginning and end of a levelling line
                blocks.add(new GSIBlock(isGSI16, 41, "?......1"));
                break;

            case KN:
                // point identification: beginning and end of a levelling line
                blocks.add(new GSIBlock(isGSI16, 41, "?......1"));
                break;

            case KR:
                // information with code and point number
                preparePointAndCodeNumber(blocks, zeissBlock, isGSI16);
                break;

            case L:
                // single staff reading at levelling
                blocks.add(new GSIBlock(isGSI16, 330, zeissBlock.getValue()));
                break;

            case Lr:
                // backwards staff reading at levelling
                blocks.add(new GSIBlock(isGSI16, 331, zeissBlock.getValue()));
                break;

            case Lv:
                // forward staff reading at levelling
                blocks.add(new GSIBlock(isGSI16, 332, zeissBlock.getValue()));
                break;

            case Lz:
                // intermediate staff reading at levelling
                blocks.add(new GSIBlock(isGSI16, 332, zeissBlock.getValue()));
                break;

            case PI:
                // general point information
                preparePointAndCodeNumber(blocks, zeissBlock, isGSI16);
                break;

            case SD:
                // slope distance
                blocks.add(new GSIBlock(isGSI16, 31, zeissBlock.getValue()));
                break;

            case T:
                // text information
                blocks.add(new GSIBlock(isGSI16, 42, zeissBlock.getValue()));
                break;

            case TI:
                // text information
                blocks.add(new GSIBlock(isGSI16, 42, zeissBlock.getValue()));
                break;

            case TN:
                // general text information (levelling)
                blocks.add(new GSIBlock(isGSI16, 42, zeissBlock.getValue()));
                break;

            case TO:
                // general text information (levelling)
                blocks.add(new GSIBlock(isGSI16, 42, zeissBlock.getValue()));
                break;

            case TR:
                // text information
                blocks.add(new GSIBlock(isGSI16, 42, zeissBlock.getValue()));
                break;

            case V1:
                // vertical angle
                blocks.add(new GSIBlock(isGSI16, 22, zeissBlock.getValue()));
                break;

            case X:
                // northing coordinate
                blocks.add(new GSIBlock(isGSI16, 82, zeissBlock.getValue()));
                break;

            case X_:
                // northing coordinate
                blocks.add(new GSIBlock(isGSI16, 82, zeissBlock.getValue()));
                break;

            case Y:
                // easting coordinate
                blocks.add(new GSIBlock(isGSI16, 81, zeissBlock.getValue()));
                break;

            case Y_:
                // easting coordinate
                blocks.add(new GSIBlock(isGSI16, 81, zeissBlock.getValue()));
                break;

            case Z:
                // height coordinate
                blocks.add(new GSIBlock(isGSI16, 83, zeissBlock.getValue()));
                break;

            case ZE:
                // height coordinate
                blocks.add(new GSIBlock(isGSI16, 83, zeissBlock.getValue()));
                break;

            case Z_:
                // height coordinate
                blocks.add(new GSIBlock(isGSI16, 83, zeissBlock.getValue()));
                break;

            default:
                logger.trace("Line contains less or more tokens ({}) than needed or allowed.", zeissBlock.getTypeIdentifier());
                break;
        }
    }

    /*
    The correction value (ppm) and the addition/prism constant are stored in two different Zeiss blocks (pm and DI),
    but has to be stored in one GSI block (WI51)
     */
    // TODO find out how the ppm and prism constant is stored in the Zeiss REC file
    private void prepareConstants(ZeissBlock zeissBlock) {
        // prepare ppm and prism constant string
        String s = zeissBlock.getValue();

        ppmAndAdditionConstant = null;
    }

    /*
    The date and time are stored in two different Zeiss blocks (KA and KB), but has to be stored in two different
    GSI blocks (WI18 and WI19)
     */
    // TODO find out how the date and time is stored in Zeiss REC file
    private void prepareDateAndTime(ZeissBlock zeissBlock) {
        // prepare date and time string
        String s = zeissBlock.getValue();

        format1 = null;
        format2 = null;
    }

    /*
    The point number and code number are stored in an combined string and has to be split here.
     */
    // TODO find out how the point and code number storing is organized
    private void preparePointAndCodeNumber(List<GSIBlock> blocks, ZeissBlock zeissBlock, boolean isGSI16) {
        String s = zeissBlock.getValue();

        String codeNumber = "", pointNumber = "";

        blocks.add(new GSIBlock(isGSI16, 11, pointNumber));
        blocks.add(new GSIBlock(isGSI16, 41, codeNumber));
    }

    /*
    Writes values that are read from more than one Zeiss REC line into one GSI block (e.g. date and time,
    or ppm and prism constant)
     */
    private void writeMultiLinedValues(List<GSIBlock> blocks, boolean isGSI16) {
        // date and time
        if ((dateLine == timeLine) || (dateLine == timeLine + 1)) {
            // writer blocks for date and time strings
            blocks.add(new GSIBlock(isGSI16, 18, format1));
            blocks.add(new GSIBlock(isGSI16, 19, format2));

            // reset helper values
            dateLine = -1;
            timeLine = -1;
        } else if (dateLine > timeLine) {
            // writer block for date string
            blocks.add(new GSIBlock(isGSI16, 18, format1));

            // reset helper value
            dateLine = -1;
        } else {
            // writer block for time string
            blocks.add(new GSIBlock(isGSI16, 19, format1));

            // reset helper value
            timeLine = -1;
        }

        // ppm and prism constant
        if ((constantLine >= ppmLine + 1) || (constantLine <= ppmLine + 1)) {
            // writer block
            blocks.add(new GSIBlock(isGSI16, 51, ppmAndAdditionConstant));

            // reset helper values
            constantLine = -1;
            ppmLine = -1;
        }

    }

}
