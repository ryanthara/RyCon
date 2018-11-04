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
package de.ryanthara.ja.rycon.core.converter.caplan;

import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDecoder;
import de.ryanthara.ja.rycon.core.elements.ZeissBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert measurement files from the Zeiss
 * REC format and it's dialects (R4, R5, REC500 and M5) into Caplan K files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Zeiss2K {

    private static final Logger logger = LoggerFactory.getLogger(Zeiss2K.class.getName());
    private final String freeSpace;
    private final String objectTyp;
    private final List<String> lines;
    private int valencyIndicator;
    private String number;
    private String valency;
    private String easting;
    private String northing;
    private String height;
    private String attr;

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
    public Zeiss2K(List<String> lines) {
        this.lines = new ArrayList<>(lines);

        // prevent wrong output with empty strings of defined length from class
        this.number = "";
        this.valency = BaseToolsCaplanK.valency;
        this.easting = BaseToolsCaplanK.easting;
        this.northing = BaseToolsCaplanK.northing;
        this.height = BaseToolsCaplanK.height;
        this.freeSpace = BaseToolsCaplanK.freeSpace;
        this.objectTyp = BaseToolsCaplanK.objectTyp;
        this.attr = "";
    }

    /**
     * Converts a measurement file from Zeiss REC format (R4, R5, REC500 or M5) into a Caplan K file.
     *
     * @param useSimpleFormat  option to writer a reduced K file which is compatible to Z+F LaserControl
     * @param writeCommentLine writer comment line to output file
     * @return converted K file
     */
    public List<String> convert(boolean useSimpleFormat, boolean writeCommentLine) {
        List<String> result = new ArrayList<>();

        String pointNumber = "";
        StringBuilder stringBuilder;

        int readLineCounter = 0, writeLineCounter = 0;

        // helper for storing the valency values
        valencyIndicator = 0;

        if (writeCommentLine) {
            BaseToolsCaplanK.writeCommentLine(result);
        }

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

                        // prepare the read line results for the new point
                        pointNumber = decoder.getPointNumber();

                        writeLineCounter = writeLineCounter + 1;

                        // run the new line information
                        if (valencyIndicator > 0) {
                            valency = Separator.WHITESPACE.getSign().concat(Integer.toString(valencyIndicator));

                            stringBuilder = BaseToolsCaplanK.prepareCaplanLine(useSimpleFormat, number, valency,
                                    easting, northing, height, freeSpace, objectTyp);

                            // maybe obsolete until information about the code is known
                            if ((!useSimpleFormat) && (!attr.equals(""))) {
                                stringBuilder.append(attr);
                            }

                            result.add(stringBuilder.toString());

                            // clean up some variables after line reading is finished
                            attr = "";
                            valencyIndicator = 0;
                        }
                    }

                    for (ZeissBlock zeissBlock : decoder.getZeissBlocks()) {
                        fillSimpleFormat(zeissBlock);
                    }
                }
            }

            // flush last element if exists
            if (valencyIndicator > 0) {
                valency = Separator.WHITESPACE.getSign().concat(Integer.toString(valencyIndicator));

                stringBuilder = BaseToolsCaplanK.prepareCaplanLine(useSimpleFormat, number, valency,
                        easting, northing, height, freeSpace, objectTyp);

                result.add(stringBuilder.toString());
            }
        }

        return List.copyOf(result);
    }

    /*
    This is used when the option 'useSimpleFormat' is valued to writer a reduced K file (no 7 y x z) which is
    compatible to Z+F LaserControl registration run
     */
    private void fillSimpleFormat(ZeissBlock zeissBlock) {
        switch (zeissBlock.getTypeIdentifier()) {
            case e:
                // easting coordinate
                easting = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case n:
                // northing coordinate
                northing = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case x:
                // local easting coordinate
                easting = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case y:
                // local northing coordinate
                northing = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case E_:
                // easting coordinate
                easting = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case KR:
                // information with code and point number
                number = zeissBlock.getValue();
                break;
            case N_:
                // northing coordinate
                northing = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case PI:
                // general point information
                number = zeissBlock.getValue();
                break;
            case X:
                // northing coordinate
                northing = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case X_:
                // northing coordinate
                northing = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case Y:
                // easting coordinate
                easting = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case Y_:
                // easting coordinate
                easting = zeissBlock.getValue();
                valencyIndicator += 3;
                break;
            case Z:
                // height coordinate
                height = zeissBlock.getValue();
                valencyIndicator += 4;
                break;
            case ZE:
                // height coordinate
                height = zeissBlock.getValue();
                valencyIndicator += 4;
                break;
            case Z_:
                // height coordinate
                height = zeissBlock.getValue();
                valencyIndicator += 4;
                break;
            default:
                logger.trace("Found unknown identifier '{}'.", zeissBlock.getTypeIdentifier().toString());
        }
    }

}
