/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.io
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

package de.ryanthara.ja.rycon.io;

import de.ryanthara.ja.rycon.Main;

import java.io.*;
import java.util.ArrayList;

/**
 * This class reads a file line by line.
 * <p>
 * A couple of things are implemented as additional functionality. At the moment,
 * there is no thread safety implemented or planed.
 * <p>
 * The demo and open source version of RyCON will be limited to read only 20 lines
 * and show a notification window always on top.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class LineReader {

    private int countReadLines = -1;
    private int countStoredLines = -1;
    private ArrayList<String> lines = null;
    private final File file;

    /**
     * Class constructor with parameter that accept a file object for the file to read.
     *
     * @param file file name as file object
     */
    public LineReader(File file) {
        this.file = file;
    }

    /**
     * Returns the number of read lines.
     * <p>
     * By default the value is set to -1, which shows, that no line has been read.
     *
     * @return number of read lines
     */
    public int getCountReadLines() {
        return countReadLines;
    }

    /**
     * Returns the number of stored lines.
     * <p>
     * By default the value is set to -1, which shows, that no read line has been stored to the {@code ArrayList<String>}.
     *
     * @return number of stored lines
     */
    public int getCountStoredLines() {
        return countStoredLines;
    }

    /**
     * Returns the read lines as an {@code ArrayList<String>} object.
     * <p>
     * The {@code ArrayList<String>} contains every read line, first line on top.s
     *
     * @return read lines as {@code ArrayList<String>} object
     */
    public ArrayList<String> getLines() {
        return lines;
    }

    /**
     * Reads a file line by line and returns the read success.
     *
     * @return success of file reading
     */
    public boolean readFile() {
        return readFile(null);
    }

    /**
     * Reads a file line by line and returns the read success.
     * <p>
     * Additionally with the parameter 'comment', there is the possibility to use
     * a {@code String} as comment sign. These lines will be ignored and not read.
     *
     * @param comment String for comment signs
     * @return success of file reading
     */
    public boolean readFile(String comment) {

        // some basic initialization
        boolean success = false;
        lines = new ArrayList<String>();
        BufferedReader bufferedReader;
        FileInputStream fileInputStream = null;
        String line;

        // check file for a couple of things
        //  null-reference       directory or file      file                   can be read
        if (this.file == null || !this.file.exists() || !this.file.isFile() || !this.file.canRead()) {
            return false;
        } else {

            try {
                fileInputStream = new FileInputStream(this.file);

                try {
                    fileInputStream.getChannel().lock(0, Long.MAX_VALUE, true);
                    bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                    // read the lines into an ArrayList
                    while ((line = bufferedReader.readLine()) != null) {
                        countReadLines++;
                        if (comment == null) {
                            lines.add(line);
                            countStoredLines++;
                        } else {
                            if (!line.startsWith(comment)) {
                                lines.add(line);
                                countStoredLines++;
                            }
                        }

                        // TODO here is the limitation of the demo version
                        if (!Main.LICENSE) {
                            if (countReadLines > 20) {
                                success = true;
                                break;
                            }
                        }

                    }
                    // hack to get rid of the -1 initialization
                    countReadLines++;
                    countStoredLines++;

                    success = true;

                } catch (IOException e) {
                    System.err.println("File " + file.getName() + " could not be locked.");
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                System.err.println("File: " + file.getName() + "could not be read.");
                e.printStackTrace();
            } finally {
                // reset variables back to initialization values
                countReadLines = -1;
                countStoredLines = -1;
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    System.err.println("File input stream couldn't be closed.");
                    e.printStackTrace();
                }
            }

            return success;
        }
    }

} // end of LineReader
