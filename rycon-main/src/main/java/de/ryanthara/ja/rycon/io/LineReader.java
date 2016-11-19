/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Instances of this class implements functions to read a text based path line by line and stores it's values
 * in an {code ArrayList<String>}.
 * <p>
 * A couple of things are implemented as additional functionality. At the moment there is no thread safety
 * implemented or planed due to some reasons.
 *
 * @author sebastian
 * @version 4
 * @since 1
 */
public class LineReader {

    private final Path path;
    private int countReadLines = -1;
    private int countStoredLines = -1;
    private ArrayList<String> lines = null;

    /**
     * Constructs a new instance of this class with a parameter that accepts a {@link Path} object for the file to be read.
     *
     * @param path file name as path object
     */
    public LineReader(Path path) {
        this.path = path;
    }

    /**
     * Return the number of read lines.
     * <p>
     * By default the value is set to -1, which shows, that no line has been read.
     *
     * @return number of read lines
     */
    // TODO Implement the usage of the count read lines to the status bar
    public int getCountReadLines() {
        return countReadLines;
    }

    /**
     * Return the number of stored lines.
     * <p>
     * By default the value is set to -1, which shows, that no read line has been stored to the {@code ArrayList<String>}.
     *
     * @return number of stored lines
     */
    // TODO Implement the usage of the stored read lines to the status bar (better not dropped lines?)
    public int getCountStoredLines() {
        return countStoredLines;
    }

    /**
     * Return the read lines as an {@code ArrayList<String>} object.
     * <p>
     * The {@code ArrayList<String>} contains every read line, first line on top.s
     *
     * @return read lines as {@code ArrayList<String>} object
     */
    public ArrayList<String> getLines() {
        return lines;
    }

    /**
     * Read a path line by line and return the read success.
     *
     * @return success of path reading
     */
    public boolean readFile() {
        return readFile(null);
    }

    /**
     * Read a path line by line and returns the read success.
     * <p>
     * Additionally with the parameter 'comment', there is the possibility to use
     * a {@code String} as comment sign. These lines will be ignored and not read.
     *
     * @param comment String for comment signs
     *
     * @return success of path reading
     */
    private boolean readFile(String comment) {
        boolean success = false;
        lines = new ArrayList<>();
        FileInputStream fileInputStream = null;
        String line;

        // check path for a couple of things
        if (path == null || !Files.exists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            return false;
        } else {
            try {
                fileInputStream = new FileInputStream(path.toFile());

                try {
                    fileInputStream.getChannel().lock(0, Long.MAX_VALUE, true);
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {

                        // read the lines into an ArrayList
                        while ((line = bufferedReader.readLine()) != null) {
                            countReadLines = countReadLines + 1;
                            if (!line.trim().equals("")) {
                                if (comment == null) {
                                    lines.add(line);
                                    countStoredLines = countStoredLines + 1;
                                } else {
                                    if (!line.startsWith(comment)) {
                                        lines.add(line);
                                        countStoredLines = countStoredLines + 1;
                                    }
                                }
                            }
                        }
                        // hack to get rid of the -1 initialization
                        countReadLines = countReadLines + 1;
                        countStoredLines = countStoredLines + 1;

                        success = true;
                    }

                } catch (IOException e) {
                    System.err.println("File " + path.getFileName() + " could not be locked.");
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                System.err.println("File: " + path.getFileName() + "could not be read.");
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
