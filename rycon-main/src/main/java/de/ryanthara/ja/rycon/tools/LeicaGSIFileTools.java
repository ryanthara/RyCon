/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceHandler;

import java.math.BigDecimal;
import java.util.*;

/**
 * This class implements several basic operations on Leica GSI files.
 * <p>
 * The Leica Geo Serial Interface (GSI) is a general purpose, serial data
 * interface for bi-directional communication between TPS Total Stations,
 * Levelling instruments and computers.
 * <p>
 * The GSI interface is composed in a sequence of blocks, ending with a
 * terminator (CR or CR/LF). The later introduced enhanced GSI16 format
 * starts every line with a <code>*</code> sign.
 * <p>
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>6: precise the header clean up for conversion from ASCII to GSI after changes in NIGRA WIN line endings</li>
 *     <li>5: NIGRA support implemented for dnd-support of the levelling widget</li>
 *     <li>4: defeat bug #3 blank sign at line ending in GSI file and bug #1</li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 6
 * @since 1
 */
public class LeicaGSIFileTools {

    private boolean isGSI16 = false;
    private ArrayList<String> readStringLines;
    private List<String[]> readCSVLines;
    private TreeSet<Integer> foundCodes = new TreeSet<Integer>();
    private TreeSet<Integer> foundWordIndices = new TreeSet<Integer>();

    /**
     * Class constructor with parameter for the read lines as {@code ArrayList<String>} object.
     * <p>
     * This constructor is used for read text and GSI file lines.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public LeicaGSIFileTools(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Class constructor with parameter for the read lines as {@code List<String[]>} object.
     * <p>
     * This constructor is used for read csv file lines.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public LeicaGSIFileTools(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a cadwork node.dat file into GSI8 or GS16 format.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16 Output file is GSI16 format
     * @param useCodeColumn Use the code column from node.dat
     * @param useZeroHeights Use heights with zero (0.000) values
     * @return converted {@code ArrayList<String>} with lines of GSI8 or GSI16 format
     */
    public ArrayList<String> convertCadwork2GSI(boolean isGSI16, boolean useCodeColumn, boolean useZeroHeights) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<ArrayList<GSIBlock>>();

        readStringLines.remove(0);
        readStringLines.remove(0);
        readStringLines.remove(0);

        int lineCounter = 1;

        for (String line : readStringLines) {
            blocks = new ArrayList<GSIBlock>();

            String[] lineSplit = line.trim().split("\\s+");

            // point number
            blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[6]));

            // use code if necessary
            if (useCodeColumn) {
                blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[5]));
            }

            // easting and northing
            blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[2]));
            blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[3]));

            // use height if necessary
            if (useZeroHeights) {
                blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[4]));
            } else {
                if (!lineSplit[4].equals("0.000000")) {
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[4]));
                }
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter++;
                blocksInLines.add(blocks);
            }
        }

        return lineTransformation(isGSI16, blocksInLines);
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (switzerland) into a GSI format file.
     * <p>
     * With a parameter it is possible to distinguish between GSI8 and GSI16
     *
     * @param isGSI16 distinguish between GSI8 or GSI16
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertCSVBaselStadt2GSI(boolean isGSI16) {
        ArrayList<String> result = new ArrayList<String>();

        for (String[] stringField : readCSVLines) {
            String line;

            // point number is in column 1
            line = stringField[0].replaceAll("\\s+", "").trim();
            line = line.concat(" ");

            // easting (Y) is in column 3
            line = line.concat(stringField[2]);
            line = line.concat(" ");

            // northing (X) is in column 4
            line = line.concat(stringField[3]);
            line = line.concat(" ");

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                line = line.concat(stringField[4]);
            } else {
                line = line.concat("-9999");
            }

            result.add(line.trim());
        }

        this.readStringLines = result;

        return convertTXT2GSI(isGSI16);
    }

    /**
     * Converts a CSV file (comma or semicolon delimited) into a GSI file.
     * <p>
     * The format of the GSI file is controlled with a parameter. The delimitor
     * sign is automatically detected.
     *
     * @param isGSI16 control if GSI8 or GSI16 format is written
     * @return converted {@code ArrayList<String>} with lines of GSI format
     */
    public ArrayList<String> convertCSV2GSI(boolean isGSI16) {
        ArrayList<String> result = new ArrayList<String>();

        // convert the List<String[]> into an ArrayList<String> and use known stuff (-:
        for (String[] stringField : readCSVLines) {

            String line = "";

            for (String s : stringField) {

                line = line.concat(s);
                line = line.concat(" ");

            }

            line = line.trim();
            line = line.replace(',', '.');

            // skip empty lines
            if (!line.equals("")) {
                result.add(line);
            }

        }

        this.readStringLines = result;

        return convertTXT2GSI(isGSI16);
    }

    /**
     * Converts a GSI file into a comma or semicolon delimited csv file.
     * <p>
     * With parameter it is possible to set the separation char (comma or semicolon).
     *
     * @param delimiter delimiter sign as {@code String}
     * @param writeCommentLine if comment line should be written
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertGSI2CSV(String delimiter, boolean writeCommentLine) {
        ArrayList<String> result = new ArrayList<String>();

        // transform lines into GSI-Blocks
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = blockEncoder(readStringLines);

        // prepare comment line if necessary
        if (writeCommentLine) {
            StringBuilder builder = new StringBuilder();

            int counter = 0;

            for (Integer wordIndex : foundWordIndices) {
                builder.append("WI_");
                builder.append(wordIndex.toString());

                if (counter < foundWordIndices.size() - 1) {
                    builder.append(delimiter);
                }

                counter++;
            }

            result.add(0, builder.toString());
        }

        for (ArrayList<GSIBlock> blocksAsLines : gsiBlocks) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndice = it.next();
                String intern = "";

                for (GSIBlock block : blocksAsLines) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndice == block.wordIndex) {
                        intern = block.toPrintFormatCSV();
                        break; // important if else statement will be added!!!
                    }
                }

                newLine = newLine.concat(intern);

                if (i < foundWordIndices.size() - 1) {
                    newLine = newLine.concat(delimiter);
                }
            }
            result.add(newLine);
        }

        return result;
    }

    /**
     * Converts a GSI file into a space or tab delimited text file.
     * <p>
     * With parameter it is possible to set the separation char (space or tab).
     *
     * @param delimiter        delimiter sign as {@code String}
     * @param isGSI16          true if GSI16 format is used
     * @param writeCommentLine if comment line should be written
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertGSI2TXT(String delimiter, boolean isGSI16, boolean writeCommentLine) {
        String commentLine = "";
        String delim;
        ArrayList<String> result = new ArrayList<String>();

        // transform lines into GSI-Blocks
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = blockEncoder(readStringLines);

        if (delimiter.equals(" ")) {
            delim = "    ";
        } else {
            delim = delimiter;
        }

        // prepare comment line if necessary
        if (writeCommentLine) {
            int length;

            if (isGSI16) {
                length = 16;
            } else {
                length = 8;
            }

            String format = "%" + length + "." + length + "s";
            String s;

            int counter = 0;

            for (Integer wordIndice : foundWordIndices) {
                s = String.format(format, wordIndice.toString());
                commentLine = commentLine.concat(s);

                if (counter < foundWordIndices.size() - 1) {
                    commentLine = commentLine.concat(delim);
                }
                counter++;
            }

            StringBuilder builder = new StringBuilder(commentLine);
            commentLine = builder.replace(0, 5, "# WI:").toString();

            result.add(0, commentLine);
        }

        for (ArrayList<GSIBlock> blocksAsLines : gsiBlocks) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndice = it.next();

                String intern = "";

                for (GSIBlock block : blocksAsLines) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndice == block.wordIndex) {
                        intern = block.toPrintFormatTXT();
                        break; // important!!!
                    } else {
                        String emptyBlock;

                        if (isGSI16) {
                            emptyBlock = "                ";
                        } else {
                            emptyBlock = "        ";
                        }

                        intern = emptyBlock;
                    }
                }

                newLine = newLine.concat(intern);

                if (i < foundWordIndices.size() - 1) {
                    newLine = newLine.concat(delim);
                }
            }
            result.add(newLine);
        }
        return result;
    }

    /**
     * Converts a GSI file into GSI8 or GS16 format.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16 Output file is GSI16 format
     * @return converted {@code ArrayList<String>} with lines of GSI8 or GSI16 format
     */
    public ArrayList<String> convertGSI8vsGSI16(boolean isGSI16) {
        // transform lines into GSI-Blocks
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = blockEncoder(readStringLines);
        return lineTransformation(isGSI16, gsiBlocks);
    }

    /**
     * Converts a NIGRA height listing (*.ASC) into a Leica GSI file.
     * @param isGSI16 true if GSI16 format is used
     * @return converted GSI format file
     * @since 5
     */
    public ArrayList<String> convertNIGRA2GSI(boolean isGSI16) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<ArrayList<GSIBlock>>();
        StringTokenizer stringTokenizer;

        int lineCounter = 1;

        // skip the first 7 lines without any needed information
        for (int i = 5; i < readStringLines.size(); i++) {
            blocks = new ArrayList<GSIBlock>();
            String line = readStringLines.get(i);
            stringTokenizer = new StringTokenizer(line);

            if (stringTokenizer.countTokens() > 2) {
                String number = stringTokenizer.nextToken();
                String easting = Integer.toString(i);
                String northing = Integer.toString(i);
                String height = stringTokenizer.nextToken();

                blocks.add(new GSIBlock(isGSI16, 11, lineCounter, number));
                blocks.add(new GSIBlock(isGSI16, 81, lineCounter, easting));
                blocks.add(new GSIBlock(isGSI16, 82, lineCounter, northing));
                blocks.add(new GSIBlock(isGSI16, 83, lineCounter, height));
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter++;
                blocksInLines.add(blocks);
            }
        }

        return lineTransformation(isGSI16, blocksInLines);
    }

    /**
     * Converts a text file (space or tabulator separated) into a GSI file.
     * <p>
     * The GSI format decision is done by a parameter in the constructor.
     *
     * @param isGSI16 decision which GSI format is used
     * @return converted {@code ArrayList<String>>} with lines
     */
    public ArrayList<String> convertTXT2GSI(boolean isGSI16) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<ArrayList<GSIBlock>>();

        int lineCounter = 1;

        for (String line : readStringLines) {
            blocks = new ArrayList<GSIBlock>();

            String[] lineSplit = line.trim().split("\\s+");
            switch (lineSplit.length) {

                case 1:     // prevent fall through
                    break;

                case 2:     // no, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[1]));
                    break;

                case 3:     // no, code, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[1]));
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[2]));
                    break;

                case 4:     // no, easting, northing, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[1]));
                    blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[2]));

                    // necessary because of Basel Stadt CSV distinguish between points without height
                    if (!lineSplit[3].equals("-9999")) {
                        blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[3]));
                    }
                    break;

                case 5:     // no, code, easting, northing, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[1]));
                    blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[2]));
                    blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[3]));
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[4]));
                    break;
            }

            // check for at least one or more added elements to prevent writing empty lines
            if (blocks.size() > 0) {
                lineCounter++;
                blocksInLines.add(blocks);
            }
        }

        return lineTransformation(isGSI16, blocksInLines);
    }

    /**
     * Returns the found codes as {@code TreeSet<Integer>}.
     * <p>
     * This method is necessary because of the elimination of the code in the string of the read lines.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Prepares the read GSI data for table output.
     * <p>
     *
     */
    public void prepareTableData(boolean writeCommentLine) {

        int row = 1;
        String commentLine = "";

        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();

        // transform lines into GSI-Blocks
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = blockEncoder(readStringLines);

        // prepare comment line if necessary
        if (writeCommentLine) {
            StringBuffer stringBuffer = new StringBuffer(commentLine);

            for (Integer wordIndice : foundWordIndices) {
                stringBuffer.append("\"");
                stringBuffer.append(wordIndice.toString());
                stringBuffer.append("\", ");
            }

            stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(", "));
            System.out.println(stringBuffer.toString());
            commentLine = stringBuffer.toString();
        }
/*
        for (ArrayList<GSIBlock> blocksAsLines : gsiBlocks) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndice = it.next();

                String intern = "";

                for (GSIBlock block : blocksAsLines) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndice == block.wordIndex) {
                        intern = block.toPrintFormatTXT();
                        break; // important!!!
                    } else {
                        String emptyBlock;

                        if (isGSI16) {
                            emptyBlock = "                ";
                        } else {
                            emptyBlock = "        ";
                        }

                        intern = emptyBlock;
                    }
                }

                newLine = newLine.concat(intern);

                if (i < foundWordIndices.size() - 1) {
                    newLine = newLine.concat(delim);
                }
            }
            result.add(newLine);
        }
*/
    }

    /**
     * Splits a code based file into separate files by code.
     * <p>
     * A separate file is generated for every existing code. Lines without code will be ignored.
     * RyCON need a valid GSI format file with code blocks (WI 71). The block order is equal.
     *
     * @param dropCode if code block should dropped out of the result string
     * @param writeLinesWithoutCode if lines without code should be written
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<ArrayList<String>> processCodeSplit(boolean dropCode, boolean writeLinesWithoutCode) {
        ArrayList<GSIHelper> linesWithCode = new ArrayList<GSIHelper>();
        ArrayList<GSIHelper> linesWithOutCode = new ArrayList<GSIHelper>();
        String newLine = null;

        // transform lines into GSI-Blocks
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = blockEncoder(readStringLines);

        // one top level for every code
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (ArrayList<GSIBlock> blocksInLines : gsiBlocks) {

            // helper for code handling inside the switch statements
            int code = -1;
            int validCheckHelperValue = 0;

            for (GSIBlock block : blocksInLines) {

                switch (block.wordIndex) {

                    case 11:
                        newLine = block.toString();
                        break;

                    case 71:
                        code = Integer.parseInt(block.dataGSI);
                        if (dropCode) {
                            newLine = newLine != null ? newLine.concat(" " + block.toString()) : null;
                        }
                        break;

                    case 81:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 1;
                        break;

                    case 82:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 3;
                        break;

                    case 83:
                        assert newLine != null;
                        newLine = newLine.concat(" " + block.toString());
                        validCheckHelperValue += 6;
                        break;
                }
            }

            newLine = prepareLineEnding(newLine);

            // split lines with and without code
            if (((code != -1) & (newLine != null)) & validCheckHelperValue > 1) {
                foundCodes.add(code);
                linesWithCode.add(new GSIHelper(code, newLine));
            } else {
                // use 'blind' code '987789' for this
                linesWithOutCode.add(new GSIHelper(-987789, newLine));
            }
        }

        // sorting the ArrayList
        Collections.sort(linesWithCode, new Comparator<GSIHelper>() {
            @Override
            public int compare(GSIHelper o1, GSIHelper o2) {
                if (o1.getCode() > o2.getCode()) {
                    return 1;
                } else if (o1.getCode() == o2.getCode()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        // helpers for generating a new array for every found code
        if (linesWithCode.size() > 0) {

            int code = linesWithCode.get(0).getCode();
            ArrayList<String> temp = new ArrayList<String>();

            // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
            for (GSIHelper gsiHelpers : linesWithCode) {

                if (code == gsiHelpers.getCode()) {
                    temp.add(gsiHelpers.getLine());
                } else {
                    result.add(temp);
                    temp = new ArrayList<String>(); // do not use temp.clear()!!!
                    temp.add(gsiHelpers.getLine());
                }

                code = gsiHelpers.getCode();

            }
            // insert last element
            result.add(temp);
        }

        // insert lines without code for writing
        if (writeLinesWithoutCode && (linesWithOutCode.size() > 0)){
            ArrayList<String> temp = new ArrayList<String>();

            for (GSIHelper gsiHelper : linesWithOutCode) {
                temp.add(gsiHelper.getLine());
            }

            foundCodes.add(987789);
            result.add(temp);
        }

        return result;
    }

    /**
     * Converts a Levelling file to a coordinate one (no, x, y, z) in GSI format for cad import.
     * <p>
     * Within this conversation a x,y coordinate will be generated from the line number. The units are
     * rounded down to 1/10mm.
     *
     * @param ignoreChangePoints if change points with number '0' has to be ignored
     * @return Converted {@code ArrayList<String>} for cad import
     */
    public ArrayList<String> processLevelling2Cad(boolean ignoreChangePoints) {
        int lineCounter = 1;
        String newLine;

        ArrayList<String> result = new ArrayList<String>();

        for (String line : readStringLines) {
            String[] lineSplit = line.trim().split("\\s+");

            // line with height information from levelling has four tokens in GSI format
            if (lineSplit.length == 4) {

                // number - the original information from the first block is used
                newLine = lineSplit[0];

                // detect change points (number = 0)
                int number = Integer.parseInt(newLine.substring(8, newLine.length()));

                // x and y in 1/10 mm with the same value -> diagonal line later on...
                int coordinate = lineCounter * 10000;
                String value = Integer.toString(coordinate);

                GSIBlock x = new GSIBlock(isGSI16, 81, "..46", "+", value);
                GSIBlock y = new GSIBlock(isGSI16, 82, "..46", "+", value);

                newLine = newLine.concat(" " + x.toString());
                newLine = newLine.concat(" " + y.toString());

                // leveled height rounded to 1/10mm (RAPP AG hack)
                String leveled = lineSplit[3];
                String leveledRounded = leveled.substring(0, 4) + "26" + leveled.substring(6, 7) + "0" + leveled.substring(7, leveled.length() - 1);

                newLine = newLine.concat(" " + leveledRounded);
                newLine = prepareLineEnding(newLine);

                // ignore lines with change points
                if (!((ignoreChangePoints) & (number == 0))) {
                    result.add(newLine);

                    lineCounter++;
                }
            }
        }
        return result;
    }

    /**
     * Tidy up resurrection (stations) and control point measurements from files.
     * <p>
     * RyCON has the intelligence to tidy up resurrection and control points by a given
     * structure in the measurement file. Stations are identified by word index (WI) and
     * the control / stake out points by order in the file and the pattern 'STKE'.
     *
     * @param holdStations      decide to hold station lines
     * @param holdControlPoints decide to hold control points
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<String> processTidyUp(boolean holdStations, boolean holdControlPoints) {

        /**
         * Inner class in method processTidyUp to simplify getting the number (substring operations)
         */
        class StringHelper {

            /**
             * Check a line for being a target line (three times coordinate is zero)
             * @param line line to check
             * @return true if it is a target line
             */
            boolean isTargetLine(String line) {
                if (isGSI16) {
                    return (line.split("0000000000000000").length - 1) == 3;
                } else {
                    return (line.split("00000000").length - 1) == 3;
                }
            }

            /**
             * Returns the number of a given line (substring operations)
             * @param string string to get a defined substring from
             * @return substring
             */
            String numberConvert(String string) {
                if (isGSI16) {
                    return string.substring(8, 24);
                } else {
                    return string.substring(8, 16);
                }
            }

        } // end of class StringHelper

        String controlPointIdentifier = Main.pref.getUserPref(PreferenceHandler.PARAM_CONTROL_POINT_STRING);
        String freeStationIdentifier = "000" + Main.pref.getUserPref(PreferenceHandler.PARAM_FREE_STATION_STRING);
        String stationIdentifier = "000" + Main.pref.getUserPref(PreferenceHandler.PARAM_KNOWN_STATION_STRING);

        ArrayList<String> result = new ArrayList<String>();

        // handle special case / exception when the file starts with one or more free station or (station) lines
        String firstRow = readStringLines.get(0).toUpperCase();

        if (firstRow.startsWith("*")) {
            isGSI16 = true;
            freeStationIdentifier = "00000000" + freeStationIdentifier;
            stationIdentifier = "00000000" + stationIdentifier;
        } else {
            isGSI16 = false;
        }

        breakOut:
        // breaking out of nested loops with a label called 'breakOut'
        if (firstRow.contains(freeStationIdentifier) || firstRow.contains(stationIdentifier)) {
            for (Iterator<String> iter = readStringLines.iterator(); iter.hasNext(); ) {
                firstRow = iter.next();
                if (firstRow.toUpperCase().contains(freeStationIdentifier) || firstRow.toUpperCase().contains(stationIdentifier)) {
                    if (!holdStations) {
                        iter.remove();
                    }
                } else if (firstRow.toUpperCase().contains(controlPointIdentifier)) {
                    if (!holdControlPoints) {
                        iter.remove();
                    }
                } else {
                    break breakOut;
                }
            }
        }

        /*
         * Use a helper array to identify the different lines by 'type'.
         *
         * type:
         * =================================
         * 1: target measurement
         * 2: free station
         * 3: stake out value / control points
         * 9: measurement value
         */
        int[] helperArray = new int[readStringLines.size()];

        /*
         * Try to detect single and two face measurements of control points.
         *
         * A one face measured control point contains only zero values as coordinates. A two face
         * measured control point contains the coordinates of the control point in the first, and
         * only zeros in the second line. Therefore the comparison has to be made from current
         * to previous line!
         *
         * The first comparison is made with the biggest integer value.
         */
        String currentLine;
        String previousLine = "12345678901234567890" + Integer.toString(Integer.MAX_VALUE);

        // The operations starts with the last line outside the for-loop!
        for (int i = 0; i < readStringLines.size(); i++) {
            currentLine = readStringLines.get(i);

            // detect two face measurement for target measurement
            String currentLineNumber = new StringHelper().numberConvert(currentLine);
            String previousLineNumber = new StringHelper().numberConvert(previousLine);

            // detect line type
            if (new StringHelper().isTargetLine(currentLine)) {
                helperArray[i] = 1;

                if (currentLineNumber.equals(previousLineNumber)) {
                    helperArray[i - 1] = 1;
                } else if (previousLineNumber.contains(controlPointIdentifier)) {
                    helperArray[i - 1] = 3;
                }
            } else if (currentLine.contains(freeStationIdentifier) || currentLine.contains(stationIdentifier)) {
                helperArray[i] = 2;
            } else if (currentLine.contains(controlPointIdentifier)) {

                // line above is free station
                if (previousLineNumber.contains(freeStationIdentifier) || currentLineNumber.contains(stationIdentifier)) {
                    helperArray[i] = 3;
                }
                // line above is the same control point -> stake out point is marked as target point
                else if (currentLineNumber.equals(previousLineNumber)) {
                    if (holdControlPoints) {
                        helperArray[i] = 3;
                    } else {
                        helperArray[i] = 1;
                    }
                }
                // line above is control point and not last line -> stake out point is measurement value
                else {
                    if (i < readStringLines.size() - 1) {
                        helperArray[i] = 9;
                    } else {
                        helperArray[i] = 3;
                    }
                }
            } else {
                helperArray[i] = 9;
            }
            previousLine = currentLine;
        }

        // preparing the result lines
        for (int i = 0; i < helperArray.length; i++) {
            int value = helperArray[i];

            String resultLine = prepareLineEnding(readStringLines.get(i));

            if (value == 9) {
                result.add(resultLine);
            } else {
                if (holdStations) {
                    if (value == 2) {
                        result.add(resultLine);
                    }
                }

                if (holdControlPoints) {
                    if (value == 3) {
                        result.add(resultLine);
                    }
                }
            }
        }

        return result;
    }

    private String prepareLineEnding(String stringToPrepare) {
        boolean concatBlankAtLineEnding = Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.GSI_SETTING_LINE_ENDING_WITH_BLANK));

        if (concatBlankAtLineEnding) {
            if (!stringToPrepare.endsWith(" ")) {
                stringToPrepare = stringToPrepare.concat(" ");
            }
        }

        return stringToPrepare;
    }

    private ArrayList<ArrayList<GSIBlock>> blockEncoder(ArrayList<String> lines) {

        ArrayList<GSIBlock> blocks; // full initialisation may be better
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<ArrayList<GSIBlock>>();

        // do it over all read lines
        for (String line : lines) {
            blocks = new ArrayList<GSIBlock>();
            String[] lineSplit = line.trim().split("\\s+");

            // used instead of 'deprecated' StringTokenizer here
            for (String aResult : lineSplit) {
                GSIBlock block = new GSIBlock(aResult);
                blocks.add(block);

                // detect WIs
                foundWordIndices.add(block.wordIndex);
            }

            // sort every 'line' of GSI blocks by word index (WI)
            Collections.sort(blocks, new Comparator<GSIBlock>() {
                @Override
                public int compare(GSIBlock o1, GSIBlock o2) {
                    if (o1.wordIndex > o2.wordIndex) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            // fill in the sorted 'line' of blocks into an array
            blocksInLines.add(blocks);
        }

        return blocksInLines;
    }

    private ArrayList<String> lineTransformation(boolean isGSI16, ArrayList<ArrayList<GSIBlock>> gsiBlocks) {
        ArrayList<String> result = new ArrayList<String>();

        for (ArrayList<GSIBlock> blocksInLines : gsiBlocks) {
            String newLine = "";

            if (isGSI16) {
                newLine = "*";
            }

            int counter = 0;

            for (GSIBlock block : blocksInLines) {
                newLine = newLine.concat(block.toString(isGSI16));

                if (counter < blocksInLines.size()) {
                    newLine = newLine.concat(" ");
                }

                counter++;
            }

            newLine = prepareLineEnding(newLine);

            result.add(newLine);
        }

        return result;
    }

    /**
     * Defines an inner object for better access to elements and so on.
     * <p>
     * In the first version this GSIBlock object is used only internally in this class.
     * Maybe later on, there will be a good reason to make an own public class from it.
     */
    private class GSIBlock {

        private final int wordIndex;
        private String sign = "+";
        private final String dataGSI;
        private final String information;

        /**
         * Constructor which defines the object with parameter.
         *
         * @param blockAsString complete block as one String
         */
        public GSIBlock(String blockAsString) {
            if (blockAsString.startsWith("*")) {
                isGSI16 = true;
                blockAsString = blockAsString.substring(1, blockAsString.length()); // get rid of the star sign at the line beginning
            }

            if (blockAsString.length() > 15) {
                isGSI16 = true;
            }

            this.wordIndex = new Integer(blockAsString.substring(0, 2));
            this.information = blockAsString.substring(2, 6);
            this.sign = blockAsString.substring(6, 7);
            this.dataGSI = blockAsString.substring(7, blockAsString.length());
        }

        /**
         * Constructor with given parameters to build the GSI structure.
         *
         * @param isGSI16   true if it is GSI16 format
         * @param wordIndex word index (WI) of the block
         * @param number    information for the point number (filled up with zeros)
         * @param dataGSI         {@code String} to transform into a GSIBlock
         */
        public GSIBlock(boolean isGSI16, int wordIndex, int number, String dataGSI) {

            String intern = "";

            int length = 8;

            if (isGSI16) {
                length = 16;
            }

            this.wordIndex = wordIndex;

            if (wordIndex == 11) {                                          // point number
                this.information = String.format("%04d", number);
                intern = dataGSI;
            } else if (wordIndex == 71) {                                   // code
                this.information = "..46";
                intern = dataGSI;
            } else if ((wordIndex > 80) || (wordIndex < 90)) {              // coordinates
                this.information = "..46";
                if (dataGSI.startsWith("-")) {
                    this.sign = "-";
                    intern = dataGSI.substring(1, dataGSI.length());
                } else if (dataGSI.startsWith("+")) {
                    intern = dataGSI.substring(1, dataGSI.length());
                } else {
                    intern = dataGSI;
                }

                Double d = Double.parseDouble(intern);
                if (d == 0d) {
                    intern = "0";
                } else {
                    d = d * 10000.0; // value d in 1/10mm

                    BigDecimal bigDecimal = new BigDecimal(d);
                    bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);

                    intern = bigDecimal.toString();
                }

            } else {                                                        // not used other values
                this.information = "..4.";
            }

            this.dataGSI = fillWithZeros(length, intern);
        }

        /**
         * Constructor with parameters to build the GSI structure.
         *
         * @param isGSI16     boolean for indicating a GSI16 file
         * @param wordIndex   word index (pos 1-2)
         * @param information information related to data (pos 3-6)
         * @param sign        sign (+ or -)(pos 7)
         * @param dataGSI     GSI8 data (pos 8-15) or GSI16 data (pos8-23)
         */
        public GSIBlock(boolean isGSI16, int wordIndex, String information, String sign, String dataGSI) {
            this.wordIndex = wordIndex;
            this.information = information;
            this.sign = sign;

            char[] leadingZeros;

            // fill the GSI data up to 8 or 16 signs with leading zeros
            if (isGSI16) {
                leadingZeros = new char[16 - dataGSI.length()];

            } else {
                leadingZeros = new char[8 - dataGSI.length()];

            }
            Arrays.fill(leadingZeros, '0');

            this.dataGSI = new String(leadingZeros) + dataGSI.substring(0, dataGSI.length());
        }

        /**
         * Returns a GSIBlock in a csv format.
         *
         * @return formatted {@code String} for CSV output
         */
        public String toPrintFormatCSV() {
            return this.toPrintFormatTXT().trim();
        }

        /**
         * Returns a GSIBlock in a printable format.
         *
         * @return formatted {@code String} for TXT output
         */
        public String toPrintFormatTXT() {
            String s = this.dataGSI;
            int length = s.length();

            StringBuilder stringBuilder;

            switch (wordIndex) {

                case 11:        // point number
                    s = trimLeadingZeros(s);
                    s = fillWithSpaces(length, s);
                    break;

                case 21:        // angle Hz
                case 22:        // angle Vz
                case 24:        // angle Hz0
                case 25:        // angle difference (Hz0 - Hz)
                    s = trimLeadingZeros(s);
                    s = fillWithSpaces(length, s);
                    break;

                case 26:        // offset
                case 27:        // angle Vz0
                case 28:        // angle difference (Vz0 - Vz)

                    break;

                case 58:        // addition constant in 1/10 mm
                    stringBuilder = new StringBuilder(s);
                    s = stringBuilder.insert(length - 4, ".").toString();

                    s = this.sign + trimLeadingZeros(s);
                    s = fillWithSpaces(length, s);
                    break;

                case 71:        // comment 1, mostly used for code
                    s = trimLeadingZeros(s);
                    s = fillWithSpaces(length, s);
                    break;

                case 81:        // easting E
                case 82:        // northing N
                case 83:        // height H
                case 84:        // easting E0
                case 85:        // northing N0
                case 86:        // height H0
                case 87:        // target height
                case 88:        // instrument height
                    stringBuilder = new StringBuilder(s);

                    if (this.information.endsWith("0")) {       // mm
                        s = stringBuilder.insert(this.dataGSI.length() - 3, ".").toString();
                    } else {                                    // 1/10 mm
                        s = stringBuilder.insert(this.dataGSI.length() - 4, ".").toString();
                    }

                    // insert sign if sign is negative ('-')
                    if (this.sign.equals("-")) {
                        s = this.sign + trimLeadingZeros(s);
                    } else {
                        s = trimLeadingZeros(s);
                    }

                    s = fillWithSpaces(length, s);

                    break;

            }

            return s;
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

                if (dataGSI.length() == 8) {
                    data = leadingZeros.concat(dataGSI);
                } else {
                    data = dataGSI;
                }

                result = result.concat(data);
            } else {
                if (dataGSI.length() == 8) {
                    result = wordIndex + information + sign + dataGSI;
                } else {
                    result = wordIndex + information + sign + dataGSI.substring(dataGSI.length() - 8, dataGSI.length());
                }

            }

            return result;
        }

        private String fillWithSpaces(int length, String input) {
            String format = "%" + length + "." + length + "s";
            return String.format(format, input);
        }

        private String fillWithZeros(int length, String input) {
            String format = "%" + length + "s";
            return String.format(format, input).replace(' ', '0');
        }

        private String trimLeadingZeros(String s) {
            String intern = s.replaceFirst("^0+(?!$)", "");  // cut off leading zeros with regex;

            if (intern.startsWith(".")) {
                return "0" + intern;
            } else {
                return intern;
            }
        }

    } // end of inner class GSIBlock

    /**
     * Defines an inner object for better handling and the ability to sort easily.
     * <p>
     * Later on, this could be done better.
     */
    private class GSIHelper {

        private final int code;
        private final String line;

        /**
         * Simple definition with the code as int and a String for the complete {@code GSIBlocks}.
         *
         * @param code code of the {@code GSIBlocks}
         * @param line {@code String} of the {@code GSIBlocks}
         */
        public GSIHelper(int code, String line) {
            this.code = code;
            this.line = line;
        }

        /**
         * Returns the code as Integer value.
         *
         * @return code as Integer value
         */
        public int getCode() {
            return code;
        }

        /**
         * Returns the line as String.
         *
         * @return line as String
         */
        public String getLine() {
            return line;
        }


    } // end of inner class GSIHelper

} // end of LeicaGSIFileTools
