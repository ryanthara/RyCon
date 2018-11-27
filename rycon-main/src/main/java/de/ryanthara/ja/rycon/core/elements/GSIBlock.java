/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.core.elements;

import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.WordIndices;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WORDINDEX;

/**
 * A GSIBlock is an object to store and handle the values of a Leica Geosystems GSI block.
 *
 * <p>
 * The Leica Geo Serial Interface (GSI) is a general purpose, serial data interface
 * for bi-directional communication between TPS Total Stations, GNSS receivers and
 * Levelling instruments and computers.
 *
 * <p>
 * The GSI interface is composed in a sequence of blocks, ending with a terminator (CR or CR/LF).
 * The later introduced enhanced GSI16 format starts every line with a <code>*</code> sign.
 * It stores the values in 16 instead of 8 digits.
 *
 * @author sebastian
 * @version 6
 * @since 8
 */
public class GSIBlock {

    private static final Logger logger = LoggerFactory.getLogger(GSIBlock.class.getName());
    private boolean isGSI16;
    private int wordIndex;
    private String dataGSI, information, sign;

    /**
     * Creates a GSIBlock from a read GSI block string.
     *
     * <p>
     * This constructor is used for reading Leica Geosystems GSI formatted files.
     *
     * @param blockAsString complete GSI block as string
     */
    public GSIBlock(String blockAsString) {
        blockAsString = blockAsString.trim();

        this.isGSI16 = blockAsString.length() == 23;
        this.wordIndex = StringUtils.parseIntegerValue(blockAsString.substring(0, 2));
        this.information = blockAsString.substring(2, 6);
        this.sign = blockAsString.substring(6, 7);
        this.dataGSI = blockAsString.substring(7);
    }

    /**
     * Creates a GSIBlock from values given as parameters.
     *
     * <p>
     * This constructor is used for the GSI block that contains the line number
     * and the point number string. (WI=11)
     *
     * @param isGSI16    boolean for indicating a GSI16 file
     * @param wordIndex  word index (WI) of the block
     * @param lineNumber information for the point number (filled up with zeros)
     * @param dataGSI    GSI data as string
     */
    public GSIBlock(boolean isGSI16, int wordIndex, int lineNumber, String dataGSI) {
        int length = isGSI16 ? 16 : 8;

        if (wordIndex == 11) {
            this.wordIndex = wordIndex;
            this.information = String.format("%04d", lineNumber);
            this.sign = "+";
            this.dataGSI = StringUtils.fillWithZerosFromBeginning(dataGSI, length);
        }
    }

    /**
     * Creates a GSIBlock from values given as parameters.
     *
     * <p>
     * This constructor is used for all GSI blocks except the point number
     * one (WI=11). The data string can contains the sign ('+' or '-') or not.
     *
     * @param isGSI16   boolean for indicating a GSI16 file
     * @param wordIndex word index (WI) of the block
     * @param dataGSI   GSI data as string with sign
     */
    public GSIBlock(boolean isGSI16, int wordIndex, String dataGSI) {
        int length = isGSI16 ? 16 : 8;

        this.wordIndex = wordIndex;

        dataGSI = removeSign(dataGSI);                          // sets the sign internally

        if (wordIndex == 71) {                                  // code
            this.information = "..46";
        } else if ((wordIndex > 80) && (wordIndex < 90)) {      // coordinates
            this.information = "..46";

            try {
                double d = StringUtils.parseDoubleValue(dataGSI);

                if (d == 0d) {
                    dataGSI = "0";
                } else {
                    d = d * 10000.0; // value d in 1/10mm

                    BigDecimal bigDecimal = new BigDecimal(d);
                    bigDecimal = bigDecimal.setScale(0, RoundingMode.HALF_UP);

                    dataGSI = bigDecimal.toString();
                }
            } catch (NumberFormatException e) {
                logger.error("Can not convert GSI data block '{}' to double.", dataGSI, e.getCause());
            }
        } else {
            // not used other values
            this.information = "..4.";
        }

        this.dataGSI = StringUtils.fillWithZerosFromBeginning(dataGSI, length);
    }

    /**
     * Returns the gsi data as string
     *
     * @return gsi data as string
     */
    public String getDataGSI() {
        return dataGSI;
    }

    /**
     * Returns the word index as integer value.
     *
     * @return word index as integer value
     */
    public int getWordIndex() {
        return wordIndex;
    }

    /**
     * Returns true if GSIBlock is GSI16 format.
     *
     * @return true if GSIBlock is GSI16 format
     */
    public boolean isGSI16() {
        return isGSI16;
    }

    /**
     * Returns a GSIBlock in asc format without separation sign. No additional invisible spaces are created.
     *
     * @return formatted {@code String} for ascii output
     */
    public String toPrintFormatAsc() {
        return this.toPrintFormatTxt().trim();
    }

    /**
     * Returns a GSIBlock in csv format without separation sign. No additional invisible spaces are created.
     *
     * @return formatted {@code String} for csv output
     */
    public String toPrintFormatCsv() {
        return this.toPrintFormatTxt().trim();
    }

    /**
     * Returns a GSIBlock in a printable format filled up with invisible spaces to a defined length (e.g. 16 characters).
     *
     * @return formatted {@code String} for column based txt output
     */
    // TODO: 29.10.16 checks the right length for print string length (16, 17, sign)
    public String toPrintFormatTxt() {
        String txt = this.dataGSI;
        int length = txt.length();

        StringBuilder stringBuilder;

        switch (wordIndex) {
            case 11:        // point number
                txt = preparePrintString(txt);
                break;
            case 21:        // angle Hz
            case 22:        // angle Vz
            case 24:        // angle Hz0
            case 25:        // angle difference (Hz0 - Hz)
                if (this.information.endsWith("2") || this.information.endsWith("3")) {
                    stringBuilder = new StringBuilder(txt);
                    txt = stringBuilder.insert(length - 5, ".").toString();
                    txt = trimLeadingZeros(txt);
                    txt = StringUtils.fillWithSpacesFromBeginning(txt, length + 1);
                }
                break;
            case 26:        // offset
            case 27:        // angle Vz0
            case 28:        // angle difference (Vz0 - Vz)
                break;
            case 31:        // slope distance
            case 32:        // horizontal distance
            case 33:        // height difference
                stringBuilder = new StringBuilder(txt);

                if (this.information.endsWith("0")) {
                    txt = stringBuilder.insert(length - 3, ".").toString();
                } else if (this.information.endsWith("6")) {
                    txt = stringBuilder.insert(length - 4, ".").toString();
                } else if (this.information.endsWith("8")) {
                    txt = stringBuilder.insert(length - 5, ".").toString();
                } else {
                    txt = stringBuilder.insert(length - 3, ".").toString();
                }

                txt = insertMinusSign(txt);
                txt = StringUtils.fillWithSpacesFromBeginning(txt, length + 2);
                break;
            case 41:        // code
                txt = trimLeadingZeros(txt);
                txt = StringUtils.fillWithSpacesFromBeginning(txt, length);
                break;
            case 58:        // addition constant in 1/10 mm
                stringBuilder = new StringBuilder(txt);
                txt = stringBuilder.insert(length - 4, ".").toString();

                txt = this.sign + trimLeadingZeros(txt);
                txt = StringUtils.fillWithSpacesFromBeginning(txt, length);
                break;
            case 71:        // comment 1, mostly used for code
            case 72:        // attribute 1
            case 73:        // attribute 2
            case 74:        // attribute 3
            case 75:        // attribute 4
            case 76:        // attribute 5
            case 77:        // attribute 6
            case 78:        // attribute 7
            case 79:        // attribute 8
                txt = preparePrintString(txt);
                break;
            case 81:        // easting E
            case 82:        // northing N
            case 83:        // height H
            case 84:        // easting E0
            case 85:        // northing N0
            case 86:        // height H0
            case 87:        // target height
            case 88:        // instrument height
                stringBuilder = new StringBuilder(txt);

                if (this.information.endsWith("0")) {           // mm
                    txt = stringBuilder.insert(this.dataGSI.length() - 3, ".").toString();
                } else if (this.information.endsWith("6")) {    // 1/10 mm
                    txt = stringBuilder.insert(this.dataGSI.length() - 4, ".").toString();
                }

                txt = insertMinusSign(txt);

                // add two spaces, one for the sign and one for the decimal dot
                txt = StringUtils.fillWithSpacesFromBeginning(txt, length + 2);
                break;
            default:
                txt = ResourceBundleUtils.getLangString(WORDINDEX, WordIndices.WI9999);
                logger.trace("Line contains unknown word index ({}).", dataGSI);
                break;
        }

        return txt;
    }

    /**
     * Returns a GSIBlock as String in the origin format.
     *
     * @return GSIBlock as String
     */
    public String toString() {
        return wordIndex + information + sign + dataGSI;
    }

    /**
     * Returns a GSIBlock as String in defined format (GSI8 or GSI16).
     * <p>
     * Due to issues of the format, leading zeros are added or values are cut off.
     *
     * @param isGSI16 True for GSI16 format
     * @return GSIBlock as String depending on format GSI8/GSI16
     */
    public String toString(boolean isGSI16) {
        String data;
        String leadingZeros = "00000000";
        String result;

        if (isGSI16) {
            result = wordIndex + information + sign;

            data = dataGSI.length() == 8 ? leadingZeros.concat(dataGSI) : dataGSI;

            result = result.concat(data);
        } else {
            if (dataGSI.length() == 8) {
                result = wordIndex + information + sign + dataGSI;
            } else {
                result = wordIndex + information + sign + dataGSI.substring(dataGSI.length() - 8);
            }
        }

        return result;
    }

    private String insertMinusSign(String s) {
        if (this.sign.equals("-")) {
            return this.sign + trimLeadingZeros(s);
        } else {
            return trimLeadingZeros(s);
        }
    }

    private String preparePrintString(String s) {
        return StringUtils.fillWithSpacesFromBeginning(trimLeadingZeros(s), s.length());
    }

    private String removeSign(String dataGSI) {
        sign = "+";

        if (dataGSI.startsWith("+")) {
            return dataGSI.substring(1);
        }

        if (dataGSI.startsWith("-")) {
            sign = "-";
            return dataGSI.substring(1);
        }

        return dataGSI;
    }

    private String trimLeadingZeros(String s) {
        // cut off leading zeros with regex;
        String intern = s.replaceFirst("^0+(?!$)", "");

        if (intern.startsWith(".")) {
            return "0" + intern;
        } else {
            return intern;
        }
    }

}
