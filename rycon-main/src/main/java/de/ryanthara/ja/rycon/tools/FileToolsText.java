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

import de.ryanthara.ja.rycon.tools.elements.GSIBlock;

import java.util.*;

/**
 * This class implements basic operations on text based measurement and coordinate files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>5: support for cadwork node.dat files, code clean up</li>
 *     <li>4: support for NIGRA levelling files</li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 5
 * @since 1
 */
public class FileToolsText {

    private FileToolsLeicaGSI toolsLeicaGSI = null;

    private ArrayList<String> readStringLines;
    private List<String[]> readCSVLines;
    private TreeSet<Integer> foundCodes = new TreeSet<>();

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param arrayList {@code ArrayList<String>} with lines in text format
     */
    public FileToolsText(ArrayList<String> arrayList) {
        this.readStringLines = arrayList;
    }

    /**
     * Class constructor for read line based Leica GSI files.
     * <p>
     * Due to some details of the Leica GSI format it is easier to get access to the {@link FileToolsLeicaGSI} object
     * instead of having a couple of more parameters.
     *
     * @param toolsLeicaGSI {@link FileToolsLeicaGSI} object
     */
    public FileToolsText(FileToolsLeicaGSI toolsLeicaGSI) {
        this.toolsLeicaGSI = toolsLeicaGSI;
    }

    /**
     * Class constructor for read line based CSV files.
     *
     * @param readCSVLines {@code List<String[]>} with lines in csv format
     */
    public FileToolsText(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Convert a CSV file into a TXT file with a given separator sign.
     *
     * @param separator separator sign to use for conversion
     * @return converted TXT file
     */
    public ArrayList<String> convertCSV2TXT(String separator) {
        ArrayList<String> result = new ArrayList<>();

        // convert the List<String[]> into an ArrayList<String> and use known stuff (-:
        for (String[] stringField : readCSVLines) {
            String line = "";

            for (String s : stringField) {
                line = line.concat(s);
                line = line.concat(separator);
            }

            line = line.trim();
            line = line.replace(',', '.');

            // skip empty lines
            if (!line.equals("")) {
                result.add(line);
            }
        }
        return result;
    }

    /**
     * Convert a CSV file from the geodata server Basel Stadt (Switzerland) into a txt format file.
     * <p>
     * With a parameter it is possible to distinguish between space or tabulator as separator.
     *
     * @param separator separator sign as {@code String}
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertCSVBaselStadt2TXT(String separator) {
        ArrayList<String> result = new ArrayList<>();

        // remove comment line
        readCSVLines.remove(0);

        for (String[] stringField : readCSVLines) {
            String line;

            // point number is in column 1
            line = stringField[0].replaceAll("\\s+", "").trim();
            line = line.concat(separator);

            // easting (Y) is in column 3
            line = line.concat(stringField[2]);
            line = line.concat(separator);

            // northing (X) is in column 4
            line = line.concat(stringField[3]);

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                line = line.concat(separator);
                line = line.concat(stringField[4]);
            }

            result.add(line.trim());
        }
        return result;
    }

    /**
     * Convert a GSI file into a space or tab delimited text file.
     * <p>
     * With parameter it is possible to set the separation char (space or tab).
     *
     * @param separator         separator sign as {@code String}
     * @param isGSI16           true if GSI16 format is used
     * @param writeCommentLine  if comment line should be written
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertGSI2TXT(String separator, boolean isGSI16, boolean writeCommentLine) {
        String commentLine = "";
        ArrayList<String> result = new ArrayList<>();

        String sep = separator.equals(" ") ? "    " : separator;

        TreeSet<Integer> foundWordIndices = toolsLeicaGSI.getFoundWordIndices();

        if (writeCommentLine) {
            int length;

            length = isGSI16 ? 16 : 8;

            String format = "%" + length + "." + length + "s";
            String s;

            int counter = 0;

            for (Integer wordIndex : foundWordIndices) {
                s = String.format(format, wordIndex.toString());
                commentLine = commentLine.concat(s);

                if (counter < foundWordIndices.size() - 1) {
                    commentLine = commentLine.concat(sep);
                }
                counter++;
            }

            StringBuilder builder = new StringBuilder(commentLine);
            commentLine = builder.replace(0, 5, "# WI:").toString();

            result.add(0, commentLine);
        }

        for (ArrayList<GSIBlock> blocksAsLines : toolsLeicaGSI.getEncodedLinesOfGSIBlocks()) {
            String newLine = "";

            Iterator<Integer> it = foundWordIndices.iterator();

            for (int i = 0; i < foundWordIndices.size(); i++) {
                Integer wordIndex = it.next();

                String intern = "";

                for (GSIBlock block : blocksAsLines) {
                    // check the WI and fill in an empty block of spaces if WI doesn't match to 'column'
                    if (wordIndex == block.getWordIndex()) {
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
                    newLine = newLine.concat(sep);
                }
            }
            result.add(newLine);
        }
        return result;
    }

    /**
     * Convert an K formatted file (CAPLAN) to txt formatted file.
     *
     * @param separator         distinguish between tabulator or space as division sign
     * @param writeCommentLine  writes an comment line into the file
     * @param writeCodeColumn   writes a code column (nr code x y z attr)
     * @param writeSimpleFormat writes a simple format (nr x y z or nr code x y z)
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertK2TXT(String separator, boolean writeCommentLine, boolean writeCodeColumn,
                                          boolean writeSimpleFormat) {

        ArrayList<String> result = new ArrayList<>();

        if (writeCommentLine) {
            String commentLine = "";

            if (writeSimpleFormat) {
                commentLine = "nr" + separator + "x" + separator + "y" + separator + "z";
            } else if (writeCodeColumn) {
                commentLine = "nr" + separator + "code" + separator + "x" + separator + "y" + separator + "z" + separator + "attribute";
            }

            result.add(commentLine);
        }

        for (String line : readStringLines) {
            if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                String s = "";

                if (line.length() >= 16) {
                    s = line.substring(0, 16).trim();       // point number (no '*', ',' and ';'), column 1 - 16
                }

                if ((line.length() >= 62) && writeSimpleFormat && writeCodeColumn) {
                    String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");
                    String code = lineSplit[0].trim();      // code is the same as object type, column 62...

                    s = s.concat(separator);
                    s = s.concat(code);
                } else if (writeCodeColumn) {
                    s = s.concat(separator);
                    s = s.concat("NULL");
                }

                if (line.length() >= 32) {
                    String easting = line.substring(20, 32).trim();     // easting E, column 19-32
                    s = s.concat(separator);
                    s = s.concat(easting);
                }

                if (line.length() >= 46) {
                    String northing = line.substring(34, 46).trim();    // northing N, column 33-46
                    s = s.concat(separator);
                    s = s.concat(northing);
                }

                if (line.length() >= 59) {
                    String height = line.substring(48, 59).trim();      // height H, column 47-59
                    s = s.concat(separator);
                    s = s.concat(height);
                }

                if ((line.length() >= 62) && !writeSimpleFormat && writeCodeColumn) {
                    String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");

                    String code = lineSplit[0].trim();              // code is the same as object type, column 62...
                    s = s.concat(separator);
                    s = s.concat(code);

                    for (int i = 1; i < lineSplit.length; i++) {
                        String attr = lineSplit[i].trim();
                        s = s.concat(separator);
                        s = s.concat(attr);
                    }
                }

                result.add(s.trim());
            }
        }

        return result;
    }

    /**
     * Convert a text file from the geodata server Basel Landschaft (Switzerland) into a TXT formatted file (no code x y z).
     * <p>
     * This method can differ between LFP and HFP files, which has a given different structure.
     * With a parameter it is possible to distinguish between tabulator and space divided files.
     *
     * @param separator         distinguish between tabulator or space as division sign
     * @param writeCodeColumn   use 'Versicherungsart' (LFP) as code column on second position
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertTXTBaselLandschaft2TXT(String separator, boolean writeCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        // remove comment line
        readStringLines.remove(0);

        for (String line : readStringLines) {
            String s;

            String[] lineSplit = line.trim().split("\\t", -1);

            // point number is in column 2
            s = lineSplit[1];
            s = s.concat(separator);

            switch (lineSplit.length) {
                case 5:     // HFP file
                    // easting (Y) is in column 3
                    s = s.concat(lineSplit[2]);
                    s = s.concat(separator);

                    // northing (X) is in column 4
                    s = s.concat(lineSplit[3]);
                    s = s.concat(separator);

                    // height (Z) is in column 5, and always valued (HFP file)
                    s = s.concat(lineSplit[4]);
                    s = s.concat(separator);

                    result.add(s.trim());
                    break;

                case 6:     // LFP file
                    // use 'Versicherungsart' as code. It is in column 3
                    if (writeCodeColumn) {
                        s = s.concat(lineSplit[2]);
                        s = s.concat(separator);
                    }

                    // easting (Y) is in column 4
                    s = s.concat(lineSplit[3]);
                    s = s.concat(separator);

                    // northing (X) is in column 5
                    s = s.concat(lineSplit[4]);
                    s = s.concat(separator);

                    // height (Z) is in column 6, and not always valued (LFP file)
                    if (lineSplit[5].equals("NULL")) {
                        s = s.concat("-9999");
                    } else {
                        s = s.concat(lineSplit[5]);
                    }

                    result.add(s.trim());
                    break;

                default:
                    System.err.println("Error in convertTXTBaselLandschaft2TXT: line length doesn't match 5 or 6 elements");
            }
        }
        return result;
    }

    /**
     * Return the found codes as an integer array.
     * <p>
     * This is necessary because of the elimination of the code in the string line.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Split a code based file into separate files by code.
     * <p>
     * A separate file is generated for every existing code. Lines without code will ignored.
     * RyCON need a text file format that is nr, code, x, y, z and divided by blank or tab.
     *
     * @param dropCode if code column should dropped out of the result
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<ArrayList<String>> processCodeSplit(boolean dropCode) {
        String newLine;
        StringTokenizer stringTokenizer;

        ArrayList<TextHelper> linesWithCode = new ArrayList<>();

        // one top level for every code
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        for (String line : readStringLines) {
            stringTokenizer = new StringTokenizer(line);

            // a line with code contains 5 tokens (nr, code, y, y, z)
            if (stringTokenizer.countTokens() == 5) {

                // number
                newLine = stringTokenizer.nextToken();

                // code
                String codeBlock = stringTokenizer.nextToken();
                foundCodes.add(Integer.parseInt(codeBlock));

                if (dropCode) {
                    newLine = newLine.concat(" " + codeBlock);
                }

                // x coordinate
                newLine = newLine.concat(" " + stringTokenizer.nextToken());

                // y coordinate
                newLine = newLine.concat(" " + stringTokenizer.nextToken());

                // z coordinate
                newLine = newLine.concat(" " + stringTokenizer.nextToken());

                linesWithCode.add(new TextHelper(Integer.parseInt(codeBlock), newLine));
            }
        }

        Collections.sort(linesWithCode, new Comparator<TextHelper>() {
            @Override
            public int compare(TextHelper o1, TextHelper o2) {
                if (o1.code > o2.code) {
                    return 1;
                } else if (o1.code == o2.code) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        // helpers for generating a new array for every found code
        // TODO a file without code is not supported
        int code = linesWithCode.get(0).code;
        ArrayList<String> lineStorage = new ArrayList<>();

        // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing it out
        for (TextHelper textBlock : linesWithCode) {
            if (code == textBlock.code) {
                lineStorage.add(textBlock.block);
            } else {
                result.add(lineStorage);
                lineStorage = new ArrayList<>(); // do not use temp.clear()!!!
                lineStorage.add(textBlock.block);
            }
            code = textBlock.code;
        }

        // insert last element
        result.add(lineStorage);

        return result;
    }

    /**
     * Define an inner object for better access to read text elements.
     * <p>
     * In the first version this TextHelper object is used only internally in this class.
     * Maybe later on, there will be a good reason to make an own class from it.
     *
     * @author sebastian
     * @version 1
     * @since 2
     */
    private static class TextHelper {

        final int code;
        final String block;

        /**
         * Constructor with parameters to build the block structure.
         *
         * @param code  code as integer value
         * @param block complete block as String
         */
        TextHelper(int code, String block) {
            this.code = code;
            this.block = block;
        }

        /**
         * Return the code and the block to String.
         *
         * @return code and block as String
         */
        public String toString() {
            return code + " " + block;
        }

    } // end of inner class TextHelper

}  // end of TextFileTools
