/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

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
public class FileToolsLeicaGSI {

    private boolean isGSI16 = false;
    private ArrayList<String> readStringLines = null;
    private List<String[]> readCSVLines = null;
    private TreeSet<Integer> foundCodes = new TreeSet<>();
    private TreeSet<Integer> foundWordIndices = new TreeSet<>();

    /**
     * Class constructor with parameter for the read lines as {@code ArrayList<String>} object.
     * <p>
     * This constructor is used for reading line based text files without a special separator sign.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public FileToolsLeicaGSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Class constructor with parameter for the read lines as {@code List<String[]>} object.
     * <p>
     * This constructor is used for reading csv file lines.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public FileToolsLeicaGSI(List<String[]> readCSVLines) {
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
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        int lineCounter = 1;

        for (String line : readStringLines) {
            blocks = new ArrayList<>();

            String[] lineSplit = line.trim().split("\\s+");

            // point number
            blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[5]));

            // use code if necessary
            if (useCodeColumn) {
                blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[4]));
            }

            // easting and northing
            blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[1]));
            blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[2]));

            // use height if necessary
            if (useZeroHeights) {
                blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[3]));
            } else {
                if (!lineSplit[4].equals("0.000000")) {
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[3]));
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
     * @param sourceContainsCodeColumn if source file contains a code column
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertCSVBaselStadt2GSI(boolean isGSI16, boolean sourceContainsCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

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

        return convertTXT2GSI(isGSI16, sourceContainsCodeColumn);
    }

    /**
     * Converts a CSV file (comma or semicolon delimited) into a GSI file.
     * <p>
     * The format of the GSI file is controlled with a parameter. The separator
     * sign is automatically detected.
     *
     * @param isGSI16 control if GSI8 or GSI16 format is written
     * @param sourceContainsCodeColumn if source file contains a code column
     * @return converted {@code ArrayList<String>} with lines of GSI format
     */
    public ArrayList<String> convertCSV2GSI(boolean isGSI16, boolean sourceContainsCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

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

        return convertTXT2GSI(isGSI16, sourceContainsCodeColumn);
    }

    /**
     * Converts a GSI file into GSI8 or GS16 formatted file.
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
     * Converts a CAPLAN K file to GSI8 or GSI16 formatted file.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16 true if GSI16 format is used
     * @return converted GSI format file
     */
    public ArrayList<String> convertK2GSI(boolean isGSI16) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        for (String line : readStringLines) {
            blocks = new ArrayList<>();
            String number, easting, northing, height;

            if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                if (line.length() >= 16) {
                    number = line.substring(0, 16).trim();       // point number (no '*', ',' and ';'), column 1 - 16
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, number));
                }

                // String valency = line.substring(18, 18);            // valency, column 18

                if (line.length() >= 32) {
                    easting = line.substring(20, 32).trim();     // easting E, column 19-32
                    blocks.add(new GSIBlock(isGSI16, 81, lineCounter, easting));
                }

                if (line.length() >= 46) {
                    northing = line.substring(34, 46).trim();    // northing N, column 33-46
                    blocks.add(new GSIBlock(isGSI16, 82, lineCounter, northing));
                }

                if (line.length() >= 59) {
                    height = line.substring(48, 59).trim();      // height H, column 47-59
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, height));
                }

                if (line.length() >= 62) {
                    String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");

                    String code = lineSplit[0].trim();              // code is the same as object type, column 62...
                    blocks.add(new GSIBlock(isGSI16, 71, lineCounter, code));

                    for (int i = 1; i < lineSplit.length; i++) {
                        String attr = lineSplit[i].trim();
                        blocks.add(new GSIBlock(isGSI16, (71 + i), lineCounter, attr));
                        lineCounter++;
                    }
                }

                // check for at least one or more added elements to prevent writing empty lines
                if (blocks.size() > 0) {
                    lineCounter++;
                    blocksInLines.add(blocks);
                }
            }
        }

        return lineTransformation(isGSI16, blocksInLines);
    }

    /**
     * Converts a NIGRA height listing (*.ASC) into a Leica GSI file.
     * @param isGSI16 true if GSI16 format is used
     * @return converted GSI format file
     * @since 5
     */
    public ArrayList<String> convertNIGRA2GSI(boolean isGSI16) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();
        StringTokenizer stringTokenizer;

        int lineCounter = 1;

        // skip the first 7 lines without any needed information
        for (int i = 5; i < readStringLines.size(); i++) {
            blocks = new ArrayList<>();
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
     * @param sourceContainsCodeColumn if source file contains a code column
     * @return converted {@code ArrayList<String>>} with lines
     */
    public ArrayList<String> convertTXT2GSI(boolean isGSI16, boolean sourceContainsCodeColumn) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        for (String line : readStringLines) {
            blocks = new ArrayList<>();

            String[] lineSplit = line.trim().split("\\s+");
            switch (lineSplit.length) {
                case 1:     // prevent fall through
                    break;

                case 2:     // no, height
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[1]));
                    break;

                case 3:     // no, code, height or no, easting, northing
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[0]));
                    if (sourceContainsCodeColumn) {
                        blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[1]));
                        blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[2]));
                    } else {
                        blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[1]));
                        blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[2]));
                    }
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
     * Converts a text file from the geodata server Basel Landschaft (switzerland) into a GSI formatted file.
     * <p>
     * This method can differ between LFP and HFP files, which has a different structure.
     * With a parameter it is possible to distinguish between GSI8 and GSI16.
     *
     * @param isGSI16 distinguish between GSI8 or GSI16
     * @param useAnnotationColumn write additional information as annotation column (WI 71)
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertTXTBaselLandschaft2GSI(boolean isGSI16, boolean useAnnotationColumn) {
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        // remove comment line
        readStringLines.remove(0);

        for (String line : readStringLines) {
            blocks = new ArrayList<>();

            String[] lineSplit = line.trim().split("\\s+");

            switch (lineSplit.length) {
                case 5:     // HFP file
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[1]));

                    if (useAnnotationColumn) {
                        blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[0]));
                    }

                    blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[2]));
                    blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[3]));
                    blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[4]));
                    break;

                case 6:     // LFP file
                    blocks.add(new GSIBlock(isGSI16, 11, lineCounter, lineSplit[1]));

                    if (useAnnotationColumn) {
                        if (lineSplit[2].equals("NULL")) {
                            blocks.add(new GSIBlock(isGSI16, 41, lineCounter, "-1"));
                        } else {
                            blocks.add(new GSIBlock(isGSI16, 41, lineCounter, lineSplit[2]));
                        }
                        blocks.add(new GSIBlock(isGSI16, 71, lineCounter, lineSplit[0]));
                    }

                    blocks.add(new GSIBlock(isGSI16, 81, lineCounter, lineSplit[3]));
                    blocks.add(new GSIBlock(isGSI16, 82, lineCounter, lineSplit[4]));

                    // prevent 'NULL' element in height
                    if (!lineSplit[5].equals("NULL")) {
                        blocks.add(new GSIBlock(isGSI16, 83, lineCounter, lineSplit[5]));
                    }

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
     * Returns the found word indices (WI) as {@code TreeSet<Integer>}.
     *
     * @return found word indices as {@code TreeSet<Integer>}
     */
    TreeSet<Integer> getFoundWordIndices() {
        return foundWordIndices;
    }

    /**
     * Encodes a read string line that contains gsi data into an encapsulated <code>ArrayList</code> of
     * <code>GSIBlock</code>s.
     * <p>
     * Depending on the constructor pasted line type, the right encoding will be done.
     *
     * @return encoded GSIBlocks
     */
    ArrayList<ArrayList<GSIBlock>> getEncodedGSIBlocks() {
        if (readCSVLines != null && readCSVLines.size() > 0) {
            return blockEncoder(convertCSV2GSI(isGSI16,false));
        } else if (readStringLines != null && readStringLines.size() > 0) {
            return blockEncoder(readStringLines);
        } else {
            return new ArrayList<>();
        }
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
        Map<String, Object[]> data = new TreeMap<>();

        // transform lines into GSI-Blocks
        //ArrayList<ArrayList<GSIBlock>> gsiBlocks = blockEncoder(readStringLines);

        // prepare comment line if necessary
        if (writeCommentLine) {
            StringBuffer stringBuffer = new StringBuffer(commentLine);

            for (Integer wordIndex : foundWordIndices) {
                stringBuffer.append("\"");
                stringBuffer.append(wordIndex.toString());
                stringBuffer.append("\", ");
            }

            stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(", "));
            commentLine = stringBuffer.toString();
        }
/*
        for (ArrayList<GSIBlock> blocksAsLines : gsiBlocks) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndex = it.next();

                String intern = "";

                for (GSIBlock block : blocksAsLines) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndex == block.wordIndex) {
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
                    newLine = newLine.concat(separator);
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
        ArrayList<GSIHelper> linesWithCode = new ArrayList<>();
        ArrayList<GSIHelper> linesWithOutCode = new ArrayList<>();
        String newLine = null;

        // transform lines into GSI-Blocks
        ArrayList<ArrayList<GSIBlock>> gsiBlocks = blockEncoder(readStringLines);

        // one top level for every code
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (ArrayList<GSIBlock> blocksInLines : gsiBlocks) {

            // helper for code handling inside the switch statements
            int code = -1;
            int validCheckHelperValue = 0;

            for (GSIBlock block : blocksInLines) {
                switch (block.getWordIndex()) {
                    case 11:
                        newLine = block.toString();
                        break;

                    case 71:
                        code = Integer.parseInt(block.getDataGSI());
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
            ArrayList<String> temp = new ArrayList<>();

            // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
            for (GSIHelper gsiHelpers : linesWithCode) {

                if (code == gsiHelpers.getCode()) {
                    temp.add(gsiHelpers.getLine());
                } else {
                    result.add(temp);
                    temp = new ArrayList<>(); // do not use temp.clear()!!!
                    temp.add(gsiHelpers.getLine());
                }

                code = gsiHelpers.getCode();

            }
            // insert last element
            result.add(temp);
        }

        // insert lines without code for writing
        if (writeLinesWithoutCode && (linesWithOutCode.size() > 0)){
            ArrayList<String> temp = new ArrayList<>();

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

        ArrayList<String> result = new ArrayList<>();

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
            private boolean isTargetLine(String line) {
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
            private String numberConvert(String string) {
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

        ArrayList<String> result = new ArrayList<>();

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
        ArrayList<GSIBlock> blocks;
        ArrayList<ArrayList<GSIBlock>> blocksInLines = new ArrayList<>();

        // do it over all read lines
        for (String line : lines) {
            blocks = new ArrayList<>();
            String[] lineSplit = line.trim().split("\\s+");

            // used instead of 'deprecated' StringTokenizer here
            for (String aResult : lineSplit) {
                GSIBlock block = new GSIBlock(aResult);
                blocks.add(block);

                // detect WIs
                foundWordIndices.add(block.getWordIndex());
            }

            // sort every 'line' of GSI blocks by word index (WI)
            Collections.sort(blocks, new Comparator<GSIBlock>() {
                @Override
                public int compare(GSIBlock o1, GSIBlock o2) {
                    if (o1.getWordIndex() > o2.getWordIndex()) {
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
        ArrayList<String> result = new ArrayList<>();

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
        GSIHelper(int code, String line) {
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

} // end of FileToolsLeicaGSI
