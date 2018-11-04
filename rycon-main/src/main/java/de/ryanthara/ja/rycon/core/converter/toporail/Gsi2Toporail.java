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
package de.ryanthara.ja.rycon.core.converter.toporail;

import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.core.converter.gsi.GsiDecoder;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.nio.FileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert measurement files from Leica Geosystems
 * GSI into a text formatted measurement or coordinate file for Toporail.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class Gsi2Toporail {

    private static final Logger logger = LoggerFactory.getLogger(Gsi2Toporail.class.getName());

    private static final String variant = "B";

    private final GsiDecoder gsiDecoder;

    /**
     * Creates a converter with a list for the read line based
     * Leica Geosystems GSI8 or GSI16 file.
     *
     * @param lines list with Leica Geosystems GSI8 or GSI16 lines
     */
    public Gsi2Toporail(List<String> lines) {
        gsiDecoder = new GsiDecoder(lines);
    }

    /**
     * Converts a GSI file into a Toporail coordinate or measurement file.
     * <p>
     * With a parameter it is possible to distinguish between the MEP or PTS file.
     *
     * @param fileFormat file type of the read file
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convertGsi2Toporail(FileFormat fileFormat) {
        if (fileFormat == FileFormat.MEP) {
            return convertGsi2Mep();
        } else {
            return convertGsi2Pts();
        }
    }

    private List<String> convertGsi2Mep() {
        List<String> result = new ArrayList<>();

        final String fileHeader = "MEP" + variant;
        final String sep = "\t";

        /*

        How to identify a

         - station line?
            + by word index
            + by identifier e.g. 'FS' or 'ST'
            + by logic like in the clean up widget
            + ...

         - measurement line?
            + by word index and structure (WI 21, 22, 31/32, 33, 87, 88)
            + by count tokens
            + ...

         - control measurement line?
            + by word index and structure
            + by count tokens
            + ...

         - coordinate line?
            + by word index and structure (WI 11, 41, 81 - 86)
            + ...
         */


        /*

        ArrayList<GsiBlock> blocks;
        List<List<GsiBlock>> blocksInLines = new ArrayList<>();

        // check for being a valid Toporail coordinate file
        if (lines.get(0).startsWith("@MEP")) {

            int lineCounter = 1;

            // skip first line
            for (int i = 1; i < lines.size(); i++) {
                blocks = new ArrayList<>();
                String[] tokens = lines.get(i).split("\t");

                switch (tokens[0]) {
                    case "K": // control measurement line
                        blocks = transformControlMeasurementLine(tokens, isGSI16, lineCounter);
                        break;

                    case "M": // measurement line
                        blocks = transformMeasurementLine(tokens, isGSI16, lineCounter);
                        break;

                    case "P": // coordinate line
                        blocks = transformCoordinateLine(tokens, isGSI16, lineCounter);
                        break;

                    case "S": // station line
                        blocks = transformStationLine(tokens, isGSI16, lineCounter);
                        break;
                }

                if (blocks.size() > 0) {
                    lineCounter = lineCounter + 1;

                    // sort every 'line' of GSI blocks by word index (WI)
                    SortUtils.sortByWordIndex(blocks);

                    blocksInLines.add(blocks);
                }
            }
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
         */


        // TODO implement comment handling
        final String commentString = null; // comment lines starts with ':'

        for (List<GsiBlock> blocksInLine : gsiDecoder.getDecodedLinesOfGsiBlocks()) {
            // helpers
            String numericCode = "";
            String pointNumber = "";
            String easting = "";
            String northing = "";
            String height = "";
            String date = "";
            String author = "";
            String comment = "";
            String overhauling = "";
            String azimuth = "";

            // blocksInLine is sorted!
            for (GsiBlock block : blocksInLine) {
                switch (block.getWordIndex()) {
                    case 11: // point number
                        pointNumber = block.toPrintFormatCsv();
                        break;
                    case 18: // date YYYYMMDD
                        // extract year
                        // blocks.add(new GsiBlock(isGSI16, 18, year.substring(2, 4) + "000000"));
                        date = block.toPrintFormatCsv();
                        break;
                    case 19: // date YYYYMMDD
                        // extract month and day
                        date = block.toPrintFormatCsv();

                        // blocks.add(new GsiBlock(isGSI16, 17, day + month + year));
                        //blocks.add(new GsiBlock(isGSI16, 19, month + day + "0000"));
                        break;
                    case 21: // azimuth (gon)
                        azimuth = block.toPrintFormatCsv();
                        break;
                    case 41: // numeric code
                        numericCode = block.toPrintFormatCsv();
                        break;
                    case 71: // author
                        author = block.toPrintFormatCsv();
                        break;
                    case 72: // comment
                        comment = block.toPrintFormatCsv();
                        break;
                    case 73: // overhauling (mm)
                        overhauling = block.toPrintFormatCsv();
                        break;
                    case 81: // easting
                        easting = block.toPrintFormatCsv();
                        break;
                    case 82: // northing
                        northing = block.toPrintFormatCsv();
                        break;
                    case 83: // height
                        height = block.toPrintFormatCsv();
                        break;
                    default:
                        logger.trace("Found one more word index '{}'.", block.getWordIndex());
                }
            }

            final String values = numericCode + pointNumber + easting + northing + height + date + author + comment + overhauling + azimuth;

            // one or more values are set
            if (values.length() > 0) {
                final String resultLine = numericCode + "\t" + pointNumber + "\t" + easting + "\t" + northing + "\t"
                        + height + "\t" + date + "\t" + author + "\t" + comment + "\t" + overhauling + "\t" + azimuth;

                result.add(resultLine);
            }

        }

        return List.copyOf(result);
    }

    private List<String> convertGsi2Pts() {
        List<String> result = new ArrayList<>();

        final String fileHeader = "PTS" + variant;
        final String sep = "\t";

        // TODO implement comment handling
        final String commentString = null; // comment lines starts with ':'

        for (List<GsiBlock> blocksInLine : gsiDecoder.getDecodedLinesOfGsiBlocks()) {
            // helpers
            String numericCode = "";
            String pointNumber = "";
            String easting = "";
            String northing = "";
            String height = "";
            String date = "";
            String author = "";
            String comment = "";
            String overhauling = "";
            String azimuth = "";

            // blocksInLine is sorted!
            for (GsiBlock block : blocksInLine) {
                switch (block.getWordIndex()) {
                    case 11: // point number
                        pointNumber = block.toPrintFormatCsv();
                        break;
                    case 18: // date YYYYMMDD
                        // extract year
                        // blocks.add(new GsiBlock(isGSI16, 18, year.substring(2, 4) + "000000"));
                        date = block.toPrintFormatCsv();
                        break;
                    case 19: // date YYYYMMDD
                        // extract month and day
                        date = block.toPrintFormatCsv();

                        // blocks.add(new GsiBlock(isGSI16, 17, day + month + year));
                        //blocks.add(new GsiBlock(isGSI16, 19, month + day + "0000"));
                        break;
                    case 21: // azimuth (gon)
                        azimuth = block.toPrintFormatCsv();
                        break;
                    case 41: // numeric code
                        numericCode = block.toPrintFormatCsv();
                        break;
                    case 71: // author
                        author = block.toPrintFormatCsv();
                        break;
                    case 72: // comment
                        comment = block.toPrintFormatCsv();
                        break;
                    case 73: // overhauling (mm)
                        overhauling = block.toPrintFormatCsv();
                        break;
                    case 81: // easting
                        easting = block.toPrintFormatCsv();
                        break;
                    case 82: // northing
                        northing = block.toPrintFormatCsv();
                        break;
                    case 83: // height
                        height = block.toPrintFormatCsv();
                        break;
                    default:
                        logger.trace("Found one more word index '{}'.", block.getWordIndex());
                }
            }

            final String values = numericCode + pointNumber + easting + northing + height + date + author + comment + overhauling + azimuth;

            // one or more values are set
            if (values.length() > 0) {
                final String resultLine =
                        numericCode + Separator.TABULATOR
                                + pointNumber + Separator.TABULATOR
                                + easting + Separator.TABULATOR
                                + northing + Separator.TABULATOR
                                + height + Separator.TABULATOR
                                + date + Separator.TABULATOR
                                + author + Separator.TABULATOR
                                + comment + Separator.TABULATOR
                                + overhauling + Separator.TABULATOR
                                + azimuth;

                result.add(resultLine);
            }

        }

        return List.copyOf(result);
    }

}
