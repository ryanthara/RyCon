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

import de.ryanthara.ja.rycon.core.converter.Converter;
import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.core.elements.GSIBlock;
import de.ryanthara.ja.rycon.data.PreferenceKey;
import de.ryanthara.ja.rycon.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides basic and helper functions that are used for converting different
 * file formats into Leica Geosystems GSI files.
 *
 * <p>
 * The Leica Geo Serial Interface (GSI) is a general purpose, serial data
 * interface for bi-directional communication between TPS Total Stations,
 * Levelling instruments and computers.
 *
 * <p>
 * The GSI interface is composed in a sequence of blocks, ending with a
 * terminator (CR or CR/LF). The later introduced enhanced GSI16 format
 * starts every line with a <code>*</code> sign.
 *
 * <p>
 * Du to some issues or personal limitations sometimes a blank is added
 * to line endings. This is wrong, but RyCON can handle it.
 *
 * @author sebastian
 * @version 3
 * @since 12
 */
public final class BaseToolsGsi {

    /**
     * BaseToolsGsi is non-instantiable.
     */
    private BaseToolsGsi() {
        throw new AssertionError();
    }

    private static String getBlockByWordIndex(List<GSIBlock> blocks, int wordIndex) {
        for (GSIBlock block : blocks) {
            if (block.getWordIndex() == wordIndex) {
                return block.toString();
            }
        }

        return "";
    }

    /**
     * Returns the block size (number of characters) of a GSI block depending on
     * it's format (GSI8 = 16, GSI16 = 24).
     *
     * @param line GSI formatted line to check for block size
     * @return block size
     */
    public static int getBlockSize(String line) {
        return line.startsWith("*") ? 24 : 16;
    }

    /**
     * Returns the point number for the line as string without encoding it into blocks.
     *
     * @param line Leica Geosystems GSI formatted line
     * @return point number or empty string if line is empty string
     */
    public static String getPointNumber(String line) {
        if (!line.equalsIgnoreCase("")) {
            if (line.startsWith("*")) {
                return line.substring(8, 24);
            } else {
                return line.substring(8, 16);
            }
        } else {
            return "";
        }
    }

    /**
     * Checks a valid Leica Geosystems GSI formatted string line for being a one face target line.
     * <p>
     * The one face target line contains three times the zero coordinate.
     *
     * @param line line to be checked for one face target line
     * @return true if line is a one face target line
     */
    public static boolean isTargetLine(String line) {
        GsiLineDecoder gsiLineDecoder = new GsiLineDecoder();
        List<GSIBlock> blocks = gsiLineDecoder.decode(line);

        final String block11 = getBlockByWordIndex(blocks, 11);
        final String block81 = getBlockByWordIndex(blocks, 81);
        final String block82 = getBlockByWordIndex(blocks, 82);
        final String block83 = getBlockByWordIndex(blocks, 83);

        // TODO check the WI 84 till 86 and the result values if they are not present in combination with the 81 till 83

        final String decodedLine = block11 + Separator.WHITESPACE.getSign() + block81 + Separator.WHITESPACE.getSign() + block82 + Separator.WHITESPACE.getSign() + block83;

        String pattern;

        // differ between GSI8 and GSI16 format
        if (line.startsWith("*")) {
            pattern = "0000000000000000";
        } else {
            pattern = "00000000";
        }

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(decodedLine);
        int founds = 0;

        while (m.find()) {
            founds = founds + 1;
        }

        return founds == 3;
    }

    /**
     * Transforms a line of encoded {@code GSIBlock}s into a string line and fill it up into an
     * {@code List<String>} for file writing.
     *
     * @param isGSI16  distinguish between GSI8 or GSI16 output format
     * @param gsiLines List<List<GSIBlock>> of encoded GSIBlocks
     * @return transformed string line with GSI content
     */
    static List<String> lineTransformation(boolean isGSI16, List<List<GSIBlock>> gsiLines) {
        List<String> result = new ArrayList<>();

        for (List<GSIBlock> gsiLine : gsiLines) {
            String newLine = "";

            if (isGSI16) {
                newLine = "*";
            }

            int counter = 0;

            for (GSIBlock block : gsiLine) {
                newLine = newLine.concat(block.toString(isGSI16));

                if (counter < gsiLine.size()) {
                    newLine = newLine.concat(Separator.WHITESPACE.getSign());
                }

                counter = counter + 1;
            }

            newLine = prepareLineEnding(newLine);

            result.add(newLine);
        }

        return List.copyOf(result);
    }

    /**
     * Prepares the line ending with an additional white space character.
     * <p>
     * For some reasons (e.g. self written Autocad VBA utilities) it is necessary to add
     * an additional white space at the end of a line. This is done with this helper.
     *
     * @param stringToPrepare string to prepare with line ending
     * @return prepared string
     */
    public static String prepareLineEnding(String stringToPrepare) {
        if (StringUtils.parseBooleanValue(PreferenceKey.GSI_SETTING_LINE_ENDING_WITH_BLANK)) {
            if (!stringToPrepare.endsWith(Separator.WHITESPACE.getSign())) {
                stringToPrepare = stringToPrepare.concat(Separator.WHITESPACE.getSign());
            }
        }

        return stringToPrepare;
    }

    /**
     * Sorts a List<String> ascending by point number.
     *
     * @param gsiLines unsorted List<String> of Leica Geosystems GSI lines
     * @return sorted List<String>
     */
    static List<String> sortResult(List<String> gsiLines) {
        List<String> helper = new ArrayList<>(gsiLines.size());
        List<String> result = new ArrayList<>(gsiLines.size());

        for (String gsiLine : gsiLines) {
            helper.add(BaseToolsGsi.getPointNumber(gsiLine).concat(Converter.SEPARATOR).concat(gsiLine));
        }

        helper.sort(String::compareToIgnoreCase);

        for (String helperLine : helper) {
            result.add(helperLine.split(Converter.SEPARATOR)[1]);
        }

        return List.copyOf(result);
    }

}
